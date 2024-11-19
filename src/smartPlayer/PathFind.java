package smartPlayer;
import battlecode.common.*;
public class PathFind {

    public static void moveTowards(RobotController rc, MapLocation loc) throws GameActionException {
        Direction dir = rc.getLocation().directionTo(loc);
        if(rc.canMove(dir) && (!rc.senseMapInfo(loc).isWall())) {
            rc.move(dir);
        }
        else if(rc.canFill(rc.getLocation().add(dir))) {
            rc.fill(rc.getLocation().add(dir));
        }
        else {
            Direction randomDir = Direction.allDirections()[RobotPlayer.rng.nextInt(8)];
            if(rc.canMove(randomDir) && (!rc.senseMapInfo(loc).isWall())) {
                rc.move(randomDir);
            }
        }
    }
    public static void explore(RobotController rc) throws GameActionException {
        MapLocation[] crumLocs = rc.senseNearbyCrumbs(-1);
        if(crumLocs.length > 0){
            System.out.println("Crumbs found.");
            moveTowards(rc, crumLocs[0]);
        }
        if (crumLocs.length == 0) {
            System.out.println("No crumbs found.");
            if (rc.isMovementReady()) {
                System.out.println("Movement is ready.");
                Direction randomDirection = Direction.allDirections()[RobotPlayer.rng.nextInt(8)];
                if (rc.canMove(randomDirection)) {
                    System.out.println("Can move in direction: " + randomDirection);
                    rc.move(randomDirection);
                } else {
                    System.out.println("Cannot move in direction: " + randomDirection);
                }
            } else {
                System.out.println("Movement is not ready.");
            }
        }
    }
}
