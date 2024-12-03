package smartPlayer;

import battlecode.common.*;
import org.junit.Before;
import org.junit.Test;
import java.util.Random;
import static org.mockito.Mockito.*;

public class RobotPlayerTest {
   private Random rng;
   private RobotController rc;
   private MapLocation[] mapLocations;
   private MapLocation mapLocation;

   @Before
   public void setUp() {
       rc = mock(RobotController.class);
       rng = mock(Random.class);
       mapLocations = new MapLocation[] {new MapLocation(0, 0), new MapLocation(1, 1)};
       mapLocation = mapLocations[rng.nextInt(mapLocations.length)];
   }

    @Test
    public void testRun_WhenSpawned() throws GameActionException {
        when(rc.isSpawned()).thenReturn(true);

        RobotPlayer.run(rc);

        verify(rc, never()).getAllySpawnLocations();
    }

    @Test
    public void testRun_NotSpawnedCantSpawn() throws GameActionException {
        when(rc.getAllySpawnLocations()).thenReturn(mapLocations);
        when(rc.canSpawn(mapLocation)).thenReturn(false);

        RobotPlayer.runWhenNotSpawned(rc);

        verify(rc, never()).spawn(mapLocation);
    }

    @Test
    public void testRun_NotSpawnedCanSpawn() throws GameActionException {
        when(rc.isSpawned()).thenReturn(false);
        when(rc.getAllySpawnLocations()).thenReturn(mapLocations);
        when(rc.canSpawn(mapLocation)).thenReturn(true);

        RobotPlayer.run(rc);

        verify(rc, times(1)).getAllySpawnLocations();
        verify(rc, times(1)).canSpawn(mapLocation);
        verify(rc, times(1)).spawn(mapLocation);
    }

//    @Test
//    public void testRun_NotSpawnedSpawnsAttackDuck() throws GameActionException {
//        Random rng = mock(Random.class);
//        RobotController rc = mock(RobotController.class);
//
//        AttackDuck attackDuck = mock(AttackDuck.class);
//
//        when(rng.nextInt(anyInt())).thenReturn(3);
//        when(rc.isSpawned()).thenReturn(false);
//
//        RobotPlayer.run(rc);
//
//        verify(attackDuck, times(1)).run(rc);
//    }

//    @Test
//    public void testRun_NotSpawnedSpawnsHealerDuck() throws GameActionException {
//        Random rng = mock(Random.class);
//        RobotController rc = mock(RobotController.class);
//
//        HealerDuck healerDuck = mock(HealerDuck.class);
//
//        when(rng.nextInt(anyInt())).thenReturn(5);
//        when(rc.isSpawned()).thenReturn(false);
//
//        RobotPlayer.run(rc);
//
//        verify(healerDuck, times(1)).run(rc);
//    }

//    @Test
//    public void testRun_NotSpawnedSpawnsBuilderDuck() throws GameActionException {
//        Random rng = mock(Random.class);
//        RobotController rc = mock(RobotController.class);
//
//        BuilderDuck builderDuck = mock(BuilderDuck.class);
//
//        when(rng.nextInt(anyInt())).thenReturn(7);
//        when(rc.isSpawned()).thenReturn(false);
//
//        RobotPlayer.run(rc);
//
//        verify(builderDuck, times(1)).run(rc);
//    }
}
