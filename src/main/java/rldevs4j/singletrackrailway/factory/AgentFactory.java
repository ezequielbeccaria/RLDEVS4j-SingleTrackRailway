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

    public static rldevs4j.agents.ppov2.PPO ppoDiscrete(Map<String,Object> params, EnvironmentFactory envFactory){
        rldevs4j.agents.ppov2.FFCritic critic = new rldevs4j.agents.ppov2.FFCritic(
                (int) params.get("OBS_DIM"),
                (double) params.get("LEARNING_RATE"),
                (double) params.getOrDefault("L2", 0.001D),
                (float) params.getOrDefault("EPSILON_CLIP", 0.2F),
                (int) params.get("HIDDEN_SIZE"));
        rldevs4j.agents.ppov2.FFDiscreteActor actor = new rldevs4j.agents.ppov2.FFDiscreteActor(
                (int) params.get("OBS_DIM"),
                (int) params.get("ACTION_DIM"),
                (double) params.get("LEARNING_RATE"),
                (double) params.getOrDefault("L2", 0.001D),
                (float) params.getOrDefault("ENTROPY_FACTOR", 0.001F),
                (float) params.getOrDefault("EPSILON_CLIP", 0.2F),
                (int) params.get("HIDDEN_SIZE"));
        return new rldevs4j.agents.ppov2.PPO(actor, critic, new MinMaxScaler((double[])params.get("OBS_MIN"), (double[])params.get("OBS_MAX")), envFactory, params);
    }

    public static Agent ppo(Map<String,Object> params){
        PPOActor actor = new ContinuousActionActorFixedStd(params);
        PPOCritic PPOCritic = new PPOCritic(params);
        return new ProximalPolicyOptimization("PPO", new MinMaxScaler((double[])params.get("OBS_MIN"), (double[])params.get("OBS_MAX")), actor, PPOCritic, params);
    }

    public static Agent ppo_test(String modelPath, Map<String,Object> params){
        try {
            return new ContinuousActionActorTest("PPO", new MinMaxScaler((double[])params.get("OBS_MIN"), (double[])params.get("OBS_MAX")), modelPath, (Double) params.get("TAHN_ACTION_LIMIT"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        return null;
    }

    public static Agent ddqn(Map<String,Object> params){
        Model model = new Model(params);
        return new DDQN("DDQN", new MinMaxScaler((double[])params.get("OBS_MIN"), (double[])params.get("OBS_MAX")), model, params);
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
                (int) params.get("HIDDEN_SIZE"));
        return new A3C(actor, critic, new MinMaxScaler((double[])params.get("OBS_MIN"), (double[])params.get("OBS_MAX")), envFactory, params);
    }
}
