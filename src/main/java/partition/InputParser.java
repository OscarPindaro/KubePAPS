package partition;
import org.apache.commons.lang.ObjectUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class InputParser {

    //    private final File file;
    private final Scanner input;
    private final List<String> nodes;
    private float[][] delayMatrix;
    private int numberOfNodes;
    private float delayThreshold;
    private int numberOfIterations;
    private float probabilityThreshold;
    private int maxSize;


//    public InputParser(String pathToFile, int numberOfNodes) throws FileNotFoundException {
//
//        this.file = new File(pathToFile);
//        this.input = new Scanner(file);
//        this.nodes = new LinkedList<>();
//        this.delayMatrix = new float[numberOfNodes][numberOfNodes];
//
//    }

    public InputParser(String inputString) {
//        this.file = null;
        this.input = new Scanner(inputString);
        this.nodes = new LinkedList<>();
        this.numberOfIterations = 0;
        this.numberOfNodes = 0;
        this.delayThreshold = 0;
        this.probabilityThreshold = 0;
        this.maxSize = 0;

    }


    public float[][] getDelayMatrix() {
        return delayMatrix;
    }

    public int getNumberOfNodes() {
        return numberOfNodes;
    }

    public float getDelayThreshold() {
        return delayThreshold;
    }

    public int getNumberOfIterations() {
        return numberOfIterations;
    }

    public float getProbabilityThreshold() {
        return probabilityThreshold;
    }

    public List<String> getNodes() {
        return nodes;
    }

    public int getMaxSize() {
        return maxSize;
    }


    public void parseFile() {
        String nextParameter = "";

        while (input.hasNext()) {
            nextParameter = input.next();

            if (nextParameter.equals("#PARAMETERS")) {
                nextParameter = input.next();

                while (!(nextParameter.equals("#DELAY_MATRIX"))) {
                    switch (nextParameter) {
                        case "NumberOfNodes":
                            this.numberOfNodes = input.nextInt();
                            break;
                        case "DelayThreshold":
                            this.delayThreshold = input.nextFloat();
                            break;
                        case "NumberOfIterations":
                            this.numberOfIterations = input.nextInt();
                            break;
                        case "ProbabilityThreshold":
                            this.probabilityThreshold = input.nextFloat();
                            break;
                        case "MaxSize":
                            this.maxSize = input.nextInt();
                            break;
                        default:
                            throw new RuntimeException("unknown parameter");
                    }
                    nextParameter = input.next();
                }
            }

            if (nextParameter.equals("#DELAY_MATRIX")) {
                delayMatrix = new float[numberOfNodes][numberOfNodes];
                nextParameter = input.next();
                int i = 0;
                int j = 0;
                this.nodes.add(nextParameter);
                while (input.hasNext()) {
                    if (input.hasNextFloat()) {
                        this.delayMatrix[i][j] = input.nextFloat();
                        j++;
                    } else {
                        this.nodes.add(input.next());
                        j = 0;
                        i++;
                    }
                }
            }
        }

        if (nextParameter.equals("")) {
            throw new RuntimeException("Empty input file");
        }

    }


//    public static void main(String[] args) {
//
//        String path = "input.txt";
//        try {
//            InputParser parser = new InputParser(path, 4);
//            parser.parseFile();
//            for (String node: parser.getNodes()) {
//                System.out.println(node);
//                float[] delays = parser.getDelayMatrix()[parser.getNodes().indexOf(node)];
//                for (int i = 0; i < delays.length; i++) {
//                    System.out.println(delays[i]);
//                }
//            }
//        } catch (FileNotFoundException e) {
//            e.notify();
//        }
//
//    }

}


