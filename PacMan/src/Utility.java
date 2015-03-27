
import info.gridworld.actor.Actor;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;
import java.util.ArrayList;

/**
 * A class to be used statically for Ghosts and PacMan, to assist with direction
 * calculating and walltesting.
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

    public static ArrayList<Location> validSurrounding(Location current, Grid grid) {
        ArrayList<Location> solutionlist = new ArrayList<Location>();
        for (int direction : DIRECTIONS) {
            if (Utility.directionMoveIsValid(direction, current, grid)) {
                solutionlist.add(Utility.directionMove(direction, current));
            }
        }
        return solutionlist;
    }

    public static Location directionMove(int direction, Location current) {
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
        return !(grid.get(directionMove(direction, current)) instanceof Wall);
    }

    public static boolean directionIsOpposite(int direction1, int direction2) {
        return Math.abs(direction1 - direction2) != 180;
    }

    /**
     * Returns if any elements of tofind are part of list, using two ArrayLists
     * of locations.
     *
     * @param list
     * @param tofind
     * @return
     */
    public static boolean containsTest(ArrayList<Location> list, ArrayList<Location> tofind) {
        for (int i = 0; i < tofind.size(); i++) {
            if (list.contains(tofind.get(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns if any elements of tofind are part of list, using a tofind of
     * Actor.
     *
     * @param list
     * @param tofind
     * @return
     */
    public static boolean containsTestActor(ArrayList<Location> list, ArrayList<Actor> tofind) {
        ArrayList<Location> templist = new ArrayList<Location>();
        for (Actor temp : tofind) {
            templist.add(temp.getLocation());
        }
        return Utility.containsTest(list, templist);
    }

    public static Location closestLocation(ArrayList<Location> list, Location target) {
        double closestdistance = Integer.MAX_VALUE;
        Location closest = null;
        for (Location testing : list) {
            if (euclideanDistance(testing, target) < closestdistance) {
                closestdistance = euclideanDistance(testing, target);
                closest = testing;
            }
        }
        return closest;
    }

    public static double manhattanDistance(Location location1, Location location2) {
        return Math.abs(location1.getRow() - location2.getRow()) + Math.abs(location1.getCol() - location2.getCol());
    }

    public static double euclideanDistance(Location location1, Location location2) {
        return Math.sqrt(
                Math.pow(location1.getRow() - location2.getRow(), 2)
                + Math.pow(location1.getCol() - location2.getCol(), 2)
        );
    }

    public static ArrayList<Location> removeEquivalent(ArrayList<Location> list, Location loc) {
        ArrayList<Location> out = (ArrayList<Location>) list.clone();
        for (int i = 0; i < list.size(); i++) {
            if (loc.equals(list.get(i))) {
                out.remove(i);
            }
        }
        return out;
    }

    public static ArrayList<Location> filter(ArrayList<Location> places, ArrayList<Actor> dontwant) {
        ArrayList<Location> result = (ArrayList<Location>) places.clone();
        for (Actor checking : dontwant) {
            result.remove(checking.getLocation());
        }
        return result;
    }

    public static ArrayList<Location> withinRadius(Location whereIAm, int distance, Grid grid) {
        ArrayList<Location> results = new ArrayList<Location>();
        for (Location testing : (ArrayList<Location>) grid.getOccupiedLocations()) {
            if (Utility.manhattanDistance(whereIAm, testing) <= distance) {
                results.add(testing);
            }
        }
        return results;
    }
}
