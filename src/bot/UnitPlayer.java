package bot;

import aic2023.engine.Unit;
import aic2023.user.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class UnitPlayer {

	public void run(UnitController uc) {
		/*Insert here the code that should be executed only at the beginning of the unit's lifespan*/
		while (true) {
			/*Insert here the code that should be executed every round*/
			UnitType type = uc.getType();
			/*Get random direction*/
			if (type == UnitType.HQ) {
				runHq(uc);
			} else if (type == UnitType.PITCHER) {
				runPitcher(uc);
			} else if (type == UnitType.BATTER) {
				runBatter(uc);
			} else if (type == UnitType.CATCHER) {
				runCatcher(uc);
			}

			uc.yield(); //End of turn
		}
	}


	void runHq(UnitController uc)
	{
		if (uc.getReputation() < 100) return;

		Direction[] directions = randomDirections();
		UnitType[] units = randomUnitTypes();

		for (UnitType type : units) {
			for (Direction dir : directions) {
				if (uc.canRecruitUnit(type, dir)) {
					uc.recruitUnit(type, dir);
				}
			}
		}
	}

	void runPitcher(UnitController uc)
	{
		float radius = uc.getType().getStat(UnitStat.VISION_RANGE);

		Location[] bases = uc.senseObjects(MapObject.BASE, radius);
		Location[] stadiums = uc.senseObjects(MapObject.STADIUM, radius);
		if (bases.length == 0 && stadiums.length == 0) {
			moveAtRandom(uc);
		} else if (bases.length > 0) {
			moveTowards(uc, bases[0]);
		} else {
			moveTowards(uc, stadiums[0]);
		}
	}

	void runBatter(UnitController uc)
	{
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

	void runCatcher(UnitController uc)
	{
		moveAtRandom(uc);
	}


	void moveTowards(UnitController uc, Location loc)
	{
		if (!uc.canMove()) return;

		Location myLoc = uc.getLocation();
		Direction dir = myLoc.directionTo(loc);

		if (uc.canMove(dir)) {
			uc.move(dir);
		} else {
			moveAtRandom(uc);
		}
	}

	void moveAtRandom(UnitController uc)
	{
		if (!uc.canMove()) return;

		Direction[] directions = randomDirections();
		for (Direction dir : directions) {
			if (uc.canMove(dir)) {
				uc.move(dir);
				break;
			}
		}
	}

	Direction[] randomDirections()
	{
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

	UnitType[] randomUnitTypes()
	{
		UnitType[] types = UnitType.values();

		for (int i = 0; i < types.length; i++) {
			int a = (int) (Math.random() * types.length);
			int b = (int) (Math.random() * types.length);
			UnitType tmp = types[a];
			types[a] = types[b];
			types[b] = tmp;
		}

		return types;
	}


	/**
	 * Returns a location with a base or stadium that does nt have a pitcher on top. If there is none
	 * inside vision range, it returns null.
	 */
	Location getTarget(UnitController uc){
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
	Location getFirstAvailable(UnitController uc, Location[] locs){
		for (Location loc : locs){
			UnitInfo unit = uc.senseUnitAtLocation(loc);
			if (unit == null || unit.getTeam() != uc.getTeam() || uc.getType() != UnitType.PITCHER || unit.getID() == uc.getInfo().getID()) return loc;
		}
		return null;
	}

}
