package service;

import allocation.FogNode;

public class ServiceDemand {

    private final FogNode source;
    private final Service service;
    private float demand;

    //there was in the original implementation a field called link delay, but i think it's a thing of peerSim

    public ServiceDemand(FogNode source, Service service, float demand){
        this.source = source;
        this.service = service;
        this.demand = demand;
    }

    public FogNode getSource() {
        return source;
    }

    public Service getService() {
        return service;
    }

    public float getDemand() {
        return demand;
    }


    //TODO capire se viene usato o meno questo metodo
/*    @Override
    public int compareTo(Object o) {
        ServiceDemand other = (ServiceDemand) o;
        String combinedId = demand + "_" + service.getId() + "_" + source.getID();
        String otherCombinedId = other.getDemand() + "_" + other.getService().getId() + "_" + other.getSource().getID();
        return combinedId.compareTo(otherCombinedId);
    }*/

    @Override
    public String toString() {
        return demand + " to " + service + " from " + source;
    }
}
