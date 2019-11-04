import lejos.nxt.*;

public class RobotLabSonic {
  public static void main(String[] args) throws Exception {
    UltrasonicSensor sonar = new UltrasonicSensor(SensorPort.S3);
    sonar.continuous();

    // Outputs 10 at 8cm
    while (!Button.ESCAPE.isDown()) {
      LCD.clear(0);
      LCD.drawInt(sonar.getDistance(), 0, 0);
      LCD.refresh();
    }//end while

  }//end class main
}//end RobotLabSonic
