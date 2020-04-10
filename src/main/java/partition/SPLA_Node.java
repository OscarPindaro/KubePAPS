package partition;

import io.kubernetes.client.models.V1Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class SPLA_Node {

    //variables
    private final V1Node kubeNode;
    private final String nodeID;
    private String labelToSpread;   //label that the node is going to spread, set up checking the memory
    private final Memory memory;    //used to save all the labels received by other nodes
    private final List<SPLA_Node> nearbyNodes;    //store all the connected nodes, used to set speakers

    //constructor
    public SPLA_Node(V1Node node) {

        this.kubeNode = node;
        this.nodeID = kubeNode.getMetadata().getName();
        this.labelToSpread = kubeNode.getMetadata().getName();
        this.memory = new Memory(kubeNode.getMetadata().getName());
        this.nearbyNodes = new LinkedList<>();

    }


    //getter and setters
    public V1Node getKubeNode() { return kubeNode; }

    public String getNodeID() { return nodeID; }

    public String getLabelToSpread() { return labelToSpread; }

    public void setLabelToSpread() {

        List<String> sortedLabels = memory.returnSortedLabels();
        int selectionValue = new Random().nextInt(memory.getTotLabelReceived());
        int sum = 0;
        int pos = 0;
        while( sum < selectionValue){
            pos++;
            sum += memory.getOccurrences(sortedLabels.get(pos-1));
        }

        this.labelToSpread = sortedLabels.get(pos-1);
    }

    public Memory getMemory() { return memory; }

    public void addToMemory(String labelToAdd) {
        this.memory.updateMemory(labelToAdd);
    }

    public List<SPLA_Node> getNearbyNodes() { return new LinkedList<SPLA_Node>(nearbyNodes); }

    public void addNearbyNode(SPLA_Node nearbyNode) { this.nearbyNodes.add(nearbyNode); }



    // functions
    public String speak() { return labelToSpread; }

    public String listen(List<String> receivedLabels) {

        return "TODO";
    }


}
