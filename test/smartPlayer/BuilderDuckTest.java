package smartPlayer;

import battlecode.common.*;
import org.mockito.Mockito;

import static battlecode.common.Team.A;
import static battlecode.common.TrapType.WATER;
import static org.mockito.Mockito.*;
import org.junit.Test;

import javax.xml.stream.Location;

public class BuilderDuckTest {

    @Test
    public void testRobotMovesRandomlyInSetup() throws GameActionException {
        // Mock RobotController
        RobotController rc = mock(RobotController.class);
        when(rc.isSpawned()).thenReturn(true);
        MapLocation targetLocation = new MapLocation(1, 0);
        MapLocation[] targetLocations= new MapLocation[]{new MapLocation(1, 0)};
        //rc.canPickupFlag(rc.getLocation()) && rcInSpawn(rc)
        when(rc.getLocation()).thenReturn(targetLocation);
        when(rc.canPickupFlag(targetLocation)).thenReturn(true);
        when(rc.getRoundNum()).thenReturn(1); // In setup rounds
        when(rc.canMove(any(Direction.class))).thenReturn(true);
        when(rc.hasFlag()).thenReturn(false);
        when(rc.getAllySpawnLocations()).thenReturn(targetLocations);
        BuilderDuck builderDuck = new BuilderDuck();

        builderDuck.run(rc);

        // Verify that move was called with a valid direction
        verify(rc, atLeastOnce()).move(any(Direction.class));
    }
    @Test
    public void testRobotMovesRandomlyInNonSetupRounds() throws GameActionException {
        // Mock RobotController
        RobotController rc = mock(RobotController.class);
        when(rc.isSpawned()).thenReturn(true);
        MapLocation targetLocation = new MapLocation(1, 0);
        MapLocation[] targetLocations= new MapLocation[]{new MapLocation(1, 0)};
        //rc.canPickupFlag(rc.getLocation()) && rcInSpawn(rc)
        when(rc.getLocation()).thenReturn(targetLocation);
        when(rc.canPickupFlag(targetLocation)).thenReturn(true);
        when(rc.getRoundNum()).thenReturn(300); // In setup rounds
        when(rc.canMove(any(Direction.class))).thenReturn(true);
        when(rc.hasFlag()).thenReturn(false);
        when(rc.getAllySpawnLocations()).thenReturn(targetLocations);
        BuilderDuck builderDuck = new BuilderDuck();
        //rc.senseMapInfo(mapLocation).getTrapType() != TrapType.NONE
        MapInfo mapInfo = Mockito.mock(MapInfo.class);
        MapLocation currentLocation = new MapLocation(3, 3);
        MapLocation adjacentLocation = currentLocation.add(Direction.NORTH);
        // Mocking RobotController methods
        when(rc.getLocation()).thenReturn(currentLocation);
        when(rc.isSpawned()).thenReturn(true);
        when(rc.canBuild(eq(TrapType.WATER), any(MapLocation.class))).thenReturn(true);

        // Mocking MapInfo
        MapInfo mapInfo1 = new MapInfo(currentLocation, true, false, false, 1, true, 200, WATER, A);
        when(rc.senseMapInfo(any())).thenReturn(mapInfo1);
        //rc.canBuild(TrapType.WATER, rc.getLocation()) && !rcInSpawn(rc)
        when(rc.canBuild(eq(TrapType.WATER), any(MapLocation.class))).thenReturn(true);
        // rc.build(TrapType.WATER, rc.getLocation());
        when(rc.getAllySpawnLocations()).thenReturn(targetLocations);


        builderDuck.run(rc);

        // Verify that the robot moved randomly
        verify(rc, atLeastOnce()).senseMapInfo(any(MapLocation.class));
    }

}
