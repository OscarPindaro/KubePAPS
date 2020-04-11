package partition;

import io.kubernetes.client.ApiException;
import io.kubernetes.client.models.V1Node;
import kubernetesApiWrapper.KubeApi;

import java.util.*;

public class SPLA {

    private List<SPLA_Node> topologyNodes;

    // constructor
    public SPLA(float[][] delayMatrix, float delayThreshold) throws ApiException {

        List<V1Node> kubeNodes = KubeApi.getNodeList();
        kubeNodes.sort(Comparator.comparing(a -> a.getMetadata().getName()));

        if (!kubeNodes.isEmpty()) {
            for (V1Node node : kubeNodes) {
                topologyNodes.add(new SPLA_Node(node));
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


    public List<Community> computeCommunities(int numberOfIterations, float probabilityThreshold) {

        List<Community> returnCommunities = new LinkedList<>();
        CommunityBuilder communityBuilder = new CommunityBuilder();

        List<SPLA_Node> listenerOrder = topologyNodes;

        // algorithm evolution, spread labels for a given number of iterations, ideal number is 20 iterations
        for (int i = 0; i < numberOfIterations ; i++) {

            Collections.shuffle(listenerOrder);

            for (SPLA_Node listener : listenerOrder) {

                List<SPLA_Node> speakers = listener.getNearbyNodes();
                List<String> receivedLabels = new ArrayList<>(speakers.size());

                if (speakers.isEmpty()){
                    throw new NullPointerException("This listener has no nearby nodes, speaker list is empty");
                }
                else{
                    Collections.shuffle(speakers);
                }

                for (SPLA_Node speaker : speakers) {
                    receivedLabels.add(speaker.speak());
                }

                listener.addToMemory(SPLA_Node.listen(receivedLabels));
                listener.setLabelToSpread();

            }

        }

        // post processing to set the communities
        for (SPLA_Node node : topologyNodes) {

            Set<String> communityCandidates = node.getMemory().getDifferentReceivedLabels();

            communityCandidates.removeIf(label -> node.computeOccurrenceProbability(label) < probabilityThreshold);
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

            }
            else{
                throw new NullPointerException("The node doesn't belong to any community");
            }

        }

        return returnCommunities;
    }

}

