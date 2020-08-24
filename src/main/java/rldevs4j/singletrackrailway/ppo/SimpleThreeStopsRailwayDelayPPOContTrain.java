package rldevs4j.singletrackrailway.ppo;

import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.nd4j.linalg.api.rng.Random;
import rldevs4j.agents.ppov2.PPO;
import rldevs4j.base.agent.preproc.MinMaxScaler;
import rldevs4j.base.agent.preproc.Preprocessing;
import rldevs4j.base.env.factory.EnvironmentFactory;
import rldevs4j.experiment.Experiment;
import rldevs4j.experiment.ExperimentResult;
import rldevs4j.singletrackrailway.factory.AgentFactory;
import rldevs4j.singletrackrailway.factory.SimpleThreeStopsRailwayFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ezequiel Beccaria
 */
public class SimpleThreeStopsRailwayDelayPPOContTrain extends Experiment{
    private final double EPISODE_MAX_TIME=3000;
    private final Map<String, Object> agentParams;
    protected UIServer uiServer;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Experiment exp = new SimpleThreeStopsRailwayDelayPPOContTrain();
        exp.run();

//        System.exit(0);
    }

    public SimpleThreeStopsRailwayDelayPPOContTrain() {
        super("PPOTrain", 1, false, true, "/home/ezequiel/experiments/SimpleThreeStopsRailway/", null);
        this.agentParams = new HashMap<>();
        this.agentParams.put("RESULTS_FILE_PATH", resultsFilePath);
        this.agentParams.put("OBS_DIM", 23);
        this.agentParams.put("LEARNING_RATE", 1e-5);
        this.agentParams.put("HIDDEN_SIZE", 128);
        this.agentParams.put("L2", 1e-3);
        this.agentParams.put("DISCOUNT_RATE", 0.995F);
        this.agentParams.put("LAMBDA_GAE", 0.96F);
        this.agentParams.put("HORIZON", Integer.MAX_VALUE);
        this.agentParams.put("TARGET_KL", 0.05F);
        this.agentParams.put("EPOCHS", 10);
        this.agentParams.put("EPSILON_CLIP", 0.4F);
        this.agentParams.put("ENTROPY_FACTOR", 0.001F);
        this.agentParams.put("TAHN_ACTION_LIMIT", 10F*60F); //Max action 10 min
        this.agentParams.put("ACTION_SPACE", null);
        this.agentParams.put("ACTION_DIM", 3);
        this.agentParams.put("NUMBER_WORKERS", 1 );
        this.agentParams.put("EPISODES_WORKER", 50000);
        this.agentParams.put("SIMULATION_TIME", EPISODE_MAX_TIME);
        this.agentParams.put("DEBUG", true);
        double[] minFeatureValues = {0D, -27D, 0D, -27D, 0D, -27D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D};
        this.agentParams.put("OBS_MIN", minFeatureValues);
        double[] maxFeatureValues = {15200D, 27D, 15200D, 27D, 15200D, 27D, 3D, 3D, 3D, 3D, 3D, 3D, 3D, 3D, 1D, 1D, 1D, 1D, 1D, 1D, 1D, 1D, 3000};
        this.agentParams.put("OBS_MAX", maxFeatureValues);
        this.agentParams.put("PREPROCESSING", new MinMaxScaler(minFeatureValues, maxFeatureValues));

        //Initialize the user interface backend
        uiServer = UIServer.getInstance();
        //Configure where the network information (gradients, score vs. time etc) is to be stored. Here: store in memory.
        StatsStorage statsStorage = new InMemoryStatsStorage();         //Alternative: new FileStatsStorage(File), for saving and loading later
        //Attach the StatsStorage instance to the UI: this allows the contents of the StatsStorage to be visualized
        uiServer.attach(statsStorage);
        this.agentParams.put("STATS_STORAGE", statsStorage);
    }

    @Override
    public ExperimentResult experiment(Random rnd, int experiment) {

        EnvironmentFactory factory = new SimpleThreeStopsRailwayFactory(EPISODE_MAX_TIME, new double[]{10D*60D,0D,0D}, false,false);

        rldevs4j.agents.ppov2.FFCritic critic = new rldevs4j.agents.ppov2.FFCritic(
                (int) agentParams.get("OBS_DIM"),
                (double) agentParams.get("LEARNING_RATE"),
                (double) agentParams.getOrDefault("L2", 0.001D),
                (float) agentParams.get("EPSILON_CLIP"),
                (int) agentParams.get("HIDDEN_SIZE"),
                (StatsStorage) agentParams.get("STATS_STORAGE"));
        rldevs4j.agents.ppov2.ContinuousActorFixedStd actor = new rldevs4j.agents.ppov2.ContinuousActorFixedStd(
                (int) agentParams.get("OBS_DIM"),
                (int) agentParams.get("ACTION_DIM"),
                (double) agentParams.get("LEARNING_RATE"),
                (double) agentParams.getOrDefault("L2", 0.001D),
                (float) agentParams.get("TAHN_ACTION_LIMIT"),
                (float) agentParams.get("ENTROPY_FACTOR"),
                (float) agentParams.get("EPSILON_CLIP"),
                (int) agentParams.get("HIDDEN_SIZE"),
                (StatsStorage) agentParams.get("STATS_STORAGE"));
        PPO global = new rldevs4j.agents.ppov2.PPO(actor, critic, (Preprocessing) agentParams.get("PREPROCESSING"), factory, agentParams);

        logger.log(Level.INFO, "Training Start. Experiment #{0}", new Object[]{experiment});

        try {
            logger.log(Level.INFO, "Training Start. Experiment #{0}", new Object[]{experiment});
            global.startTraining((Integer) agentParams.getOrDefault("NUMBER_WORKERS", 1));
            logger.log(Level.INFO, "Training Finalized. Avg-Reward: {0}", new Object[]{global.getResults().getLastAverageReward()});

            global.saveModel(resultsFilePath+name+"_"+experiment+"_");
        } catch (InterruptedException | IOException ex) {
            Logger.getGlobal().severe(ex.getLocalizedMessage());
        }
        return global.getResults(); //Training results
    }    
}
