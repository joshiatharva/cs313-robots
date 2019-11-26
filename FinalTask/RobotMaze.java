import exits.*;
import lejos.nxt.*;
import lejos.robotics.navigation.*;
import java.util.*;
import java.io.*;

public class RobotMaze {
  // LeJos Hardware objects
  static LightSensor rLight;
  static LightSensor lLight;
  static DifferentialPilot pilot;
  static Navigator nav;
  static UltrasonicSensor sonar;

  // Graph explore objects
  public static Node Start;
  public static Node Goal;
  public static Node curNode;
  public static int Heading;
  public static int enterHeading;
  public static int exitHeading;
  public static boolean backtrack = false;
  public static ArrayList<Node> nodeList;
  public static Stack<Node> nodeStack;

  // Absolute bearings
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

  // Debug variables
  static int loopCount = 0;

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
      // If not backtracking, update new node
      if (!backtrack){
        curNode.setCoords(getRobotX(),getRobotY());
        // Check if node already exists, backtrack if so
        Node temp = getExistingNode(curNode);
        if (temp == null) {
          searchNode(curNode);
          nodeList.add(curNode);
        } else {
          // Stop exploring if no more nodes to backtrack to
          if (!startBacktrack())
            break;
          turnBack();
          travelEdge();
          continue;
        }
        curNode.enterHeading = Heading; // Heading made when entering node
      }
      // Dump debug data to file
      appendDebug("nodelist.txt", "Node List "+loopCount+": "+nodeList.toString()+"\n\n");
      appendDebug("nodestack.txt", "Node Stack "+loopCount+": "+nodeStack.toString()+"\n\n");
      loopCount++;
      /*
        Check if node is a goal node, set it as goal state if true
      */
      if (isGoal(curNode))
        Goal = curNode;
      /*
        Select first unvisited exit (Starting from Forward clockwise) to explore
      */
      boolean foundNewNode = false;
      for (int i=0; i < 4; i++){
        int newHeading = (Heading + i)%4;
        // Look at each direction and find all unvisited exits (that are not obstacles)
        if (curNode.exits[newHeading] != null && !curNode.exits[newHeading].isVisited()){
          switch (i) {
            case 1: // Right
              junctionTurnRight();
              System.out.println("Turn Right");
              break;
            case 3: // Left
              junctionTurnLeft();
              System.out.println("Turn Left");
              break;
            case 0: // Forwards
              System.out.println("Go Forwards");
          }
          curNode.exitHeading = Heading = newHeading;
          foundNewNode = true;
          nodeStack.push(curNode);
          curNode = (Node) curNode.exits[newHeading];
        }
      }
      /*
        If unvisited exit found, travel down it, otherwise turn back to previous node
      */
      if (foundNewNode){
        backtrack = false;
        pilot.travel(3.0f);
        curNode.exits[relToAbsTurn(2)] = nodeStack.peek();
        travelEdge();
      } else {
        // Face towards exit we've come from
        int turns = 0;
        while ((Heading + turns)%4 != (curNode.enterHeading+2)%4) turns++;
        if (turns > 0){
          junctionTurnRight();
          turns--;
          pilot.rotate(turns*-90.0f);
          Heading = (curNode.enterHeading+2)%4;
        }
        if (!startBacktrack())  // Stop exploring if no more nodes to backtrack to
          break;
        travelEdge();
      }
    }
    /*
      Using node map, find shortest path
    */

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

  // Write (Overwrite) debug info to file
  public static void writeDebug(String filename, String msg){
    BufferedWriter debugWriter = null;
    try {
      debugWriter = new BufferedWriter(new FileOutputStream(new File(filename)));
      for (int i=0; i < msg.length(); i++){
        if (msg.charAt(i) == '\n')
          debugWriter.newLine();
        else
          debugWriter.write(msg.charAt(i));
      }
      debugWriter.flush();
    } catch (IOException e) {
      e.printStackTrace();
    } finally { // Close file when done
      try {
        debugWriter.close();
      } catch (IOException f) {
        f.printStackTrace();
      }
    }
  }

  // Append debug info to file
  public static void appendDebug(String filename, String msg){
    BufferedWriter debugWriter = null;
    try {
      debugWriter = new BufferedWriter(new FileOutputStream(new File(filename), true));
      for (int i=0; i < msg.length(); i++){
        if (msg.charAt(i) == '\n')
          debugWriter.newLine();
        else
          debugWriter.write(msg.charAt(i));
      }
      debugWriter.flush();
    } catch (IOException e) {
      e.printStackTrace();
    } finally { // Close file when done
      try {
        debugWriter.close();
      } catch (IOException f) {
        f.printStackTrace();
      }
    }
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
  public static void junctionTurnLeft(){
    pilot.travel(8.0f);
    pilot.rotate(90.0f);
    Heading = relToAbsTurn(-1);
  }

  // Turns robot right on a junction, updates heading
  public static void junctionTurnRight(){
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
        startBacktrack();
        turnBack();
        curNode.exits[exitHeading] = new Obstacle();  // Change exit to obstacle
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
        Node testNode = new Node(); // Temp node for testing junction, deleted after use
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
            junctionTurnLeft();
          else
            junctionTurnRight();
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
        //     junctionTurnLeft();
        //   else
        //     junctionTurnRight();
        //   continue;
        // } else {
          pilot.stop();
          break;
        // }
      }
    }
  }

  // Start backtrack mode, return true if able to backtrack (If nodeStack has nodes)
  public static boolean startBacktrack(){
    backtrack = true;
    if (!nodeStack.empty())
      curNode = nodeStack.pop();
    else
      return false;
    return true;
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
