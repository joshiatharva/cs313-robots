import lejos.nxt.*;
import lejos.robotics.navigation.DifferentialPilot;

public class TestDistance {

  public static void main(String[] args) {

      DifferentialPilot pilot;
      // First parameter defines wheel diameter in cm (May need to callibrate)
      // Second parameter defines track width (tyre to tyre) in cm (May need to callibrate)
      pilot = new DifferentialPilot(5.4f, 17.0f, Motor.A, Motor.C);
      pilot.setTravelSpeed(20);
      pilot.travel(9.75f);

      // Angle offset at +15 degrees
      // Hexagon: angle=75 degrees x6
      // for(int i = 0; i<6 ; i++) {
      //   pilot.travel(3);
      //   pilot.rotate(75);
      // }

  }//end class main
}//end RobotLab1
