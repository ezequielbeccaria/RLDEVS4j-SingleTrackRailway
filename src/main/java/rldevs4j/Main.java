package rldevs4j;

import rldevs4j.singletrackrailway.a3c.SimpleThreeStopsRailwayRandomDelayA3CTrain;
import rldevs4j.singletrackrailway2.a3c.SimpleThreeStopsRailway10DelayA3CTrain;
import rldevs4j.testenv.TestEnvApi;
import rldevs4j.singletrackrailway2.ppo.SimpleThreeStopsRailway10DelayPPOTrain;
import rldevs4j.singletrackrailway2.ppo.SimpleThreeStopsRailwayRandomDelayPPOTrain;

public class Main {
    public static void main(String[] args) {
        switch (args[0].toLowerCase()) {
            case "testenv":
                TestEnvApi.main(args);
                break;
            case "10min_delay_ppo":
                SimpleThreeStopsRailway10DelayPPOTrain.main(args);
            case "random_delay_ppo":
                SimpleThreeStopsRailwayRandomDelayPPOTrain.main(args);
            case "10min_delay_a3c":
                SimpleThreeStopsRailway10DelayA3CTrain.main(args);
            case "random_delay_a3c":
                SimpleThreeStopsRailwayRandomDelayA3CTrain.main(args);
            default:
                throw new AssertionError();
        }
    }
}
