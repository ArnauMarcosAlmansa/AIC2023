package bot;

import aic2023.user.Location;
import aic2023.user.UnitController;

public class Comms {
    private UnitController uc;

    int STATE = 9999;
    int EXPLORING = 0;
    int ATTACKING = 1;


    int ENEMY_HQ = 9000;

    int BASES = 1;

    int NUM_BASES = 0;

    int MAX_BASES = 100;

    public Comms(UnitController uc) {
        this.uc = uc;
    }

    public boolean weAreExploring()
    {
        int state = uc.read(STATE);
        return state == EXPLORING;
    }

    public boolean weAreAttacking()
    {
        int state = uc.read(STATE);
        return state == ATTACKING;
    }

    public void foundEnemyHQ(Location where) {
        uc.write(ENEMY_HQ, where.x);
        uc.write(ENEMY_HQ + 1, where.y);
    }

    public Location getEnemyHQ() {
        int x = uc.read(ENEMY_HQ);
        int y = uc.read(ENEMY_HQ + 1);

        return new Location(x, y);
    }

    public void findBase(Location loc) {
        int numBases = uc.read(NUM_BASES);
        boolean exists = false;
        for (int i = 0; !exists && i < numBases; i++) {
            int x = uc.read(BASES + i * 2);
            int y = uc.read(BASES + i * 2 + 1);
            if (x == loc.x && y == loc.y) exists = true;
        }

        if (!exists) {
            uc.write(BASES + numBases * 2, loc.x);
            uc.write(BASES + numBases * 2 + 1, loc.y);
        }
    }

    public void getBases(Location loc) {
        int numBases = uc.read(NUM_BASES);
        boolean exists = false;
        for (int i = 0; !exists && i < numBases; i++) {
            int x = uc.read(BASES + i * 2);
            int y = uc.read(BASES + i * 2 + 1);
            if (x == loc.x && y == loc.y) exists = true;
        }

        if (!exists) {
            uc.write(BASES + numBases * 2, loc.x);
            uc.write(BASES + numBases * 2 + 1, loc.y);
            uc.write(NUM_BASES,  numBases + 1);
        }
    }
}
