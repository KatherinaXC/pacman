
import info.gridworld.actor.Actor;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;
import java.awt.Color;
import java.util.ArrayList;

/**
 *
 * @author Joyce
 */
public class Ghost extends Actor implements GhostInterface {

    //Grid fields
    private Grid grid;
    private GhostArea spawnLoc;
    private PacMan pacman;
    private Actor blinky; //for the use of Inky

    //Personal fields
    private Color origColor;
    private MyStats myStats;
    private boolean fullyInitialized = false;
    private Pellet pickedUp = null;
    private Location pickedUpLoc = null;
    private int scatterTimer = 0;

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
        myStats.died();
        this.removeSelfFromGrid();
        spawnLoc.add(this);
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
            this.setColor(Color.BLUE);
        } else {
            this.setColor(origColor);
        }
    }

    /**
     * Performs one step of action.
     */
    @Override
    public void act() {
        if (!fullyInitialized) {
            //I can't do this earlier since every object is placed on the board in-order
            //Read through the grid to get the location of PacMan
            ArrayList<Location> locs = grid.getOccupiedLocations();
            for (Location location : locs) {
                if (grid.get(location) instanceof PacMan) {
                    this.pacman = (PacMan) grid.get(location);
                }
                if (grid.get(location) instanceof Blinky) {
                    this.blinky = (Blinky) grid.get(location);
                    fullyInitialized = true;
                }
            }
        }

        Location directtarget = this.getLocation();
        if (Utility.directionMoveIsValid(this.getDirection(), this.getLocation(), grid)) {
            directtarget = Utility.directionMove(this.getDirection(), this.getLocation());
        }
        Pellet tempPickedUp = null;
        Location tempPickedUpLoc = null;

        //Find the metastep closest to my target
        ArrayList<Location> metalist = Utility.validSurrounding(directtarget, grid);
        metalist.remove(this.getLocation());
        Location metatarget = Utility.closestLocation(metalist, this.getTarget());
        //If I'm stuck, then set metatarget so that I'll rotate
        if (metatarget == null) {
            metatarget = Utility.directionMove(this.getDirection() + 90, directtarget);
        }

        //Reactions to potential things that I may hit
        if (grid.get(directtarget) instanceof Pellet) { //Pick up a pellet
            tempPickedUp = (Pellet) grid.get(directtarget);
            tempPickedUpLoc = directtarget;
        } else if (grid.get(directtarget) instanceof PacMan) { //Crash into a PacMan
            if (this.myStats.anySuperTime()) { //Eaten by a superPacMan
                this.eaten();
            } else { //Eating a regular PacMan
                this.myStats.scoreAtePacman();
                pacman.eaten();
            }
        }

        //Set direction and make final move, log it
        this.moveTo(directtarget);
        this.setDirection(this.getLocation().getDirectionToward(metatarget));
        this.myStats.moved();

        //If I picked up a pellet earlier, drop it
        if (pickedUp != null) {
            pickedUp.putSelfInGrid(grid, pickedUpLoc);
        }
        //Replace the pelletvariables
        this.pickedUp = tempPickedUp;
        this.pickedUpLoc = tempPickedUpLoc;
    }

    /**
     * Returns the target location for a Ghost, depending on their scattermode
     * and PacMan's supermode.
     *
     * @return
     */
    public Location getTarget() {
        if (this.myStats.anySuperTime() || getPacMan().getLocation() == null) {
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
}
