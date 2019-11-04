import lejos.nxt.*;
import lejos.robotics.navigation.DifferentialPilot;

public class RobotLabLight {
  static int white = 60;
  static int black = 42;
  public static void main(String[] args) throws Exception {
      LightSensor rLight = new LightSensor(SensorPort.S1);
      LightSensor lLight = new LightSensor(SensorPort.S4);
      DifferentialPilot pilot;
      // Need to callibrate first 2 args (wheel diameter and track width)
      pilot = new DifferentialPilot(5.4f, 17.0f, Motor.A, Motor.C);
      boolean leftOnBlack;
      boolean rightOnBlack;
      pilot.setTravelSpeed(10);

      while (!Button.ESCAPE.isDown()) {
        leftOnBlack = lightOnBlack(lLight);
        rightOnBlack = lightOnBlack(rLight);
        if (!leftOnBlack && !rightOnBlack){  // Go forwards
          pilot.forward();
        } else if (leftOnBlack && !rightOnBlack){ // Turn left
          pilot.steer(200); // Try the steer method
        } else if (!leftOnBlack && rightOnBlack) { // Turn right
          pilot.steer(-200);
        } else{  // On junction
          pilot.stop();
          continue;
        }
      } //end while

    }//end class main

  public static boolean lightOnBlack(LightSensor sensor){
    int senseVal = sensor.getLightValue();
    if (senseVal <= black + 10)
      return true;
    else
      return false;
  }
}//end RobotLabLight
