package smartPlayer;

import battlecode.common.*;
import java.util.Random;

public strictfp class RobotPlayer {

    static int  turnCount = 0;

    static final Random rng = new Random(6147);
    static final Direction[] directions = {
            Direction.NORTH,
            Direction.NORTHEAST,
            Direction.EAST,
            Direction.SOUTHEAST,
            Direction.SOUTH,
            Direction.SOUTHWEST,
            Direction.WEST,
            Direction.NORTHWEST,
    };

    public static void run(RobotController rc) throws GameActionException {
        while (true) {
            turnCount += 1;

            try {

                if (!rc.isSpawned()) {
                    MapLocation[] spawnLocs = rc.getAllySpawnLocations();
                    MapLocation randomLoc = spawnLocs[rng.nextInt(spawnLocs.length)];

                    // Spawn and assign roles based on the current counts
                    if (rc.canSpawn(randomLoc)) {
                        rc.spawn(randomLoc);

                        int randomDuckType = rng.nextInt(3);

                        switch(randomDuckType) {
                            case 0:
                                PathFind.explore(rc);
                            case 1:
                                HealerDuck.run(rc);
                            case 2:
                                BuilderDuck.run(rc);
                        }

                    }

                }

            } catch (GameActionException e) {
                System.out.println("GameActionException");
                e.printStackTrace();

            } catch (Exception e) {
                System.out.println("Exception");
                e.printStackTrace();

            } finally {
                Clock.yield();
            }
        }
    }

}
