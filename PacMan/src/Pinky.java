
import info.gridworld.grid.Location;

/**
 *
 * @author s-zhouj
 */
public class Pinky extends Ghost {

    /**
     * Returns the target location that the ghost wants to get to when it's in
     * regular mode.
     *
     * @return
     */
    @Override
    public Location regularTarget() {
        Location base = getPacMan().getLocation();
        //get the location 4 steps ahead of pacman
        for (int i = 0; i < 4; i++) {
            base = Utility.directionMove(getPacMan().getDirection(), base);
        }
        return base;
    }

    /**
     * Returns the target location that the ghosts wants to get to when it's in
     * scatter mode.
     *
     * @return
     */
    @Override
    public Location scatterTarget() {
        return new Location(0, 0);
    }

    /**
     * Returns the chances of this particular ghost not moving (Speed of the
     * ghost - higher means faster).
     *
     * @return
     */
    @Override
    public int movementChance() {
        return 200;
    }
}
