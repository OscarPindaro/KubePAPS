package partition;

import io.kubernetes.client.models.V1Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class SPLA_Node {

    //variables
    private final String nodeID;
    private String currentLabel;       //label that the node is going to spread, set up checking the memory
    private final Memory memory;      //used to save all the labels received by other nodes
    private final List<SPLA_Node> nearbyNodes;    //store all the connected nodes, used to set speakers

    //constructor
    public SPLA_Node(V1Node node) {

        this.nodeID = node.getMetadata().getNamespace();
        this.currentLabel = node.getMetadata().getNamespace();
        this.memory = new Memory(node.getMetadata().getNamespace());
        this.nearbyNodes = new LinkedList<>();

    }

    //getter and setters
    public String getNodeID() { return nodeID; }

    public String getCurrentLabel() { return currentLabel; }

    public void computeCurrentLabel() {
        this.currentLabel = labelToSpread(memory.returnSortedLabels());
    }

    public Memory getMemory() { return memory; }

    public void addToMemory(String labelToAdd) {
        this.memory.updateMemory(labelToAdd);
    }

    public List<SPLA_Node> getNearbyNodes() { return nearbyNodes; }

    public void addNearbyNode(SPLA_Node nearbyNode) { this.nearbyNodes.add(nearbyNode); }




    public String labelToSpread(List<String> sortedLabels) {

        int selectionValue = new Random().nextInt(memory.getTotLabelReceived());
        int sum = 0;
        int pos = 0;
        while( sum < selectionValue){
            pos++;
            sum += memory.getOccurrences(sortedLabels.get(pos-1));
        }

        return sortedLabels.get(pos-1);
    }

}

