
import info.gridworld.grid.Location;

/**
 *
 * @author s-zhouj
 */
public class Inky extends Ghost {

    /**
     * Returns the target location that the ghost wants to get to when it's in
     * regular mode.
     *
     * @return
     */
    public Location regularTarget() {
        Location pacman = getPacMan().getLocation();
        Location blinky = getBlinky().getLocation();
        return new Location(2 * pacman.getRow() - blinky.getRow(),
                2 * pacman.getCol() - blinky.getCol());
    }

    /**
     * Returns the target location that the ghosts wants to get to when it's in
     * scatter mode.
     *
     * @return
     */
    public Location scatterTarget() {
        return new Location(getGrid().getNumRows(), 0);
    }
}
