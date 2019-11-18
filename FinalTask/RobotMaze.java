import exits.*;
import lejos.nxt.*;
import lejos.robotics.navigation.*;

public class RobotMaze {
  static LightSensor rLight;
  static LightSensor lLight;
  static DifferentialPilot pilot;
  static Navigator nav;

  public static Node Start;
  public static Node curNode;
  public static Node prevNode;
  public static int Heading;

  static int NORTH = 0;
  static int EAST = 1;
  static int SOUTH = 2;
  static int WEST = 3;

  // Light Sensor data and variables
  static final int white = 60;
  static final int black = 42;
  static boolean leftOnBlack;
  static boolean rightOnBlack;

  public static void main(String[] args){
    init();
    /* Setup
      - Create start node
      - Set heading to North (Assuming robot starts pointing north)
    */
    Start = new Node();
    Start.setCoords(getRobotX(),getRobotY());
    curNode = Start;
    Heading = NORTH;
    /*
      When on junction
      - Check if an already explored node (Need to find way of detecting this)
      - Search left and right for exits
    */
    while (!Button.ESCAPE.isDown()){
      searchNode(curNode);
      // nav.getPoseProvider().getPose().setHeading(0.0f);
      // System.out.println(getRobotAngle());
      // pilot.rotate(90.0f);
      // System.out.println(getRobotAngle());
      //
      /*
        Select an unvisited exit to explore
      */
      int i = 0;
      boolean foundNewNode = false;
      for (; i < 4 && !foundNewNode; i++){
        int newHeading = (Heading + i)%4;
        // Look at each direction and find unvisited exits
        if (curNode.exits[newHeading] != null && !curNode.exits[newHeading].isVisited()){
          switch (i) {
            case 1: // Right
              pilot.rotate(-90.0f);
              break;
            case 2: // Back
              pilot.rotate(-180.0f);
              break;
            case 3: // Left
              pilot.rotate(90.0f);
              break;
            case 0: // Forwards
          }
          Heading = newHeading;
          foundNewNode = true;
        }
      }
      /*
        If unvisited exit found, travel down it, otherwise backtrack
      */
      if (foundNewNode){
        travelEdge();
      }

    }
  }

  public static void init(){
    rLight = new LightSensor(SensorPort.S1);
    lLight = new LightSensor(SensorPort.S4);
    pilot = new DifferentialPilot(5.4f, 17.0f, Motor.A, Motor.C);
    nav = new Navigator(pilot);
    pilot.setTravelSpeed(10);
  }

  public static boolean lightOnBlack(LightSensor sensor){
    int senseVal = sensor.getLightValue();
    if (senseVal >= white - 10)
      return false;
    return true;
  }

  public static int getRobotX(){
    return (int) nav.getPoseProvider().getPose().getX();
  }

  public static int getRobotY(){
    return (int) nav.getPoseProvider().getPose().getY();
  }

  public static int getRobotAngle(){
    return (int) nav.getPoseProvider().getPose().getHeading();
  }

  public static void searchNode(Node N){
    int forward = Heading;
    // Set left and right headings
    int left = Heading - 1 < 0?3:Heading-1;
    int right = Heading + 1 > 3?0:Heading+1;
    // Move forward, turn left and look for exits
    pilot.travel(2.5f);
    pilot.rotate(45.0f, true);
    // Set forward and left exits
    while (pilot.isMoving()){
      leftOnBlack = lightOnBlack(lLight);
      rightOnBlack = lightOnBlack(rLight);
      if (leftOnBlack && N.exits[left] == null) {
        N.exits[left] = new Node();
        System.out.println("Left");
      }
      if (rightOnBlack && N.exits[forward] == null) {
        N.exits[forward] = new Node();
        System.out.println("Forward");
      }
    }
    // Turn right and look for exits
    pilot.rotate(-45.0f);
    pilot.rotate(-45.0f, true);
    // Set forward and right exits
    while (pilot.isMoving()){
      leftOnBlack = lightOnBlack(lLight);
      rightOnBlack = lightOnBlack(rLight);
      if (leftOnBlack && N.exits[forward] == null) {
        N.exits[forward] = new Node();
        System.out.println("Forward");
      }
      if (rightOnBlack && N.exits[right] == null) {
        N.exits[right] = new Node();
        System.out.println("Right");
      }
    }
    pilot.rotate(45.0f);
    pilot.travel(-2.5f);
    // Move back to original position
  }

  /* Travel down edge until a junction is found */
  public static void travelEdge(){
    while (true) {
      leftOnBlack = lightOnBlack(lLight);
      rightOnBlack = lightOnBlack(rLight);
      if (!leftOnBlack && !rightOnBlack){  // Go forwards
        pilot.forward();
      } else if (leftOnBlack && !rightOnBlack){ // Turn left
        pilot.steer(175); // Steer smoothly turns the robot
      } else if (!leftOnBlack && rightOnBlack) { // Turn right
        pilot.steer(-175);
      } else{  // On junction
        pilot.stop();
        break;
      }
    }
  }
}
