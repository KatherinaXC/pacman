
import info.gridworld.actor.Actor;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;
import java.awt.Color;

/**
 *
 * @author Joyce
 */
public class Ghost extends Actor implements GhostInterface {

    private PacMan pacman;

    private Color origColor;
    private GhostArea spawnLoc;
    private MyStats myStats;

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
        this.putSelfInGrid(grid, lctn);
        for (Location location : grid.getOccupiedLocations()) {
            if (grid.get(location) instanceof PacMan) {
                this.pacman = (PacMan) grid.get(location);
            }
        }
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
     * Returns the target location that the ghost wants to get to.
     *
     * @return
     * @author Joyce
     */
    public Location target() {
        //TODO
        return this.getLocation();
    }

    public PacMan getPacMan() {
        return this.pacman;
    }
}
