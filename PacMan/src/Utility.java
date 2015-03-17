
import info.gridworld.actor.Actor;
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

    public Location optimalStep(Location current, Actor actor) {
        Location target = current;
        //If I'm adjacent to something I want to eat, go there instead. (2nd priority)
        for (Object temploc : grid.getValidAdjacentLocations(current)) {
            Location surrloc = (Location) temploc;
            //if it's not a diagonal (can't go in diagonals :P)
            if (surrloc.getRow() == current.getRow()
                    || surrloc.getCol() == current.getCol()) {
                if (actor instanceof PacMan) {
                    PacMan pacman = (PacMan) actor;
                    if (grid.get(surrloc) instanceof Pellet) {
                        target = surrloc;
                    } else if (pacman.isSuperPacMan() && grid.get(surrloc) instanceof Ghost) {
                        target = surrloc;
                    }
                } else if (actor instanceof Ghost) {
                    Ghost ghost = (Ghost) actor;
                    if (!ghost.getPacMan().isSuperPacMan() && surrloc.equals(ghost.target())) {
                        target = surrloc;
                    }
                }
            }
        }
        //if I've cycled through everything and found nothing immediately near me, try again to a 2nd degree
        if (target == current) {
            for (Object temploc : grid.getValidAdjacentLocations(current)) {
                Location surrloc = (Location) temploc;
                if (optimalStep(surrloc, actor) != surrloc) {
                    target = surrloc;
                    break;
                }
            }
        }
        return target;
    }
}
