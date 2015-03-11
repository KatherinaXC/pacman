
import info.gridworld.actor.Actor;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author s-zhouj
 */
public class PacMan extends Actor implements PacManInterface {

    private Grid<Actor> grid;
    private ArrayList<Actor> pellets = new ArrayList<Actor>();
    private ArrayList<Actor> ghosts = new ArrayList<Actor>();
    private MyStats myStats;
    private boolean amSuper = false;

    /**
     * Called after class creation to initialize a Pac-Man. Do not put your
     * Actor in the grid in this method. The world will call
     * start(info.gridworld.grid.Grid<info.gridworld.actor.Actor>,
     * info.gridworld.grid.Location) when you should do this.
     *
     * @param ms
     * @param color
     */
    @Override
    public void initializeStats(MyStats ms, Color color) {
        this.myStats = ms;
        this.setColor(color);
    }

    /**
     * This method is called when Pac-Man should enter the Maze. Use
     * putSelfInGrid to add yourself to the designated location.
     *
     * @param grid
     * @param lctn
     */
    @Override
    public void start(Grid<Actor> grid, Location lctn) {
        //Set basic stats
        this.grid = grid;
        this.putSelfInGrid(grid, lctn);
        this.setDirection(Location.EAST);
        //Read through the grid to get locations for pellets and ghosts
        ArrayList<Location> locs = grid.getOccupiedLocations();
        for (int i = 0; i < locs.size(); i++) {
            //if it's a pellet or power pellet (power pellets extend pellet)
            if (grid.get(locs.get(i)) instanceof Pellet) {
                pellets.add(grid.get(locs.get(i)));
            }
            //if it's a ghost
            if (grid.get(locs.get(i)) instanceof Ghost) {
                ghosts.add(grid.get(locs.get(i)));
            }
        }
    }

    /**
     * Called when this Pac-Man eats a PowerPellet. Pac-Man should set their
     * color to BLUE when Pac-Man turns super and return to their original color
     * when they've stopped. A Pac-Man may eat ghosts when any other Pac-Man is
     * super.
     *
     * @param bln
     * @param actor
     */
    @Override
    public void superPacMan(boolean bln, Actor actor) {
        if (actor == this) {
            this.amSuper = bln;
            if (bln) {
                myStats.addSuper();
                this.setColor(Color.BLUE);
            } else {
                this.setColor(Color.YELLOW);
            }
        }
    }

    /**
     * Called when Pac-Man is eaten by a Ghost. You should remove yourself from
     * the grid (if you haven't already), and indicate that you've died.
     *
     */
    @Override
    public void eaten() {
        myStats.died();
        this.removeSelfFromGrid();
    }

    /**
     * Tell Ghosts if this Pac-Man is currently super-powered.
     *
     * @return boolean
     */
    @Override
    public boolean isSuperPacMan() {
        return this.amSuper;
    }

    /**
     * Performs one step of action.
     */
    @Override
    public void act() {
        //Find the closest pellet.
        Actor closestPellet = this.pellets.get(0);
        for (Actor pellet : this.pellets) {
            if (Math.sqrt(
                    Math.pow(pellet.getLocation().getRow() - this.getLocation().getRow(), 2)
                    + Math.pow(pellet.getLocation().getCol() - this.getLocation().getCol(), 2)
            ) < Math.sqrt(
                    Math.pow(closestPellet.getLocation().getRow() - this.getLocation().getRow(), 2)
                    + Math.pow(closestPellet.getLocation().getCol() - this.getLocation().getCol(), 2)
            )) {
                closestPellet = pellet;
            }
        }
        //If there's already a pellet there, eat it.
        if (grid.get(filter(forward())) instanceof PowerPellet) {
            this.pellets.remove(closestPellet);
            this.myStats.addSuper();
        } else if (grid.get(filter(forward())) instanceof Pellet) {
            this.pellets.remove(closestPellet);
            this.myStats.scorePellet();
        }
        //Move forwards
        this.moveTo(filter(forward()));
        //Find the direction towards the closest pellet.
        int direction = this.getLocation().getDirectionToward(closestPellet.getLocation());
        this.setDirection(direction);
    }

    /**
     * Prevent the Pac-Man from walking into a wall, and if Pac-Man needs to
     * walk into a wall, redirect him. Be sure to always call this method when
     * attempting to move forward.
     *
     * @param loc
     * @return
     */
    private Location filter(Location loc) {
        if (grid.get(loc) instanceof Wall) {
            Random rand = new Random();
            if (rand.nextInt(3) == 0) {
                this.setDirection(Location.NORTH);
                return filter(forward());
            } else if (rand.nextInt(3) == 0) {
                this.setDirection(Location.EAST);
                return filter(forward());
            } else if (rand.nextInt(3) == 0) {
                this.setDirection(Location.SOUTH);
                return filter(forward());
            } else {
                this.setDirection(Location.WEST);
                return filter(forward());
            }
        }
        return loc;
    }

    /**
     * Take the current direction and apply it to Locations, creating a
     * potential forwards movement target. This is effectively a case-based
     * movement to translate cardinal directions to Locations using the current
     * Location. This method DOES NOT filter out impossible Locations.
     *
     * @return
     */
    private Location forward() {
        if (this.getDirection() == Location.NORTH || this.getDirection() == 45) {
            //if north or northeast
            return new Location(this.getLocation().getRow() - 1, this.getLocation().getCol());
        } else if (this.getDirection() == Location.EAST || this.getDirection() == 135) {
            //if east or southeast
            return new Location(this.getLocation().getRow(), this.getLocation().getCol() + 1);
        } else if (this.getDirection() == Location.SOUTH || this.getDirection() == 225) {
            //if south or southwest
            return new Location(this.getLocation().getRow() + 1, this.getLocation().getCol());
        } else {
            //if west or northwest
            return new Location(this.getLocation().getRow(), this.getLocation().getCol() - 1);
        }
    }
}
