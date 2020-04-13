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



    public InputParser(String pathToFile) throws FileNotFoundException {
        this.file = new File(pathToFile);
        this.input = new Scanner(file);
        this.nodes = new LinkedList<>();
        this.delayMatrix = new float[4][5];

        this.ParseFile();
    }


    public float[][] getDelayMatrix() { return delayMatrix; }

    public List<String> getNodes() { return nodes; }


    public void ParseFile(){
        int i = 0;
        int j = 0;
        this.nodes.add(input.next());
        System.out.println("nodes");
        System.out.println(nodes);
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


/*
    public static void main(String[] args) {

        String path = "/home/fabio/Scrivania/input.txt";
        try {
            InputParser parser = new InputParser(path);
            for (String node: parser.getNodes()) {
                System.out.println(node);
                float[] delays = parser.getDelayMatrix()[parser.getNodes().indexOf(node)];
                for (int i = 0; i < delays.length; i++) {
                    System.out.println(delays[i]);
                }
            }
        } catch (FileNotFoundException e) {
            e.notify();
        }

    }
*/

}


