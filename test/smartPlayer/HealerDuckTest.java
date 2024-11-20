//package smartPlayer;
//
//import battlecode.common.*;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.util.Random;
//
//import static org.mockito.Mockito.*;
//
//
//public class HealerDuckTest {
//
//    private RobotController rc;
//    private RobotInfo friend;
//    private RobotInfo[] nearbyFriends;
//    private Random rng;
//    private Direction randomDirection;
//
//    @Before
//    public void setUp() {
//        rc = mock(RobotController.class);
//        friend = mock(RobotInfo.class);
//        nearbyFriends = new RobotInfo[]{friend};
//        rng = mock(Random.class);
//        randomDirection = Direction.allDirections()[0];
//    }
//
//    @Test
//    public void testRun_whenNotSpawned() throws GameActionException {
//        when(rc.isSpawned()).thenReturn(false);
//
//        HealerDuck.run(rc);
//
//        verify(rc, never()).getRoundNum();
//    }
//
//    @Test
//    public void testRun_whenSpawnedDuringSetupRounds() throws GameActionException {
//        when(rc.isSpawned()).thenReturn(true);
//        when(rc.getRoundNum()).thenReturn(GameConstants.SETUP_ROUNDS - 1);
//        when(rc.senseNearbyCrumbs(-1)).thenReturn(new MapLocation[]{});
//        when(rc.isMovementReady()).thenReturn(true);
//        when(rc.canMove(randomDirection)).thenReturn(true);
//        when(rng.nextInt(8)).thenReturn(0);
//
//        RobotPlayer.rng = rng;
//
//        HealerDuck.run(rc);
//
//        verify(rc).canWriteSharedArray(0, 0);
//        verify(rc, never()).senseNearbyRobots(anyInt(), any(Team.class));
//    }
//
//    @Test
//    public void testRun_whenSpawnedAfterSetupRounds_andHealsFriend() throws GameActionException {
//        Random rng = mock(Random.class);
//        Direction randomDirection = Direction.allDirections()[0];
//
//        when(rc.isSpawned()).thenReturn(true);
//        when(rc.getRoundNum()).thenReturn(GameConstants.SETUP_ROUNDS + 1);
//        when(rc.senseNearbyRobots(2, rc.getTeam())).thenReturn(nearbyFriends);
//        when(friend.getHealth()).thenReturn(500);
//        when(rc.canHeal(friend.getLocation())).thenReturn(true);
//        when(rc.readSharedArray(0)).thenReturn(0);
//        when(rc.senseNearbyCrumbs(-1)).thenReturn(new MapLocation[]{});
//        when(rc.isMovementReady()).thenReturn(true);
//        when(rc.canMove(randomDirection)).thenReturn(true);
//        when(rng.nextInt(8)).thenReturn(0);
//
//        RobotPlayer.rng = rng;
//
//        HealerDuck.run(rc);
//
//        verify(rc).heal(friend.getLocation());
//        verify(rc).setIndicatorString("Healing: " + friend.getLocation());
//    }
//
//    @Test
//    public void testRun_whenSpawnedAfterSetupRounds_andCannotHealFriend() throws GameActionException {
//        when(rc.isSpawned()).thenReturn(true);
//        when(rc.getRoundNum()).thenReturn(GameConstants.SETUP_ROUNDS + 1);
//        when(rc.senseNearbyRobots(2, rc.getTeam())).thenReturn(nearbyFriends);
//        when(friend.getHealth()).thenReturn(500);
//        when(rc.canHeal(friend.getLocation())).thenReturn(false);
//        when(rc.senseNearbyCrumbs(-1)).thenReturn(new MapLocation[]{});
//        when(rc.isMovementReady()).thenReturn(true);
//        when(rc.canMove(randomDirection)).thenReturn(true);
//        when(rng.nextInt(8)).thenReturn(0);
//
//        RobotPlayer.rng = rng;
//
//        HealerDuck.run(rc);
//
//        verify(rc, never()).heal(friend.getLocation());
//    }
//
//    @Test
//    public void testRun_whenSpawnedAfterSetupRounds_andSharedArrayLimitReached() throws GameActionException {
//        when(rc.isSpawned()).thenReturn(true);
//        when(rc.getRoundNum()).thenReturn(GameConstants.SETUP_ROUNDS + 1);
//        when(rc.senseNearbyRobots(2, rc.getTeam())).thenReturn(nearbyFriends);
//        when(friend.getHealth()).thenReturn(500);
//        when(rc.canHeal(friend.getLocation())).thenReturn(true);
//        when(rc.readSharedArray(0)).thenReturn(10);
//        when(rc.senseNearbyCrumbs(-1)).thenReturn(new MapLocation[]{});
//        when(rc.isMovementReady()).thenReturn(true);
//        when(rc.canMove(randomDirection)).thenReturn(true);
//        when(rng.nextInt(8)).thenReturn(0);
//
//        RobotPlayer.rng = rng;
//
//
//        HealerDuck.run(rc);
//
//        verify(rc, never()).heal(friend.getLocation());
//    }
//}