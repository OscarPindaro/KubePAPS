package service;

import allocation.FogNode;

public class ServiceWorkload {
    private final FogNode source;
    private final Service service;
    private float workload;
    private float reference;

    public ServiceWorkload(FogNode source, Service service, float workload, float reference){
        this.source = source;
        this.service = service;
        this.workload = workload;
        this.reference = reference;
    }

    public Service getService() {
        return service;
    }

    public synchronized float getWorkload() {
        return workload;
    }

    public synchronized void setWorkload(float workload) {
        this.workload = workload;
    }

    public float getReference() {
        return reference;
    }

    public void setReference(float reference) {
        this.reference = reference;
    }

    public synchronized void disableWorkload(){
        this.reference = 0f;
        this.workload = 0;
    }

    public synchronized boolean isActive(){
        return this.workload > 0;
    }

    public FogNode getSource(){
        return source;
    }

    @Override
    public String toString() {
        return "Workload for " + service.getId() + ": " + workload + "ms";
    }
}
