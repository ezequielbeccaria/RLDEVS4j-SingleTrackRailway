package rldevs4j.testenv;

import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.nd4j.linalg.api.rng.Random;
import rldevs4j.agents.ppov2.PPO;
import rldevs4j.base.agent.preproc.NoPreprocessing;
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
public class TestEnvPPOTrain extends Experiment{
    private final double EPISODE_MAX_TIME=100.5;
    private final Map<String, Object> agentParams;
    protected UIServer uiServer;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Experiment exp = new TestEnvPPOTrain();
        exp.run();

//        System.exit(0);
    }

    public TestEnvPPOTrain() {
        super("PPOTrain01", 1, false, true, "/home/ezequiel/experiments/TestEnv/", null);
        this.agentParams = new HashMap<>();
        this.agentParams.put("RESULTS_FILE_PATH", resultsFilePath);
        this.agentParams.put("OBS_DIM", 9);
        this.agentParams.put("LEARNING_RATE_CRITIC", 3e-4);
        this.agentParams.put("LEARNING_RATE_ACTOR", 3e-4);
        this.agentParams.put("HIDDEN_SIZE", 128);
        this.agentParams.put("L2", 1e-3);
        this.agentParams.put("DISCOUNT_RATE", 0.995F);
        this.agentParams.put("LAMBDA_GAE", 0.96F);
        this.agentParams.put("HORIZON", Integer.MAX_VALUE);
        this.agentParams.put("TARGET_KL", 0.03F);
        this.agentParams.put("EPOCHS", 3);
        this.agentParams.put("EPSILON_CLIP", 0.2F);
        this.agentParams.put("ENTROPY_FACTOR", 0.02F);
        float[][] actionSpace = new float[][]{{0},{1},{2}};
//        float[][] actionSpace = new float[][]{{0},{1},{2},{3},{4},{5},{6},{7},{8}};
        this.agentParams.put("ACTION_SPACE", actionSpace);
        this.agentParams.put("ACTION_DIM", actionSpace.length);
        this.agentParams.put("NUMBER_WORKERS", 1);
        this.agentParams.put("EPISODES_WORKER", 50000);
        this.agentParams.put("SIMULATION_TIME", EPISODE_MAX_TIME);
        this.agentParams.put("PREPROCESSING", new NoPreprocessing());
        this.agentParams.put("DEBUG", true);

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

        TestEnvFactory factory = new TestEnvFactory();
        
        PPO global = AgentFactory.ppoDiscrete(agentParams, factory);

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
