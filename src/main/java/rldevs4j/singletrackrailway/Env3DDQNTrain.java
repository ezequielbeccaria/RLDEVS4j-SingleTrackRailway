package rldevs4j.singletrackrailway;

import facade.DevsSuiteFacade;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.nd4j.linalg.api.rng.Random;
import rldevs4j.base.agent.Agent;
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
public class Env3DDQNTrain extends Experiment{
    private DevsSuiteFacade facade;
    private final int EPISODES = 5000;
    private final double EPISODE_MAX_TIME=3000;
    private final Map<String, Object> agentParams;
    protected UIServer uiServer;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Experiment exp = new Env3DDQNTrain();
        exp.run();

//        System.exit(0);
    }

    public Env3DDQNTrain() {
        super(0, "Env3DDQNTrainCPU", 1, false, true, "/home/ezequiel/experiments/SingleTrackRailwayEnv3/", null);
        this.facade = new DevsSuiteFacade();
        this.agentParams = new HashMap<>();
        this.agentParams.put("OBS_DIM", 25);
        this.agentParams.put("LEARNING_RATE", 1e-3);
        this.agentParams.put("HIDDEN_SIZE", 128);
        this.agentParams.put("L2", 1e-3);
        this.agentParams.put("DISCOUNT_RATE", 0.99);
        double[][] actionSpace = new double[][]{
                {1000D, 0D},{900D, 0D},{800D, 0D},{700D, 0D},{600D, 0D},{500D, 0D},{400D, 0D},{300D, 0D},{200D, 0D},{100D, 0D},{50D, 0D},{10D, 0D},
                {0D, 0D},
                {0D, 10D},{0D, 50D},{0D, 100D},{0D, 200D},{0D, 300D},{0D, 400D},{0D, 500D},{0D, 600D},{0D, 700D},{0D, 800D},{0D, 900D},{0D, 1000D}};
        this.agentParams.put("ACTION_SPACE", actionSpace);
        this.agentParams.put("OUTPUT_DIM", actionSpace.length);
        this.agentParams.put("BATCH_SIZE", 32);
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
        
        SingleTrackRailwayEnvFactory factory = new SingleTrackRailwayEnvFactory();
        
        Environment env = factory.createEnv03(EPISODE_MAX_TIME, false);
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

            if (i % 10 == 0)
                logger.log(Level.INFO, "Episode {0} Terminated. Reward: {1}. Avg-Reward: {2}", new Object[]{i, result.getLastEpisodeReward(), result.getLastAverageReward()});
            if (i % 1000 == 0)
                agent.saveModel(resultsFilePath+name);
        }
        logger.log(Level.INFO, "Training Finalized. Avg-Reward: {0}", new Object[]{result.getLastAverageReward()});
        agent.saveModel(resultsFilePath+name);

              
        return result; //Training results    
    }    
}
