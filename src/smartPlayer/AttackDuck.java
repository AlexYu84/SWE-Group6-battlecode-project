package smartPlayer;

import battlecode.common.*;

import javax.xml.stream.Location;
import java.awt.*;
import java.util.Locale;
import java.util.Random;
import java.util.HashSet;
import java.util.Set;

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
                    MapLocation closestSpawn, myLoc = null;
                    int randomVar = rng.nextInt(10) + 1;
                    Direction returnDirection = rc.getLocation().directionTo(startingLocation);

                    if(hasFlag && rc.getLocation().equals(startingLocation)){
                        hasFlag = false;
                    }

                    if (hasFlag){
                        //head back to starting location
                        if(rc.canMove(returnDirection)){
//                            System.out.println("canMove returnDirection is " + returnDirection);
                            if(rc.isActionReady()) {
                                fillWater(rc);
                            }
                            if(rc.isMovementReady()) {
                                rc.move(returnDirection);
                                addRecentLocation(rc.getLocation());

                            }
                        }else{
                            // A* implementation!
                            Direction[] allDirections = Direction.values();
                            for (Direction direction : allDirections) {
                                MapLocation potentialLocation = rc.getLocation().add(direction);
                                if(rc.canMove(direction) && !recentLocations.contains(potentialLocation)){
                                    if(rc.isMovementReady()) {
                                        rc.move(direction);
                                        addRecentLocation(potentialLocation);
                                        break;
                                    }
                                }
                            };
                        }
                        //drop flag when at starting location
                        if(rc.getLocation() == startingLocation && rc.canDropFlag(rc.getLocation())){
                            rc.dropFlag(rc.getLocation());
                            hasFlag = false;
                            System.out.println("Flag secured!");
                            //reward 50 crumbs for retrieving flag
                            awardCrumbs(rc, 50);
                        }
                    }

                    if (isStuck){
                        fillWater(rc);
                        isStuck = false;
                    } else if (round < 150) {

                        if (crumbLocations.length > 0) {
                            MapLocation nearestCrumb = crumbLocations[0];
                            if (rc.canMove(rc.getLocation().directionTo(nearestCrumb))) {
                                if(rc.isActionReady()) {
                                    fillWater(rc);
                                }
                                if(rc.isMovementReady()) {
                                    rc.move(rc.getLocation().directionTo(nearestCrumb));
                                }
                            } else{
                                isStuck = true;
                            }
                        }else {
                            Direction dir = directions[rng.nextInt(directions.length)];
                            if (rc.canMove(dir)) {
                                if(rc.isActionReady()) {
                                    fillWater(rc);
                                }
                                if(rc.isMovementReady()) {
                                    rc.move(dir);
                                }
                            }
                        }
                    }else if (round > 150 && round < 200) {
                        MapInfo[] damn = rc.senseNearbyMapInfos(-1);
                        if (damn.length > 0) {
                            MapLocation nearestDamWall = damn[0].getMapLocation();
                            MapLocation currentLocation = rc.getLocation();
                            double minDistance = currentLocation.distanceSquaredTo(nearestDamWall);


                            for (MapInfo d : damn) {
                                MapLocation damWallLocation = d.getMapLocation();
                                double distance = currentLocation.distanceSquaredTo(damWallLocation);

                                if (distance < minDistance) {
                                    nearestDamWall = damWallLocation;
                                    minDistance = distance;
                                }
                            }
                            Direction directionToDam = currentLocation.directionTo(nearestDamWall);
                            if (rc.canMove(directionToDam)) {
                                if(rc.isMovementReady()) {
                                    rc.move(directionToDam);
                                }
                            }
                        }
                    }else{
                        // Sense nearby enemy robots within √4 (i.e., 2 tiles)
                        RobotInfo[] enemies = rc.senseNearbyRobots(4, rc.getTeam().opponent());
                        if (enemies.length > 0) {
                            MapLocation enemyLocation = enemies[0].location;
                            if (rc.canMove(rc.getLocation().directionTo(enemyLocation))) {
                                if(rc.isActionReady()) {
                                    fillWater(rc);
                                }
                                if(rc.isMovementReady()) {
                                    rc.move(rc.getLocation().directionTo(enemyLocation));
                                }
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
                        } else if (rc.isActionReady() && randomVar % 2 == 2) {
                            MapLocation currentLocation = rc.getLocation();
                            Direction backwardsDirection = returnDirection.opposite();
                            MapLocation trapLocation = currentLocation.add(backwardsDirection);
                            System.out.println("attempting to build trap");
                            if(rc.canBuild(TrapType.EXPLOSIVE, trapLocation)) {
                                rc.build(TrapType.EXPLOSIVE, trapLocation);
                                System.out.println("Dropping someting at: " + trapLocation);
                            }else{
                                System.out.println("Cannot build trap at: " + trapLocation);
                            }
                        } else {
                            if(rc.canPickupFlag(rc.getLocation())){
                                rc.pickupFlag(rc.getLocation());
                                hasFlag = true;
                                System.out.println("Picked up a flag!");
                            }

                            if(flags.length > 0) {
                                MapLocation nearestFlag = flags[0];
//                                for (MapLocation i : flags){
//                                    System.out.println("flag: " + i);
//                                }
                                if (rc.canMove(rc.getLocation().directionTo(nearestFlag))) {
                                    if(rc.isActionReady()) {
                                        fillWater(rc);
                                    }
                                    if(rc.isMovementReady()) {
                                        rc.move(rc.getLocation().directionTo(nearestFlag));
                                        System.out.println("Moving to nearest flag: " + nearestFlag);
                                    }
                                }else{
                                    Direction randomDir;
                                    for (int i = 0; i < directions.length; i++){
                                        randomDir = directions[rng.nextInt(directions.length)];
                                        if (rc.canMove(randomDir)) {
                                            if(rc.isActionReady()) {
                                                fillWater(rc);
                                                // cooldown stuff going on here need to fix
                                            }
                                            if(rc.isMovementReady()) {
                                                rc.move(randomDir);
                                                break;
                                            }
                                        }
                                    }
                                }
                            FlagInfo[] closeFlags = rc.senseNearbyFlags(8);
                            if (closeFlags.length > 0) {
                                System.out.println("I've sensed the flag!! going for it!");
                                for (FlagInfo i : closeFlags) {
                                    if (i.getTeam() != rc.getTeam()){
                                        if (rc.canPickupFlag(i.getLocation())) {
                                            rc.pickupFlag(i.getLocation());
                                            hasFlag = true;
                                            System.out.println("Picked up a flag!");
                                            break;
                                        } else {
                                            System.out.println("can't pick up flag for some reason?????!");
                                        }
                                    }
                                }
                            }
                            } else{
                                Direction dir = directions[rng.nextInt(directions.length)];
                                if (rc.canMove(dir)) {
                                    if(rc.isActionReady()) {
                                        fillWater(rc);
                                    }
                                    if(rc.isMovementReady()) {
                                    rc.move(dir);
                                    }
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

    private static void fillWater(RobotController rc) throws GameActionException{
        int roundNumber = rc.getRoundNum();
//        if(!rc.isActionReady()) return;
        if(roundNumber > 0){
            MapInfo[] water = rc.senseNearbyMapInfos(2);
            for (MapInfo w:water){
                MapLocation fillLoc = w.getMapLocation();
                if(w.isWater() && rc.canFill(fillLoc)){
                    if ((fillLoc.x+ fillLoc.y)%2 == 1){
                        if(rc.isActionReady()) {
                            rc.fill(fillLoc);
                        }
                    }else{
                        wander(rc);
                    }
                    if(w.getCrumbs() > 30){
                        if(rc.isActionReady()) {
                            rc.fill(fillLoc);
                            return;
                        }
                    }
                    wander(rc);
                    if (rc.canSenseLocation(fillLoc.add(Direction.NORTH))){
                        if(rc.senseMapInfo(fillLoc.add(Direction.NORTH)).isWall()){
                            if(rc.canFill(fillLoc)) {
                                if(rc.isActionReady()) {
                                    rc.fill(fillLoc);
                                    return;
                                }
                            }
                        }
                    }
                    if(rc.canSenseLocation(fillLoc.add(Direction.SOUTH))){
                        if(rc.senseMapInfo(fillLoc.add(Direction.NORTH)).isWall()){
                            if(rc.isActionReady()) {
                                if (rc.canFill(fillLoc)) {
                                    rc.fill(fillLoc);
                                    return;
                                }
                            }
                        }
                    }
                    if(rc.canSenseLocation(fillLoc.add(Direction.EAST))){
                        if(rc.senseMapInfo(fillLoc.add(Direction.EAST)).isWall()){
                            if(rc.isActionReady()) {
                                if (rc.canFill(fillLoc)) {
                                    rc.fill(fillLoc);
                                    return;
                                }
                            }
                        }
                    }
                    if(rc.canSenseLocation(fillLoc.add(Direction.WEST))){
                        if(rc.senseMapInfo(fillLoc.add(Direction.WEST)).isWall()){
                            if(rc.isActionReady()) {
                                if (rc.canFill(fillLoc)) {
                                    rc.fill(fillLoc);
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void addRecentLocation(MapLocation location){
        recentLocations.add(location);
        if(recentLocations.size() > MAX_RECENT_LOCATIONS){
            recentLocations.iterator().next();
        }
    }

    private static void wander(RobotController rc) throws GameActionException{
        Direction[] directions = {
                Direction.NORTH, Direction.NORTHEAST, Direction.EAST, Direction.SOUTHEAST,
                Direction.SOUTH, Direction.SOUTHWEST, Direction.WEST, Direction.NORTHWEST
        };
        Random rng = new Random(6147);
        Direction randomDir = directions[rng.nextInt(directions.length)]; if (rc.canMove(randomDir)) { if(rc.isMovementReady()){ rc.move(randomDir); }}
    }

    private static void awardCrumbs(RobotController rc, int crumbs) throws GameActionException {
        // Example of using a shared array to track crumbs
        int currentCrumbs = rc.readSharedArray(0);  // Read crumbs from index 0 of the shared array
        rc.writeSharedArray(0, currentCrumbs + crumbs);  // Update the shared array with the new crumbs total
    }
}
