package rldevs4j.singletrackrailway;

import facade.DevsSuiteFacade;
import rldevs4j.base.env.Environment;
import rldevs4j.singletrackrailway.factory.SingleTrackRailwayEnvFactory;

/**
 *
 * @author ezequiel
 */
public class Test2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new Test2();
    }

    public Test2() {
        SingleTrackRailwayEnvFactory factory = new SingleTrackRailwayEnvFactory();
        Environment env = factory.createEnv02();
        env.initialize(); //initialize model state
        DevsSuiteFacade facade = new DevsSuiteFacade(env);
        
        facade.simulateToTime(3000);
    }
    
}
