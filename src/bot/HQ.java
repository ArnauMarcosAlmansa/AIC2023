package bot;

import aic2023.user.Direction;
import aic2023.user.UnitController;
import aic2023.user.UnitType;

class Entry {
    public double ratio;
    public UnitType type;

    public Entry(double ratio, UnitType type) {
        this.ratio = ratio;
        this.type = type;
    }
}

public class HQ extends Unit {


    private Entry[] explorers = {
            new Entry(0.1, UnitType.CATCHER),
            new Entry(0.45, UnitType.BATTER),
            new Entry(0.45, UnitType.PITCHER)
    };

    private Entry[] attackers = {
            new Entry(0.9, UnitType.BATTER),
            new Entry(0.1, UnitType.PITCHER)
    };

    private Entry[] defenders = {
            new Entry(1, UnitType.BATTER),
    };

    protected HQ(UnitController uc) {
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
        senseEnemyHQ(this.sensedEnemies);
        if (this.sensedEnemies.length > 0) {
            // TODO: dirigir a los que defienden a los enemigos usando el array
            spawnAsManyAsPossible(defenders, 60);
        } else {
            spawnAsManyAsPossible(explorers, 60);
        }
    }

    private void playAttackingPhase() {
        spawnAsManyAsPossible(attackers, 50);
    }

    private void spawnAsManyAsPossible(Entry[] entries, float lowestCost) {
        while (uc.getReputation() >= lowestCost) {
            UnitType type = selectRandomUnit(entries);
            for (Direction dir : randomDirections()) {
                if (uc.canRecruitUnit(type, dir)) {
                    uc.recruitUnit(type, dir);
                }
            }
        }
    }

    UnitType selectRandomUnit(Entry[] entries) {
        double r = Math.random();
        double ratio = 0;
        for (Entry e : entries) {
            ratio += e.ratio;
            if (r <= ratio) {
                uc.println(e.type);
                return e.type;
            }
        }

        return UnitType.BATTER;
    }
}
