package smartPlayer;

import battlecode.common.*;
import org.junit.Test;

import java.util.Random;

import static org.mockito.Mockito.*;

public class PathFindTest {

    @Test
    public void testMoveTowards_CanMove() throws GameActionException {
        RobotController rc = mock(RobotController.class);
        MapLocation currentLocation = new MapLocation(0, 0);
        MapLocation targetLocation = new MapLocation(1, 0);
        Direction direction = currentLocation.directionTo(targetLocation);

        when(rc.getLocation()).thenReturn(currentLocation);
        when(rc.canMove(direction)).thenReturn(true);
        when(rc.senseMapInfo(targetLocation)).thenReturn(mock(MapInfo.class));

        PathFind.moveTowards(rc, targetLocation);

        verify(rc).move(direction);
    }

    @Test
    public void testMoveTowards_CannotMove_CanFill() throws GameActionException {
        RobotController rc = mock(RobotController.class);
        MapLocation currentLocation = new MapLocation(0, 0);
        MapLocation targetLocation = new MapLocation(1, 0);
        Direction direction = currentLocation.directionTo(targetLocation);

        when(rc.getLocation()).thenReturn(currentLocation);
        when(rc.canMove(direction)).thenReturn(false);
        when(rc.canFill(currentLocation.add(direction))).thenReturn(true);

        PathFind.moveTowards(rc, targetLocation);

        verify(rc).fill(currentLocation.add(direction));
    }

    @Test
    public void testMoveTowards_CannotMove_CannotFill() throws GameActionException {
        RobotController rc = mock(RobotController.class);
        MapLocation currentLocation = new MapLocation(0, 0);
        MapLocation targetLocation = new MapLocation(1, 0);
        Direction direction = currentLocation.directionTo(targetLocation);
        Direction randomDirection = Direction.allDirections()[0];

        when(rc.getLocation()).thenReturn(currentLocation);
        when(rc.canMove(direction)).thenReturn(false);
        when(rc.canFill(currentLocation.add(direction))).thenReturn(false);
        when(rc.canMove(randomDirection)).thenReturn(true);
        when(rc.senseMapInfo(targetLocation)).thenReturn(mock(MapInfo.class));

        // Mock the static method RobotPlayer.rng.nextInt(8)
        Random rngMock = mock(Random.class);
        when(rngMock.nextInt(8)).thenReturn(0);
        RobotPlayer.rng = rngMock;

        PathFind.moveTowards(rc, targetLocation);

        verify(rc).move(randomDirection);
    }

    @Test
    public void testExplore_WithCrumbs() throws GameActionException {
        RobotController rc = mock(RobotController.class);
        MapLocation crumbLocation = new MapLocation(1, 1);
        MapLocation currentLocation = new MapLocation(0, 0);

        when(rc.senseNearbyCrumbs(-1)).thenReturn(new MapLocation[]{crumbLocation});
        when(rc.getLocation()).thenReturn(currentLocation);
        when(rc.canMove(any(Direction.class))).thenReturn(true);
        when(rc.senseMapInfo(crumbLocation)).thenReturn(mock(MapInfo.class));

        PathFind.explore(rc);

        verify(rc).move(any(Direction.class));
    }

    @Test
    public void testExplore_NoCrumbs() throws GameActionException {
        RobotController rc = mock(RobotController.class);
        Random rng = mock(Random.class);
        Direction randomDirection = Direction.allDirections()[0];

        when(rc.senseNearbyCrumbs(-1)).thenReturn(new MapLocation[]{});
        when(rc.isMovementReady()).thenReturn(true);
        when(rc.canMove(randomDirection)).thenReturn(true);
        when(rng.nextInt(8)).thenReturn(0);

        // Inject the mocked rng into RobotPlayer
        RobotPlayer.rng = rng;

        PathFind.explore(rc);

        verify(rc).move(randomDirection);
    }
}
