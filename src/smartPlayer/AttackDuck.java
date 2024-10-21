package smartPlayer;

import battlecode.common.*;
import java.util.Random;

public class AttackDuck {
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

    static MapLocation startingLocation;

    public static void run(RobotController rc) throws GameActionException {
        while (true) {
            try {
                if (rc.isSpawned()) {
                    System.out.println("Attacker Running...");

                    int round = rc.getRoundNum();
                    MapLocation[] crumbLocations = rc.senseNearbyCrumbs(-1);
                    MapLocation[] flags = rc.senseBroadcastFlagLocations();
                    boolean hasFlag = false;

                    if (round < 150 && !hasFlag) {
                        if (crumbLocations.length > 0) {
                            MapLocation nearestCrumb = crumbLocations[0];
                            if (rc.canMove(rc.getLocation().directionTo(nearestCrumb))) {
                                rc.move(rc.getLocation().directionTo(nearestCrumb));
                            }
                        }else{
                            Direction dir = directions[rng.nextInt(directions.length)];
                            if (rc.canMove(dir)) {
                                rc.move(dir);
                            }
                        }
                    } else {
                        // Sense nearby enemy robots within √4 (i.e., 2 tiles)
                        RobotInfo[] enemies = rc.senseNearbyRobots(4, rc.getTeam().opponent());

                        if (hasFlag){
                            //head back to starting location
                            Direction returnDirection = rc.getLocation().directionTo(startingLocation);
                            if(rc.canMove(returnDirection)){
                                rc.move(returnDirection);
                            }
                            //drop flag when at starting location
                            if(rc.getLocation().equals(startingLocation)){
                                hasFlag = false;
                                System.out.println("Flag secured!");
                                //reward 50 crumbs for retrieving flag
                                awardCrumbs(rc, 50);
                            }
                        }

                        if (enemies.length > 0) {
                            MapLocation enemyLocation = enemies[0].location;
                            if (rc.canMove(rc.getLocation().directionTo(enemyLocation))) {
                                rc.move(rc.getLocation().directionTo(enemyLocation));
                            }
                            if (rc.canAttack(enemyLocation)) {
                                rc.attack(enemyLocation);  // Attack method handles damage internally
                                System.out.println("Just swung at an enemy.");

                                // Check if the enemy robot is destroyed and if the robot is in enemy territory
                                if (rc.senseRobotAtLocation(enemyLocation) == null) {
                                    awardCrumbs(rc, 30);  // Award crumbs to the team
                                    System.out.println("Awarded 30 crumbs for taking down enemy!");
                                }
                            }
                        } else {
                            if (crumbLocations.length > 0) {
                                MapLocation nearestCrumb = crumbLocations[0];
                                if (rc.canMove(rc.getLocation().directionTo(nearestCrumb))) {
                                    rc.move(rc.getLocation().directionTo(nearestCrumb));
                                }
                            } else if(flags.length > 0 && !hasFlag) {
                                MapLocation nearestFlag = flags[0];
                                if (rc.canMove(rc.getLocation().directionTo(nearestFlag))) {
                                    rc.move(rc.getLocation().directionTo(nearestFlag));
                                }else{
                                    Direction randomDir;
                                    for (int i = 0; i < directions.length; i++){
                                        randomDir = directions[rng.nextInt(directions.length)];
                                        if (rc.canMove(randomDir)) {
                                            rc.move(randomDir);
                                            break;
                                        }
                                    }
                                }
                                if (rc.getLocation().equals(nearestFlag)) {
                                    hasFlag = true;
                                    System.out.println("Picked up a flag!");
                                }
                            } else{
                                Direction dir = directions[rng.nextInt(directions.length)];
                                if (rc.canMove(dir)) {
                                    rc.move(dir);
                                }
                            }
                        }
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

    private static void awardCrumbs(RobotController rc, int crumbs) throws GameActionException {
        // Example of using a shared array to track crumbs
        int currentCrumbs = rc.readSharedArray(0);  // Read crumbs from index 0 of the shared array
        rc.writeSharedArray(0, currentCrumbs + crumbs);  // Update the shared array with the new crumbs total
    }
}
