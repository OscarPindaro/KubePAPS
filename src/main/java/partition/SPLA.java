package partition;

import io.kubernetes.client.models.V1Node;
import kubernetesApiWrapper.KubeApi;

import java.util.Comparator;
import java.util.List;

public class SPLA {

    private List<SPLA_Node> topologyNodes;

    public SPLA(float[][] delayMatrix, float delayThreshold) throws io.kubernetes.client.ApiException {

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


}

