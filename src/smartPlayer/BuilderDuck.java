package smartPlayer;

import battlecode.common.*;
import java.util.Random;

public class BuilderDuck {

    static final Random rng = new Random(6147);
    static MapLocation naturalBarrier = null;

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

            try {

                if (rc.isSpawned()) {

                    if (rc.getRoundNum() <= GameConstants.SETUP_ROUNDS) {
                        handleSetupRound(rc, naturalBarrier);
                    } else {
                        handleNonSetupRound(rc);
                    }

                }

            } catch (Exception e) {
                System.out.println("Exception");
                e.printStackTrace();

            } finally {
                Clock.yield();
                // break;
            }
        }
    }

    static void handleSetupRound(RobotController rc, MapLocation naturalBarrier) throws GameActionException {
        if (rc.canPickupFlag(rc.getLocation()) && rcInSpawn(rc)) {
            pickupAndMove(rc);
        } else if (rc.hasFlag()) {
            if (naturalBarrier == null) {
                findBarrierAndMove(rc);
            } else {
                moveToBarrierAndPlaceFlag(rc, naturalBarrier);
            }
        } else {
            defendFlags(rc);
        }
    }

    static void pickupAndMove(RobotController rc) throws GameActionException {
        rc.pickupFlag(rc.getLocation());
        Direction nextDir = directions[rng.nextInt(directions.length)];
        if (rc.canMove(nextDir)) {
            rc.move(nextDir);
        }
    }

    static void findBarrierAndMove(RobotController rc) throws GameActionException {
        MapInfo[] sensedInfos = rc.senseNearbyMapInfos();
        for (MapInfo mapInfo : sensedInfos) {
            if (mapInfo.isWall() || mapInfo.isWater()) {
                naturalBarrier = mapInfo.getMapLocation();
                break;
            }
        }

        if (naturalBarrier == null) {
            Direction dir = directions[rng.nextInt(directions.length)];
            if (rc.canMove(dir)) {
                rc.move(dir);
            }
        }
    }

    private static void moveToBarrierAndPlaceFlag(RobotController rc, MapLocation naturalBarrier) throws GameActionException {
        Direction nextDir = rc.getLocation().directionTo(naturalBarrier);
        if (rc.canMove(nextDir)) {
            rc.move(nextDir);
        } else if (rc.getLocation().add(nextDir).equals(naturalBarrier)) {
            placeFlagAndBuildTraps(rc);
        }
    }

    private static void placeFlagAndBuildTraps(RobotController rc) throws GameActionException {
        while (!rc.senseLegalStartingFlagPlacement(rc.getLocation())) {
            Direction randomDir = directions[rng.nextInt(directions.length)];
            if (rc.canMove(randomDir)) {
                rc.move(randomDir);
            }
        }

        if (rc.canDropFlag(rc.getLocation())) {
            rc.dropFlag(rc.getLocation());
            buildExplosiveTraps(rc);
        }
    }

    static void buildExplosiveTraps(RobotController rc) throws GameActionException {
        for (Direction dir : directions) {
            MapLocation trapLocation = rc.getLocation().add(dir);
            if (rc.canBuild(TrapType.EXPLOSIVE, trapLocation)) {
                rc.build(TrapType.EXPLOSIVE, trapLocation);
            }
        }
    }

    static void defendFlags(RobotController rc) throws GameActionException {
        FlagInfo[] flags = rc.senseNearbyFlags(-1);

        if (flags.length > 0) {
            FlagInfo flagToProtect = flags[rng.nextInt(flags.length)];

            if (!flagToProtect.isPickedUp()) {
                moveToFlagAndProtect(rc, flagToProtect);
            }

            // move randomly
            Direction direction = directions[rng.nextInt(directions.length)];
            if (rc.canMove(direction)) {
                rc.move(direction);
            }
        }
    }

    static void moveToFlagAndProtect(RobotController rc, FlagInfo flagToProtect) throws GameActionException {
        Direction dir = rc.getLocation().directionTo(flagToProtect.getLocation());
        if (!rc.getLocation().add(dir).equals(flagToProtect.getLocation())) {
            if (rc.canMove(dir)) {
                rc.move(dir);
            }
        } else {
            if (rc.canMove(dir)) {
                rc.move(dir);
            }

            for (Direction direction : directions) {
                if (rc.canBuild(TrapType.EXPLOSIVE, flagToProtect.getLocation().add(direction)) && !rcInSpawn(rc)) {
                    rc.build(TrapType.EXPLOSIVE, flagToProtect.getLocation().add(direction));
                }
            }
        }
    }

    static void handleNonSetupRound(RobotController rc) throws GameActionException {
        int action = rng.nextInt(10);

        if (action <= 5) { // 60% chance to protect flag
            protectRandomFlag(rc);
        } else if (action == 6 || action == 7) { // 20% chance to build water trap
            buildWaterTraps(rc);
        } else { // 20% chance to move randomly, save crumbs
            Direction direction = directions[rng.nextInt(directions.length)];
            if (rc.canMove(direction)) {
                rc.move(direction);
            }
        }
    }


    // Helper function which will lead the RC to build water traps
    public static void buildWaterTraps(RobotController rc) throws GameActionException {

        boolean hasTrap = false;

        for (Direction dir : directions) {

            try {
                MapLocation mapLocation = rc.getLocation().add(dir);
                if (rc.senseMapInfo(mapLocation).getTrapType() != TrapType.NONE) {
                    hasTrap = true;
                }
            } catch (Exception e) {
                System.out.println("Tried to move to a location beyond map... moving on...");
            }

        }

        if (!hasTrap) {
            if (rc.canBuild(TrapType.WATER, rc.getLocation()) && !rcInSpawn(rc)) {
                rc.build(TrapType.WATER, rc.getLocation());
            }
        }

    }

    public static void protectRandomFlag(RobotController rc) throws GameActionException {
        // Sense all nearby flags
        FlagInfo[] flags = rc.senseNearbyFlags(-1, rc.getTeam());

        if (flags.length > 0) {
            // Randomly select a flag to protect
            FlagInfo flagToProtect = selectRandomFlag(flags);

            // Move towards the selected flag
            moveTowardsFlag(rc, flagToProtect);

            // If near the flag, build traps or attack based on the flag's protection status
            if (rc.getLocation().equals(flagToProtect.getLocation())) {
                if (!isFlagProtected(rc)) {
                    buildExplosiveTraps(rc, flagToProtect);
                } else {
                    attackEnemiesIfAny(rc);
                }
            }
        }
    }

    public static FlagInfo selectRandomFlag(FlagInfo[] flags) {
        return flags[rng.nextInt(flags.length)];
    }

    public static void moveTowardsFlag(RobotController rc, FlagInfo flagToProtect) throws GameActionException {
        Direction dir = rc.getLocation().directionTo(flagToProtect.getLocation());

        if (!rc.getLocation().add(dir).equals(flagToProtect.getLocation()) && rc.canMove(dir)) {
            rc.move(dir);
        } else if (rc.canMove(dir)) {
            rc.move(dir);
        }
    }

    public static void buildExplosiveTraps(RobotController rc, FlagInfo flagToProtect) throws GameActionException {
        for (Direction direction : directions) {
            if (rc.canBuild(TrapType.EXPLOSIVE, flagToProtect.getLocation().add(direction)) && !rcInSpawn(rc)) {
                rc.build(TrapType.EXPLOSIVE, flagToProtect.getLocation().add(direction));
            }
        }
    }

    public static void attackEnemiesIfAny(RobotController rc) throws GameActionException {
        RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(4, rc.getTeam().opponent());

        if (nearbyEnemies.length > 0) {
            RobotInfo toAttack = findClosestEnemy(rc, nearbyEnemies);

            if (toAttack != null && rc.canAttack(toAttack.getLocation())) {
                rc.attack(toAttack.getLocation());
            }
        }
    }

    public static RobotInfo findClosestEnemy(RobotController rc, RobotInfo[] nearbyEnemies) {
        RobotInfo closestEnemy = null;
        int closestDistance = Integer.MAX_VALUE;

        for (RobotInfo enemyRobot : nearbyEnemies) {
            int distance = rc.getLocation().distanceSquaredTo(enemyRobot.getLocation());
            if (distance < closestDistance) {
                closestDistance = distance;
                closestEnemy = enemyRobot;
            }
        }

        return closestEnemy;
    }


    // Helper function to check if the flag near rc is protected by bombs on all sides
    public static boolean isFlagProtected(RobotController rc) throws GameActionException {
        for (Direction dir : directions) {

            try {
                if (rc.senseMapInfo(rc.getLocation().add(dir)).getTrapType() == TrapType.NONE &&
                        !rc.senseMapInfo(rc.getLocation().add(dir)).isWall()) {
                    return false;
                }
            } catch (Exception e) {
                System.out.println("Tried to move to a location beyond map... moving on...");
            }

        }
        return true;
    }


    // Helper function to check if the RC is in the spawn
    public static boolean rcInSpawn(RobotController rc) {
        MapLocation[] spawnLocations  = rc.getAllySpawnLocations();
        MapLocation rcLocation = rc.getLocation();
        for (MapLocation spawnLocation : spawnLocations) {
            if (rcLocation.equals(spawnLocation)) {
                return true;
            }
        }
        return false;
    }
}
