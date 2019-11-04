import lejos.nxt.*;
import lejos.robotics.navigation.*;

class Robot {
  private DifferentialPilot pilot;
  private Navigator navbot;

  public Robot() {
    //set up the robot
    pilot = new DifferentialPilot(5.4f, 17.0f, Motor.A, Motor.C);
    //set up the navigator
    navbot = new Navigator(pilot);
  }

  public void goTo(Node S){
    navbot.goTo(new Waypoint(S.xPos, S.yPos));
  }

  public void addWaypoint(Node S){
    navbot.addWaypoint(new Waypoint(S.xPos, S.yPos));
  }

  public void travel(){
    Thread t1 = new Thread(new Sounds());
    t1.start();
    navbot.waitForStop();
  }

}

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
