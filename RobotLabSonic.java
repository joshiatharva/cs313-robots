
import lejos.nxt.*;

public class RobotLabSonic {
  public static void main(String[] args) throws Exception {
    UltrasonicSensor sonar = new UltrasonicSensor(SensorPort.S3);
    while (true) {
        while (Sound.getTime() > 0)
            ;
        Sound.playTone(960, 500, 75);
        while (Sound.getTime() > 0)
            ;
        Sound.playTone(770, 500, 75);
    }
    /*
    while (!Button.ESCAPE.isDown()) {
      LCD.drawInt(sonar.getDistance(), 0, 0);
      LCD.refresh();

  }//end while*/

  }//end class main
}//end RobotLabSonic
