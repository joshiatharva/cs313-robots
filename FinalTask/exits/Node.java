package exits;

public class Node extends exits.Exit {
  private int x;
  private int y;
  private boolean visited;
  public Exit exits[];

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

}
