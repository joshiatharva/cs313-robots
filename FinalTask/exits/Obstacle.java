package exits;

public class Obstacle extends exits.Exit {
  // Obstacles are already visited
  public boolean isVisited() {
    return true;
  }
}
