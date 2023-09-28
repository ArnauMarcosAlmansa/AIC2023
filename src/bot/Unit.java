package bot;

import aic2023.user.*;

import java.util.Locale;

public abstract class Unit {

    protected UnitController uc;
    protected Comms comms;

    protected int id;

    protected float vision;
    protected float actionRange;
    protected Team opponent;


    protected UnitInfo[] sensedEnemies;
    protected Location[] sensedBases;
    protected Location[] sensedStadiums;
    protected Location[] sensedBalls;
    protected Location[] sensedWater;


    protected Unit(UnitController uc) {
        this.uc = uc;
        this.id = uc.getInfo().getID();
        this.comms = new Comms(uc);
        this.vision = uc.getInfo().getType().getStat(UnitStat.VISION_RANGE);
        this.actionRange = uc.getInfo().getType().getStat(UnitStat.ACTION_RANGE);
        this.opponent = uc.getOpponent();
    }

    protected void senseAround() {
        this.sensedEnemies = this.senseEnemies();
        this.sensedBases = this.senseBases();
        this.sensedStadiums = this.senseStadiums();
        this.sensedBalls = this.senseBalls();
        this.sensedWater = this.senseWater();
    }

    protected int randInt(int min, int max) {
        return (int) (Math.random() * (max - min) + min);
    }

    protected Direction[] randomDirections() {
        Direction[] dirs = {Direction.NORTH, Direction.NORTHEAST, Direction.EAST,
                Direction.SOUTHEAST, Direction.SOUTH, Direction.SOUTHWEST,
                Direction.WEST, Direction.NORTHWEST};

        for (int i = 0; i < dirs.length; i++) {
            int a = (int) (Math.random() * dirs.length);
            int b = (int) (Math.random() * dirs.length);
            Direction tmp = dirs[a];
            dirs[a] = dirs[b];
            dirs[b] = tmp;
        }

        return dirs;
    }

    protected void senseEnemyHQ(UnitInfo[] enemies) {
        for (UnitInfo info : enemies) {
            if (info.getType() == UnitType.HQ) {
                comms.foundEnemyHQ(info.getLocation());
            }
        }
    }

    protected UnitInfo[] senseEnemies() {
        return uc.senseUnits(vision, opponent);
    }

    protected Location[] senseBases() {
        return uc.senseObjects(MapObject.BASE, vision);
    }

    protected Location[] senseStadiums() {
        return uc.senseObjects(MapObject.STADIUM, vision);
    }

    protected Location[] senseBalls() {
        return uc.senseObjects(MapObject.BALL, vision);
    }

    protected Location[] senseWater() {
        return uc.senseObjects(MapObject.WATER, vision);
    }
}