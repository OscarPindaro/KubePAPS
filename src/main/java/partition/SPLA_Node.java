package partition;

import java.util.Random;

public class SPLA_Node {

    private String nodeID;
    private String role;        //can be SPEAKER or LISTENER
    private String currentLabel;       //label currently selected by the node, set up checking the memory
    private Memory memory;      //used to save all the labels received by other nodes

    public void extractLabel(){
        int gaussianSum = individuals.size()*(individuals.size()-1)/2;
        int selectionValue = new Random().nextInt(gaussianSum);
        int sum= 0;
        int pos= 0;
        while( sum < selectionValue){
            pos++;
            sum += pos;
        }
        return new Individual(individuals.get(pos-1));
    }
}
