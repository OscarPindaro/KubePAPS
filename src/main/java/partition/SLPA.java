package partition;

import io.kubernetes.client.models.V1Node;
import kubernetesApiWrapper.KubeApi;

import java.util.*;

public class SLPA {

    private List<SLPA_Node> topologyNodes;  //list of all tje nodes present in the topology

    // constructor
    public SLPA(float[][] delayMatrix, float delayThreshold) throws io.kubernetes.client.ApiException {

        List<V1Node> kubeNodes = KubeApi.getNodeList();
        kubeNodes.sort(Comparator.comparing(a -> a.getMetadata().getName()));

        if (!kubeNodes.isEmpty()) {
            for (V1Node node : kubeNodes) {
                topologyNodes.add(new SLPA_Node(node));
            }
            computeTopologyMatrix(delayMatrix, delayThreshold);
        }
        else{
            throw new RuntimeException("Kubernetes getNodeList returned an empty list");
        }
    }


    // functions
    private void computeTopologyMatrix(float[][] delayMatrix, float delayThreshold){

        for (int i = 0; i < delayMatrix.length ; i++) {
            for (int j = i; j < delayMatrix[i].length; j++) {
                if (delayMatrix[i][j] < delayThreshold){
                    topologyNodes.get(i).addNearbyNode(topologyNodes.get(j));
                    topologyNodes.get(j).addNearbyNode(topologyNodes.get(i));
                }
            }
        }

    }

    // function used to create and set up Kubernetes communities, returns a list with all the communities created.
    // a community will contain information about members and leader.
    public List<Community> computeCommunities(int numberOfIterations, float probabilityThreshold) {

        List<Community> returnCommunities = new LinkedList<>();
        CommunityBuilder communityBuilder = new CommunityBuilder();

        List<SLPA_Node> listenerOrder = topologyNodes;

        // algorithm evolution, spread labels for a given number of iterations, ideal number is 20 iterations
        for (int i = 0; i < numberOfIterations ; i++) {

            Collections.shuffle(listenerOrder);     // shuffle the list to better spread labels

            for (SLPA_Node listener : listenerOrder) {

                List<SLPA_Node> speakers = listener.getNearbyNodes();
                List<String> receivedLabels = new ArrayList<>(speakers.size());

                if (speakers.isEmpty()){
                    throw new NullPointerException("This listener has no nearby nodes, speaker list is empty");
                }
                else{
                    Collections.shuffle(speakers);      // shuffle the list to better spread labels
                }

                for (SLPA_Node speaker : speakers) {
                    receivedLabels.add(speaker.speak());
                }

                listener.addToMemory(SLPA_Node.listen(receivedLabels));
                listener.setLabelToSpread();

            }

        }

        // post processing to set the communities
        for (SLPA_Node node : topologyNodes) {

            // get all the different labels received
            Set<String> communityCandidates = node.getMemory().getDifferentReceivedLabels();

            communityCandidates.removeIf(label -> node.computeOccurrenceProbability(label) < probabilityThreshold);

            // convert the set into an array to manipulate it
            String[] array = new String[communityCandidates.size()];
            communityCandidates.toArray(array);

            if (communityCandidates.size() == 1) {
                String communitySelected = array[0];
                Community selectedCommunity = communityBuilder.getCommunity(communitySelected);
                if (node.getNodeID().equals(communitySelected)){
                    selectedCommunity.addLeader(node.getKubeNode());
                }
                else{
                    selectedCommunity.addMember(node.getKubeNode());
                }

                if(!returnCommunities.contains(selectedCommunity)){
                    returnCommunities.add(selectedCommunity);
                }

            }
            else if (communityCandidates.size() > 1){
                // part of the code to manage overlapping nodes
                // in this case we just pick the first one of the list (same code as if case)
                String communitySelected = array[0];
                Community selectedCommunity = communityBuilder.getCommunity(communitySelected);
                if (node.getNodeID().equals(communitySelected)){
                    selectedCommunity.addLeader(node.getKubeNode());
                }
                else{
                    selectedCommunity.addMember(node.getKubeNode());
                }

                if(!returnCommunities.contains(selectedCommunity)){
                    returnCommunities.add(selectedCommunity);
                }

            }
            else{
                throw new NullPointerException("The node doesn't belong to any community");
            }

        }

        return returnCommunities;
    }

}

