package rldevs4j.singletrackrailway;

import facade.DevsSuiteFacade;
import rldevs4j.base.env.Environment;
import rldevs4j.singletrackrailway.factory.SingleTrackRailwayEnvFactory;

/**
 *
 * @author Ezequiel Beccaria
 */
public class Test1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new Test1();
    }

    public Test1() {
        SingleTrackRailwayEnvFactory factory = new SingleTrackRailwayEnvFactory();
        Environment env = factory.createEnv01();
        
        DevsSuiteFacade facade = new DevsSuiteFacade(env);
        
        facade.simulateToTime(50);
    }
    
}
