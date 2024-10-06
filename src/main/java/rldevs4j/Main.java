package rldevs4j;

import rldevs4j.singletrackrailway2.api.EnvNoDelay;
import rldevs4j.singletrackrailway2.api.EnvRandomDelay;
import rldevs4j.singletrackrailway2.api.EnvT1D10;

public class Main {
    public static void main(String[] args) {
        switch (args[0].toLowerCase()) {
            case "t1d10":
                EnvT1D10.main(args);
            case "no_delay":
                EnvNoDelay.main(args);
            case "random_delay":
                EnvRandomDelay.main(args);
            default:
                throw new AssertionError();
        }
    }
}
