import lejos.nxt.*;
import lejos.robotics.navigation.*;

public class RobotLabNav {

  public static void main(String[] args) {

      DifferentialPilot pilot;
      Navigator navbot;

      pilot = new DifferentialPilot(5.4f, 17.0f, Motor.A, Motor.C);
      navbot = new Navigator(pilot);

      LCD.clear();
      LCD.drawInt(5, 0, 0);
      LCD.drawInt(5, 5, 0);

      Waypoint wpA = new Waypoint(0,0);
      Waypoint wpB = new Waypoint(50, 50);
      Waypoint wpC = new Waypoint(-50, 50);

      navbot.goTo(wpB);
      navbot.addWaypoint(wpA);
      navbot.addWaypoint(wpC);
      navbot.addWaypoint(wpA);
      navbot.waitForStop();

  }//end class main
}//end RobotLabNav
