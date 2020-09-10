package rldevs4j.singletrackrailway.factory;

import org.deeplearning4j.api.storage.StatsStorage;
import org.nd4j.linalg.activations.Activation;
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
import rldevs4j.base.agent.preproc.Preprocessing;
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
                (double) params.get("LEARNING_RATE_CRITIC"),
                (double) params.getOrDefault("L2", 0.001D),
                (float) params.getOrDefault("EPSILON_CLIP", 0.2F),
                (int) params.get("HIDDEN_SIZE"),
                (StatsStorage) params.get("STATS_STORAGE"));
        rldevs4j.agents.ppov2.FFDiscreteActor actor = new rldevs4j.agents.ppov2.FFDiscreteActor(
                (int) params.get("OBS_DIM"),
                (int) params.get("ACTION_DIM"),
                (double) params.get("LEARNING_RATE_ACTOR"),
                (double) params.getOrDefault("L2", 0.001D),
                (float) params.getOrDefault("ENTROPY_FACTOR", 0.001F),
                (float) params.getOrDefault("EPSILON_CLIP", 0.2F),
                (int) params.get("HIDDEN_SIZE"),
                (StatsStorage) params.get("STATS_STORAGE"));
        return new rldevs4j.agents.ppov2.PPO(actor, critic, (Preprocessing) params.get("PREPROCESSING"), envFactory, params);
    }

    public static rldevs4j.agents.ppov2.PPO ppoLSTMDiscrete(Map<String,Object> params, EnvironmentFactory envFactory){
        rldevs4j.agents.ppov2.LSTMCritic critic = new rldevs4j.agents.ppov2.LSTMCritic(
                (int) params.get("OBS_DIM"),
                (double) params.get("LEARNING_RATE"),
                (double) params.getOrDefault("L2", 0.001D),
                (float) params.getOrDefault("EPSILON_CLIP", 0.2F),
                (int) params.get("HIDDEN_SIZE"),
                (StatsStorage) params.get("STATS_STORAGE"));
        rldevs4j.agents.ppov2.LSTMDiscreteActor actor = new rldevs4j.agents.ppov2.LSTMDiscreteActor(
                (int) params.get("OBS_DIM"),
                (int) params.get("ACTION_DIM"),
                (double) params.get("LEARNING_RATE"),
                (double) params.getOrDefault("L2", 0.001D),
                (float) params.getOrDefault("ENTROPY_FACTOR", 0.001F),
                (float) params.getOrDefault("EPSILON_CLIP", 0.2F),
                (int) params.get("HIDDEN_SIZE"),
                (StatsStorage) params.get("STATS_STORAGE"));
        return new rldevs4j.agents.ppov2.PPO(actor, critic, (Preprocessing) params.get("PREPROCESSING"), envFactory, params);
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
        return new DDQN("DDQN", (Preprocessing) params.get("PREPROCESSING"), model, params);
    }

    public static A3C a3cDiscrete(Map<String,Object> params, EnvironmentFactory envFactory){
        FFCritic critic = new FFCritic(
                (int) params.get("OBS_DIM"),
                (double) params.get("LEARNING_RATE"),
                (double) params.getOrDefault("L2", 0.001D),
                (int) params.get("HIDDEN_SIZE"),
                (Activation) params.get("HIDDEN_ACTIVATION"),
                (StatsStorage) params.get("STATS_STORAGE"));
        FFDiscreteActor actor = new FFDiscreteActor(
                (int) params.get("OBS_DIM"),
                (int) params.get("ACTION_DIM"),
                (double) params.get("LEARNING_RATE"),
                (double) params.getOrDefault("L2", 0.001D),
                (double) params.getOrDefault("ENTROPY_FACTOR", 0.001D),
                (int) params.get("HIDDEN_SIZE"),
                (Activation) params.get("HIDDEN_ACTIVATION"),
                (StatsStorage) params.get("STATS_STORAGE"));
        return new A3C(actor, critic, (Preprocessing) params.get("PREPROCESSING"), envFactory, params);
    }
}
