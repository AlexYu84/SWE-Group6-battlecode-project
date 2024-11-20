package smartPlayer;

import battlecode.common.*;

import java.awt.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;


public class BuilderDuck {

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

        // The location of the natural barrier to where the robot will move to place the flag
        MapLocation naturalBarrier = null;

        // Robot life loop
        while (true) {

            try {

                if (rc.isSpawned()) {

                    if(rc.getRoundNum() <= GameConstants.SETUP_ROUNDS) {

                        // If robot can pick up flag and is in spawn, pick it up and begin moving
                        if (rc.canPickupFlag(rc.getLocation()) && rcInSpawn(rc)) {
                            rc.pickupFlag(rc.getLocation());

                            Direction nextDir = directions[rng.nextInt(directions.length)];

                            if (rc.canMove(nextDir)) {
                                rc.move(nextDir);
                            }

                        } else if (rc.hasFlag()) {

                            // If the robot has the flag, move to the closest valid wall or water tile
                            if (naturalBarrier == null) {

                                MapInfo[] sensedInfos = rc.senseNearbyMapInfos();

                                for (MapInfo mapInfo : sensedInfos) {
                                    if (mapInfo.isWall() || mapInfo.isWater()) {
                                        naturalBarrier = mapInfo.getMapLocation(); // save water/wall tile
                                        break;
                                    }
                                }

                                // If no barrier is found, keep moving randomly until you find one
                                if (naturalBarrier == null) {
                                    Direction dir = directions[rng.nextInt(directions.length)];
                                    if (rc.canMove(dir)) {
                                        rc.move(dir);
                                    }
                                }


                            } else { // natural barrier has been found

                                // Once a natural barrier is sensed, move towards it and place the flag there
                                Direction nextDir = rc.getLocation().directionTo(naturalBarrier);
                                if (rc.canMove(nextDir)) {
                                    rc.move(nextDir);

                                } else if (rc.getLocation().add(nextDir).equals(naturalBarrier)) {

                                    // Keep moving randomly until a valid flag location is found
                                    while (!rc.senseLegalStartingFlagPlacement(rc.getLocation())) {
                                        Direction randomDir = directions[rng.nextInt(directions.length)];
                                        if (rc.canMove(randomDir)) {
                                            rc.move(randomDir);
                                        }

                                    }

                                    // Once a valid location is found, drop the flag
                                    if (rc.canDropFlag(rc.getLocation())) {
                                        rc.dropFlag(rc.getLocation());

                                        // Start placing bombs around the flag after dropping it
                                        for (Direction dir : directions) {
                                            MapLocation trapLocation = rc.getLocation().add(dir);
                                            if (rc.canBuild(TrapType.EXPLOSIVE, trapLocation)) {
                                                rc.build(TrapType.EXPLOSIVE, trapLocation);
                                            }
                                        }
                                    }

                                }
                            }

                        } else { // SETUP ROUND: builder actions after flags has been set

                            FlagInfo[] flags = rc.senseNearbyFlags(-1);

                            // Randomly select a flag to protect
                            if(flags.length > 0) {
                                FlagInfo flagToProtect = flags[rng.nextInt(flags.length)];

                                if (!flagToProtect.isPickedUp()) {

                                    Direction dir = rc.getLocation().directionTo(flagToProtect.getLocation());
                                    if(!rc.getLocation().add(dir).equals(flagToProtect.getLocation())) {
                                        if (rc.canMove(dir)) {
                                            rc.move(dir);
                                        }
                                    } else {

                                        // this will move rc onto the flag location
                                        if (rc.canMove(dir)) {
                                            rc.move(dir);
                                        }

                                        // surround flag in all directions with explosive trap
                                        for (Direction direction : directions) {
                                            if (rc.canBuild(TrapType.EXPLOSIVE, flagToProtect.getLocation().add(direction)) && !rcInSpawn(rc)) {
                                                rc.build(TrapType.EXPLOSIVE, flagToProtect.getLocation().add(direction));
                                            }
                                        }
                                    }
                                }

                                // move randomly
                                Direction direction = directions[rng.nextInt(directions.length)];
                                if (rc.canMove(direction)) {
                                    rc.move(direction);
                                }
                            }

                        }

                    } else {
                        // outside of setup rounds... do...

                        int action = rng.nextInt(10);

                        if (action <= 5) { // 60% chance to protect flag
                            protectRandomFlag(rc);
                        } else if (action == 6 || action == 7) { // 20% chance to built water trap
                            buildWaterTraps(rc);
                        } else { // 20% chance to just move randomly, save crumbs
                            Direction direction = directions[rng.nextInt(directions.length)];
                            if (rc.canMove(direction)) {
                                rc.move(direction);
                            }
                        }

                    }

                }

            }  catch (Exception e) {
                System.out.println("Exception");
                e.printStackTrace();

            } finally {
                Clock.yield();
                //break;
            }
        }
    }

    // Helper function which will lead the RC to build water traps
    private static void buildWaterTraps(RobotController rc) throws GameActionException {

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

    // Helper function which will lead the RC to randomly protect a flag and build traps around it
    private static void protectRandomFlag(RobotController rc) throws GameActionException {
        // Sense all nearby flags
        FlagInfo[] flags = rc.senseNearbyFlags(-1, rc.getTeam());

        if(flags.length > 0) {

            // Randomly select a flag to protect
            FlagInfo flagToProtect = flags[rng.nextInt(flags.length)];

            // Keep moving towards the flag
            Direction dir = rc.getLocation().directionTo(flagToProtect.getLocation());

            if (!rc.getLocation().add(dir).equals(flagToProtect.getLocation())) {
                if (rc.canMove(dir)) {
                    rc.move(dir);
                }
            } else {

                // Once near the flag, perform one more move to move onto the flag location
                if (rc.canMove(dir)) {
                    rc.move(dir);
                }

                // If the nearby flag is protected, switch to attack mode, otherwise stay in defend mode
                if (!isFlagProtected(rc)) {
                    // defend mode
                    // Surround flag in all directions with explosive trap
                    for (Direction direction : directions) {
                        if (rc.canBuild(TrapType.EXPLOSIVE, flagToProtect.getLocation().add(direction)) && !rcInSpawn(rc)) {
                            rc.build(TrapType.EXPLOSIVE, flagToProtect.getLocation().add(direction));
                        }
                    }
                } else {
                    // attack mode
                    // Sense nearby enemy robots
                    RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(4, rc.getTeam().opponent());

                    // If enemies are found, find the closest enemy and attack it.
                    if (nearbyEnemies.length > 0) {

                        // find the closest enemy to attack
                        RobotInfo toAttack = null;
                        int closestDistance = Integer.MAX_VALUE;
                        for (RobotInfo enemyRobot : nearbyEnemies) {
                            if (rc.getLocation().distanceSquaredTo(enemyRobot.getLocation()) <= closestDistance) {
                                closestDistance = rc.getLocation().distanceSquaredTo(enemyRobot.getLocation());
                                toAttack = enemyRobot;
                            }
                        }

                        // if it can attack without moving, attack it.
                        if (toAttack != null) {
                            if(rc.canAttack(toAttack.getLocation())) {
                                rc.attack(toAttack.getLocation());
                            }
                        }
                    }
                }

            }
        }
    }

    // Helper function to check if the flag near rc is protected by bombs on all sides
    private static boolean isFlagProtected(RobotController rc) throws GameActionException {
        for (Direction dir : directions) {

            try {
                if (rc.senseMapInfo(rc.getLocation().add(dir)).getTrapType() != TrapType.EXPLOSIVE
                        || rc.senseMapInfo(rc.getLocation().add(dir)).getTrapType() != TrapType.WATER
                        || !rc.senseMapInfo(rc.getLocation().add(dir)).isWall()) {
                    return false;
                }
            } catch (Exception e) {
                System.out.println("Tried to move to a location beyond map... moving on...");
            }

        }
        return true;
    }


    // Helper function to check if the RC is in the spawn
    private static boolean rcInSpawn(RobotController rc) {
        MapLocation[] spawnLocations  = rc.getAllySpawnLocations();
        for (MapLocation spawnLocation : spawnLocations) {
            if (rc.getLocation().equals(spawnLocation)) {
                return true;
            }
        }
        return false;
    }
}
