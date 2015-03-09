
import info.gridworld.actor.Actor;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;
import java.awt.Color;

/**
 *
 * @author s-zhouj
 */
public class PacMan extends Actor implements PacManInterface {

    MyStats mystats;

    @Override
    public void initializeStats(MyStats ms, Color color) {
        this.mystats = ms;
        this.setColor(color);
    }

    @Override
    public void start(Grid<Actor> grid, Location lctn) {
        this.putSelfInGrid(grid, lctn);
    }

    @Override
    public void superPacMan(boolean bln, Actor actor) {
        mystats.addSuper();
        if (actor == this) {
            if (bln) {
                this.setColor(Color.BLUE);
            } else {
                this.setColor(Color.YELLOW);
            }
        }
    }

    @Override
    public void eaten() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isSuperPacMan() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
