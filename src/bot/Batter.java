package bot;

import aic2023.user.*;

public class Batter extends MovableUnit {
    protected Batter(UnitController uc) {
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
        boolean couldAttack = attackIfPossible();
        boolean couldMove = moveInDir();
        if (!couldMove) {
            if (changeDirectionRandomly())
                moveInDir();
        }
        senseEnemyHQ(this.sensedEnemies);
    }

    private void playAttackingPhase() {

        boolean couldAttack = attackIfPossible();
        boolean couldMove = moveInDir();
        if (!couldMove) {
            if (changeDirectionRandomly())
                moveInDir();
        }
    }

    private boolean attackIfPossible() {
        UnitInfo[] enemies = uc.senseUnits(vision, opponent);

        if (enemies.length == 0) {
            return false;
        }

        int[] closest = closestEnemy(uc.getLocation(), enemies);
        UnitInfo closestEnemy = enemies[closest[0]];
        Direction dir = uc.getLocation().directionTo(closestEnemy.getLocation());
        if (closest[1] <= actionRange) {
            if (uc.canBat(dir, GameConstants.MAX_STRENGTH)) {
                uc.bat(dir, GameConstants.MAX_STRENGTH);
                return true;
            }
        } else {
            moveTowards(closestEnemy.getLocation());
            if (closest[1] <= 2) {
                if (uc.canBat(dir, GameConstants.MAX_STRENGTH)) {
                    uc.bat(dir, GameConstants.MAX_STRENGTH);
                    return true;
                }
            }
            // TODO: run away if we cannot bat ?
        }

        return false;
    }

    private int[] closestEnemy(Location here, UnitInfo[] enemies) {
        int index = 0;
        // FIXME: squared ?
        int minDistance = enemies[index].getLocation().distanceSquared(here);

        for (int i = 1; i < enemies.length; i++) {
            int distance = enemies[i].getLocation().distanceSquared(here);
            if (distance <= minDistance) {
                minDistance = distance;
                index = i;
            }
        }

        return new int[] {index, minDistance};
    }
}
