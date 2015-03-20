
import info.gridworld.grid.Location;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author s-zhouj
 */
public class SourcedLocationStep extends Location {

    Location whereDidIComeFrom;

    public SourcedLocationStep(Location whereIAm, Location whereDidIComeFrom) {
        super(whereIAm.getRow(), whereIAm.getCol());
        this.whereDidIComeFrom = whereDidIComeFrom;
    }

    public Location source() {
        if (!this.isBase()) {
            SourcedLocationStep temp = (SourcedLocationStep) whereDidIComeFrom;
            return temp.source();
        }
        return this;
    }

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

    public boolean isBase() {
        return this.whereDidIComeFrom == null;
    }
}
