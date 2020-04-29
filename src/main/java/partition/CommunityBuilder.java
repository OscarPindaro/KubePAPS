package partition;

import io.kubernetes.client.models.V1Node;
import io.kubernetes.client.models.V1ObjectMeta;
import io.kubernetes.client.proto.V1;

import java.util.*;

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
    public static void main(String[] args) {

        Community community = new Community("prova");
        V1Node leader = new V1Node();
        leader.setMetadata(new V1ObjectMeta());
        leader.getMetadata().setName("leader");
        community.addLeader(leader);
        for(int i = 0; i < 4; i++){
            V1Node member = new V1Node();
            member.setMetadata(new V1ObjectMeta());
            member.getMetadata().setName("member" + i);
            community.addMember(member);
        }

        System.out.println(community.getAllMembers().size());
        List<Community> littleCommunities = CommunityBuilder.decomposeCommunity(community, 5);
        for(Community com: littleCommunities){
            List<V1Node> nodes = com.getAllMembers();
            System.out.println(com + "  SIZE: " + com.getAllMembers().size());
            for(V1Node node : nodes){
                System.out.println("name: " +node.getMetadata().getName() + "\nrole: " + node.getMetadata().getLabels());
            }
            System.out.println();
            
        }
    }
}
