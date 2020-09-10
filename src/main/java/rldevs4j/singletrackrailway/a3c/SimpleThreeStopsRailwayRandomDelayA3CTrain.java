package rldevs4j.singletrackrailway.a3c;

import facade.DevsSuiteFacade;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.rng.Random;
import rldevs4j.agents.ac.A3C;
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
public class SimpleThreeStopsRailwayRandomDelayA3CTrain extends Experiment{
    private DevsSuiteFacade facade;
    private final double EPISODE_MAX_TIME=3000;
    private final Map<String, Object> agentParams;
    protected UIServer uiServer;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Experiment exp = new SimpleThreeStopsRailwayRandomDelayA3CTrain();
        exp.run();

//        System.exit(0);
    }

    public SimpleThreeStopsRailwayRandomDelayA3CTrain() {
        super("A3C", 5, false, true, "/home/ezequiel/experiments/SimpleThreeStopsRailwayV2/A3C_RandomDelay/", null);
        this.facade = new DevsSuiteFacade();
        this.agentParams = new HashMap<>();
        this.agentParams.put("OBS_DIM", 23);
        this.agentParams.put("LEARNING_RATE", 1e-6);
        this.agentParams.put("HIDDEN_SIZE", 1024);
        this.agentParams.put("HIDDEN_ACTIVATION", Activation.TANH);
        this.agentParams.put("L2", 1e-6);
        this.agentParams.put("DISCOUNT_RATE", 0.99);
        this.agentParams.put("ENTROPY_FACTOR", 0.2);
        this.agentParams.put("HORIZON", Integer.MAX_VALUE);
        float[][] actionSpace = new float[][]{
                {0F, 0F, 0F},
                {960F, 0F, 0F},{480F, 0F, 0F},{240F, 0F, 0F},{120F, 0F, 0F},{60F, 0F, 0F},
                {0F, 960F, 0F},{0F, 480F, 0F},{0F, 240F, 0F},{0F, 120, 0F},{0F, 60F, 0F},
                {0F, 0F, 960F},{0F, 0F, 480F},{0F, 0F, 240F},{0F, 0F, 120},{0F, 0F, 60F}};
        this.agentParams.put("ACTION_SPACE", actionSpace);
        this.agentParams.put("ACTION_DIM", actionSpace.length);
        this.agentParams.put("NUMBER_WORKERS", 5);
        this.agentParams.put("EPISODES_WORKER", 10000);
        this.agentParams.put("SIMULATION_TIME", EPISODE_MAX_TIME);
        this.agentParams.put("DEBUG", false);
        double[] minFeatureValues = {0D, -27D, 0D, -27D, 0D, -27D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D};
        this.agentParams.put("OBS_MIN", minFeatureValues);
        double[] maxFeatureValues = {15200D, 27D, 15200D, 27D, 15200D, 27D, 3D, 3D, 3D, 3D, 3D, 3D, 3D, 3D, 1D, 1D, 1D, 1D, 1D, 1D, 1D, 1D, 3000};
        this.agentParams.put("OBS_MAX", maxFeatureValues);
        this.agentParams.put("PREPROCESSING", new NoPreprocessing());

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

        EnvironmentFactory factory = new SimpleThreeStopsRailwayFactory(EPISODE_MAX_TIME, new double[]{0D,0D,0D}, true,false);
        
        A3C a3cGlobal = AgentFactory.a3cDiscrete(agentParams, factory);

        logger.log(Level.INFO, "Training Start. Experiment #{0}", new Object[]{experiment});

        try {
            logger.log(Level.INFO, "Training Start. Experiment #{0}", new Object[]{experiment});
            a3cGlobal.startTraining((Integer) agentParams.getOrDefault("NUMBER_WORKERS", 1));
            logger.log(Level.INFO, "Training Finalized. Avg-Reward: {0}", new Object[]{a3cGlobal.getResults().getLastAverageReward()});

            a3cGlobal.saveModel(resultsFilePath+name+"_"+experiment);
        } catch (InterruptedException | IOException ex) {
            Logger.getGlobal().severe(ex.getLocalizedMessage());
        }
        return a3cGlobal.getResults(); //Training results
    }    
}
