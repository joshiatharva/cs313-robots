import java.util.*;

class Node {
    public String name; //name of node
    public Node pred; //predecessor
    public Boolean explored = false; //whether node has been explored
    public ArrayList<Node> children = new ArrayList<>(); //stores children
    public int xPos, yPos; //coordinates of the node in cartesian space

    //constructor
    public Node(String name, int xPos, int yPos) {
        this.name = name;
        this.xPos = xPos;
        this.yPos = yPos;
        this.explored = false;
        this.children = new ArrayList<Node>();
    }

    public String toString(){
        return this.name + ", " + this.xPos + ", " + this.yPos;
    }

    public void addChild(Node node) {
        this.children.add(node);
        node.pred=this;
    }

    public boolean equals(Node V){
      return this.name.equals(V.name);
    }

}
