package allocation;

import io.kubernetes.client.models.V1Node;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Map;

public class CommunityProtocol {

    /**
     * questo metodo prima attiva il metodo monitor del nodo
     * in cui viene richiesto la local service workload history e la current
     * workload allocation. UNa volta ottenuti, questi oggetti vengono inviati al leader per processare le infos
     * //TODO forse qui metteremo delle interfacce di rete
     * se il nodo è pure leader, viene chiamato il metodo cyclic behaviour, ma nella loro implementazione è vuoto
     * @param node
     * @param pid
     */
    public void nextCycle(V1Node node, int pid){

    }

    public void processEvent(){

    }

    private void processMemberMessage(){

    }

    private void updateNodeServiceWorkload(){

    }

    private void storeNodeWorkloadAllocation(){

    }

    private boolean isAllMonitoringReceived(){
        throw new NotImplementedException();
    }

    private void processLeaderMessage(){

    }

}
