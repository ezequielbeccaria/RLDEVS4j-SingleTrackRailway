package rldevs4j.testenv;

import facade.DevsSuiteFacade;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.nd4j.linalg.api.rng.Random;
import rldevs4j.agents.ac.A3C;
import rldevs4j.agents.ac.FFCritic;
import rldevs4j.agents.ac.FFDiscreteActor;
import rldevs4j.base.agent.preproc.MinMaxScaler;
import rldevs4j.base.agent.preproc.NoPreprocessing;
import rldevs4j.base.env.Environment;
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
public class TestEnvA3CTrain extends Experiment{
    private final double EPISODE_MAX_TIME=100.5;
    private final Map<String, Object> agentParams;
    protected UIServer uiServer;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Experiment exp = new TestEnvA3CTrain();
        exp.run();

//        System.exit(0);
    }

    public TestEnvA3CTrain() {
        super(0, "TestEnvA3CTrain", 1, false, true, "/home/ezequiel/experiments/TestEnv/", null);
        this.agentParams = new HashMap<>();
        this.agentParams.put("OBS_DIM", 9);
        this.agentParams.put("LEARNING_RATE", 1e-4);
        this.agentParams.put("HIDDEN_SIZE", 128);
        this.agentParams.put("L2", 1e-3);
        this.agentParams.put("DISCOUNT_RATE", 0.99);
        this.agentParams.put("HORIZON", Integer.MAX_VALUE );
        float[][] actionSpace = new float[][]{{0},{1},{2}};
//        float[][] actionSpace = new float[][]{{0},{1},{2},{3},{4},{5},{6},{7},{8}};
        this.agentParams.put("ACTION_SPACE", actionSpace);
        this.agentParams.put("ACTION_DIM", actionSpace.length);
        this.agentParams.put("NUMBER_WORKERS", 1);
        this.agentParams.put("EPISODES_WORKER", 5000);
        this.agentParams.put("SIMULATION_TIME", EPISODE_MAX_TIME);
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

        FFCritic critic = new FFCritic(
                (int) agentParams.get("OBS_DIM"),
                (double) agentParams.get("LEARNING_RATE"),
                (double) agentParams.getOrDefault("L2", 0.001D),
                (int) agentParams.get("HIDDEN_SIZE"),
                (StatsStorage) agentParams.get("STATS_STORAGE"));
        FFDiscreteActor actor = new FFDiscreteActor(
                (int) agentParams.get("OBS_DIM"),
                (int) agentParams.get("ACTION_DIM"),
                (double) agentParams.get("LEARNING_RATE"),
                (double) agentParams.getOrDefault("L2", 0.001D),
                (double) agentParams.getOrDefault("ENTROPY_FACTOR", 0.0015D),
                (int) agentParams.get("HIDDEN_SIZE"),
                (StatsStorage) agentParams.get("STATS_STORAGE"));
        A3C a3cGlobal = new A3C(actor, critic, new NoPreprocessing(), factory, agentParams);

        logger.log(Level.INFO, "Training Start. Experiment #{0}", new Object[]{experiment});

        try {
            logger.log(Level.INFO, "Training Start. Experiment #{0}", new Object[]{experiment});
            a3cGlobal.startTraining((Integer) agentParams.getOrDefault("NUMBER_WORKERS", 1));
            logger.log(Level.INFO, "Training Finalized. Avg-Reward: {0}", new Object[]{a3cGlobal.getResults().getLastAverageReward()});

            a3cGlobal.saveModel(resultsFilePath+name);
        } catch (InterruptedException | IOException ex) {
            Logger.getGlobal().severe(ex.getLocalizedMessage());
        }
        return a3cGlobal.getResults(); //Training results
    }    
}
