
import info.gridworld.actor.Actor;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;
import java.awt.Color;

/**
 *
 * @author Joyce
 */
public class Ghost extends Actor implements GhostInterface {

    MyStats mystats;

    @Override
    /**
     * Called after class creation to initialize a Ghost. Use
     * GhostArea.add(Actor) to add your ghost to the board. Do not put your
     * Actor in the grid in this method. The GhostArea will call
     * start(info.gridworld.grid.Grid<info.gridworld.actor.Actor>,
     * info.gridworld.grid.Location) to when you should do this.
     *
     */
    public void initializeGhost(GhostArea ga, MyStats ms, Color color) {
        ga.add(this);
        this.mystats = ms;
        this.setColor(color);
    }

    @Override
    /**
     * This method is called when this Ghost should enter the Maze. Use
     * putSelfInGrid to add yourself to the designated location.
     */
    public void start(Grid<Actor> grid, Location lctn) {
        this.putSelfInGrid(grid, lctn);
    }

    @Override
    public void eaten() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void superPacMan(boolean bln) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
