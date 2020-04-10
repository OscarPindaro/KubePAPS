package Partition;

import java.util.LinkedList;
import java.util.List;

public class SPLA_Node {

    //variables
    private final String nodeID;
    private String currentLabel;       //label that the node is going to spread, set up checking the memory
    private final Memory memory;      //used to save all the labels received by other nodes
    private final List<SPLA_Node> nearbyNodes;    //store all the connected nodes, used to set speakers

    //constructor
    public SPLA_Node(String nodeName) {

        this.nodeID = nodeName;
        this.currentLabel = nodeName;
        this.memory = new Memory(nodeName);
        this.nearbyNodes = new LinkedList<>();

    }

    //getter and setters
    public String getNodeID() { return nodeID; }

    public String getCurrentLabel() { return currentLabel; }

    public void computeCurrentLabel() {
        this.currentLabel = labelToSpread(this.memory);
    }

    public Memory getMemory() { return memory; }

    public void addToMemory(String nodeToAdd) {
        this.memory.updateMemory(nodeToAdd);
    }

    public List<SPLA_Node> getNearbyNodes() { return nearbyNodes; }

    public void setNearbyNodes(int numberOfNearbyNodes) {
        this.nearbyNodes = nearbyNodes;
    }

    public String labelToSpread(Memory memory) {

        String selectedlabel;

        return selectedlabel;
    }
}
