package allocation;

import io.kubernetes.client.models.V1Node;
import network.CommunityMessage;
import network.MemberMessage;
import service.Service;
import service.ServiceCatalog;
import service.ServiceWorkload;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class CommunityProtocol extends MemberStateHolder{

    /**
     * questo metodo prima attiva il metodo monitor del nodo
     * in cui viene richiesto la local service workload history e la current
     * workload allocation. UNa volta ottenuti, questi oggetti vengono inviati al leader per processare le infos
     * //TODO forse qui metteremo delle interfacce di rete
     * se il nodo è pure leader, viene chiamato il metodo cyclic behaviour, ma nella loro implementazione è vuoto
     * @param node
     * @param pid
     */
    public void nextCycle(FogNode node, int pid){

    }

    public void processEvent(){

    }

    private void processMemberMessage(FogNode node, int pid, CommunityMessage msg){
        MemberMessage memberMessage = (MemberMessage) msg;
        updateNodeServiceWorkload(memberMessage.getSender(), memberMessage.getLocalServiceWorkloadHistory());
        storeNodeWorkloadAllocation(memberMessage.getSender(), memberMessage.getWorkloadAllocation());
        incMonitoringCount();
        if(isAllMonitoringReceived()) {
            communityLeaderBehaviour.analyze(node, pid);
            communityLeaderBehaviour.plan(node, pid);
            resetMonitoringCount();
        }
    }

    private void updateNodeServiceWorkload(FogNode sender, Map<Service, Set<ServiceWorkload>> localServiceWorkloadHistory){
        Map<Service, Set<ServiceWorkload>> nodeServiceWorkloads = nodeServiceWorkload.getOrDefault(sender, new TreeMap<>());
        nodeServiceWorkload.put(sender, nodeServiceWorkloads);
        for(Service service : ServiceCatalog.getServiceCatalog()) {
            Set<ServiceWorkload> serviceWorkloads = nodeServiceWorkload.get(sender).getOrDefault(service, new HashSet<>());
            if (localServiceWorkloadHistory.containsKey(service))
                for(ServiceWorkload serviceWorkload : localServiceWorkloadHistory.get(service))
                    serviceWorkloads.add(serviceWorkload);
            else
                serviceWorkloads.add(new ServiceWorkload(sender, service, 0f, 0f));

            nodeServiceWorkload.get(sender).put(service, serviceWorkloads);
        }
    }

    private void storeNodeWorkloadAllocation(FogNode sender, Map<Service, Map.Entry<Float, Float>> currentWorkloadAllocation){
        for(Service service : currentWorkloadAllocation.keySet()) {
            if(!workloadAllocationHistory.containsKey(service))
                workloadAllocationHistory.put(service, new TreeMap<>());
            Map.Entry<Float, Float> workloadAllocation = currentWorkloadAllocation.get(service);
            if(workloadAllocation.getValue() > 0)
                workloadAllocationHistory.get(service).put(1 / workloadAllocation.getKey(), workloadAllocation.getValue());
        }
    }

    private boolean isAllMonitoringReceived(){
       return getMonitoringCount() == getCommunitySize();
    }

    private void processLeaderMessage(){
        throw new NotImplementedException();
    }

}
