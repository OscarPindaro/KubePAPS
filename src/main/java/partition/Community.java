package partition;

import io.kubernetes.client.ApiException;
import io.kubernetes.client.models.V1Node;
import io.kubernetes.client.models.V1ObjectMeta;
import kubernetesApiWrapper.KubeApi;

import java.util.LinkedList;
import java.util.List;

public class Community {

    private final static String ROLE_KEY= "role";
    private final static String COMMUNITY_KEY= "community";

    private String name;

    private V1Node leader;

    private final List<V1Node> members;

    public Community(String name){
        this.name = name;
        leader = null;
        members = new LinkedList<>();
    }

    public void addLeader(V1Node node){
        if (leader != null) throw  new RuntimeException("The leader was already set");
        this.leader = node;
        setRoleLabel(node, Role.LEADER);
        setCommunityLabel(node);
    }

    public void addMember(V1Node member){
        if (members.contains(member)) throw new RuntimeException("This member is already present in the community");
        members.add(member);
        setRoleLabel(member, Role.MEMBER);
        setCommunityLabel(member);
    }

    private void setRoleLabel(V1Node node, Role role){
        V1ObjectMeta metadata = node.getMetadata();
        metadata = metadata.putLabelsItem(ROLE_KEY, role.toString());
        node.setMetadata(metadata);
    }

    private void setCommunityLabel(V1Node node){
        V1ObjectMeta metadata = node.getMetadata();
        metadata = metadata.putLabelsItem(COMMUNITY_KEY, this.name);
        node.setMetadata(metadata);
    }

    public void setName(String newName){
        this.name = newName;
    }

    /**
     * @return a list containing members and the leader;
     */
    public List<V1Node> getAllMembers(){
        List<V1Node> allMembers = new LinkedList<>(members);
        allMembers.add(leader);
        return allMembers;
    }

    public void loadOnKube() throws ApiException {
        //load leader
        KubeApi.updateNode(leader);
        //load community members
        for(V1Node member : members){
            KubeApi.updateNode(member);
        }
    }


}
