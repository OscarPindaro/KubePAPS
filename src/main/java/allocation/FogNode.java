package allocation;

import io.kubernetes.client.models.V1Node;

public class FogNode {

    private final V1Node node;

    public FogNode(V1Node node ){
        this.node = node;
    }
}
