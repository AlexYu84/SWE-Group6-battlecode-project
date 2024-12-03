package smartPlayer;

import battlecode.common.*;
import org.junit.Test;

import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AttackDuckTest {
    @Test
    public void attckerSetUpPhase() throws GameActionException {
        // Mocking RobotController
        RobotController rc = mock(RobotController.class);
        when(rc.isSpawned()).thenReturn(true);
        when(rc.getRoundNum()).thenReturn(100);

        // Random generator mock
        Random rng = mock(Random.class);
        when(rng.nextInt(8)).thenReturn(0);

        // Define mock locations
        MapLocation[] mapLocations = new MapLocation[]{new MapLocation(0, 0), new MapLocation(1, 0), new MapLocation(2, 0)};
        when(rc.getAllySpawnLocations()).thenReturn(mapLocations);

        // Crumbs and flag location
        MapLocation[] crumsLocations = new MapLocation[]{new MapLocation(3, 4)};
        when(rc.senseNearbyCrumbs(-1)).thenReturn(crumsLocations);
        MapLocation[] flagLocation = new MapLocation[]{new MapLocation(3, 2)};
        when(rc.senseBroadcastFlagLocations()).thenReturn(flagLocation);

        // Current location of the robot
        MapLocation currentLocation = new MapLocation(4, 4);
        when(rc.getLocation()).thenReturn(currentLocation);
        when(rc.getLocation().add(Direction.CENTER)).thenReturn(new MapLocation(4, 4)); // Ensure this is mocked properly

        // Can move check
        when(rc.canMove(any())).thenReturn(true);
        when(rc.isActionReady()).thenReturn(true);

        // MapInfo mock
        MapInfo[] mapInfos = new MapInfo[]{new MapInfo(currentLocation, true, false, false, 1, true, 500, TrapType.WATER, Team.A)};
        when(rc.senseNearbyMapInfos(2)).thenReturn(mapInfos);
        when(rc.canFill(any())).thenReturn(true);
        when(rc.isMovementReady()).thenReturn(true);
        // Create the AttackDuck and run the test
        AttackDuck attackDuck = new AttackDuck();

        // Check if necessary mocks are not null
        assertNotNull("RobotController is null", rc);
        assertNotNull("MapLocation for currentLocation is null", currentLocation);
        assertNotNull("MapInfo is null", mapInfos);
        assertNotNull("Flag Location is null", flagLocation);

        // Run the attackDuck method
        attackDuck.run(rc);
    }
    @Test
    public void attckerFillWater() throws GameActionException {
        // Mocking RobotController
        RobotController rc = mock(RobotController.class);
        when(rc.isSpawned()).thenReturn(true);
        when(rc.getRoundNum()).thenReturn(100);

        // Random generator mock
        Random rng = mock(Random.class);
        when(rng.nextInt(8)).thenReturn(0);

        // Define mock locations
        MapLocation[] mapLocations = new MapLocation[]{new MapLocation(0, 0), new MapLocation(1, 0), new MapLocation(2, 0)};
        when(rc.getAllySpawnLocations()).thenReturn(mapLocations);

        // Crumbs and flag location
        MapLocation[] crumsLocations = new MapLocation[]{new MapLocation(3, 4)};
        when(rc.senseNearbyCrumbs(-1)).thenReturn(crumsLocations);
        MapLocation[] flagLocation = new MapLocation[]{new MapLocation(3, 2)};
        when(rc.senseBroadcastFlagLocations()).thenReturn(flagLocation);

        // Current location of the robot
        MapLocation currentLocation = new MapLocation(2, 1);
        when(rc.getLocation()).thenReturn(currentLocation);
        when(rc.getLocation().add(Direction.CENTER)).thenReturn(new MapLocation(4, 4)); // Ensure this is mocked properly

        // Can move check
        when(rc.canMove(any())).thenReturn(true);
        when(rc.isActionReady()).thenReturn(true);

        // MapInfo mock
        MapInfo[] mapInfos = new MapInfo[]{new MapInfo(currentLocation, true, false, false, 1, true, 500, TrapType.WATER, Team.A)};
        when(rc.senseNearbyMapInfos(2)).thenReturn(mapInfos);
        when(rc.canFill(any())).thenReturn(true);
        when(rc.isMovementReady()).thenReturn(true);
        // Create the AttackDuck and run the test
        AttackDuck attackDuck = new AttackDuck();

        // Check if necessary mocks are not null
        assertNotNull("RobotController is null", rc);
        assertNotNull("MapLocation for currentLocation is null", currentLocation);
        assertNotNull("MapInfo is null", mapInfos);
        assertNotNull("Flag Location is null", flagLocation);

        // Run the attackDuck method
        attackDuck.run(rc);
        verify(rc).fill(currentLocation);
    }
    @Test
    public void handleDam() throws GameActionException {
        RobotController rc = mock(RobotController.class);
        when(rc.isSpawned()).thenReturn(true);
        when(rc.getRoundNum()).thenReturn(152);

        // Random generator mock
        Random rng = mock(Random.class);
        when(rng.nextInt(8)).thenReturn(0);

        // Define mock locations
        MapLocation[] mapLocations = new MapLocation[]{new MapLocation(0, 0), new MapLocation(1, 0), new MapLocation(2, 0)};
        when(rc.getAllySpawnLocations()).thenReturn(mapLocations);

        // Crumbs and flag location
        MapLocation[] crumsLocations = new MapLocation[]{new MapLocation(3, 4)};
        when(rc.senseNearbyCrumbs(-1)).thenReturn(crumsLocations);
        MapLocation[] flagLocation = new MapLocation[]{new MapLocation(3, 2)};
        when(rc.senseBroadcastFlagLocations()).thenReturn(flagLocation);
        MapLocation currentLocation = new MapLocation(2, 1);
        when(rc.getLocation()).thenReturn(new MapLocation(4, 4));
        MapInfo[] mapInfos = new MapInfo[]{new MapInfo(currentLocation, true, false, true, 1, false, 500, TrapType.NONE, Team.A)};
        when(rc.senseNearbyMapInfos(-1)).thenReturn(mapInfos);
        when(rc.canMove(any())).thenReturn(true);
        when(rc.isMovementReady()).thenReturn(true);
        AttackDuck attackDuck = new AttackDuck();
        attackDuck.run(rc);
        verify(rc).move(any());


    }
    @Test
    public void handleEnemiesAndFlags()throws GameActionException {
        RobotController rc = mock(RobotController.class);
        when(rc.isSpawned()).thenReturn(true);
        when(rc.getRoundNum()).thenReturn(400);

        // Random generator mock
        Random rng = mock(Random.class);
        when(rng.nextInt(8)).thenReturn(0);

        // Define mock locations
        MapLocation[] mapLocations = new MapLocation[]{new MapLocation(0, 0), new MapLocation(1, 0), new MapLocation(2, 0)};
        when(rc.getAllySpawnLocations()).thenReturn(mapLocations);

        // Crumbs and flag location
        MapLocation[] crumsLocations = new MapLocation[]{new MapLocation(3, 4)};
        when(rc.senseNearbyCrumbs(-1)).thenReturn(crumsLocations);
        MapLocation[] flagLocation = new MapLocation[]{new MapLocation(3, 2)};
        when(rc.senseBroadcastFlagLocations()).thenReturn(flagLocation);
        MapLocation currentLocation = new MapLocation(2, 1);
        when(rc.getLocation()).thenReturn(new MapLocation(4, 4));
        MapInfo[] mapInfos = new MapInfo[]{new MapInfo(currentLocation, true, false, true, 1, false, 500, TrapType.NONE, Team.A)};
        when(rc.senseNearbyMapInfos(-1)).thenReturn(mapInfos);
        when(rc.senseNearbyMapInfos(2)).thenReturn(mapInfos);
        when(rc.canFill(any())).thenReturn(true);
       // RobotInfo[] enemies = rc.senseNearbyRobots(4, rc.getTeam().opponent());

        RobotInfo robotInfo = new RobotInfo(234, Team.B, 200, new MapLocation(3,1), false, 1, 60, 0);
        RobotInfo [] enemies = new RobotInfo[]{robotInfo};
        when(rc.getTeam()).thenReturn(Team.A);
        when(rc.getTeam().opponent()).thenReturn(Team.B);
        when(rc.senseNearbyRobots(4, Team.A)).thenReturn(enemies);
        when(rc.canMove(any())).thenReturn(true);
        when(rc.isActionReady()).thenReturn(true);
        when(rc.canAttack(any())).thenReturn(true);
        when(rc.senseRobotAtLocation(any())).thenReturn(null);
        when(rc.readSharedArray(0)).thenReturn(1);
        AttackDuck attackDuck = new AttackDuck();
        attackDuck.run(rc);
        verify(rc).attack(any());


    }
    @Test
    public void AttackerNotSpawned() throws GameActionException {
        RobotController rc = mock(RobotController.class);
        when(rc.isSpawned()).thenReturn(false);
        when(rc.getRoundNum()).thenReturn(152);
        AttackDuck attackDuck = new AttackDuck();
        attackDuck.run(rc);

    }

    @Test
    public void testMoveToStartingLocation() throws GameActionException {
        RobotController rc = mock(RobotController.class);
        when(rc.isSpawned()).thenReturn(true);
        when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
        when(rc.canMove(any())).thenReturn(true);
        when(rc.isMovementReady()).thenReturn(true);
        when(rc.isActionReady()).thenReturn(true);
        when(rc.canDropFlag(any())).thenReturn(true);

        AttackDuck attackDuck = new AttackDuck();
        AttackDuck.hasFlag = true;
        AttackDuck.startingLocation = new MapLocation(0, 0);
        Direction returnDirection = Direction.EAST;
        attackDuck.moveToStartingLocation(rc, returnDirection);

        verify(rc).move(any());
        verify(rc).dropFlag(any());
    }

    @Test
    public void testHandleCrumbs() throws GameActionException {
        RobotController rc = mock(RobotController.class);
        when(rc.isSpawned()).thenReturn(true);
        when(rc.getRoundNum()).thenReturn(100);
        when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
        when(rc.canMove(any())).thenReturn(true);
        when(rc.isMovementReady()).thenReturn(true);
        when(rc.isActionReady()).thenReturn(false);

        MapLocation[] crumbLocations = new MapLocation[]{new MapLocation(1, 1)};
        when(rc.senseNearbyCrumbs(-1)).thenReturn(crumbLocations);

        AttackDuck attackDuck = new AttackDuck();
        attackDuck.handleCrumbs(rc, crumbLocations);

        verify(rc).move(any());
    }

    @Test
    public void testHandleFlagCollection() throws GameActionException {
        RobotController rc = mock(RobotController.class);
        when(rc.isSpawned()).thenReturn(true);
        when(rc.getRoundNum()).thenReturn(300);
        when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
        when(rc.canMove(any())).thenReturn(true);
        when(rc.isMovementReady()).thenReturn(true);
        when(rc.isActionReady()).thenReturn(false);
        when(rc.canPickupFlag(any())).thenReturn(true);

        MapLocation[] flagLocations = new MapLocation[]{new MapLocation(1, 1)};
        when(rc.senseBroadcastFlagLocations()).thenReturn(flagLocations);

        AttackDuck attackDuck = new AttackDuck();
        attackDuck.handleFlagCollection(rc, flagLocations);

        verify(rc).pickupFlag(any());
        verify(rc).move(any());
    }

    @Test
    public void testMoveToNewLocation() throws GameActionException {
        RobotController rc = mock(RobotController.class);
        when(rc.isSpawned()).thenReturn(true);
        when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
        when(rc.canMove(any())).thenReturn(true);
        when(rc.isMovementReady()).thenReturn(true);

        AttackDuck attackDuck = new AttackDuck();
        attackDuck.moveToNewLocation(rc);

        verify(rc).move(any());
    }

    @Test
    public void testMoveRandomly() throws GameActionException {
        RobotController rc = mock(RobotController.class);
        when(rc.isSpawned()).thenReturn(true);
        when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
        when(rc.canMove(any())).thenReturn(true);
        when(rc.isMovementReady()).thenReturn(true);

        AttackDuck attackDuck = new AttackDuck();
        attackDuck.moveRandomly(rc);

        verify(rc).move(any());
    }

    @Test
    public void testBuildTrap() throws GameActionException {
        RobotController rc = mock(RobotController.class);
        when(rc.isSpawned()).thenReturn(true);
        when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
        when(rc.canBuild(any(), any())).thenReturn(true);

        AttackDuck attackDuck = new AttackDuck();
        attackDuck.buildTrap(rc, Direction.EAST);

        verify(rc).build(any(), any());
    }

    @Test
    public void testWander() throws GameActionException {
        RobotController rc = mock(RobotController.class);
        when(rc.isSpawned()).thenReturn(true);
        when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
        when(rc.canMove(any())).thenReturn(true);
        when(rc.isMovementReady()).thenReturn(true);

        AttackDuck attackDuck = new AttackDuck();
        attackDuck.wander(rc);

        verify(rc).move(any());
    }

    @Test
    public void testAwardCrumbs() throws GameActionException {
        RobotController rc = mock(RobotController.class);
        when(rc.isSpawned()).thenReturn(true);
        when(rc.readSharedArray(0)).thenReturn(100);

        AttackDuck attackDuck = new AttackDuck();
        attackDuck.awardCrumbs(rc, 50);

        verify(rc).writeSharedArray(0, 150);
    }
}
