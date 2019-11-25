import exits.*;
import lejos.nxt.*;
import lejos.robotics.navigation.*;
import java.util.*;

public class RobotMaze {
  static LightSensor rLight;
  static LightSensor lLight;
  static DifferentialPilot pilot;
  static Navigator nav;
  static UltrasonicSensor sonar;

  public static Node Start;
  public static Node curNode;
  public static Node prevNode;
  public static int Heading;
  public static ArrayList<Node> nodeList;

  static int NORTH = 0;
  static int EAST = 1;
  static int SOUTH = 2;
  static int WEST = 3;

  // Light Sensor data and variables
  static final int white = 60;
  static final int black = 42;
  static boolean leftOnBlack;
  static boolean rightOnBlack;

  // Obstacle Sensor constants
  static final int obsRange = 10;

  public static void main(String[] args){
    init();
    /* Setup
      - Create start node
      - Set heading to North (Assuming robot starts pointing north)
    // */
    Start = new Node();
    curNode = Start;
    Heading = NORTH;
    while (!Button.ESCAPE.isDown()){
      /*
      When on junction
      - Check if an already searched node (Need to find way of detecting this)
      - Search left and right for exits, if not already searched
      */
      curNode.setCoords(getRobotX(),getRobotY());
      Node temp = getExistingNode(curNode);
      if (temp == null) {
        nodeList.add(curNode);
        searchNode(curNode);
      } else {
        curNode = temp;
      }
      System.out.println(nodeList);
      /*
        Select an unvisited exit to explore
      */
      boolean foundNewNode = false;

      for (int i=0; i < 4; i++){
        int newHeading = (Heading + i)%4;
        // System.out.println(i);
        // Look at each direction and find all unvisited exits (that are not obstacles)
        if (curNode.exits[newHeading] != null && !curNode.exits[newHeading].isVisited()){
          switch (i) {
            case 1: // Right
              turnRight();
              System.out.println("Turn Right");
              break;
            case 2: // Back
              pilot.rotate(-180.0f);
              System.out.println("Turn Back");
              break;
            case 3: // Left
              turnLeft();
              System.out.println("Turn Left");
              break;
            case 0: // Forwards
              System.out.println("Go Forwards");
          }
          Heading = newHeading;
          foundNewNode = true;
          prevNode = curNode;
          curNode = (Node) curNode.exits[newHeading];
        }
      }
      /*
        If unvisited exit found, travel down it, otherwise backtrack
      */
      if (foundNewNode){
        pilot.travel(3.0f);
        curNode.exits[relToAbsTurn(2)] = prevNode;
        travelEdge();
      }

    }
  }

  public static void init(){
    rLight = new LightSensor(SensorPort.S1);
    lLight = new LightSensor(SensorPort.S4);
    pilot = new DifferentialPilot(5.4f, 17.0f, Motor.A, Motor.C);
    nav = new Navigator(pilot);
    sonar = new UltrasonicSensor(SensorPort.S3);
    pilot.setTravelSpeed(10);
    nodeList = new ArrayList<Node>();
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

  // Turns robot left on a junction, updates heading
  public static void turnLeft(){
    pilot.travel(8.0f);
    pilot.rotate(90.0f);
    Heading = relToAbsTurn(-1);
  }

  // Turns robot right on a junction, updates heading
  public static void turnRight(){
    pilot.travel(8.0f);
    pilot.rotate(-90.0f);
    Heading = relToAbsTurn(1);
  }

  // Turns robot back from a junction, or from a completely visited node, updates heading
  public static void turnBack(){
    pilot.travel(-5.0f);
    pilot.rotate(180);
    Heading = relToAbsTurn(2);
  }

  public static void searchNode(Node N){
    pilot.steer(0);
    int forward = Heading;
    // Set left and right headings
    int left = relToAbsTurn(-1);
    int right = relToAbsTurn(1);
    // Move forward, turn left and look for exits
    pilot.travel(3.0f);
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
    pilot.travel(-3.0f);
    // Move back to original position
  }

  /* Travel down edge until a junction is found */
  public static void travelEdge(){
    while (true) {
      leftOnBlack = lightOnBlack(lLight);
      rightOnBlack = lightOnBlack(rLight);
      // When an obstacle encountered, turn back to prevNode
      if (hasObstacle()){
        Exit obsNode = (Exit) curNode;
        curNode = prevNode;
        obsNode = new Obstacle();
        turnBack();
      }
      if (!leftOnBlack && !rightOnBlack){  // Go forwards
        pilot.forward();
      } else if (leftOnBlack && !rightOnBlack){ // Turn left
        pilot.steer(175); // Steer smoothly turns the robot
        // pilot.rotate(5);
      } else if (!leftOnBlack && rightOnBlack) { // Turn right
        pilot.steer(-175);
        // pilot.rotate(-5);
      } else{  // On junction (Sometimes can be a left/right only turn)
        // Check if only left/right turn
        Node testNode = new Node(); // Temp node for testing junction
        int exitCount = 0;
        boolean isLeftTurn = false;
        searchNode(testNode);
        // Counts the number of exits from test node
        for (int i=0; i<testNode.exits.length; i++){
          if (testNode.exits[i] != null){
            exitCount++;
            // Turn set to most recent exit detected
            if (i == (Heading - 1 < 0?3:Heading-1))
              isLeftTurn = true;
            else
              isLeftTurn = false;
          }
        }
        // Left/right only turn if only one exit found
        if (exitCount == 1){
          if (isLeftTurn)
            turnLeft();
          else
            turnRight();
          continue;
        // Otherwise, it is a junction
        } else {
          pilot.stop();
          break;
        }
      }
    }
  }

  /* Deprecated */
  public static void reverseEdge(){
    while (true) {
      leftOnBlack = lightOnBlack(lLight);
      rightOnBlack = lightOnBlack(rLight);
      if (!leftOnBlack && !rightOnBlack){  // Go forwards
        pilot.backward();
      } else if (leftOnBlack && !rightOnBlack){ // Reverse anticlockwise
        pilot.rotate(5);
      } else if (!leftOnBlack && rightOnBlack) { // Reverse clockwise
        pilot.rotate(-5);
      } else{  // On junction
        // Check if only left/right turn
        Node testNode = new Node();
        int exitCount = 0;
        boolean isLeftTurn = false;
        pilot.travel(15.0f);
        pilot.rotate(180.0f);
        searchNode(testNode);
        pilot.rotate(180.0f);
        pilot.travel(-15.0f);
        // for (int i=0; i<testNode.exits.length; i++){
        //   if (testNode.exits[i] != null){
        //     exitCount++;
        //     if (i == (Heading - 1 < 0?3:Heading-1))
        //       isLeftTurn = true;
        //     else
        //       isLeftTurn = false;
        //   }
        // }
        // if (exitCount == 1){
        //   if (isLeftTurn)
        //     turnLeft();
        //   else
        //     turnRight();
        //   continue;
        // } else {
          pilot.stop();
          break;
        // }
      }
    }
  }

  /* Gives an absolute heading from relative turns (positive = right, negative = left) */
  public static int relToAbsTurn(int relTurn){
    int absHead;
    if (relTurn < 0)
      absHead = Heading + relTurn < 0?3:Heading + relTurn;
    else
      absHead = Heading + relTurn > 3?0:Heading + relTurn;
    return absHead;
  }

  public static Node getExistingNode(Node N){
    for (Node M : nodeList){
      if (M.equals(N))
        return M;
    }
    return null;
  }

  public static boolean hasObstacle() {
    int d1 = sonar.getDistance();
    if (d1 < obsRange) {
      return true;
    }
    return false;
  }

  // Goal node has always got only South and West exits
  public static boolean isGoal(Node N) {
    if (N.exits[WEST] != null && N.exits[SOUTH] != null){
      return true;
    }
    return false;
  }
}
