
import lejos.nxt.*;
import lejos.robotics.navigation.DifferentialPilot;

public class RobotLabMotor {

  public static void main(String[] args) {

      DifferentialPilot pilot;s
      pilot = new DifferentialPilot(2.25f, 5.5f, Motor.A, Motor.C);
      UltrasonicSensor sonar = new UltrasonicSensor(SensorPort.S3);
      pilot.setTravelSpeed(100);
      int d1;

      while (!Button.ESCAPE.isDown()) {
          while (Sound.getTime() > 0) {
              d1 = sonar.getDistance();
              LCD.clear();
              LCD.drawInt(d1, 0, 0);
              if (d1 < 30) {
                  pilot.rotate(105);
                  break;
              }
              pilot.travel(10);
          }
          Sound.playTone(960, 500, 60);
          while (Sound.getTime() > 0) {
          }
          Sound.playTone(770, 500, 60);
    }
        // if (sonar.getRange() == 10) {
        //
        // } }

      // Angle offset at +15 degrees
      // Hexagon: angle=75 degrees x6
      // for(int i = 0; i<6 ; i++) {
      //   pilot.travel(3);
      //   pilot.rotate(75);
      //   }

  }//end class main
}//end RobotLab1
