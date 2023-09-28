package bot;

import aic2023.user.Direction;
import aic2023.user.Location;
import aic2023.user.UnitController;

public abstract class MovableUnit extends Unit {

    protected Direction dir;
    protected boolean turnBias;

    protected MovableUnit(UnitController uc) {
        super(uc);
        this.dir = dirFromId(id);
        this.turnBias = turnBiasFromId(id);
    }

    protected Direction dirFromId(int id) {
        int rem = id % 8;
        if (rem == 0) {
            return Direction.NORTH;
        } else if (rem == 1) {
            return Direction.NORTHEAST;
        } else if (rem == 2) {
            return Direction.EAST;
        } else if (rem == 3) {
            return Direction.SOUTHEAST;
        } else if (rem == 4) {
            return Direction.SOUTH;
        } else if (rem == 5) {
            return Direction.SOUTHWEST;
        } else if (rem == 6) {
            return Direction.WEST;
        } else {
            return Direction.NORTHWEST;
        }
    }

    protected boolean moveTowards(Location loc) {
        if (!uc.canMove()) return false;

        Location myLoc = uc.getLocation();
        Direction dir = myLoc.directionTo(loc);

        if (uc.canMove(dir)) {
            uc.move(dir);
            return true;
        }

        return false;
    }

    protected boolean moveInDir()
    {
        if (!uc.canMove()) return false;

        if (Math.random() < 0.05) return false;

        if (uc.canMove(dir)) {
            uc.move(dir);
            return true;
        }

        return false;
    }

    protected boolean changeDirectionRandomly() {
        Direction[] dirs = randomDirections();
        for (Direction dir : dirs) {
            if (dir != this.dir && dir != this.dir.opposite() && this.uc.canMove(dir)) {
                this.dir = dir;
                return true;
            }
        }

        return false;
    }

    public boolean turnBiasFromId(int id) {
        if (id % 2 == 1) {
            return true;
        } else {
            return false;
        }
    }
}
