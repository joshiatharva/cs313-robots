package exits;

public class Node extends exits.Exit {
  private int x;
  private int y;
  private boolean visited;
  public int fvalue;
  public int gvalue;
  public Node parent;
  public Exit exits[];
  public int enterHeading;
  public int exitHeading;

  private final int error = 5;

  public Node () {
    visited = false;
    exits = new Exit[4];
  }

  public void setCoords(int x, int y){
    this.x = x;
    this.y = y;
  }

  public int getX(){
    return this.x;
  }

  public int getY(){
    return this.y;
  }

  public boolean isVisited(){
    return this.visited;
  }

  public void visit(){
    this.visited = true;
  }

  public int distanceFrom(Node M){
    return Math.abs((M.getX()-x)) + Math.abs((M.getY()-y));
  }

  public boolean equals(Node M){
    if (M.getX() >= this.x - error && M.getX() <= this.x + error)
      if (M.getY() >= this.y - error && M.getY() <= this.y + error)
        return true;
    return false;
  }

  public String toString() {
    return "("+ this.x + "," + this.y + ")";
  }

}
