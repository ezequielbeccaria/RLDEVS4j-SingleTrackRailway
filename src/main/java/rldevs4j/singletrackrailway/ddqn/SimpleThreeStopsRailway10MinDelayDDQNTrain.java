package rldevs4j.singletrackrailway.ddqn;

import facade.DevsSuiteFacade;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.nd4j.linalg.api.rng.Random;
import rldevs4j.agents.ac.A3C;
import rldevs4j.base.agent.Agent;
import rldevs4j.base.agent.preproc.NoPreprocessing;
import rldevs4j.base.env.Environment;
import rldevs4j.base.env.RLEnvironment;
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
public class SimpleThreeStopsRailway10MinDelayDDQNTrain extends Experiment{
    private DevsSuiteFacade facade;
    private final double EPISODE_MAX_TIME=3000;
    private final Map<String, Object> agentParams;
    protected UIServer uiServer;
    private int EPISODES = 50;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Experiment exp = new SimpleThreeStopsRailway10MinDelayDDQNTrain();
        exp.run();

//        System.exit(0);
    }

    public SimpleThreeStopsRailway10MinDelayDDQNTrain() {
        super("DDQNTrain", 5, false, true, "/home/ezequiel/experiments/SimpleThreeStopsRailway/DDQNTrain_10Delay/", null);
        this.facade = new DevsSuiteFacade();
        this.agentParams = new HashMap<>();
        this.agentParams.put("OBS_DIM", 23);
        this.agentParams.put("LEARNING_RATE", 1e-6);
        this.agentParams.put("HIDDEN_SIZE", 1024);
        this.agentParams.put("L2", 1e-6);
        this.agentParams.put("DISCOUNT_RATE", 0.99);
        this.agentParams.put("CLIP_GRADIENT_THRESHOLD", 1D);
        this.agentParams.put("RWD_MEAN_SCALE", true);
        this.agentParams.put("RWD_STD_SCALE", true);
        this.agentParams.put("TARGET_UPDATE", 50);
        this.agentParams.put("MEMORY_SIZE", 10000);
        float[][] actionSpace = new float[][]{
                {0F, 0F, 0F},
                {960F, 0F, 0F},{480F, 0F, 0F},{240F, 0F, 0F},{120F, 0F, 0F},{60F, 0F, 0F},
                {0F, 960F, 0F},{0F, 480F, 0F},{0F, 240F, 0F},{0F, 120, 0F},{0F, 60F, 0F},
                {0F, 0F, 960F},{0F, 0F, 480F},{0F, 0F, 240F},{0F, 0F, 120},{0F, 0F, 60F}};
        this.agentParams.put("ACTION_SPACE", actionSpace);
        this.agentParams.put("OUTPUT_DIM", actionSpace.length);
        this.agentParams.put("SIMULATION_TIME", EPISODE_MAX_TIME);
        this.agentParams.put("BATCH_SIZE", 32);
        this.agentParams.put("DEBUG", false);
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

        EnvironmentFactory factory = new SimpleThreeStopsRailwayFactory(EPISODE_MAX_TIME, new double[]{10D*60D,0D,0D}, false, false);
        Environment env = factory.createInstance();
        env.initialize(); //initialize model state

        Agent agent = AgentFactory.ddqn(agentParams);

        RLEnvironment rlEnv = new RLEnvironment(agent, env);

        logger.log(Level.INFO, "Training Start. Experiment #{0}", new Object[]{experiment});

        for(int i=1;i<=EPISODES;i++) {
            //Inititalize environment and simulator
            facade = new DevsSuiteFacade(rlEnv);
            facade.reset();
            //Episode time start
            long initTime = System.currentTimeMillis();
            //Simulate during "episodeTime" t (minuts of the day)
            facade.simulateToTime(EPISODE_MAX_TIME);
            //Episode time stop
            long finishTime = System.currentTimeMillis();
            //Save episode results
            result.addResult(agent.getTotalReward(), finishTime - initTime);

            // reset agent
            agent.episodeFinished();

            if (i % 1 == 0)
                logger.log(Level.INFO, "Episode {0} Terminated. Reward: {1}. Avg-Reward: {2}", new Object[]{i, result.getLastEpisodeReward(), result.getLastAverageReward()});
            if (i % 1000 == 0)
                agent.saveModel(resultsFilePath+name+"_"+experiment);
        }
        logger.log(Level.INFO, "Experiment {1} Training Finalized. Avg-Reward: {0}", new Object[]{result.getLastAverageReward(), experiment});
        agent.saveModel(resultsFilePath+name+"_"+experiment);

        return result; //Training results
    }    
}
