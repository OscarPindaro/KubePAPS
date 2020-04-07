package Partition;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.models.V1Node;
import io.kubernetes.client.models.V1NodeBuilder;
import io.kubernetes.client.models.V1ObjectMeta;
import io.kubernetes.client.proto.V1;

import java.util.LinkedList;
import java.util.List;

public class Community {

    private final static String ROLE_KEY= "role";
    private final static String COMMUNITY_KEY= "community";

    private final String name;

    private V1Node leader;

    private final List<V1Node> members;

    public Community(String name){
        this.name = name;
        leader = null;
        members = new LinkedList<V1Node>();
    }

    public void addLeader(V1Node node){
        this.leader = node;
        setRoleLabel(node, Role.LEADER);
    }

    public void setRoleLabel(V1Node node, Role role){
        V1ObjectMeta metadata = node.getMetadata();
        metadata = metadata.putLabelsItem(ROLE_KEY, role.toString()).putLabelsItem(COMMUNITY_KEY, this.name);
        node.setMetadata(metadata);
    }


    public void addMember(V1Node member){
        if (members.contains(member)) throw new RuntimeException("This member is already present in the community");
        members.add(member);
        setRoleLabel(member, Role.MEMBER);
    }

    public void loadOnKube(){
        loadLeader();
        loadMembers();
    }

    private void loadLeader(){

    }

    private void loadMembers(){

    }
}
