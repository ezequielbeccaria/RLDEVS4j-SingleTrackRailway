package rldevs4j.singletrackrailway.factory;

import java.util.Map;
import rldevs4j.base.agent.Agent;
import rldevs4j.base.agent.preproc.MinMaxScaler;
import rldevs4j.base.agent.preproc.NoPreprocessing;
import rldevs4j.agents.dummy.DummyAgent;
import rldevs4j.agents.ppo.ContinuousActionActor;
import rldevs4j.agents.ppo.Critic;
import rldevs4j.agents.ppo.ProximalPolicyOptimization;

/**
 *
 * @author Ezequiel Beccaria
 */
public class AgentFactory {   

    public static Agent dummy(){
        return new DummyAgent("dummy_agent", new NoPreprocessing());
    }
    
    public static Agent ppo(Map<String,Object> params){             
        double[] minFeatureValues = {0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D};
        double[] maxFeatureValues = {14000D, 1666.7D, 14000D, 1666.7D, 2D, 2D, 2D, 2D, 2D, 2D, 2D, 2D, 2D, 2D, 1D, 1D, 1D, 1D, 1D, 1D, 1D, 1D, 1D, 1D, 1444};
        
        assert minFeatureValues.length == maxFeatureValues.length;
        
        ContinuousActionActor actor = new ContinuousActionActor(params);
        Critic critic = new Critic(params);
        return new ProximalPolicyOptimization("PPO", new MinMaxScaler(minFeatureValues, maxFeatureValues), actor, critic, params);
    }
}
