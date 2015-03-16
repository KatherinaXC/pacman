
import info.gridworld.actor.Actor;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;
import java.awt.Color;
import java.util.ArrayList;

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

    private static Utility utility;

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
        this.utility = new Utility(grid);
        this.putSelfInGrid(grid, lctn);
        this.setDirection(Location.EAST);
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
                this.setColor(Color.YELLOW);
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
        if (!this.pellets.contains(closestPellet)) {
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
            //Find the path towards the new closest pellet.
            this.path = utility.getPath(this.getLocation(), this.closestPellet.getLocation(), this.path);
        }
        //Move along the path and delete the step after you take it
        this.moveTo(this.path.get(0));
        this.path.remove(0);
    }

}
