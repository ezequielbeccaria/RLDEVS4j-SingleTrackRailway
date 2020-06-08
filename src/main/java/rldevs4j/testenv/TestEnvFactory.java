package rldevs4j.testenv;

import rldevs4j.base.env.Environment;
import rldevs4j.base.env.factory.EnvironmentFactory;

public class TestEnvFactory implements EnvironmentFactory {
    @Override
    public Environment createInstance() {
        return new TestEnv();
    }
}
