import lejos.nxt.*;
import lejos.robotics.navigation.DifferentialPilot;

public class RobotLabMotor {

  public static void main(String[] args) {

      DifferentialPilot pilot;
      // First parameter defines wheel diameter in cm (May need to callibrate)
      // Second parameter defines track width (tyre to tyre) in cm (May need to callibrate)
      pilot = new DifferentialPilot(5.4f, 17.0f, Motor.A, Motor.C);
      UltrasonicSensor sonar = new UltrasonicSensor(SensorPort.S3);
      sonar.continuous(); // Force the sensor's continuous mode
      pilot.setTravelSpeed(20);
      int d1;

      pilot.forward();
      while (!Button.ESCAPE.isDown()) {
        d1 = sonar.getDistance();
        if (d1 < 15) {
            // Immediately (hopefully) stops the robot, reverses it, turns left and carries on
            pilot.stop();
            pilot.travel(-2.75); // Reverse back 3cm
            pilot.rotate(90);
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
