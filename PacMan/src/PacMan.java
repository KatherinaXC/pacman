
import info.gridworld.actor.Actor;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;
import java.awt.Color;

/**
 *
 * @author s-zhouj
 */
public class PacMan extends Actor implements PacManInterface {

    private Grid<Actor> grid;
    private MyStats myStats;
    private boolean amSuper = false;

    @Override
    public void initializeStats(MyStats ms, Color color) {
        this.myStats = ms;
        this.setColor(color);
    }

    @Override
    public void start(Grid<Actor> grid, Location lctn) {
        this.grid = grid;
        this.putSelfInGrid(grid, lctn);
    }

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

    @Override
    public void eaten() {
        myStats.died();
        this.removeSelfFromGrid();
    }

    @Override
    public boolean isSuperPacMan() {
        return this.amSuper;
    }

    @Override
    public void act() {
    }

}
