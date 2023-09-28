package bot;

import aic2023.user.Location;
import aic2023.user.MapObject;
import aic2023.user.UnitController;
import aic2023.user.UnitStat;

public class Pitcher extends MovableUnit {
    protected Pitcher(UnitController uc) {
        super(uc);
    }

    public void play() {
        while (true) {
            senseAround();
            if (comms.weAreExploring()) {
                playExploringPhase();
            } else {
                playAttackingPhase();
            }
            uc.yield();
        }
    }

    private void playExploringPhase() {
        if (sensedBases.length > 0) {
            moveTowards(sensedBases[0]);
        } else if (sensedStadiums.length > 0) {
            moveTowards(sensedStadiums[0]);
        } else if (!moveInDir()) {
            if (changeDirectionRandomly()) moveInDir();
        }
        senseEnemyHQ(this.sensedEnemies);
    }

    private void playAttackingPhase() {
        if (sensedBases.length > 0) {
            moveTowards(sensedBases[0]);
        } else if (sensedStadiums.length > 0) {
            moveTowards(sensedStadiums[0]);
        } else if (!moveInDir()) {
            if (changeDirectionRandomly()) moveInDir();
        }
    }

    void runPitcher(UnitController uc) {
        float radius = uc.getType().getStat(UnitStat.VISION_RANGE);

        Location[] bases = sensedBases;
        Location[] stadiums = sensedStadiums;
        if (bases.length == 0 && stadiums.length == 0) {
            if (Math.random() < 0.2) {
                if (!moveInDir()) {
                    if (changeDirectionRandomly()) moveInDir();
                }
            } else {
                if (!moveInDir()) {
                    if (changeDirectionRandomly()) moveInDir();
                }
//				Location loc = ggetRandomBaseetRandomBase(uc);
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
            moveTowards(bases[0]);
        } else {
            moveTowards(stadiums[0]);
        }
    }
}
