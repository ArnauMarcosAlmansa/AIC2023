package bot;

import aic2023.user.UnitController;

public class Catcher extends MovableUnit {

    protected Catcher(UnitController uc) {
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
        boolean couldMove = moveInDir();
        if (!couldMove) {
            if (changeDirectionRandomly())
                moveInDir();
        }
        senseEnemyHQ(this.sensedEnemies);
    }

    private void playAttackingPhase() {
        boolean couldMove = moveInDir();
        if (!couldMove) {
            if (changeDirectionRandomly())
                moveInDir();
        }
    }
}
