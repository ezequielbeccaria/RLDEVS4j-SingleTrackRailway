package rldevs4j.singletrackrailway.ppo;

import facade.DevsSuiteFacade;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.nd4j.linalg.api.rng.Random;
import org.nd4j.linalg.factory.Nd4j;
import rldevs4j.agents.ppov2.PPO;
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
public class SimpleThreeStopsRailwayDelayPPOTrain extends Experiment{
    private final double EPISODE_MAX_TIME=3000;
    private final Map<String, Object> agentParams;
    protected UIServer uiServer;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Experiment exp = new SimpleThreeStopsRailwayDelayPPOTrain();
        exp.run();

//        System.exit(0);
    }

    public SimpleThreeStopsRailwayDelayPPOTrain() {
        super(0, "PPOTrain2", 1, false, true, "/home/ezequiel/experiments/SimpleThreeStopsRailway/", null);
        this.agentParams = new HashMap<>();
        this.agentParams.put("RESULTS_FILE_PATH", resultsFilePath);
        this.agentParams.put("OBS_DIM", 23);
        this.agentParams.put("LEARNING_RATE", 1e-4);
        this.agentParams.put("HIDDEN_SIZE", 64);
        this.agentParams.put("L2", 1e-3);
        this.agentParams.put("DISCOUNT_RATE", 0.995F);
        this.agentParams.put("LAMBDA_GAE", 0.96F);
        this.agentParams.put("HORIZON", Integer.MAX_VALUE);
        this.agentParams.put("TARGET_KL", 0.02F);
        this.agentParams.put("EPOCHS", 10);
        this.agentParams.put("EPSILON_CLIP", 0.2F);
        this.agentParams.put("ENTROPY_FACTOR", 0.001F);
        float[][] actionSpace = new float[][]{
                {0F, 0F, 0F},
                {600F, 0F, 0F},{480F, 0F, 0F},{360F, 0F, 0F},{240F, 0F, 0F},{120F, 0F, 0F},
                {0F, 600F, 0F},{0F, 480F, 0F},{0F, 360F, 0F},{0F, 240F, 0F},{0F, 120F, 0F},
                {0F, 0F, 600F},{0F, 0F, 480F},{0F, 0F, 360F},{0F, 0F, 240F},{0F, 0F, 120F}};
//        float[][] actionSpace = new float[][]{
//                {0F, 0F, 0F},
//                {1000F, 0F, 0F},{900F, 0F, 0F},{800F, 0F, 0F},{700F, 0F, 0F},{600F, 0F, 0F},{500F, 0F, 0F},{400F, 0F, 0F},{300F, 0F, 0F},{200F, 0F, 0F},{100F, 0F, 0F},{50F, 0F, 0F},{10F, 0F, 0F},
//                {0F, 1000F, 0F},{0F, 900F, 0F},{0F, 800F, 0F},{0F, 700F, 0F},{0F, 600F, 0F},{0F, 500F, 0F},{0F, 400F, 0F},{0F, 300F, 0F},{0F, 200F, 0F},{0F, 100F, 0F},{0F, 50F, 0F},{0F, 10F, 0F},
//                {0F, 0F, 1000F},{0F, 0F, 900F},{0F, 0F, 800F},{0F, 0F, 700F},{0F, 0F, 600F},{0F, 0F, 500F},{0F, 0F, 400F},{0F, 0F, 300F},{0F, 0F, 200F},{0F, 0F, 100F},{0F, 0F, 50F},{0F, 0F, 10F}};
        this.agentParams.put("ACTION_SPACE", actionSpace);
        this.agentParams.put("ACTION_DIM", actionSpace.length);
        this.agentParams.put("NUMBER_WORKERS", 10 );
        this.agentParams.put("EPISODES_WORKER", 100000);
        this.agentParams.put("SIMULATION_TIME", EPISODE_MAX_TIME);
        this.agentParams.put("DEBUG", true);
        double[] minFeatureValues = {0D, -27D, 0D, -27D, 0D, -27D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D};
        this.agentParams.put("OBS_MIN", minFeatureValues);
        double[] maxFeatureValues = {15200D, 27D, 15200D, 27D, 15200D, 27D, 3D, 3D, 3D, 3D, 3D, 3D, 3D, 3D, 1D, 1D, 1D, 1D, 1D, 1D, 1D, 1D, 3000};
        this.agentParams.put("OBS_MAX", maxFeatureValues);

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

        ExperimentResult result = new ExperimentResult();

        EnvironmentFactory factory = new SimpleThreeStopsRailwayFactory(EPISODE_MAX_TIME, new double[]{10D*60D,0D,0D}, false);
        
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
