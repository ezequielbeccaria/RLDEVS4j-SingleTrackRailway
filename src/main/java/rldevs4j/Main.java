package rldevs4j;

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
            default:
                throw new AssertionError();
        }
    }
}
