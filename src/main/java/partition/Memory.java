package partition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Memory {

    //variables
    private final Map<String, Integer> memory;    //String will contain a label and integer will represent the number of occurrences.
    private int totLabelReceived;

    //constructor
    public Memory(String nodeID){

        this.totLabelReceived = 0;
        this.memory = new HashMap<String, Integer>();
        this.updateMemory(nodeID);

    }


    //getters and setters
    public int getTotLabelReceived() {
        return totLabelReceived;
    }

    private void updateTotLabelReceived() {
        this.totLabelReceived = totLabelReceived +1;
    }

    public Map<String, Integer> getMemory() { return new HashMap<String, Integer>(memory); }

    public void updateMemory(String label) {
        int previousValue;

        if (memory.containsKey(label)){
            previousValue = memory.get(label);
            memory.put(label, previousValue + 1);
        }
        else{
            memory.put(label, 1);
        }
        this.updateTotLabelReceived();
    }

    public int getOccurrences(String label) { return memory.get(label); }


    // FUNCTIONS

    // this function returns a list of all the labels occurring in the memory
    // sorted by number of occurrences and sorted in ascending order
    public List<String> returnSortedLabels() {
        List<String> sortedList;
        sortedList = new ArrayList<>(this.memory.keySet());
        sortedList.sort((a,b)->{
            if (memory.get(a) > memory.get(b)) {
                return 1;
            }
            else if(memory.get(a).equals(memory.get(b))){
                return 0;
            }
            else {
                return -1;
            }
        });
        return sortedList;
    }


/*    // debug test
    public static void main(String[] args) {
        Memory memoriaProva = new Memory("fabio");
        System.out.println(memoriaProva.getMemory());
        memoriaProva.updateMemory("fabio");
        memoriaProva.updateMemory("oscar");
        memoriaProva.updateMemory("oscar");
        memoriaProva.updateMemory("oscar");
        memoriaProva.updateMemory("enka");
        System.out.println(memoriaProva.getMemory());
        System.out.println(memoriaProva.getTotLabelReceived());
        System.out.println(memoriaProva.returnSortedLabels());

    }*/
}
