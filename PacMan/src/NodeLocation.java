
import info.gridworld.actor.Actor;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;
import java.util.ArrayList;

/**
 * A mod-"wrapper" class (is that what you'd call it?) that is effectively a
 * Linked List for Locations but also holds data necessary for the use of A*.
 *
 * @author s-zhouj
 */
public class NodeLocation extends Location {

    //instance vars
    private Location whereDidIComeFrom;
    private int cost;

    //static cost variables
    public static final int SUPER_PELLET_COST = 1;
    public static final int REGULAR_PELLET_COST = 20;
    public static final int SCARED_GHOST_COST = 7;
    public static final int EMPTY_COST = 100;
    public static final int SCARY_GHOST_COST = 100000;
    public static final int WALL_COST = 50000000;

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
     * Returns if this node is standalone or has a previous step.
     *
     * @return
     */
    public boolean isBase() {
        return this.whereDidIComeFrom == null;
    }

    /**
     * Returns this node's cost.
     *
     * @param target
     * @return
     */
    public int getCost(Location target) {
        return this.cost + (int) Utility.euclideanDistance(this, target);
    }

    /**
     * Calculates the cost of the given path, using the static variables that
     * assign costs to certain moves.
     *
     * @param path
     * @param grid
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
                } else {
                    result += NodeLocation.SCARY_GHOST_COST;
                }
            } else if (testing instanceof Wall) {
                if (testing instanceof Warp) {
                    result += NodeLocation.EMPTY_COST;
                } else {
                    result += NodeLocation.WALL_COST;
                }
            } else {
                result += NodeLocation.EMPTY_COST;
            }
        }
        return result;
    }
}
