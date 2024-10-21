package smartPlayer;
import battlecode.common.*;
public class PathFind {
    static Direction direction;

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
            moveTowards(rc, crumLocs[0]);
        }
        if(rc.isMovementReady()) {
            if(direction != null && rc.canMove(direction)) {
                rc.move(direction);
            }
            else {
                direction = Direction.allDirections()[RobotPlayer.rng.nextInt(8)];
            }
        }
    }
}
