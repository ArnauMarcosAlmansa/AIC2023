package bot;

import aic2023.engine.Unit;
import aic2023.user.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class UnitPlayer {
    int STATE = 9999;
    int WEST_BORDER = 9996;
    int SOUTH_BORDER = 9994;
    int EAST_BORDER = 9992;
    int NORTH_BORDER = 9990;


    int BASES = 7000;
    int NUM_BASES = 6999;

    boolean isExploring = true;

    Direction favouriteDirection;


    public void run(UnitController uc) {
        /*Insert here the code that should be executed only at the beginning of the unit's lifespan*/
        UnitType type = uc.getType();

        if (type == UnitType.HQ) {
            new HQ(uc).play();
        } else if (type == UnitType.PITCHER) {
            new Pitcher(uc).play();
        } else if (type == UnitType.BATTER) {
            new Batter(uc).play();
        } else if (type == UnitType.CATCHER) {
            new Catcher(uc).play();
        }
    }


    void runHq(UnitController uc) {
        if (state(uc) == 0) {
            if (allBordersFound(uc)) {
                setState(uc, 1);
            }

            runHqExplore(uc);
            return;
        }

        if (uc.getReputation() < 100) return;

        Direction[] directions = randomDirections();
        UnitType[] units = shuffleUnitTypes(new UnitType[]{UnitType.BATTER, UnitType.BATTER, UnitType.BATTER, UnitType.BATTER, UnitType.BATTER, UnitType.BATTER, UnitType.PITCHER});

        for (UnitType type : units) {
            for (Direction dir : directions) {
                if (uc.canRecruitUnit(type, dir)) {
                    uc.recruitUnit(type, dir);
                }
            }
        }
    }

    void runHqExplore(UnitController uc) {
        findTargets(uc);
        if (uc.getReputation() < 40) return;

        Direction[] directions = randomDirections();
        UnitType[] units = shuffleUnitTypes(new UnitType[]{UnitType.BATTER, UnitType.BATTER, UnitType.BATTER, UnitType.BATTER, UnitType.BATTER, UnitType.BATTER, UnitType.PITCHER});

        boolean canSpawn = true;
        while (canSpawn) {
            for (UnitType type : units) {
                for (Direction dir : directions) {
                    if (uc.canRecruitUnit(type, dir)) {
                        uc.recruitUnit(type, dir);
                    } else {
                        canSpawn = false;
                    }
                }
            }
        }
    }

    void runPitcher(UnitController uc) {
        findTargets(uc);
        if (uc.read(STATE) == 0 && isExploring) {
            // estamos explorando
            runExplore(uc);
        }

        float radius = uc.getType().getStat(UnitStat.VISION_RANGE);

        Location[] bases = uc.senseObjects(MapObject.BASE, radius);
        Location[] stadiums = uc.senseObjects(MapObject.STADIUM, radius);
        if (bases.length == 0 && stadiums.length == 0) {
            if (Math.random() < 0.2) {
                moveAtRandom(uc);
            } else {
                moveAtRandom(uc);
//				Location loc = getRandomBase(uc);
//				if (loc == null) {
//					moveAtRandom(uc);
//				} else {
//					Direction dir = uc.getLocation().directionTo(loc);
//					if (uc.canMove(dir)) {
//						uc.move(dir);
//					} else {
//						moveAtRandom(uc);
//					}
//				}
            }
        } else if (bases.length > 0) {
            moveTowards(uc, bases[0]);
        } else {
            moveTowards(uc, stadiums[0]);
        }
    }

    void runBatter(UnitController uc) {
        findTargets(uc);
        if (uc.read(STATE) == 0 && isExploring) {
            // estamos explorando
            runExplore(uc);
            return;
        }

        float radius = uc.getType().getStat(UnitStat.VISION_RANGE);
        Team opponent = uc.getOpponent();
        UnitInfo[] enemies = uc.senseUnits(radius, opponent);

        if (enemies.length == 0) {
            moveAtRandom(uc);
        } else {
            moveTowards(uc, enemies[0].getLocation());
            Direction dir = uc.getLocation().directionTo(enemies[0].getLocation());
            if (uc.canBat(dir, GameConstants.MAX_STRENGTH)) {
                uc.bat(dir, GameConstants.MAX_STRENGTH);
            }
        }
    }

    void runCatcher(UnitController uc) {
        findTargets(uc);
        if (uc.read(STATE) == 0 && isExploring) {
            // estamos explorando
            runExplore(uc);
        } else {
            moveAtRandom(uc);
        }
    }

    void runExplore(UnitController uc) {
        Direction dir = fourDirFromId(uc.getInfo().getID());
        if (uc.canMove(dir)) {
            uc.move(dir);
            Location end = uc.getLocation().add(dir);
            if (uc.isOutOfMap(end)) {
                int border = getBorderFromDir(dir);
                uc.write(border, end.x);
                uc.write(border + 1, end.y);

                isExploring = false;
            }
        } else {
            moveAtRandom(uc);
        }
    }

    int getBorderFromDir(Direction dir) {
        if (dir == Direction.NORTH) {
            return NORTH_BORDER;
        } else if (dir == Direction.EAST) {
            return EAST_BORDER;
        } else if (dir == Direction.SOUTH) {
            return SOUTH_BORDER;
        } else {
            return WEST_BORDER;
        }
    }

    Direction fourDirFromId(int id) {
        int rem = id % 4;
        if (rem == 0) {
            return Direction.NORTH;
        } else if (rem == 1) {
            return Direction.EAST;
        } else if (rem == 2) {
            return Direction.SOUTH;
        } else {
            return Direction.WEST;
        }
    }

    Direction dirFromId(int id) {
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


    int state(UnitController uc) {
        return uc.read(STATE);
    }

    void setState(UnitController uc, int state) {
        uc.write(STATE, state);
    }

    boolean allBordersFound(UnitController uc) {
        return uc.read(NORTH_BORDER) != 0 && uc.read(EAST_BORDER) != 0 && uc.read(SOUTH_BORDER) != 0 && uc.read(WEST_BORDER) != 0;
    }

    void moveTowards(UnitController uc, Location loc) {
        if (!uc.canMove()) return;

        Location myLoc = uc.getLocation();
        Direction dir = myLoc.directionTo(loc);

        if (uc.canMove(dir)) {
            uc.move(dir);
        } else {
            moveAtRandom(uc);
        }
    }

    void moveAtRandom(UnitController uc) {
        if (!uc.canMove()) return;

        Direction[] directions = randomDirections();
        for (Direction dir : directions) {
            if (uc.canMove(dir)) {
                uc.move(dir);
                break;
            }
        }
    }

    Direction[] randomDirections() {
        Direction[] dirs = Direction.values();

        for (int i = 0; i < dirs.length; i++) {
            int a = (int) (Math.random() * dirs.length);
            int b = (int) (Math.random() * dirs.length);
            Direction tmp = dirs[a];
            dirs[a] = dirs[b];
            dirs[b] = tmp;
        }

        return dirs;
    }

    UnitType[] randomUnitTypes() {
        UnitType[] types = UnitType.values();

        shuffleUnitTypes(types);

        return types;
    }

    UnitType[] shuffleUnitTypes(UnitType[] types) {
        for (int i = 0; i < types.length; i++) {
            int a = (int) (Math.random() * types.length);
            int b = (int) (Math.random() * types.length);
            UnitType tmp = types[a];
            types[a] = types[b];
            types[b] = tmp;
        }

        return types;
    }

    void findTargets(UnitController uc) {
        float myVision = uc.getType().getStat(UnitStat.VISION_RANGE);
        Location[] bases = uc.senseObjects(MapObject.BASE, myVision);
        Location[] stadiums = uc.senseObjects(MapObject.STADIUM, myVision);

        for (Location loc : bases) {
            addBase(uc, loc);
        }

        for (Location loc : stadiums) {
            addBase(uc, loc);
        }
    }

    void addBase(UnitController uc, Location loc) {
        int numBases = uc.read(NUM_BASES);
        int currentIndex = BASES + numBases * 2;
        if (currentIndex >= 9000) {
            return;
        }

        uc.write(currentIndex, loc.x);
        uc.write(currentIndex + 1, loc.y);

        numBases++;
        uc.write(NUM_BASES, numBases);
    }

    Location getRandomBase(UnitController uc) {
        int numBases = uc.read(NUM_BASES);
        if (numBases == 0) {
            return null;
        }

        int index = (int) (Math.random() * 2 * numBases);

        int x = uc.read(index);
        int y = uc.read(index + 1);

        return new Location(x, y);
    }


    /**
     * Returns a location with a base or stadium that does nt have a pitcher on top. If there is none
     * inside vision range, it returns null.
     */
    Location getTarget(UnitController uc) {
        float myVision = uc.getType().getStat(UnitStat.VISION_RANGE);
        Location[] bases = uc.senseObjects(MapObject.BASE, myVision);
        Location base = getFirstAvailable(uc, bases);
        if (base != null) return base;

        Location[] stadiums = uc.senseObjects(MapObject.STADIUM, myVision);
        Location stadium = getFirstAvailable(uc, stadiums);
        return stadium;
    }

    /**
     * Returns the first location of the array that does not have one of our pitchers on top.
     * We should also look at the IDs to make sure it is not this unit the one that's standing on top.
     */
    Location getFirstAvailable(UnitController uc, Location[] locs) {
        for (Location loc : locs) {
            UnitInfo unit = uc.senseUnitAtLocation(loc);
            if (unit == null || unit.getTeam() != uc.getTeam() || uc.getType() != UnitType.PITCHER || unit.getID() == uc.getInfo().getID())
                return loc;
        }
        return null;
    }

}
