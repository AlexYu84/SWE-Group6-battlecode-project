package smartPlayer;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class AttackDuck {

    public static void run(RobotController rc) throws GameActionException {
        while (true) {

            try {
                if (rc.isSpawned()) {

                    // logic for builder that is spawned
                    System.out.println("Attacker Running...");

                } else {

                    // error log here

                }

            }  catch (Exception e) {
                System.out.println("Exception");
                e.printStackTrace();

            } finally {
                Clock.yield();
            }
        }
    }
}
