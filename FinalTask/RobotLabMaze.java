
import java.util.List;
import java.util.ArrayList;
import java.util.Stack;

// import lejos.nxt.*;
// import lejos.robotics.navigation.*;

public class RobotLabDFS {

  private static ArrayList<Node> path;
  private static Node goal;

  public static void main(String[] args) {

    ///////////////////////////////
    // 1. Setting up the tree structure
    ///////////////////////////////

    // create new nodes e.g.
    Node S = new Node("S", 0, 0);
    Node A = new Node("A", 30, 0);
    Node B = new Node("B", 30, 40);
    Node C = new Node("C", 30, 80);
    Node D = new Node("D", 60, 0);
    Node E = new Node("E", 60, 80);
    Node G = new Node("G", 90, 40);

    // add children e.g.
    S.addChild(A);
    S.addChild(B);
    A.addChild(S);
    A.addChild(B);
    A.addChild(D);
    B.addChild(S);
    B.addChild(A);
    B.addChild(C);
    C.addChild(B);
    C.addChild(E);
    D.addChild(A);
    D.addChild(G);
    E.addChild(C);
    G.addChild(D);

    Node currentNode = S; //initialise currentNode to start node S
    ///////////////////////////////
    // 2. Doing DFS
    ///////////////////////////////
    path = new ArrayList<Node>();
    goal = G;
    DFS(S);
    // for (Node u : path){
    //   System.out.println("("+u.toString()+")");
    // }

    Robot robot = new Robot();
    //////////////////////////////
    // 3. Making it work with a robot
    /////////////////////////////
    int i = 0;
    robot.goTo(path.get(i++));
    for (; i < path.size(); i++){
      robot.addWaypoint(path.get(i));
    }
    robot.travel();


  }//end class main

  /* visited[]
  if (visited[v] == false) {
    visited[v] = true
} for (u : v.children) {
  visit[u];
}
  */
  public static boolean DFS(Node S){
    if (!S.explored){
      S.explored = true;
      path.add(S);
      if (S.equals(goal))
        return true;
      for (Node u : S.children) {
        if (DFS(u))
          return true;
      }
      path.remove(S);
    }
    return false;
  }
}//end RobotLabDFS
