import lejos.nxt.*;
import lejos.robotics.navigation.DifferentialPilot;

public class RobotLabMotor {

  public static void main(String[] args) {

      DifferentialPilot pilot;
      // First parameter defines wheel diameter in cm (May need to callibrate)
      // Second parameter defines track width (tyre to tyre) in cm (May need to callibrate)
      pilot = new DifferentialPilot(2.25f, 5.5f, Motor.A, Motor.C);
      UltrasonicSensor sonar = new UltrasonicSensor(SensorPort.S3);
      sonar.continuous(); // Force the sensor's continuous mode
      pilot.setTravelSpeed(5);
      int d1;

      pilot.forward();
      while (!Button.ESCAPE.isDown()) {
        d1 = sonar.getDistance();
        if (d1 < 20) {
            // Immediately (hopefully) stops the robot, reverses it, turns left and carries on
            pilot.quickStop();
            pilot.travel(-5);
            pilot.rotate(105);
            pilot.forward();
            continue;
        }
      }

      // Angle offset at +15 degrees
      // Hexagon: angle=75 degrees x6
      // for(int i = 0; i<6 ; i++) {
      //   pilot.travel(3);
      //   pilot.rotate(75);
      // }

  }//end class main
}//end RobotLab1
