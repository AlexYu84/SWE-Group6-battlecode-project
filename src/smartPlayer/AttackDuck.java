package smartPlayer;

import battlecode.common.*;

public class AttackDuck {
    public static void run(RobotController rc) throws GameActionException {
        while (true) {
            try {
                if (rc.isSpawned()) {
                    // Attack logic for the attacker duck
                    System.out.println("Attacker Running...");

                    // Sense nearby enemy robots
                    RobotInfo[] enemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());

                    if (enemies.length > 0) {
                        // Move towards the nearest enemy
                        MapLocation enemyLocation = enemies[0].location;
                        if (rc.canMove(rc.getLocation().directionTo(enemyLocation))) {
                            rc.move(rc.getLocation().directionTo(enemyLocation));
                        }

                        // Attack the enemy if within range
                        if (rc.canAttack(enemyLocation)) {
                            rc.attack(enemyLocation);
                        }
                    }
                } else {
                    // Error log here
                    System.err.println("Attacker not spawned properly.");
                }
            } catch (Exception e) {
                System.err.println("Exception: " + e.getMessage());
                e.printStackTrace();
            } finally {
                Clock.yield();
            }
        }
    }
}
