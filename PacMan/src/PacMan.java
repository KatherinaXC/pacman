
import info.gridworld.actor.Actor;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author s-zhouj
 */
public class PacMan extends Actor implements PacManInterface {

    private ArrayList<Actor> pellets = new ArrayList<Actor>();
    private ArrayList<Actor> ghosts = new ArrayList<Actor>();
    private Actor closestPellet = null;
    private ArrayList<Location> path = new ArrayList<Location>();

    private Grid<Actor> grid;
    private MyStats myStats;
    private boolean amSuper = false;
    private Color defColor;

    private Utility utility;

    /**
     * Called after class creation to initialize a Pac-Man. Do not put your
     * Actor in the grid in this method. The world will call
     * start(info.gridworld.grid.Grid<info.gridworld.actor.Actor>,
     * info.gridworld.grid.Location) when you should do this.
     *
     * @param ms
     * @param color
     */
    @Override
    public void initializeStats(MyStats ms, Color color) {
        this.myStats = ms;
        this.setColor(color);
        this.defColor = color;
    }

    /**
     * This method is called when Pac-Man should enter the Maze. Use
     * putSelfInGrid to add yourself to the designated location.
     *
     * @param grid
     * @param lctn
     */
    @Override
    public void start(Grid<Actor> grid, Location lctn) {
        //Set basic stats
        this.grid = grid;
        this.putSelfInGrid(grid, lctn);
        this.setDirection(Location.EAST);
        this.utility = new Utility(grid);
        //Read through the grid to get locations for pellets and ghosts
        ArrayList<Location> locs = grid.getOccupiedLocations();
        for (int i = 0; i < locs.size(); i++) {
            //if it's a pellet or power pellet (power pellets extend pellet)
            if (grid.get(locs.get(i)) instanceof Pellet) {
                pellets.add(grid.get(locs.get(i)));
            }
            //if it's a ghost
            if (grid.get(locs.get(i)) instanceof Ghost) {
                ghosts.add(grid.get(locs.get(i)));
            }
        }
    }

    /**
     * Called when this Pac-Man eats a PowerPellet. Pac-Man should set their
     * color to BLUE when Pac-Man turns super and return to their original color
     * when they've stopped. A Pac-Man may eat ghosts when any other Pac-Man is
     * super.
     *
     * @param bln
     * @param actor
     */
    @Override
    public void superPacMan(boolean bln, Actor actor) {
        if (actor == this) {
            this.amSuper = bln;
            if (bln) {
                myStats.addSuper();
                this.setColor(Color.BLUE);
            } else {
                this.setColor(this.defColor);
            }
        }
    }

    /**
     * Called when Pac-Man is eaten by a Ghost. You should remove yourself from
     * the grid (if you haven't already), and indicate that you've died.
     *
     */
    @Override
    public void eaten() {
        myStats.died();
        this.removeSelfFromGrid();
    }

    /**
     * Tell Ghosts if this Pac-Man is currently super-powered.
     *
     * @return boolean
     */
    @Override
    public boolean isSuperPacMan() {
        return this.amSuper;
    }

    /**
     * Performs one step of action.
     */
    @Override
    public void act() {
        Location target = null;
        Random rand = new Random();

        /* Finds the closest pellet birds-eye distance
         if (this.pellets.indexOf(closestPellet) < 0) {
         //Find the new closest pellet, if i've already eaten the last one
         this.closestPellet = this.pellets.get(0);
         for (Actor pellet : this.pellets) {
         if (Math.sqrt(
         Math.pow(pellet.getLocation().getRow() - this.getLocation().getRow(), 2)
         + Math.pow(pellet.getLocation().getCol() - this.getLocation().getCol(), 2)
         ) < Math.sqrt(
         Math.pow(closestPellet.getLocation().getRow() - this.getLocation().getRow(), 2)
         + Math.pow(closestPellet.getLocation().getCol() - this.getLocation().getCol(), 2)
         )) {
         closestPellet = pellet;
         }
         }
         }
         */
        //If I'm not adjacent to a pellet, then pick a random direction and follow it.
        //TODO MAKE THIS NOT RANDOM PLS.
        //If I'm at an intersection, I can try to turn.
        if (utility.atIntersection(this.getLocation())) {
            this.setDirection(90 * rand.nextInt(4));
        }
        while (!utility.directionMoveIsValid(this.getDirection(), this.getLocation())) {
            this.setDirection(90 * rand.nextInt(4));
        }
        target = utility.directionMove(this.getDirection(), this.getLocation());

        //If I'm adjacent to a pellet, go there. (2nd priority)
        for (Location surrloc : grid.getValidAdjacentLocations(this.getLocation())) {
            //if it's not a diagonal (can't go in diagonals :P)
            if (surrloc.getRow() == this.getLocation().getRow()
                    || surrloc.getCol() == this.getLocation().getCol()) {
                if (this.pellets.contains(grid.get(surrloc))) {
                    target = surrloc;
                    break;
                }
            }
        }

        //If the path I'll take goes towards a ghost, STEP AWAY FROM THE GHOST. (1st priority)
        while (grid.get(target) instanceof Ghost) {
            this.setDirection(90 * rand.nextInt(4));
            target = utility.directionMove(this.getDirection(), this.getLocation());
        }

        //If I ate a pellet, count that and remove it from the pelletlist.
        if (grid.get(target) instanceof PowerPellet) {
            superPacMan(true, this);
            this.myStats.addSuper();
            this.pellets.remove(grid.get(target));
        } else if (grid.get(target) instanceof Pellet) {
            this.myStats.scorePellet();
            this.pellets.remove(grid.get(target));
        } else if (grid.get(target) instanceof Ghost) {
            if (this.isSuperPacMan()) {
                this.myStats.scoreAteGhost(grid.get(target));
            } else {
                this.myStats.died();
                this.eaten();
            }
        }

        //Set my direction and make my final move.
        this.setDirection(this.getLocation().getDirectionToward(target));
        this.moveTo(target);
    }

}
