package smartPlayer;

import battlecode.common.*;

import java.util.Random;

public strictfp class RobotPlayer {

    static int turnCount = 0;

    static Random rng = new Random(6147);

    public static void run(RobotController rc) throws GameActionException {
        while (true) {
            turnCount += 1;

            try {
                if (!rc.isSpawned()) {
                    runWhenNotSpawned(rc);
                }

            } catch (GameActionException e) {
                System.out.println("GameActionException");
                e.printStackTrace();

            } catch (Exception e) {
                System.out.println("Exception");
                e.printStackTrace();

            } finally {
                // break;
                Clock.yield();
            }
        }
    }

    public static void runWhenNotSpawned(RobotController rc) throws GameActionException {
        MapLocation[] spawnLocs = rc.getAllySpawnLocations();
        MapLocation randomLoc = spawnLocs[rng.nextInt(spawnLocs.length)];

        // Spawn and assign roles based on the current counts
        if (rc.canSpawn(randomLoc)) {
            rc.spawn(randomLoc);
            runTypeDuck(rc);
        }
    }

    public static void runTypeDuck(RobotController rc) throws GameActionException {
        int randomDuckType = rng.nextInt(10);

        if (randomDuckType <= 4) {
            // 50% chance for attack duck
            AttackDuck.run(rc);
        } else if (randomDuckType == 5 || randomDuckType == 6) {
            // 20% chance for a healer duck
            HealerDuck.run(rc);
        } else {
            // 30% chance for builder duck
            BuilderDuck.run(rc);
        }
    }

}
