package smartPlayer;

import battlecode.common.*;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class AttackDuck {
    static final Random rng = new Random(6147);
    static final Direction[] directions = Direction.values();
    static boolean hasFlag = false;
    static boolean isStuck = false;
    static MapLocation startingLocation;
    static Set<MapLocation> recentLocations = new HashSet<>();
    static final int MAX_RECENT_LOCATIONS = 5;

    public static void run(RobotController rc) throws GameActionException {
        startingLocation = rc.getLocation();
        while (true) {
            try {
                if (rc.isSpawned()) {
                    System.out.println("Attacker Running...");

                    int round = rc.getRoundNum();
                    MapLocation[] crumbLocations = rc.senseNearbyCrumbs(-1);
                    MapLocation[] flags = rc.senseBroadcastFlagLocations();
                    MapLocation closestSpawn = null;
                    Direction returnDirection = rc.getLocation().directionTo(startingLocation);

                    if (hasFlag && rc.getLocation().equals(startingLocation)) {
                        hasFlag = false;
                    }

                    if (hasFlag) {
                        moveToStartingLocation(rc, returnDirection);
                    } else if (isStuck) {
                        fillWater(rc);
                        isStuck = false;
                    } else if (round < 150) {
                        handleCrumbs(rc, crumbLocations);
                    } else if (round > 150 && round < 200) {
                        handleDamWalls(rc);
                    } else {
                        handleEnemiesAndFlags(rc, flags, returnDirection);
                    }
                } else {
                    System.err.println("Attacker not spawned properly.");
                    return;
                }
            } catch (Exception e) {
                System.err.println("Exception: " + e.getMessage());
                e.printStackTrace();
            } finally {
                Clock.yield();
            }
        }
    }

    // Helper method to handle movement when returning to the starting location with the flag
    private static void moveToStartingLocation(RobotController rc, Direction returnDirection) throws GameActionException {
        if (rc.canMove(returnDirection)) {
            if (rc.isActionReady()) {
                fillWater(rc);
            }
            if (rc.isMovementReady()) {
                rc.move(returnDirection);
                addRecentLocation(rc.getLocation());
            }
        } else {
            moveToNewLocation(rc);
        }

        if (rc.getLocation().equals(startingLocation) && rc.canDropFlag(rc.getLocation())) {
            rc.dropFlag(rc.getLocation());
            hasFlag = false;
            System.out.println("Flag secured!");
            awardCrumbs(rc, 50);
        }
    }

    // Helper method to move to a new location using random directions
    private static void moveToNewLocation(RobotController rc) throws GameActionException {
        for (Direction direction : directions) {
            MapLocation potentialLocation = rc.getLocation().add(direction);
            if (rc.canMove(direction) && !recentLocations.contains(potentialLocation)) {
                if (rc.isMovementReady()) {
                    rc.move(direction);
                    addRecentLocation(potentialLocation);
                    break;
                }
            }
        }
    }

    // Helper method to handle crumb collection
    private static void handleCrumbs(RobotController rc, MapLocation[] crumbLocations) throws GameActionException {
        if (crumbLocations.length > 0) {
            MapLocation nearestCrumb = crumbLocations[0];
            if (rc.canMove(rc.getLocation().directionTo(nearestCrumb))) {
                moveAndFillWater(rc, rc.getLocation().directionTo(nearestCrumb));
            } else {
                isStuck = true;
            }
        } else {
            moveRandomly(rc);
        }
    }

    // Helper method to handle movement when there are no crumbs
    private static void moveRandomly(RobotController rc) throws GameActionException {
        Direction dir = directions[rng.nextInt(directions.length)];
        moveAndFillWater(rc, dir);
    }

    // Helper method to move and fill water
    private static void moveAndFillWater(RobotController rc, Direction direction) throws GameActionException {
        if (rc.isActionReady()) {
            fillWater(rc);
        }
        if (rc.canMove(direction) && rc.isMovementReady()) {
            rc.move(direction);
        }
    }

    // Helper method to handle moving toward dam walls
    private static void handleDamWalls(RobotController rc) throws GameActionException {
        MapInfo[] damn = rc.senseNearbyMapInfos(-1);
        if (damn.length > 0) {
            MapLocation nearestDamWall = findNearestDamWall(rc, damn);
            Direction directionToDam = rc.getLocation().directionTo(nearestDamWall);
            if (rc.canMove(directionToDam) && rc.isMovementReady()) {
                rc.move(directionToDam);
            }
        }
    }

    // Helper method to find the nearest dam wall
    private static MapLocation findNearestDamWall(RobotController rc, MapInfo[] damn) {
        MapLocation nearestDamWall = damn[0].getMapLocation();
        double minDistance = rc.getLocation().distanceSquaredTo(nearestDamWall);

        for (MapInfo d : damn) {
            MapLocation damWallLocation = d.getMapLocation();
            double distance = rc.getLocation().distanceSquaredTo(damWallLocation);
            if (distance < minDistance) {
                nearestDamWall = damWallLocation;
                minDistance = distance;
            }
        }
        return nearestDamWall;
    }

    // Helper method to handle enemy robots and flags
    private static void handleEnemiesAndFlags(RobotController rc, MapLocation[] flags, Direction returnDirection) throws GameActionException {
        RobotInfo[] enemies = rc.senseNearbyRobots(4, rc.getTeam().opponent());
        if (enemies.length > 0) {
            attackOrMoveTowardEnemy(rc, enemies[0].location);
        } else if (rc.isActionReady() && rng.nextInt(2) == 1) {
            buildTrap(rc, returnDirection);
        } else {
            handleFlagCollection(rc, flags);
        }
    }

    // Helper method to attack or move toward an enemy robot
    private static void attackOrMoveTowardEnemy(RobotController rc, MapLocation enemyLocation) throws GameActionException {
        if (rc.canMove(rc.getLocation().directionTo(enemyLocation))) {
            moveAndFillWater(rc, rc.getLocation().directionTo(enemyLocation));
        }
        if (rc.canAttack(enemyLocation)) {
            rc.attack(enemyLocation);
            System.out.println("Just swung at an enemy.");

            if (rc.senseRobotAtLocation(enemyLocation) == null) {
                awardCrumbs(rc, 30);
                System.out.println("Awarded 30 crumbs for taking down enemy!");
            }
        }
    }

    // Helper method to handle flag collection
    private static void handleFlagCollection(RobotController rc, MapLocation[] flags) throws GameActionException {
        if (rc.canPickupFlag(rc.getLocation())) {
            rc.pickupFlag(rc.getLocation());
            hasFlag = true;
            System.out.println("Picked up a flag!");
        }

        if (flags.length > 0) {
            MapLocation nearestFlag = flags[0];
            if (rc.canMove(rc.getLocation().directionTo(nearestFlag))) {
                moveAndFillWater(rc, rc.getLocation().directionTo(nearestFlag));
                System.out.println("Moving to nearest flag: " + nearestFlag);
            } else {
                moveRandomly(rc);
            }
        }
    }

    // Helper method to build a trap
    private static void buildTrap(RobotController rc, Direction returnDirection) throws GameActionException {
        MapLocation trapLocation = rc.getLocation().add(returnDirection.opposite());
        if (rc.canBuild(TrapType.EXPLOSIVE, trapLocation)) {
            rc.build(TrapType.EXPLOSIVE, trapLocation);
            System.out.println("Dropping something at: " + trapLocation);
        } else {
            System.out.println("Cannot build trap at: " + trapLocation);
        }
    }

    // Fill water in the nearby area if possible
    private static void fillWater(RobotController rc) throws GameActionException {
        int roundNumber = rc.getRoundNum();
        if (roundNumber > 0) {
            MapInfo[] water = rc.senseNearbyMapInfos(2);
            for (MapInfo w : water) {
                if (w.isWater() && rc.canFill(w.getMapLocation())) {
                    if ((w.getMapLocation().x + w.getMapLocation().y) % 2 == 1) {
                        if (rc.isActionReady()) {
                            rc.fill(w.getMapLocation());
                        }
                    } else {
                        wander(rc);
                    }
                }
            }
        }
    }

    private static void addRecentLocation(MapLocation location) {
        recentLocations.add(location);
        if (recentLocations.size() > MAX_RECENT_LOCATIONS) {
            recentLocations.iterator().next();
        }
    }

    // Wander in a random direction
    private static void wander(RobotController rc) throws GameActionException {
        Direction randomDir = directions[rng.nextInt(directions.length)];
        if (rc.canMove(randomDir)) {
            if (rc.isMovementReady()) {
                rc.move(randomDir);
            }
        }
    }

    // Award crumbs to the team
    private static void awardCrumbs(RobotController rc, int crumbs) throws GameActionException {
        int currentCrumbs = rc.readSharedArray(0);  // Read crumbs from index 0 of the shared array
        rc.writeSharedArray(0, currentCrumbs + crumbs);  // Update the shared array with the awarded crumbs
    }
}
