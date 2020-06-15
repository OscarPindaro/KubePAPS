package partition;

import io.kubernetes.client.models.V1Node;
import io.kubernetes.client.models.V1ObjectMeta;
import io.kubernetes.client.proto.V1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class CommunityBuilder {

    private final Map<String, Community> map;

    public CommunityBuilder(){ this.map = new HashMap<>(); }

    // If a community with that label already exists, return that community. Otherwise create a new community.
    public Community getCommunity(String label) {

        if (map.containsKey(label)) {
            return map.get(label);
        }
        else {
            Community community = new Community("community" + map.size());
            map.put(label, community);
            return community;
        }

    }

    /**
     * Given a community, returns a list of community of given sizes. This method DOES NOT update the map of this community
     * builder
     * @param community community that has to be decomposed
     * @param maxSize max size that a community can have
     * @return communities with members from the original community, with size < maxSize
     */
    public static List<Community>  decomposeCommunity(Community community, int maxSize){
        List<V1Node> allMembers = community.getAllMembers();
        //if i have 12 members, and the max capacity is 5, the number of subCommunities is 12/5 + 1 = 2 + 1 = 3
        int nOfCommunities = allMembers.size() / maxSize;
        if(allMembers.size()%maxSize != 0){
            nOfCommunities += 1;
        }

        List<Community> communities = new ArrayList<>(nOfCommunities);
        //create nOfCommunities communities
        for(int i = 0; i < nOfCommunities; i++){
            Community newCommunity = new Community("");
            communities.add(newCommunity);
        }

        // ROUND ROBIN
        for(int i = 0; i < allMembers.size(); i++){
            Community selected = communities.get(i%nOfCommunities);
            if ( i < nOfCommunities){
                V1Node leader = allMembers.get(i);
                selected.setName(leader.getMetadata().getName());
                addLeader(selected,  leader);
            }
            else{
                addMember(selected, allMembers.get(i));
            }
        }

        return communities;
    }

    private static void addLeader(Community community, V1Node leader){
        community.addLeader(leader);
    }

    private static void addMember(Community community, V1Node member){
        community.addMember(member);
    }


    //TODO test da cancellareeee
    //sembra funzionare tutto, c'era un problema in cui il leader non aveva la label della community, ma risolto.
    public static void main1(String[] args) {
        List<V1Node> mockNodes = new LinkedList<>();
        for (int i = 1; i <= 15; i++) {
            V1Node node = new V1Node();
            node.setMetadata(new V1ObjectMeta());
            node.getMetadata().setName("Node" + i);
            mockNodes.add(node);
        }
        try {
            String contents = new String(Files.readAllBytes(Paths.get("/home/oscar/KubePAPS/networkProva.txt")));
            InputParser parser = new InputParser(contents);
            parser.parseFile();
            System.out.println("numberOfNodes: " + parser.getNumberOfNodes());
            System.out.println("numberOfIterations: " + parser.getNumberOfIterations());
            System.out.println("delayThreshold: " + parser.getDelayThreshold());
            System.out.println("probabilityThreshold: " + parser.getProbabilityThreshold());
            System.out.println("delayMatrix: " + parser.getDelayMatrix().length);
            System.out.println("nodes: " + parser.getNodes());
            System.out.println("maxSize: "+ parser.getMaxSize());

            SLPA partition = new SLPA(parser.getDelayMatrix(), parser.getDelayThreshold(), parser.getNodes(), mockNodes);
            List<Community> communities;
            communities = partition.computeCommunities(parser.getNumberOfIterations(), parser.getProbabilityThreshold(), parser.getMaxSize());
            for(Community c : communities){
                List<V1Node> nodes = c.getAllMembers();
                System.out.println("community: "+c.getName());
                for(V1Node v : nodes){
                    System.out.println(v.getMetadata().getName());
                }
                System.out.println();
            }

        } catch (IOException ex) {
            ex.notify();
        }

    }

    public static void main(String[] args) {
        int[] nodesNumber = {10, 20, 50, 100, 200, 500, 1000, 2000, 5000};
        float[] delayThreshold = {0.1f, 0.3f, 0.5f, 0.7f};
        int[] maxSize = {10, 25, 50 };
        Random random = new Random(1234567890);

        System.out.println("Number of nodes\tDelay Threshold\tMaxSize\tWorstTimeElapsed\tMediumTimeElapsed\tMediana");
        for(int nodes = 0; nodes < nodesNumber.length; nodes++){
            for (int delay = 0; delay < delayThreshold.length; delay++){
                for(int size = 0; size < maxSize.length; size++){
                    try{
                        if(maxSize[size] > nodesNumber[nodes]) throw new Exception("too little");
                        if ( maxSize[size] <= nodesNumber[nodes]){

                            //matrix creation
                            float[][] delayMatrix = new float[nodesNumber[nodes]][nodesNumber[nodes]];
                            for(int a =0; a < nodesNumber[nodes]; a++){
                                for(int b = a; b < nodesNumber[nodes]; b++){
                                    delayMatrix[a][b] = random.nextFloat();
                                }
                            }
                            //creatin of mock nodes
                            List<V1Node> mockNodes = new LinkedList<>();
                            for (int c = 1; c <= nodesNumber[nodes]; c++) {
                                V1Node node = new V1Node();
                                node.setMetadata(new V1ObjectMeta());
                                node.getMetadata().setName("Node" + c);
                                mockNodes.add(node);
                            }

                            long mean;
                            long sum = 0;
                            long worstCase = -1;
                            int TENTATIVI = 10;
                            long[] risultati = new long[TENTATIVI];
                            for( int tentativi = 0; tentativi< TENTATIVI; tentativi++){
                                long start = System.currentTimeMillis();
                                SLPA partition = new SLPA(delayMatrix, delayThreshold[delay], mockNodes.stream().map(s -> s.getMetadata().getName()).collect(Collectors.toList()), mockNodes);
                                partition.computeCommunities(20, 0.35f, maxSize[size]);
                                long end = System.currentTimeMillis();
                                long total = end -start;
                                sum = sum + total;
                                risultati[tentativi] = total;
                                if(total > worstCase){
                                    worstCase = total;
                                }
                            }

                            mean = sum / TENTATIVI;
                            long mediana = risultati[TENTATIVI/2];
                            System.out.println(nodesNumber[nodes]+"\t" + delayThreshold[delay] +"\t" + maxSize[size]+ "\t" + worstCase +"\t" + mean +"\t" + mediana);
                    }
                    }
                    catch (Exception ex){
                        System.out.println(nodesNumber[nodes]+"\t" + delayThreshold[delay] +"\t" + maxSize[size]+ "\t" + "XXX" +"\tYYY" +"\tZZZ");
                    }

                }
            }
        }




    }
}
