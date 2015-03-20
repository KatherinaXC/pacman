
import info.gridworld.grid.Location;
import java.util.ArrayList;

/**
 * A mod-"wrapper" class (is that what you'd call it?) that is effectively a
 * Linked List for Locations. Actually, I might replace this class with a Linked
 * List later. Eh.
 *
 * @author s-zhouj
 */
public class SourcedLocationStep extends Location {

    Location whereDidIComeFrom;

    public SourcedLocationStep(Location whereIAm, Location whereDidIComeFrom) {
        super(whereIAm.getRow(), whereIAm.getCol());
        this.whereDidIComeFrom = whereDidIComeFrom;
    }

    /**
     * Returns the direct source of this step.
     *
     * @return
     */
    public Location source() {
        if (!this.isBase()) {
            SourcedLocationStep temp = (SourcedLocationStep) whereDidIComeFrom;
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
            ArrayList<Location> presourcepath = ((SourcedLocationStep) this.whereDidIComeFrom).sourcePath();
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
}
