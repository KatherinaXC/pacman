
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

    public static final int[] DIRECTIONS = {Location.NORTH, Location.EAST, Location.SOUTH, Location.WEST};

    /**
     * Returns if the current location is an intersection on the given grid.
     *
     * @param current
     * @param grid
     * @return
     */
    public static boolean atIntersection(Location current, Grid grid) {
        int open = 0;
        for (int direction : DIRECTIONS) {
            if (directionMoveIsValid(direction, current, grid)) {
                open++;
            }
        }
        return open > 2;
    }

    /**
     * Returns an ArrayList<Location> of the valid (non-wall) locations that are
     * around the given location in the given grid.
     *
     * @param current
     * @param grid
     * @return
     */
    public static ArrayList<Location> validSurrounding(Location current, Grid grid) {
        ArrayList<Location> solutionlist = new ArrayList<Location>();
        for (int direction : DIRECTIONS) {
            if (Utility.directionMoveIsValid(direction, current, grid)) {
                solutionlist.add(Utility.directionMove(direction, current));
            }
        }
        return solutionlist;
    }

    /**
     * Returns an ArrayList<Location> of all of the valid locations (including
     * walls) that are around the given location in the given grid.
     *
     * @param current
     * @param grid
     * @return
     */
    public static ArrayList<Location> allSurrounding(Location current, Grid grid) {
        ArrayList<Location> solutionlist = new ArrayList<Location>();
        for (int direction : DIRECTIONS) {
            solutionlist.add(Utility.directionMove(direction, current));
        }
        return solutionlist;
    }

    /**
     * Returns the location that is the given direction from the given location.
     * (Basically, returns a step in that direction, where the current location
     * is the second parameter.)
     *
     * @param direction
     * @param current
     * @return
     */
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

    /**
     * Returns if the given step-move with direction is an instance of wall.
     *
     * @param direction
     * @param current
     * @param grid
     * @return
     */
    public static boolean directionMoveIsValid(int direction, Location current, Grid grid) {
        return !(grid.get(directionMove(direction, current)) instanceof Wall);
    }

    /**
     * Returns if the two given directions are opposite of each other. (This
     * might actually be completely useless, but for the sake of not deleting
     * static code...)
     *
     * @param direction1
     * @param direction2
     * @return
     */
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

    /**
     * Returns the element of the ArrayList<Location> that is closest
     * (Euclidean-distance wise) to the given target.
     *
     * @param list
     * @param target
     * @return
     */
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

    /**
     * Returns the Manhattan Distance between the two given locations.
     *
     * @param location1
     * @param location2
     * @return
     */
    public static double manhattanDistance(Location location1, Location location2) {
        return Math.abs(location1.getRow() - location2.getRow()) + Math.abs(location1.getCol() - location2.getCol());
    }

    /**
     * Returns the Euclidean Distance between the two given locations.
     *
     * @param location1
     * @param location2
     * @return
     */
    public static double euclideanDistance(Location location1, Location location2) {
        return Math.sqrt(
                Math.pow(location1.getRow() - location2.getRow(), 2)
                + Math.pow(location1.getCol() - location2.getCol(), 2)
        );
    }

    /**
     * Removes the given element (using .equals() to calculate element
     * equivalence) from the given list. (This is somewhat redundant given that
     * ArrayList.remove() apparently also uses the .equals(), but for the sake
     * of not deleting static code... again...)
     *
     * @param list
     * @param loc
     * @return
     */
    public static ArrayList<Location> removeEquivalent(ArrayList<Location> list, Location loc) {
        ArrayList<Location> out = (ArrayList<Location>) list.clone();
        for (int i = 0; i < list.size(); i++) {
            if (loc.equals(list.get(i))) {
                out.remove(i);
            }
        }
        return out;
    }

    /**
     * Filters out the actors in the list dontwant from the list of places
     * given, for the purpose of not running into some actors such as ghosts.
     *
     * @param places
     * @param dontwant
     * @return
     */
    public static ArrayList<Location> filter(ArrayList<Location> places, ArrayList<Actor> dontwant) {
        ArrayList<Location> result = (ArrayList<Location>) places.clone();
        for (Actor checking : dontwant) {
            result.remove(checking.getLocation());
        }
        return result;
    }

    /**
     * Returns an ArrayList<Location> of places within a given radius of the
     * current location.
     *
     * @param whereIAm
     * @param distance
     * @param grid
     * @return
     */
    public static ArrayList<Location> withinRadius(Location whereIAm, int distance, Grid grid) {
        ArrayList<Location> results = new ArrayList<Location>();
        for (Location testing : (ArrayList<Location>) grid.getOccupiedLocations()) {
            if (Utility.manhattanDistance(whereIAm, testing) <= distance) {
                results.add(testing);
            }
        }
        return results;
    }

    /**
     * Returns the counterpart Warp location to the given Warp.
     *
     * @param grid
     * @return
     */
    public static Location warpAlternate(Location warp, Grid grid) {
        ArrayList<Location> warps = new ArrayList<Location>();
        ArrayList<Location> locs = grid.getOccupiedLocations();
        for (int i = 0; i < locs.size(); i++) {
            if (grid.get(locs.get(i)) instanceof Warp) {
                //if it's a pellet or power pellet (power pellets extend pellet)
                warps.add(locs.get(i));
            }
        }
        return warps.get((warps.indexOf(warp) + 1) % warps.size());
    }
}
