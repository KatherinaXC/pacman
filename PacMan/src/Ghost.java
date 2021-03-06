
import info.gridworld.actor.Actor;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Joyce
 */
public class Ghost extends Actor implements GhostInterface {

    //Grid fields
    private Grid grid;
    private GhostArea spawnLoc;
    private PacMan pacman;
    private ArrayList<Actor> ghosts = new ArrayList<Actor>();
    private Actor blinky; //for the use of Inky

    //Personal fields
    private Color origColor;
    private MyStats myStats;
    private boolean fullyInitialized = false;
    private Pellet pickedUp = null;
    private Location pickedUpLoc = null;
    private boolean amScared = false;

    //Ghost-universal fields
    private static int scatterTimer = 0;

    //Static final fields
    public static final int ACTIVE_GHOSTS = 4;

    /**
     * Called after class creation to initialize a Ghost. Use
     * GhostArea.add(Actor) to add your ghost to the board. Do not put your
     * Actor in the grid in this method. The GhostArea will call
     * start(info.gridworld.grid.Grid<info.gridworld.actor.Actor>,
     * info.gridworld.grid.Location) to when you should do this.
     *
     * @param ga
     * @param ms
     * @param color
     */
    @Override
    public void initializeGhost(GhostArea ga, MyStats ms, Color color) {
        this.spawnLoc = ga;
        this.spawnLoc.add(this);
        this.myStats = ms;
        this.origColor = color;
        this.setColor(origColor);
    }

    /**
     * This method is called when this Ghost should enter the Maze. Use
     * putSelfInGrid() to add yourself to the designated location.
     *
     * @param grid
     * @param lctn
     */
    @Override
    public void start(Grid<Actor> grid, Location lctn) {
        this.grid = grid;
        this.putSelfInGrid(grid, lctn);
    }

    /**
     * Called when the ghost is eaten by a Pac-Man. You should remove yourself
     * from the grid (if you haven't already), indicate that you've died, and
     * add yourself back to the GhostArea.
     *
     */
    @Override
    public void eaten() {
        //Do grid location stuff
        this.myStats.died();
        this.removeSelfFromGrid();
        this.spawnLoc.add(this);
        //Change my scared status + color (resets when I'm in the GA)
        this.amScared = false;
        this.setColor(origColor);
        //Drop any pellets I am holding
        this.pickedUp = null;
        this.pickedUpLoc = null;
    }

    /**
     * Called when any Pac-Man eats a PowerPellet. Ghosts should set their color
     * to BLUE when a pacman is super and return to their original color when
     * they've stopped.
     *
     * @param bln
     */
    @Override
    public void superPacMan(boolean bln) {
        if (bln) {
            //if pacman just ate a pellet
            this.setColor(Color.BLUE);
            if (!amScared) {
                //if pacman ate a pellet while i wasn't scared already
                this.setDirection((this.getDirection() + Location.HALF_CIRCLE) % Location.FULL_CIRCLE);
            }
        } else {
            //if the timer on pacman's super just ran out
            this.setColor(origColor);
        }
        this.amScared = bln;
    }

    /**
     * Performs one step of action.
     */
    @Override
    public void act() {
        boolean haveDied = false;
        if (!fullyInitialized) {
            //I can't do this earlier since every object is placed on the board in-order
            //Read through the grid to get the location of PacMan
            ArrayList<Location> locs = grid.getOccupiedLocations();
            for (Location location : locs) {
                if (grid.get(location) instanceof PacMan) {
                    this.pacman = (PacMan) grid.get(location);
                }
                if (grid.get(location) instanceof Ghost) {
                    this.ghosts.add((Actor) grid.get(location));
                }
                if (grid.get(location) instanceof Blinky && !(grid.get(location) instanceof Clyde)) {
                    this.blinky = (Blinky) grid.get(location);
                }
            }
            if (ghosts.size() == ACTIVE_GHOSTS) {
                fullyInitialized = true;
            }
        }

        //if i'm not on the board don't even do anything
        if (this.getLocation() == null) {
            return;
        }

        //do direct movement
        Location directtarget = this.getLocation();
        Location forward = Utility.directionMove(this.getDirection(), this.getLocation());
        //If it's not a wall and if I won't crash into a ghost, move directly forward
        if (Utility.directionMoveIsValid(this.getDirection(), this.getLocation(), grid) && !(grid.get(forward) instanceof Ghost)) {
            directtarget = forward;
        }
        Pellet tempPickedUp = null;
        Location tempPickedUpLoc = null;

        //Reactions to potential things that I may hit (after the direct target is finalized)
        if (grid.get(directtarget) instanceof Pellet) { //Pick up a pellet
            tempPickedUp = (Pellet) grid.get(directtarget);
            tempPickedUpLoc = directtarget;
        } else if (grid.get(directtarget) instanceof PacMan) { //Crash into a PacMan
            if (this.amScared) { //Eaten when I'm scared
                this.eaten();
                haveDied = true;
            } else { //Eating a regular PacMan (when I'm in normal state)
                this.myStats.scoreAtePacman();
                pacman.eaten();
            }
        }

        //Find the metastep closest to my target (in prep for the next step)
        ArrayList<Location> metalist = Utility.validSurrounding(directtarget, grid);
        //Filter out my location
        metalist.remove(this.getLocation());
        //Filter out other ghosts' locations
        metalist = Utility.filter(metalist, ghosts);
        Location metatarget = Utility.closestLocation(metalist, this.getTarget());
        //If I'm stuck, then set metatarget so that I'll rotate
        if (metatarget == null) {
            Random rand = new Random();
            int turndirection = 0;
            //get either 1 or -1
            do {
                turndirection = rand.nextInt(2) - 1;
            } while (turndirection != 0);
            //add either 90 or -90 to my direction, set my metatarget to that
            metatarget = Utility.directionMove((this.getDirection() + Location.HALF_RIGHT * turndirection) % Location.FULL_CIRCLE, directtarget);
        }

        //if I haven't died from the move that I'd make, and my random call doesn't say that I shouldn't move (Ghosts are slow)
        Random rand = new Random();
        if (!haveDied && rand.nextInt(this.movementChance()) != 0) {
            //Set direction and make final move, log it
            this.moveTo(directtarget);
            this.setDirection(this.getLocation().getDirectionToward(metatarget));
            this.myStats.moved();
        }

        //If I picked up a pellet earlier, drop it
        if (pickedUp != null) {
            pickedUp.putSelfInGrid(grid, pickedUpLoc);
        }
        //Replace the pelletvariables
        this.pickedUp = tempPickedUp;
        this.pickedUpLoc = tempPickedUpLoc;

        //Increment my scatter timer
        scatterTimer = (scatterTimer + 1) % 100;

        //Perform flash sequence at the end of a scared-period
        if (this.currentlyFlashing()) {
            this.setColor(this.flash());
        }
    }

    /**
     * Returns the target location for a Ghost, depending on their scattermode
     * and PacMan's supermode.
     *
     * @return
     */
    public Location getTarget() {
        //if i'm on my scatterloop, scared, pacman is dead... or pacman is dead?
        if (this.currentlyScattering()
                || this.amScared
                || this.getPacMan() == null
                || this.getPacMan().getLocation() == null) {
            return scatterTarget();
        } else {
            return regularTarget();
        }
    }

    /**
     * Returns the target location that the ghost wants to get to when it's in
     * regular mode.
     *
     * @return
     * @author Joyce
     */
    public Location regularTarget() {
        return this.getLocation();
    }

    /**
     * Returns the target location that the ghosts wants to get to when it's in
     * scatter mode.
     *
     * @return
     */
    public Location scatterTarget() {
        return this.getLocation();
    }

    /**
     * Returns if this ghost is currently in the scatter portion of its movement
     * cycle.
     *
     * @return
     */
    public boolean currentlyScattering() {
        return this.scatterTimer < 40;
    }

    /**
     * Returns if this ghost is currently supposed to be flashing (within
     * 10steps before the end of a scared period).
     *
     * @return
     */
    public boolean currentlyFlashing() {
        return this.getScaredStatus() && this.getPacMan().superTimeLeft() < 8;
    }

    /**
     * Returns the color that the Ghost is supposed to flash to next.
     *
     * @return
     */
    public Color flash() {
        if (this.getColor().equals(Color.BLUE)) {
            //if I'm blue right now, return white (so I can tell that it's flashing, and not being plain weird)
            return Color.WHITE;
        } else {
            //if i'm white, return blue
            return Color.BLUE;
        }
    }

    /**
     * Returns PacMan, for the child classes to use.
     *
     * @return
     */
    public PacMan getPacMan() {
        return this.pacman;
    }

    /**
     * Returns Blinky, for Inky to use in targetfinding.
     *
     * @return
     */
    public Actor getBlinky() {
        return this.blinky;
    }

    /**
     * Returns the grid, for the child classes to use.
     *
     * @return
     */
    public Grid getGrid() {
        return this.grid;
    }

    /**
     * Returns the pellet that is currently underneath the ghost (if any).
     *
     * @return
     */
    public Pellet getHeldPellet() {
        return this.pickedUp;
    }

    /**
     * Returns if this ghost is currently in scared mode.
     *
     * @return
     */
    public boolean getScaredStatus() {
        return this.amScared;
    }

    /**
     * Returns the chances of this particular ghost not moving (Speed of the
     * ghost - higher means faster).
     *
     * @return
     */
    public int movementChance() {
        return 1000;
    }

}
