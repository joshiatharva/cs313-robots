import lejos.nxt.*;
import lejos.robotics.navigation.DifferentialPilot;

public class Ambulance {

  public static void main(String[] args) {

      DifferentialPilot pilot;
      pilot = new DifferentialPilot(5.4f, 17.0f, Motor.A, Motor.C);
      UltrasonicSensor sonar = new UltrasonicSensor(SensorPort.S3);
      sonar.continuous(); // Force the sensor's continuous mode
      pilot.setTravelSpeed(20); // I am speeeeeed!!!!!
      int d1;
      // Creates and starts the sounds thread
      Thread t1 = new Thread(new Sounds());
      t1.start();

      pilot.forward();
      while (!Button.ESCAPE.isDown()) { // Exits when the escape button (bottom middle) is pressed
        d1 = sonar.getDistance();
        if (d1 < 15) {
            // Immediately (hopefully) stops the robot, reverses it, turns left and carries on
            pilot.quickStop();
            pilot.travel(-5);
            pilot.rotate(105);
            pilot.forward();
            continue;
        }
      }
  }//end class main
}//end RobotLab1

// Makes cool sounds
class Sounds implements Runnable {
  public void run(){
    while(!Button.ESCAPE.isDown()){ // Exits when escape button pressed
      // Queues the 2 sounds, once when the other sound finishes
      while (Sound.getTime() > 0);
      Sound.playTone(960, 500, 60);
      while (Sound.getTime() > 0);
      Sound.playTone(770, 500, 60);
    }
  }
}
