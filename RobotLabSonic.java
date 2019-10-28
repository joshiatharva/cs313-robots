import lejos.nxt.*;

public class RobotLabSonic {
  public static void main(String[] args) throws Exception {
    UltrasonicSensor sonar = new UltrasonicSensor(SensorPort.S3);

    // Outputs 10 at 8cm
    while (!Button.ESCAPE.isDown()) {
      LCD.clear();
      LCD.drawInt(sonar.getDistance(), 0, 0);
      try {
        Thread.sleep(100);
      } catch(InterruptedException ex) {
        Thread.currentThread().interrupt();
      }
    }//end while

  }//end class main
}//end RobotLabSonic
