package smartPlayer;

import battlecode.common.*;

public class HealerDuck {

    public static void run(RobotController rc) throws GameActionException {
        // share
        while (true) {

            try {
                if (rc.isSpawned()) {
                    // logic for builder that is spawned
                    System.out.println("Healer Running...");
                    int round = rc.getRoundNum();
                    if(round <= GameConstants.SETUP_ROUNDS) {
                        rc.canWriteSharedArray(0,0);
                        PathFind.explore(rc);
                    }
                    else {
                        RobotInfo[] nearbyFriends = rc.senseNearbyRobots(2, rc.getTeam());
                        for (RobotInfo friend : nearbyFriends) {
                            if (friend.health < 1000 && rc.canHeal(friend.getLocation()) && rc.readSharedArray(0) < 10) {
                                rc.heal(friend.getLocation());
                                System.out.println("Healed a friendly unit!");
                                rc.setIndicatorString("Healing: " + friend.getLocation());
                                break; // Heal only one unit per turn
                            }
                        }
                        rc.canWriteSharedArray(0, rc.readSharedArray(0) + 1);
                        PathFind.explore(rc);

                    }
                } else {
                    System.out.println("duck not spawned!!!!..");
                    return;

                }

            }  catch (Exception e) {
                System.out.println("Exception");
                e.printStackTrace();

            } finally {
                break;
                // Clock.yield();
            }
        }
    }
}
