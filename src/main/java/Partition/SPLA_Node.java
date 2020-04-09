package Partition;

public class SPLA_Node {

    private String nodeID;
    private String role;        //can be SPEAKER or LISTENER
    private String currentLabel;       //label currently selected by the node, set up checking the memory
    private Memory memory;      //used to save all the labels received by other nodes

}
