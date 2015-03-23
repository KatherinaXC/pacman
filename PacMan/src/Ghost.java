
import info.gridworld.actor.Actor;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;
import java.awt.Color;
import java.util.ArrayList;

/**
 *
 * @author Joyce
 */
public class Ghost extends Actor implements GhostInterface {

    //Grid fields
    private Grid grid;
    private PacMan pacman;
    private GhostArea spawnLoc;

    //Personal fields
    private Color origColor;
    private MyStats myStats;
    private int scatterTimer = 0;
    private boolean fullyInitialized = false;
    private int pathstep = Integer.MAX_VALUE;
    private ArrayList<Location> path = null;

    /**
     * Called after class creation to initialize a Ghost. Use
     * GhostArea.add(Actor) to add your ghost to the board. Do not put your
     * Actor in the grid in this method. The GhostArea will call
     * start(info.gridworld.grid.Grid<info.gridworld.actor.Actor>,
     * info.gridworld.grid.Location) to when you should do this.
     *
     * @param ga
     * @param ms
     * @param color
     */
    @Override
    public void initializeGhost(GhostArea ga, MyStats ms, Color color) {
        this.spawnLoc = ga;
        this.spawnLoc.add(this);
        this.myStats = ms;
        this.origColor = color;
        this.setColor(origColor);
    }

    /**
     * This method is called when this Ghost should enter the Maze. Use
     * putSelfInGrid() to add yourself to the designated location.
     *
     * @param grid
     * @param lctn
     */
    @Override
    public void start(Grid<Actor> grid, Location lctn) {
        this.grid = grid;
        this.putSelfInGrid(grid, lctn);
    }

    /**
     * Called when the ghost is eaten by a Pac-Man. You should remove yourself
     * from the grid (if you haven't already), indicate that you've died, and
     * add yourself back to the GhostArea.
     *
     */
    @Override
    public void eaten() {
        myStats.died();
        this.removeSelfFromGrid();
        spawnLoc.add(this);
    }

    /**
     * Called when any Pac-Man eats a PowerPellet. Ghosts should set their color
     * to BLUE when a pacman is super and return to their original color when
     * they've stopped.
     *
     * @param bln
     */
    @Override
    public void superPacMan(boolean bln) {
        if (bln) {
            this.setColor(Color.BLUE);
        } else {
            this.setColor(origColor);
        }
    }

    /**
     * Performs one step of action.
     */
    @Override
    public void act() {/*
         Location target = this.getLocation();
         if (!fullyInitialized) {
         //I can't do this earlier since every object is placed on the board in-order
         //Read through the grid to get the location of PacMan
         ArrayList<Location> locs = grid.getOccupiedLocations();
         for (Location location : locs) {
         if (grid.get(location) instanceof PacMan) {
         this.pacman = (PacMan) grid.get(location);
         }
         }
         fullyInitialized = true;
         }

         if (this.pathstep >= path.size() || getPacMan().isSuperPacMan()) {
         //get a new path and reset my path progress
         this.pathstep = 0;
         this.path = optimalStepPath();
         }
         //follow the steps on my path
         target = this.path.get(pathstep++);
         this.myStats.moved();

         //Reactions to potential things that I may hit
         if (grid.get(target) instanceof PacMan) {
         if (this.pacman.isSuperPacMan()) { //Eaten by a superPacMan
         this.myStats.scoreAteGhost(this);
         this.eaten();
         } else { //Eating a regular PacMan
         this.myStats.scoreAtePacman();
         pacman.eaten();
         }
         }

         //Set direction and make final move
         this.setDirection(this.getLocation().getDirectionToward(target));
         this.moveTo(target);*/

    }

    /**
     * Finds a path for the Ghost to follow. If there's no possible path, then
     * return null.
     *
     * @return
     */
    private ArrayList<Location> optimalStepPath() {
        //Initialize what I need
        ArrayList<SourcedLocationStep> totest = new ArrayList<>();
        ArrayList<Location> solution = null;
        //Exclude the current-opposing-facing direction.
        for (int direction : Utility.DIRECTIONS) {
            if (Utility.directionMoveIsValid(direction, this.getLocation(), grid) && !Utility.directionIsOpposite(direction, this.getDirection())) {
                totest.add(new SourcedLocationStep(Utility.directionMove(direction, this.getLocation(), grid), null));
            }
        }
        //Run through the list of places to try
        while (totest.size() > 0) {
            //If there is my target next to me, return the path that I took in getting there
            if (adjacentTest(totest.get(0)) != null) {
                solution = totest.get(0).sourcePath();
                solution.add(adjacentTest(totest.get(0)));
                break;
            } else {
                //If there isn't, keep testing moves and adding them to the end to test later
                for (int direction : Utility.DIRECTIONS) {
                    //If it's a valid move
                    if (Utility.directionMoveIsValid(direction, totest.get(0), grid)) {
                        SourcedLocationStep temp = new SourcedLocationStep(Utility.directionMove(direction, totest.get(0), grid), totest.get(0));
                        ArrayList<Location> leadupMap = temp.sourcePath();
                        leadupMap.remove(leadupMap.size() - 1);
                        //if it isn't contained in the current sequence already
                        if (!Utility.containsTest(leadupMap, temp) && !this.getLocation().equals(temp)) {
                            //if this won't lead me towards PacMan when i'm defenseless
                            if (pacman.isSuperPacMan() || !(grid.get(temp) instanceof PacMan)) {
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
     * Returns the location of the target if it's is adjacent to the given
     * location, otherwise returns null.
     *
     * @param current
     * @return
     */
    private Location adjacentTest(Location current) {
        //If I'm adjacent to something I want to eat, go there.
        for (Object surrloct : grid.getValidAdjacentLocations(current)) {
            Location surrloc = (Location) surrloct;
            //if it's not a diagonal (can't go in diagonals :P)
            if (surrloc.getRow() == current.getRow()
                    || surrloc.getCol() == current.getCol()) {
                if (grid.get(surrloc).equals(this.getTarget())) {
                    return surrloc;
                }
            }
        }
        return null;
    }

    /**
     * Returns the target location for a Ghost, depending on their scattermode
     * and PacMan's supermode.
     *
     * @return
     */
    public Location getTarget() {
        if (myStats.anySuperTime()) {
            return scatterTarget();
        } else {
            return regularTarget();
        }
    }

    /**
     * Returns the target location that the ghost wants to get to when it's in
     * regular mode.
     *
     * @return
     * @author Joyce
     */
    public Location regularTarget() {
        return this.getLocation();
    }

    /**
     * Returns the target location that the ghosts wants to get to when it's in
     * scatter mode.
     *
     * @return
     */
    public Location scatterTarget() {
        return this.getLocation();
    }

    /**
     * Returns PacMan, for the child classes to use.
     *
     * @return
     */
    public PacMan getPacMan() {
        return this.pacman;
    }
}
