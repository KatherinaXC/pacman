
import info.gridworld.actor.Actor;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;
import java.awt.Color;

/**
 *
 * @author Joyce
 */
public class Ghost extends Actor implements GhostInterface {

    private Color origColor;
    private GhostArea spawnLoc;
    private MyStats myStats;

    @Override
    public void initializeGhost(GhostArea ga, MyStats ms, Color color) {
        this.spawnLoc = ga;
        this.spawnLoc.add(this);
        this.myStats = ms;
        this.origColor = color;
        this.setColor(origColor);
    }

    @Override
    public void start(Grid<Actor> grid, Location lctn) {
        this.putSelfInGrid(grid, lctn);
    }

    @Override
    public void eaten() {
        myStats.died();
        this.removeSelfFromGrid();
        spawnLoc.add(this);
    }

    @Override
    public void superPacMan(boolean bln) {
        if (bln) {
            this.setColor(Color.BLUE);
        } else {
            this.setColor(origColor);
        }
    }

}
