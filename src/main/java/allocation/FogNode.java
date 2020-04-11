package allocation;

import io.kubernetes.client.models.V1Node;

import java.util.Map;

public class FogNode {

    private final V1Node node;

    private long memoryCapacity;

    private Map<Long, Integer> linksDelay;

    private final int id;

    public FogNode(V1Node node , int id){
        this.node = node;
        this.id = id;
    }

    public int getID(){ return id; }

    public long getMemoryCapacity() {
        return memoryCapacity;
    }

    public void setMemoryCapacity(long capacity) {
        this.memoryCapacity = capacity;
    }

    @Override
    public String toString() {
        return "FogNode " + getID();
    }

    public void addLinkDelay(long id, int linkDelay) {
        linksDelay.put(id, linkDelay);
    }

    public int getLinkDelay(long id){
        return linksDelay.get(id);
    }
}
