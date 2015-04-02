
import info.gridworld.grid.Location;

/**
 *
 * @author s-zhouj
 */
public class Blinky extends Ghost {

    /**
     * Returns the target location that the ghost wants to get to when it's in
     * regular mode.
     *
     * @return
     */
    @Override
    public Location regularTarget() {
        return getPacMan().getLocation();
    }

    /**
     * Returns the target location that the ghosts wants to get to when it's in
     * scatter mode.
     *
     * @return
     */
    @Override
    public Location scatterTarget() {
        return new Location(0, getGrid().getNumCols() - 1);
    }
    
    /**
     * Returns the chances of this particular ghost not moving (Speed of the
     * ghost - higher means faster).
     *
     * @return
     */
    @Override
    public int movementChance() {
        return 100;
    }
}
