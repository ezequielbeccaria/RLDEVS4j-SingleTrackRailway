package rldevs4j.testenv;

import facade.DevsSuiteFacade;
import org.deeplearning4j.ui.api.UIServer;
import org.nd4j.linalg.api.rng.Random;
import rldevs4j.agents.api.ApiAgent;
import rldevs4j.base.agent.Agent;
import rldevs4j.base.agent.preproc.NoPreprocessing;
import rldevs4j.base.env.Environment;
import rldevs4j.base.env.RLEnvironment;
import rldevs4j.experiment.Experiment;
import rldevs4j.experiment.ExperimentResult;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 *
 * @author Ezequiel Beccaria
 */
public class TestEnvAgentApi extends Experiment{
    private DevsSuiteFacade facade;
    private final int EPISODES = 10000;
    private final double EPISODE_MAX_TIME=100.5;
    private final Map<String, Object> agentParams;
    protected UIServer uiServer;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Experiment exp = new TestEnvAgentApi();
        exp.run();

//        System.exit(0);
    }

    public TestEnvAgentApi() {
        super("ApiTest", 1, false, false, "/home/ezequiel/experiments/TestEnv/", null);
        this.facade = new DevsSuiteFacade();
        this.agentParams = new HashMap<>();
        this.agentParams.put("INPUT_SIZE", 9);
        this.agentParams.put("HIDDEN_SIZE", 64);
        this.agentParams.put("HIDDEN_LAYERS", 2);
        this.agentParams.put("HIDDEN_ACTIVATION", "TANH");

        this.agentParams.put("LEARNING_RATE", 1e-4);
        this.agentParams.put("DISCOUNT_RATE", 0.99);
        this.agentParams.put("LAMBDA_GAE", 0.96);
        this.agentParams.put("HORIZON", 9999);
        this.agentParams.put("ENTROPY_COEFFICIENT", 0.02F);
        this.agentParams.put("TARGET_KL", 0.03F);
        this.agentParams.put("EPOCHS", 3);
        this.agentParams.put("EPSILON_CLIP", 0.2F);
        this.agentParams.put("ENTROPY_FACTOR", 0.02F);

        this.agentParams.put("NORMALIZE_INPUT", false);
        this.agentParams.put("NORMALIZE_REWARD", false);
        float[][] actionSpace = new float[][]{{0},{1},{2}};
//        float[][] actionSpace = new float[][]{{0},{1},{2},{3},{4},{5},{6},{7},{8}};
        this.agentParams.put("ACTION_SPACE", actionSpace);
        this.agentParams.put("ACTION_SIZE", actionSpace.length);

        this.agentParams.put("EXPERIMENT_NAME", "TEST_API");
        this.agentParams.put("EXPERIMENT_ID", 1);
        this.agentParams.put("CHECKPOINT", 100);
        this.agentParams.put("DEVICE_NAME", "cpu");
        this.agentParams.put("DEBUG", false);
    }

    @Override
    public void test() {

    }

    @Override
    public ExperimentResult experiment(Random rnd, int experiment) {

        ExperimentResult result = new ExperimentResult();

        TestEnvFactory factory = new TestEnvFactory();
        
        Environment env = factory.createInstance();
        env.initialize(); //initialize model state

        Agent agent = null;
        String agentUrl = "http://localhost:5000/";
        try {
            agent = new ApiAgent("TEST_API", new NoPreprocessing(), agentUrl, agentParams);
            agent.initialize();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
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
