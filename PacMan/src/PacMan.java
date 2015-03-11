
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

    private Grid<Actor> grid;
    private ArrayList<Actor> pellets = new ArrayList<Actor>();
    private ArrayList<Actor> ghosts = new ArrayList<Actor>();
    private MyStats myStats;
    private boolean amSuper = false;

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
        this.putSelfInGrid(grid, lctn);
        //Read through the grid to get locations for pellets and ghosts
        for (int cols = 0; cols < grid.getNumCols(); cols++) {
            for (int rows = 0; rows < grid.getNumRows(); rows++) {
                Location temploc = new Location(rows, cols);
                //if it's a pellet or power pellet (power pellets extend pellet)
                if (grid.get(temploc) instanceof Pellet) {
                    pellets.add(grid.get(temploc));
                }
                //if it's a ghost
                if (grid.get(temploc) instanceof Ghost) {
                    ghosts.add(grid.get(temploc));
                }
            }
        }
    }

    /**
     * Called when this Pac-Man eats a PowerPellet. Pac-Man should set their
     * color to BLUE when Pa-cMan turns super and return to their original color
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

    @Override
    public void act() {
    }

}
