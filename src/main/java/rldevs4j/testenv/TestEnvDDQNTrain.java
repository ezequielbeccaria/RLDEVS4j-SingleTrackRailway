package rldevs4j.testenv;

import facade.DevsSuiteFacade;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.nd4j.linalg.api.rng.Random;
import rldevs4j.agents.dqn.DDQN;
import rldevs4j.agents.dqn.Model;
import rldevs4j.base.agent.Agent;
import rldevs4j.base.agent.preproc.MinMaxScaler;
import rldevs4j.base.agent.preproc.NoPreprocessing;
import rldevs4j.base.env.Environment;
import rldevs4j.base.env.RLEnvironment;
import rldevs4j.experiment.Experiment;
import rldevs4j.experiment.ExperimentResult;
import rldevs4j.singletrackrailway.factory.AgentFactory;
import rldevs4j.singletrackrailway.factory.SingleTrackRailwayEnvFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 *
 * @author Ezequiel Beccaria
 */
public class TestEnvDDQNTrain extends Experiment{
    private DevsSuiteFacade facade;
    private final int EPISODES = 5000;
    private final double EPISODE_MAX_TIME=100.5;
    private final Map<String, Object> agentParams;
    protected UIServer uiServer;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Experiment exp = new TestEnvDDQNTrain();
        exp.run();

//        System.exit(0);
    }

    public TestEnvDDQNTrain() {
        super(0, "DDQNTrain", 1, false, true, "/home/ezequiel/experiments/TestEnv/", null);
        this.facade = new DevsSuiteFacade();
        this.agentParams = new HashMap<>();
        this.agentParams.put("OBS_DIM", 9);
        this.agentParams.put("LEARNING_RATE", 1e-5);
        this.agentParams.put("HIDDEN_SIZE", 16);
        this.agentParams.put("DISCOUNT_RATE", 0.99);
        this.agentParams.put("CLIP_REWARD", 1.0);
        this.agentParams.put("RWD_MEAN_SCALE", true);
        this.agentParams.put("RWD_STD_SCALE", true);
        float[][] actionSpace = new float[][]{{0},{1},{2}};
//        float[][] actionSpace = new float[][]{{0},{1},{2},{3},{4},{5},{6},{7},{8}};
        this.agentParams.put("ACTION_SPACE", actionSpace);
        this.agentParams.put("OUTPUT_DIM", actionSpace.length);
        this.agentParams.put("BATCH_SIZE", 64);
        this.agentParams.put("TARGET_UPDATE", 500);
        this.agentParams.put("DEBUG", false);

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

        TestEnvFactory factory = new TestEnvFactory();
        
        Environment env = factory.createInstance();
        env.initialize(); //initialize model state

        Model model = new Model(agentParams);
        Agent agent = new DDQN("DDQN", new NoPreprocessing(), model, agentParams);

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
                agent.saveModel(resultsFilePath+name);
        }
        logger.log(Level.INFO, "Training Finalized. Avg-Reward: {0}", new Object[]{result.getLastAverageReward()});
        agent.saveModel(resultsFilePath+name);

              
        return result; //Training results    
    }    
}
