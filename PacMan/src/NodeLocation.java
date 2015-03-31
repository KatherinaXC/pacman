
import info.gridworld.actor.Actor;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;
import java.util.ArrayList;

/**
 * A mod-"wrapper" class (is that what you'd call it?) that is effectively a
 * Linked List for Locations. Actually, I might replace this class with a Linked
 * List later. Eh.
 *
 * @author s-zhouj
 */
public class NodeLocation extends Location {

    //instance vars
    private Location whereDidIComeFrom;
    private int cost;

    //static cost variables
    public static final int SUPER_PELLET_COST = 1;
    public static final int REGULAR_PELLET_COST = 3;
    public static final int SCARED_GHOST_COST = 5;
    public static final int EMPTY_COST = 15;
    public static final int SCARY_GHOST_COST = 50;
    public static final int WALL_COST = Integer.MAX_VALUE;

    public NodeLocation(Location whereIAm, Location whereDidIComeFrom, Grid grid) {
        super(whereIAm.getRow(), whereIAm.getCol());
        this.whereDidIComeFrom = whereDidIComeFrom;
        this.cost = NodeLocation.costCalculate(this.sourcePath(), grid);
    }

    /**
     * Returns the direct source of this step.
     *
     * @return
     */
    public Location source() {
        if (!this.isBase()) {
            NodeLocation temp = (NodeLocation) whereDidIComeFrom;
            return temp.source();
        }
        return this;
    }

    /**
     * Returns an ArrayList containing all the steps up to and containing this
     * one. Used to calculate a path for Ghosts and PacMan.
     *
     * @return
     */
    public ArrayList<Location> sourcePath() {
        ArrayList<Location> sourcepath = new ArrayList<Location>();
        if (this.isBase()) {
            ArrayList<Location> temp = new ArrayList<Location>();
            temp.add(this);
            return temp;
        } else {
            ArrayList<Location> presourcepath = ((NodeLocation) this.whereDidIComeFrom).sourcePath();
            for (Location source : presourcepath) {
                sourcepath.add(source);
            }
            sourcepath.add(this);
        }
        return sourcepath;
    }

    /**
     * Returns if this SourcedLocationStep is standalone or has a previous step.
     *
     * @return
     */
    public boolean isBase() {
        return this.whereDidIComeFrom == null;
    }

    /**
     * Calculates the cost of the given path, using the static variables that
     * assign costs to certain moves.
     *
     * @param path
     * @return
     */
    public static int costCalculate(ArrayList<Location> path, Grid grid) {
        int result = 0;
        for (Location current : path) {
            Actor testing = (Actor) grid.get(current);
            if (testing instanceof PowerPellet) {
                result += NodeLocation.SUPER_PELLET_COST;
            } else if (testing instanceof Pellet) {
                result += NodeLocation.REGULAR_PELLET_COST;
            } else if (testing instanceof Ghost) {
                if (((Ghost) testing).getScaredStatus()) {
                    result += NodeLocation.SCARED_GHOST_COST;
                } else if (!((Ghost) testing).getScaredStatus()) {
                    result += NodeLocation.SCARY_GHOST_COST;
                }
            } else if (testing instanceof Wall) {
                result += NodeLocation.WALL_COST;
            } else if (testing == null) {
                result += NodeLocation.EMPTY_COST;
            }
        }
        return result;
    }
}
