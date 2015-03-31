
import info.gridworld.actor.Actor;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;
import java.awt.Color;
import java.util.ArrayList;

/**
 *
 * @author s-zhouj
 */
public class PacMan extends Actor implements PacManInterface {

    //Variables for keeping track of objects in the grid
    private ArrayList<Location> pellets = new ArrayList<Location>();
    private ArrayList<Actor> ghosts = new ArrayList<Actor>();
    private Grid<Actor> grid;

    //Variables for keeping track of myself
    private ArrayList<Location> path = new ArrayList<Location>();
    private int pathstep = Integer.MAX_VALUE;
    private MyStats myStats;
    private boolean amSuper = false;
    private Color defColor;
    private boolean fullyInitialized = false;

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
        this.defColor = color;
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
                this.setColor(Color.BLUE);
            } else {
                this.setColor(this.defColor);
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
        this.myStats.died();
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
        boolean haveDied = false;
        if (!fullyInitialized) {
            //I can't do this earlier since every object is placed on the board in-order
            //Read through the grid to get locations for pellets and ghosts
            ArrayList<Location> locs = grid.getOccupiedLocations();
            for (int i = 0; i < locs.size(); i++) {
                if (grid.get(locs.get(i)) instanceof Pellet) {
                    //if it's a pellet or power pellet (power pellets extend pellet)
                    pellets.add(locs.get(i));
                } else if (grid.get(locs.get(i)) instanceof Ghost) {
                    //if it's a pellet or power pellet (power pellets extend pellet)
                    ghosts.add(grid.get(locs.get(i)));
                }
            }
            fullyInitialized = true;
        }

        Location target = this.getLocation();

        if (adjacentTest(this.getLocation()) != null) {
            //If i'm next to something
            target = adjacentTest(this.getLocation());
            pathstep = Integer.MAX_VALUE;
        } else {
            //if i'm not next to something and have walked through my path, or am within 10 MHsteps of a ghost
            if (this.pathstep >= path.size()
                    || Utility.containsTestActor(Utility.withinRadius(this.getLocation(), 10, grid), ghosts)) {
                //get a new path and reset my path progress
                this.pathstep = 0;
                this.path = aStarOptimalPath();
            }
            //follow the steps on my path
            target = this.path.get(pathstep++);
            this.myStats.moved();
        }

        //Reactions to potential things that I may hit, after final target
        if (grid.get(target) instanceof Pellet) { //if I hit a pellet...
            scorePellet((Pellet) grid.get(target));
        } else if (grid.get(target) instanceof Ghost) { //if i hit a ghost...
            Ghost ghost = (Ghost) grid.get(target);
            if (ghost.getScaredStatus()) { //if this ghost is scared of me:
                //get its held pellet
                if (ghost.getHeldPellet() != null) {
                    scorePellet(ghost.getHeldPellet());
                }
                this.myStats.scoreAteGhost(ghost);
                ghost.eaten();
            } else { //if i'm not super or the ghost is regular:
                this.eaten();
                haveDied = true;
            }
        }

        if (!haveDied) {
            //Set my direction and make my final move.
            this.setDirection(this.getLocation().getDirectionToward(target));
            this.moveTo(target);
        }
    }

    /**
     * Finds a path for Pac-Man to follow, based on the current location. Uses a
     * breadth-first search, filtering out steps that crash into walls and
     * stopping a search when a scatterghost or pellet is found, then returning
     * the sequence of steps taken to get there.
     *
     * @param current
     * @return
     */
    private ArrayList<Location> optimalStepPath() {
        //Initialize what I need
        ArrayList<NodeLocation> totest = new ArrayList<>();
        ArrayList<Location> solution = null;
        for (int direction : Utility.DIRECTIONS) {
            if (Utility.directionMoveIsValid(direction, this.getLocation(), grid)) {
                totest.add(new NodeLocation(Utility.directionMove(direction, this.getLocation()), null, this.grid));
            }
        }
        //Run through the list of places to try
        while (totest.size() > 0) {
            //If there is a pellet next to me, return the path that I took in getting there
            if (adjacentTest(totest.get(0)) != null) {
                solution = totest.get(0).sourcePath();
                solution.add(adjacentTest(totest.get(0)));
                break;
            } else {
                //If there isn't, keep testing moves and adding them to the end to test later
                for (int direction : Utility.DIRECTIONS) {
                    //If it's a valid move
                    if (Utility.directionMoveIsValid(direction, totest.get(0), grid)) {
                        NodeLocation temp = new NodeLocation(Utility.directionMove(direction, totest.get(0)), totest.get(0), this.grid);
                        ArrayList<Location> leadupMap = temp.sourcePath();
                        leadupMap.remove(leadupMap.size() - 1);
                        //if it isn't contained in the current sequence already
                        if (!leadupMap.contains(temp) && !this.getLocation().equals(temp)) {
                            //if this won't lead me towards a ghost when i'm defenseless
                            if (this.isSuperPacMan() || !(grid.get(temp) instanceof Ghost)) {
                                totest.add(temp);
                            }
                        }
                    }
                }
            }
            //Remove the current element (it won't be tested again)
            totest.remove(0);
        }
        return solution;
    }

    /**
     * Finds a path for Pac-Man to follow, based on the current location, using
     * A* search algorithm.
     *
     * @param current
     * @return
     */
    private ArrayList<Location> aStarOptimalPath(Location target) {
        //Initialize the open list (places not yet attempted)
        ArrayList<Location> openlist = new ArrayList<>();
        for (int row = 0; row < this.grid.getNumRows(); row++) {
            for (int col = 0; col < this.grid.getNumCols(); col++) {
                openlist.add(new Location(row, col));
            }
        }
        //initialize the closed list (places already tried) (somewhat modded, since start shouldn't be part of the path)
        ArrayList<NodeLocation> closedlist = new ArrayList<>();
        for (int direction : Utility.DIRECTIONS) {
            if (Utility.directionMoveIsValid(direction, this.getLocation(), grid)) {
                closedlist.add(new NodeLocation(Utility.directionMove(direction, this.getLocation()), null, this.grid));
            }
        }
        ArrayList<Location> solution = null;

        //Run through the list of places to try
        while (openlist.size() > 0) {
            //If there is a pellet next to me, return the path that I took in getting there
            if (adjacentTest(openlist.get(0)) != null) {

            } else {
                //If there isn't, keep testing moves and adding them to the end to test later
                for (int direction : Utility.DIRECTIONS) {
                    //If it's a valid move
                    if (Utility.directionMoveIsValid(direction, openlist.get(0), grid)) {
                        NodeLocation temp = new NodeLocation(Utility.directionMove(direction, openlist.get(0)), openlist.get(0), this.grid);
                        ArrayList<Location> leadupMap = temp.sourcePath();
                        leadupMap.remove(leadupMap.size() - 1);
                        //if it isn't contained in the current sequence already
                        if (!leadupMap.contains(temp) && !this.getLocation().equals(temp)) {
                            //if this won't lead me towards a ghost when i'm defenseless
                            if (this.isSuperPacMan() || !(grid.get(temp) instanceof Ghost)) {
                                openlist.add(temp);
                            }
                        }
                    }
                }
            }
            //Remove the current element (it won't be tested again)
            openlist.remove(0);
        }
        return solution;
    }

    /**
     * Call the required methods associated with eating the given pellet. This
     * method is called when PacMan runs into a pellet of any type, or a ghost
     * that has a pellet under/with it.
     *
     * @param pellet
     */
    private void scorePellet(Pellet pellet) {
        if (pellet instanceof PowerPellet) { //A power pellet
            this.myStats.addSuper();
        } else { //a regular pellet
            this.myStats.scorePellet();
        }
        this.pellets.remove(pellet.getLocation()); //remove eaten pellets from my list}
    }

    /**
     * Returns the location of an edible if the given location is adjacent to
     * one, otherwise returns null.
     *
     * @param current
     * @return
     */
    private Location adjacentTest(Location current) {
        //If I'm adjacent to something I want to eat, go there.
        for (Location surrloc : grid.getValidAdjacentLocations(current)) {
            //if it's not a diagonal (can't go in diagonals :P)
            if (surrloc.getRow() == current.getRow()
                    || surrloc.getCol() == current.getCol()) {
                if (grid.get(surrloc) instanceof Pellet) {
                    return surrloc;
                } else if (this.isSuperPacMan() && grid.get(surrloc) instanceof Ghost) {
                    return surrloc;
                }
            }
        }
        return null;
    }
}
