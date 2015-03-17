
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;

/**
 *
 * @author Joyce
 */
public class Utility {

    private static final int[] DIRECTIONS = {0, 90, 180, 270};
    private Grid grid;

    public Utility(Grid grid) {
        this.grid = grid;
    }

    public boolean atIntersection(Location current) {
        int open = 0;
        for (int direction : DIRECTIONS) {
            if (directionMoveIsValid(direction, current)) {
                open++;
            }
        }
        return open > 2;
    }

    public Location directionMove(int direction, Location current) {
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

    public boolean directionMoveIsValid(int direction, Location current) {
        return !(grid.get(directionMove(direction, current)) instanceof Wall);
    }

}
