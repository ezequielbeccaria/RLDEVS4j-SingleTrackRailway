package rldevs4j.singletrackrailway.factory;

import rldevs4j.agents.ac.A3C;
import rldevs4j.agents.ac.FFCritic;
import rldevs4j.agents.ac.FFDiscreteActor;
import rldevs4j.agents.dqn.DDQN;
import rldevs4j.agents.dqn.Model;
import rldevs4j.agents.dummy.DummyAgent;
import rldevs4j.agents.ppo.*;
import rldevs4j.base.agent.Agent;
import rldevs4j.base.agent.preproc.MinMaxScaler;
import rldevs4j.base.agent.preproc.NoPreprocessing;
import rldevs4j.base.env.factory.EnvironmentFactory;

import java.io.IOException;
import java.util.Map;

/**
 *
 * @author Ezequiel Beccaria
 */
public class AgentFactory {
    private static double[] minFeatureValues = {0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D};
    private static double[] maxFeatureValues = {14000D, 1666.7D, 14000D, 1666.7D, 2D, 2D, 2D, 2D, 2D, 2D, 2D, 2D, 2D, 2D, 1D, 1D, 1D, 1D, 1D, 1D, 1D, 1D, 1D, 1D, 1444};

    public static Agent dummy(){
        return new DummyAgent("dummy_agent", new NoPreprocessing());
    }


    public static Agent ppo(Map<String,Object> params){

        PPOActor actor = new ContinuousActionActorFixedStd(params);
        PPOCritic PPOCritic = new PPOCritic(params);
        return new ProximalPolicyOptimization("PPO", new MinMaxScaler(minFeatureValues, maxFeatureValues), actor, PPOCritic, params);
    }

    public static Agent ppo_test(String modelPath, double tahnActionLimit){
        try {
            return new ContinuousActionActorTest("PPO", new MinMaxScaler(minFeatureValues, maxFeatureValues), modelPath, tahnActionLimit);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        return null;
    }

    public static Agent ddqn(Map<String,Object> params){
        Model model = new Model(params);
        return new DDQN("DDQN", new MinMaxScaler(minFeatureValues, maxFeatureValues), model, params);
    }

    public static A3C a3cDiscrete(Map<String,Object> params, EnvironmentFactory envFactory){
        FFCritic critic = new FFCritic(
                (int) params.get("OBS_DIM"),
                (double) params.get("LEARNING_RATE"),
                (double) params.getOrDefault("L2", 0.001D),
                (int) params.get("HIDDEN_SIZE"));
        FFDiscreteActor actor = new FFDiscreteActor(
                (int) params.get("OBS_DIM"),
                (int) params.get("ACTION_DIM"),
                (double) params.get("LEARNING_RATE"),
                (double) params.getOrDefault("L2", 0.001D),
                (double) params.getOrDefault("ENTROPY_FACTOR", 0.001D),
                (int) params.get("HIDDEN_SIZE"),
                (double[][]) params.get("ACTION_SPACE"));
        return new A3C(actor, critic, new MinMaxScaler(minFeatureValues, maxFeatureValues), envFactory, params);
    }
}
