package partition;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class InputParser {

    private final File file;
    private final Scanner input;
    private final List<String> nodes;
    private float[][] delayMatrix;



//    public InputParser(String pathToFile, int numberOfNodes) throws FileNotFoundException {
//
//        this.file = new File(pathToFile);
//        this.input = new Scanner(file);
//        this.nodes = new LinkedList<>();
//        this.delayMatrix = new float[numberOfNodes][numberOfNodes];
//
//    }

    public InputParser(String inputString, int numberOfNodes){
        this.file = null;
        this.input = new Scanner(new String(inputString));
        this.nodes = new LinkedList<>();
        this.delayMatrix = new float[numberOfNodes][numberOfNodes];
    }


    public float[][] getDelayMatrix() { return delayMatrix; }

    public List<String> getNodes() { return nodes; }


    public void parseFile(){
        int i = 0;
        int j = 0;
        this.nodes.add(input.next());
        while (input.hasNext()) {
            if (input.hasNextFloat()) {
                this.delayMatrix[i][j] = input.nextFloat();
                j++;
            }
            else {
                this.nodes.add(input.next());
                j = 0;
                i++;
            }
        }
    }

    private static int countLines(String str){
        if(str == null){
            return -1;
        }
        int numLines = 0;
        for(int i=0; i < str.length(); i++){
            if(str.charAt(i) == '\n'){
                numLines++;
            }
        }
        return numLines;
    }



    public static void main(String[] args) {

        String path = "input.txt";
        System.out.println(countLines("pisello\nbello"));
        InputParser parser = new InputParser(path, 4);
        parser.parseFile();
        for (String node : parser.getNodes()) {
            System.out.println(node);
            float[] delays = parser.getDelayMatrix()[parser.getNodes().indexOf(node)];
            for (int i = 0; i < delays.length; i++) {
                System.out.println(delays[i]);
            }
        }
    }
}


