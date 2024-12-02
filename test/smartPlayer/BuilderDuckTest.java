package smartPlayer;

import battlecode.common.*;
import org.mockito.Mockito;
import static battlecode.common.Team.A;
import static battlecode.common.TrapType.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static smartPlayer.BuilderDuck.isFlagProtected;
import org.junit.Test;
import java.util.Arrays;

public class  BuilderDuckTest {

    @Test
    public void testRCInSpawn() throws GameActionException {
        // mock the RobotController
        RobotController rc = mock(RobotController.class);

        // set rc and spawn locations
        MapLocation[] locations = new MapLocation[] {new MapLocation(1, 1), new MapLocation(1, 2), new MapLocation(1, 3)};
        when(rc.getAllySpawnLocations()).thenReturn(locations);
        when(rc.getLocation()).thenReturn(new MapLocation(1,2));

        // test if rc is in spawn returns true
        assertTrue(BuilderDuck.rcInSpawn(rc));
    }

    @Test
    public void testRCNotInSpawn() throws GameActionException {
        // mock the RobotController
        RobotController rc = mock(RobotController.class);

        // set rc and spawn locations
        MapLocation[] locations = new MapLocation[] {new MapLocation(1, 1), new MapLocation(1, 2), new MapLocation(1, 3)};
        when(rc.getAllySpawnLocations()).thenReturn(locations);
        when(rc.getLocation()).thenReturn(new MapLocation(3,3));

        // test if rc is in spawn returns false
        assertFalse(BuilderDuck.rcInSpawn(rc));
    }

    @Test
    public void testIsFlagProtectedNot() throws GameActionException {
        RobotController rc = mock(RobotController.class);
        MapLocation rcLocation = new MapLocation(3, 3);
        when(rc.getLocation()).thenReturn(rcLocation);

        for (Direction dir : Direction.values()) {
            when(rc.senseMapInfo(rc.getLocation().add(dir))).thenReturn(mock(MapInfo.class));
        }

        // set NORTH to unprotected
        when(rc.senseMapInfo(rc.getLocation().add(Direction.NORTH)).getTrapType()).thenReturn(NONE);

        assertFalse(isFlagProtected(rc));
    }

    @Test
    public void testIsFlagProtectedIs() throws GameActionException {
        RobotController rc = mock(RobotController.class);
        MapLocation rcLocation = new MapLocation(3, 3);
        when(rc.getLocation()).thenReturn(rcLocation);

        for (Direction dir : Direction.values()) {
            // return none for all trap types
            when(rc.senseMapInfo(rc.getLocation().add(dir))).thenReturn(mock(MapInfo.class));
            // set all to protected (i.e EXPLOSIVE)
            when(rc.senseMapInfo(rc.getLocation().add(dir)).getTrapType()).thenReturn(EXPLOSIVE);
        }

        assertTrue(isFlagProtected(rc));
    }

    @Test
    public void testSelectRandomFlag() {
        FlagInfo[] flags = new FlagInfo[] {mock(FlagInfo.class), mock(FlagInfo.class), mock(FlagInfo.class)};
        FlagInfo selectedFlag = BuilderDuck.selectRandomFlag(flags);

        // test that the selected flag is one of the flags in the array
        assertTrue(Arrays.asList(flags).contains(selectedFlag));
    }

    @Test
    public void testMoveTowardsFlag() throws GameActionException {
        RobotController rc = mock(RobotController.class);
        FlagInfo flagToProtect = mock(FlagInfo.class);
        MapLocation flagLocation = new MapLocation(5, 5);
        MapLocation rcLocation = new MapLocation(3, 3);
        Direction dir = rcLocation.directionTo(flagLocation);

        when(rc.getLocation()).thenReturn(rcLocation);
        when(flagToProtect.getLocation()).thenReturn(flagLocation);
        when(rc.canMove(dir)).thenReturn(true);

        BuilderDuck.moveTowardsFlag(rc, flagToProtect);

        verify(rc).move(dir);  // test that the move method was called
    }

    @Test
    public void testBuildExplosiveTraps() throws GameActionException {
        RobotController rc = mock(RobotController.class);
        FlagInfo flagToProtect = mock(FlagInfo.class);
        MapLocation flagLocation = new MapLocation(5, 5);
        MapLocation[] locations = new MapLocation[] {new MapLocation(1, 1), new MapLocation(1, 2), new MapLocation(1, 3)};
        MapLocation rcLocation = new MapLocation(3, 3);

        // mock getLocation() to return a specific location for the robot
        when(rc.getLocation()).thenReturn(rcLocation);
        when(rc.getAllySpawnLocations()).thenReturn(locations);
        when(flagToProtect.getLocation()).thenReturn(flagLocation);

        // simulate that the robot can build traps at certain locations
        when(rc.canBuild(TrapType.EXPLOSIVE, flagLocation.add(Direction.NORTH))).thenReturn(true);

        // call the method to test
        BuilderDuck.buildExplosiveTraps(rc, flagToProtect);

        // test that build was called for NORTH
        verify(rc).build(TrapType.EXPLOSIVE, flagLocation.add(Direction.NORTH));

    }


    @Test
    public void testAttackEnemiesIfAny() throws GameActionException {
        RobotController rc = mock(RobotController.class);
        RobotInfo enemyRobot = mock(RobotInfo.class);
        MapLocation enemyLocation = new MapLocation(6, 6);
        MapLocation rcLocation = new MapLocation(4, 4);

        when(rc.getLocation()).thenReturn(rcLocation);
        when(rc.getTeam()).thenReturn(Team.A);
        when(rc.getTeam().opponent()).thenReturn(Team.B);

        when(enemyRobot.getLocation()).thenReturn(enemyLocation);
        when(rc.senseNearbyRobots(4, rc.getTeam().opponent())).thenReturn(new RobotInfo[] {enemyRobot});
        when(rc.canAttack(enemyLocation)).thenReturn(true);

        BuilderDuck.attackEnemiesIfAny(rc);

        // test
        verify(rc).attack(eq(enemyLocation));
    }


    @Test
    public void testFindClosestEnemy() {
        RobotController rc = mock(RobotController.class);
        MapLocation rcLocation = new MapLocation(3, 3);
        when(rc.getLocation()).thenReturn(rcLocation);

        RobotInfo enemy1 = mock(RobotInfo.class);
        RobotInfo enemy2 = mock(RobotInfo.class);
        MapLocation enemyLocation1 = new MapLocation(5, 5);
        MapLocation enemyLocation2 = new MapLocation(7, 7);

        when(enemy1.getLocation()).thenReturn(enemyLocation1);
        when(enemy2.getLocation()).thenReturn(enemyLocation2);

        RobotInfo[] enemies = new RobotInfo[] {enemy1, enemy2};

        RobotInfo closestEnemy = BuilderDuck.findClosestEnemy(rc, enemies);

        // test that the closest enemy is the one at 5,5
        assertEquals(enemy1, closestEnemy);
    }


    @Test
    public void testBuildWaterTrapsNoExistingTrapCanBuild() throws GameActionException {
        // mock the RobotController
        RobotController rc = mock(RobotController.class);

        // mock MapLocation and Direction
        MapLocation currentLocation = new MapLocation(1,0);
        when(rc.getLocation()).thenReturn(currentLocation);

        // mock senseMapInfo to return no trap for all directions
        MapInfo mapInfo = mock(MapInfo.class);
        when(mapInfo.getTrapType()).thenReturn(TrapType.NONE);

        // mock all directions to return mapInfo with no trap
        for (Direction dir : Direction.values()) {
            MapLocation mapLocation = new MapLocation(1,0);
            when(rc.getLocation().add(dir)).thenReturn(mapLocation);
            when(rc.senseMapInfo(mapLocation)).thenReturn(mapInfo);
        }

        // mock canBuild to return true
        when(rc.canBuild(eq(TrapType.WATER), any(MapLocation.class))).thenReturn(true);

        // mock the rcInSpawn function
        when(rc.getAllySpawnLocations()).thenReturn(new MapLocation[]{new MapLocation(0,0)});
        //when(BuilderDuck.rcInSpawn(rc)).thenReturn(false);

        // call the function to test
        BuilderDuck.buildWaterTraps(rc);

        // test that build is called exactly once
        verify(rc, times(1)).build(TrapType.WATER, currentLocation);
    }

    @Test
    public void testBuildWaterTrapsExistingTrap() throws GameActionException {
        // mock the RobotController
        RobotController rc = mock(RobotController.class);

        // mapLocation and Direction
        MapLocation currentLocation = new MapLocation(1,0);
        when(rc.getLocation()).thenReturn(currentLocation);

        // mock senseMapInfo to return a trap in one of the directions
        MapInfo mapInfoWithTrap = mock(MapInfo.class);
        when(mapInfoWithTrap.getTrapType()).thenReturn(TrapType.WATER);

        // set one direction to have a trap
        when(rc.getLocation().add(Direction.NORTH)).thenReturn(new MapLocation(Direction.NORTH.dx, Direction.NORTH.dy));
        when(rc.senseMapInfo(any(MapLocation.class))).thenReturn(mapInfoWithTrap);

        // call the function to test
        BuilderDuck.buildWaterTraps(rc);

        // verify that build is never called because there is already a trap
        verify(rc, never()).build(any(TrapType.class), any(MapLocation.class));
    }

    @Test
    public void testBuildWaterTrapsCannotBuild() throws GameActionException {
        // mock the RobotController
        RobotController rc = mock(RobotController.class);

        // mapLocation and Direction
        MapLocation currentLocation = new MapLocation(1,0);
        when(rc.getLocation()).thenReturn(currentLocation);

        // mock senseMapInfo to return no trap for all directions
        MapInfo mapInfo = mock(MapInfo.class);
        when(mapInfo.getTrapType()).thenReturn(TrapType.NONE);

        // mock all directions to return mapInfo with no trap
        for (Direction dir : Direction.values()) {
            MapLocation mapLocation = new MapLocation(1,0);
            when(rc.getLocation().add(dir)).thenReturn(mapLocation);
            when(rc.senseMapInfo(mapLocation)).thenReturn(mapInfo);
        }

        // mock canBuild to return false (the robot can't build)
        when(rc.canBuild(eq(TrapType.WATER), any(MapLocation.class))).thenReturn(false);

        // call the function to test
        BuilderDuck.buildWaterTraps(rc);

        // test that build is never called because the robot can't build
        verify(rc, never()).build(any(TrapType.class), any(MapLocation.class));
    }

    @Test
    public void testRobotMovesRandomlyInSetup() throws GameActionException {
        // Mock RobotController
        RobotController rc = mock(RobotController.class);
        when(rc.isSpawned()).thenReturn(true);
        MapLocation targetLocation = new MapLocation(1, 0);
        MapLocation[] targetLocations = new MapLocation[]{new MapLocation(1, 0)};
        //rc.canPickupFlag(rc.getLocation()) && rcInSpawn(rc)
        when(rc.getLocation()).thenReturn(targetLocation);
        when(rc.canPickupFlag(targetLocation)).thenReturn(true);
        when(rc.getRoundNum()).thenReturn(1); // In setup rounds
        when(rc.canMove(any(Direction.class))).thenReturn(true);
        when(rc.hasFlag()).thenReturn(false);
        when(rc.getAllySpawnLocations()).thenReturn(targetLocations);
        BuilderDuck builderDuck = new BuilderDuck();

        builderDuck.run(rc);

        // verify that move was called with a valid direction
        verify(rc, atLeastOnce()).move(any(Direction.class));
    }

    @Test
    public void testRobotMovesRandomlyInNonSetupRounds() throws GameActionException {
        // Mock RobotController
        RobotController rc = mock(RobotController.class);
        when(rc.isSpawned()).thenReturn(true);
        MapLocation targetLocation = new MapLocation(1, 0);
        MapLocation[] targetLocations = new MapLocation[]{new MapLocation(1, 0)};
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
    @Test
    public void testRobotMovesRandomlyOutSideSetupRound() throws GameActionException {
        RobotController rc = mock(RobotController.class);
        when(rc.isSpawned()).thenReturn(true);
        when(rc.getRoundNum()).thenReturn(1);
        BuilderDuck builderDuck = new BuilderDuck();
        when(rc.getLocation()).thenReturn(new MapLocation(1, 0));
        when(rc.canPickupFlag(any())).thenReturn(true);
        when(rc.canMove(any(Direction.class))).thenReturn(true);
        MapLocation [] mapLocations = new MapLocation[]{new MapLocation(1, 0)};
        when(rc.getAllySpawnLocations()).thenReturn(mapLocations);
        MapLocation[] spawnLocations  = rc.getAllySpawnLocations();

        builderDuck.run(rc);
        verify(rc, atLeastOnce()).move(any(Direction.class));
    }

    @Test
    public void testRobotMovesRandomlyOutSideSetupRoundCanNotPickUpFlag() throws GameActionException {
        RobotController rc = mock(RobotController.class);
        when(rc.isSpawned()).thenReturn(true);
        when(rc.getRoundNum()).thenReturn(1);
        BuilderDuck builderDuck = new BuilderDuck();
        when(rc.getLocation()).thenReturn(new MapLocation(1, 0));
        when(rc.canPickupFlag(any())).thenReturn(false);
        when(rc.hasFlag()).thenReturn(true);
        when(rc.canMove(any(Direction.class))).thenReturn(true);
        MapLocation [] mapLocations = new MapLocation[]{new MapLocation(1, 0)};
        when(rc.getAllySpawnLocations()).thenReturn(mapLocations);
        MapLocation[] spawnLocations  = rc.getAllySpawnLocations();
        MapLocation currentLocation = new MapLocation(1, 0);
        MapInfo mapInfo = new MapInfo(currentLocation, true, false, false, 1, true, 200, WATER, A);
        MapInfo [] mapInfos = new MapInfo[]{mapInfo};
        when(rc.senseMapInfo(any())).thenReturn(mapInfo);
        when(rc.senseNearbyMapInfos()).thenReturn(mapInfos);

        builderDuck.run(rc);
    }

}