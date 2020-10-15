package rldevs4j;

import rldevs4j.testenv.TestEnvApi;

public class Main {
    public static void main(String[] args) {
        if ("testenv".equals(args[0].toLowerCase())) {
            TestEnvApi.main(args);
        }
    }
}
