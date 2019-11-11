import lejos.nxt.*;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Navigator;
import java.io.FileOutputStream;

public class RobotLabLight {
  static int white = 60;
  static int black = 42;
  public static void main(String[] args) throws Exception {
      LightSensor rLight = new LightSensor(SensorPort.S1);
      LightSensor lLight = new LightSensor(SensorPort.S4);
      DifferentialPilot pilot;
      Navigator nav;

      // Need to callibrate first 2 args (wheel diameter and track width)
      pilot = new DifferentialPilot(5.4f, 17.0f, Motor.A, Motor.C);
      nav = new Navigator(pilot);
      boolean leftOnBlack;
      boolean rightOnBlack;
      pilot.setTravelSpeed(10);

      while (!Button.ESCAPE.isDown()) {
        leftOnBlack = lightOnBlack(lLight);
        rightOnBlack = lightOnBlack(rLight);
        if (!leftOnBlack && !rightOnBlack){  // Go forwards
          pilot.forward();
        } else if (leftOnBlack && !rightOnBlack){ // Turn left
          pilot.steer(175); // Try the steer method
        } else if (!leftOnBlack && rightOnBlack) { // Turn right
          pilot.steer(-175);
        } else{  // On junction
          pilot.stop();
          continue;
        }
        // LCD.clear(0);
        // LCD.drawInt(heading, 0, 0);
        // LCD.refresh();
        System.out.println(nav.getPoseProvider().getPose().getHeading());
      } //end while

    }//end class main

  public static boolean lightOnBlack(LightSensor sensor){
    int senseVal = sensor.getLightValue();
    if (senseVal >= white - 10)
      return false;
    return true;
  }
}//end RobotLabLight
