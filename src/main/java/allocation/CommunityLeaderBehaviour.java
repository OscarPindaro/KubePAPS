package allocation;

import service.Service;
import service.ServiceCatalog;
import service.ServiceWorkload;

import java.util.Set;
import java.util.stream.DoubleStream;
import  it.polimi.ppap.solver.OplModSolver;

public class CommunityLeaderBehaviour {

    final MemberStateHolder memberStateHolder;

    public CommunityLeaderBehaviour(MemberStateHolder memberStateHolder) {
        this.memberStateHolder = memberStateHolder;
    }


    //TODO pid non viene usato e non capisco  a cosa serva, forse lo tolgo
    public void analize(final FogNode node, final int pid) {
        memberStateHolder.getNodeServiceDemand().clear();
        for(FogNode member : memberStateHolder.getNodeServiceWorkload().keySet()){
            for(Service service : ServiceCatalog.getServiceCatalog()) {
                if(memberStateHolder.getNodeServiceWorkload().get(member).containsKey(service)) {
                    //average
                    float aggregateWorkload = getAverageWorkload(memberStateHolder.getNodeServiceWorkload().get(member).get(service));
                    if(aggregateWorkload > 0)
                        updateDemandFromStaticAllocation(
                                member,
                                service,
                                aggregateWorkload);
                    else
                        memberStateHolder.updateServiceDemand(member, service, 0);
                }
            }
            memberStateHolder.getNodeServiceWorkload().get(member).clear();
        }
    }

    private float getAverageWorkload(Set<ServiceWorkload> serviceWorkloads){
        DoubleStream workloadStream = serviceWorkloads.stream().mapToDouble(e -> e.getWorkload());
        int total = serviceWorkloads.size();
        Double frequencySum = workloadStream.sum();
        return frequencySum.floatValue() / total;
    }

    //TODO decidere se le richieste del workload le fa il leader o sono i membri che gliele mandano
    private void updateDemandFromStaticAllocation(final FogNode member, final Service service,final float workload){
        final float demandFromMember = NodeFacade.getStaticAllocation(workload, service.getRT(), memberStateHolder.getReferenceControlPeriod());
        memberStateHolder.updateServiceDemand(member, service, demandFromMember);
    }

    /**
     * c'era anche un parametro pid ma lo sto ignorando
     * @param node
     */
    public void plan(FogNode node){
        solvePlacementAllocation();
        sendPlanToMembers(node);
    }

    private void solvePlacementAllocation(){
        OplModSolver oplModSolver = new it.polimi.ppap.solver.OplModSolver();
        oplModSolver.generateData(ServiceCatalog.getServiceCatalog(), memberStateHolder.getNodeServiceDemand(), memberStateHolder.getOptimizationBeta());
        try {
            memberStateHolder.setNodeServiceAllocation(oplModSolver.solve(memberStateHolder.getNodeServiceDemand(), false));
        } catch (it.polimi.ppap.solver.OplModSolver.OplSolutionNotFoundException ex){
            memberStateHolder.setNodeServiceAllocation(oplModSolver.solve(memberStateHolder.getNodeServiceDemand(), true));
        }
    }

    //TODO chiamate di rete, dovremo usare un'interfaccia per ora
    private void sendPlanToMembers(){

    }
}

