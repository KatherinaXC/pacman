
import info.gridworld.actor.Actor;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;

/**
 *
 * @author Joyce
 */
public class Utility {

    public static final int[] DIRECTIONS = {0, 90, 180, 270};

    public static boolean atIntersection(Location current, Grid grid) {
        int open = 0;
        for (int direction : DIRECTIONS) {
            if (directionMoveIsValid(direction, current, grid)) {
                open++;
            }
        }
        return open > 2;
    }

    public static Location directionMove(int direction, Location current, Grid grid) {
        if (direction == 0) {
            return new Location(current.getRow() - 1, current.getCol());
        } else if (direction == 90) {
            return new Location(current.getRow(), current.getCol() + 1);
        } else if (direction == 180) {
            return new Location(current.getRow() + 1, current.getCol());
        } else {
            return new Location(current.getRow(), current.getCol() - 1);
        }
    }

    public static boolean directionMoveIsValid(int direction, Location current, Grid grid) {
        return !(grid.get(directionMove(direction, current, grid)) instanceof Wall);
    }
}
