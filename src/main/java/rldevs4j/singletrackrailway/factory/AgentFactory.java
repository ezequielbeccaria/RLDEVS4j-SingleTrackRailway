package rldevs4j.singletrackrailway.factory;

import java.io.IOException;
import java.util.Map;

import rldevs4j.agents.dqn.DDQN;
import rldevs4j.agents.dqn.Model;
import rldevs4j.agents.ppo.*;
import rldevs4j.base.agent.Agent;
import rldevs4j.base.agent.preproc.MinMaxScaler;
import rldevs4j.base.agent.preproc.NoPreprocessing;
import rldevs4j.agents.dummy.DummyAgent;

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

        Actor actor = new ContinuousActionActorFixedStd(params);
        Critic critic = new Critic(params);
        return new ProximalPolicyOptimization("PPO", new MinMaxScaler(minFeatureValues, maxFeatureValues), actor, critic, params);
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
}
