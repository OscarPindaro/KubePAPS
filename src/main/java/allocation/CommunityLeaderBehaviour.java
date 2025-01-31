package allocation;

import partition.Community;
import service.Service;
import service.ServiceCatalog;
import service.ServiceWorkload;

import java.util.Set;
import java.util.stream.DoubleStream;

import solver.OplModSolver;


public class CommunityLeaderBehaviour {

    final MemberStateHolder memberStateHolder;

    public CommunityLeaderBehaviour(MemberStateHolder memberStateHolder) {
        this.memberStateHolder = memberStateHolder;
    }


    //TODO pid non viene usato e non capisco  a cosa serva, forse lo tolgo
    public void analyze(final FogNode node, final int pid) {
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
        final float demandFromMember = CommunityLeaderBehaviour.getStaticAllocation(workload, service.getRT(), memberStateHolder.getReferenceControlPeriod());
        memberStateHolder.updateServiceDemand(member, service, demandFromMember);
    }


    /**
     * This method was originally in the NodeFacade class, but it depends only on the inputs given, so moving this method
     * in this class should not have any bad effect.
     * @param interArrivalMillis
     * @param targetResponseTimeMillis
     * @param controlPeriodMillis
     * @return
     */
    private static float getStaticAllocation(float interArrivalMillis, float targetResponseTimeMillis, float controlPeriodMillis){
        float req = controlPeriodMillis/interArrivalMillis;
        return Planner.computeStaticAllocation(req, targetResponseTimeMillis);
    }

    /**
     * c'era anche un parametro pid ma lo sto ignorando
     * @param node
     */
    public void plan(FogNode node, int pid){
        solvePlacementAllocation();
        sendPlanToMembers(node);
    }

    private void solvePlacementAllocation(){
        OplModSolver oplModSolver = new OplModSolver();
        oplModSolver.generateData(ServiceCatalog.getServiceCatalog(), memberStateHolder.getNodeServiceDemand(), memberStateHolder.getOptimizationBeta());
        try {
            memberStateHolder.setNodeServiceAllocation(oplModSolver.solve(memberStateHolder.getNodeServiceDemand(), false));
        } catch (OplModSolver.OplSolutionNotFoundException ex){
            memberStateHolder.setNodeServiceAllocation(oplModSolver.solve(memberStateHolder.getNodeServiceDemand(), true));
        }
    }

    //TODO chiamate di rete, dovremo usare un'interfaccia per ora
    private void sendPlanToMembers(FogNode node){

    }
}

