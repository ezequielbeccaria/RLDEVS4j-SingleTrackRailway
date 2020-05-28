package rldevs4j.singletrackrailway.a3c;

import facade.DevsSuiteFacade;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.nd4j.linalg.api.rng.Random;
import rldevs4j.agents.ac.A3C;
import rldevs4j.experiment.Experiment;
import rldevs4j.experiment.ExperimentResult;
import rldevs4j.singletrackrailway.factory.AgentFactory;
import rldevs4j.singletrackrailway.factory.SingleTrackRailwayEnvFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ezequiel Beccaria
 */
public class Env3A3CTrain3 extends Experiment{
    private DevsSuiteFacade facade;
    private final double EPISODE_MAX_TIME=3000;
    private final Map<String, Object> agentParams;
    protected UIServer uiServer;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Experiment exp = new Env3A3CTrain3();
        exp.run();

//        System.exit(0);
    }

    public Env3A3CTrain3() {
        super(0, "Env3A3CTrainCPU_3", 1, false, true, "/home/ezequiel/experiments/SingleTrackRailwayEnv3/", null);
        this.facade = new DevsSuiteFacade();
        this.agentParams = new HashMap<>();
        this.agentParams.put("OBS_DIM", 25);
        this.agentParams.put("LEARNING_RATE", 1e-3);
        this.agentParams.put("HIDDEN_SIZE", 128);
        this.agentParams.put("L2", 1e-3);
        this.agentParams.put("DISCOUNT_RATE", 0.99);
        this.agentParams.put("HORIZON", 100);
        double[][] actionSpace = new double[][]{
                {1000D, 0D},{900D, 0D},{800D, 0D},{700D, 0D},{600D, 0D},{500D, 0D},{400D, 0D},{300D, 0D},{200D, 0D},{100D, 0D},{50D, 0D},{10D, 0D},
                {0D, 0D},
                {0D, 10D},{0D, 50D},{0D, 100D},{0D, 200D},{0D, 300D},{0D, 400D},{0D, 500D},{0D, 600D},{0D, 700D},{0D, 800D},{0D, 900D},{0D, 1000D}};
        this.agentParams.put("ACTION_SPACE", actionSpace);
        this.agentParams.put("ACTION_DIM", actionSpace.length);
        this.agentParams.put("NUM_WORKERS", 10);
        this.agentParams.put("EPISODES_WORKER", 100000);
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

        ExperimentResult result = new ExperimentResult();
        
        SingleTrackRailwayEnvFactory factory = new SingleTrackRailwayEnvFactory();
        
        A3C a3cGlobal = AgentFactory.a3cDiscrete(agentParams, factory);

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
