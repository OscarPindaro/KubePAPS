package allocation;

/**
 * This is a semplified version of the initial planner, contains only the static method
 */
public class Planner {

    private final static float A1_NOM = 0.1963f;
    private final static float A2_NOM = 0.002f;
    private final static float A3_NOM = 0.5658f;

    public static float computeStaticAllocation(float req, float targetResponseTimeMillis){
        return -req*(1000*A2_NOM-targetResponseTimeMillis/1000+A1_NOM)/(1000*A3_NOM*(targetResponseTimeMillis/1000-A1_NOM));
    }
}
