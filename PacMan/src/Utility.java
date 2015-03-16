
import info.gridworld.actor.Actor;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;
import java.util.ArrayList;

/**
 *
 * @author Joyce
 */
public class Utility {

    Grid grid;

    public Utility(Grid grid) {
        this.grid = grid;
    }

    public ArrayList<Location> getPath(Location current, Location target, ArrayList<Location> sofar) {
        if (!this.grid.isValid(current)) {
            return null;
        }
        if (this.grid.get(current) instanceof Wall) {
            return null;
        }
        sofar.add(new Location(current.getRow() - 1, current.getCol()));
        ArrayList<Location> trynorth = getPath(new Location(current.getRow() - 1, current.getCol()), target, sofar);
        sofar.remove(sofar.size() - 1);
        sofar.add(new Location(current.getRow(), current.getCol() + 1));
        ArrayList<Location> tryeast = getPath(new Location(current.getRow(), current.getCol() + 1), target, sofar);
        sofar.remove(sofar.size() - 1);
        sofar.add(new Location(current.getRow() + 1, current.getCol()));
        ArrayList<Location> trysouth = getPath(new Location(current.getRow() + 1, current.getCol()), target, sofar);
        sofar.remove(sofar.size() - 1);
        sofar.add(new Location(current.getRow(), current.getCol() - 1));
        ArrayList<Location> trywest = getPath(new Location(current.getRow(), current.getCol() - 1), target, sofar);
        sofar.remove(sofar.size() - 1);
        if (trynorth != null && trynorth.size() < sofar.size()) {
            sofar = trynorth;
        }
        if (tryeast != null && tryeast.size() < sofar.size()) {
            sofar = tryeast;
        }
        if (trysouth != null && trysouth.size() < sofar.size()) {
            sofar = trysouth;
        }
        if (trywest != null && trywest.size() < sofar.size()) {
            sofar = trywest;
        }
        return sofar;
    }

    public Location getOptimal(Location current) {
    }

}
