package rldevs4j.singletrackrailway;

import facade.DevsSuiteFacade;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.JFrame;
import org.math.plot.Plot2DPanel;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.rng.Random;
import rldevs4j.base.agent.Agent;
import rldevs4j.base.env.Environment;
import rldevs4j.base.env.RLEnvironment;
import rldevs4j.base.env.msg.Step;
import rldevs4j.experiment.Experiment;
import rldevs4j.experiment.ExperimentResult;
import rldevs4j.singletrackrailway.factory.AgentFactory;
import rldevs4j.singletrackrailway.factory.SingleTrackRailwayEnvFactory;
import rldevs4j.utils.CSVUtils;
import rldevs4j.utils.CollectionsUtils;

/**
 *
 * @author Ezequiel Beccaria
 */
public class Env3PPOTrain extends Experiment{
    private DevsSuiteFacade facade;
    private final int EPISODES = 10000;
    private final double EPISODE_MAX_TIME=3000;
    private final Map<String, Object> agentParams;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Experiment exp = new Env3PPOTrain();
        exp.run();
        
//        System.exit(0);
    }

    public Env3PPOTrain() {
        super(0, "Env3PPOTrain", 1, false, true, "/home/ezequiel/experiments/SingleTrackRailway/", null);
        this.facade = new DevsSuiteFacade();                
        this.agentParams = new HashMap<>();
        this.agentParams.put("OBS_DIM", 25);
        this.agentParams.put("ACTION_DIM", 2);
        this.agentParams.put("LEARNING_RATE", 1e-3);
        this.agentParams.put("HIDDEN_SIZE", 128);
        this.agentParams.put("TAHN_ACTION_LIMIT", 30D*60D); //Max action 30 min
        this.agentParams.put("L2", 1e-3);
        this.agentParams.put("DISCOUNT_RATE", 0.99);
        this.agentParams.put("LAMBDA_GAE", 0.96);
        this.agentParams.put("EPSILON_CLIP", 0.2);
        this.agentParams.put("TARGET_KL", 0.02);
        this.agentParams.put("EPOCHS", 3);
        this.agentParams.put("HORIZON", Integer.MAX_VALUE);
        this.agentParams.put("ENTROPY_COEF", 0.02);
        this.agentParams.put("DEBUG", false);
    }

    @Override
    public ExperimentResult experiment(Random rnd, int experiment) {
        ExperimentResult result = new ExperimentResult();
        
        SingleTrackRailwayEnvFactory factory = new SingleTrackRailwayEnvFactory();
        
        Environment env = factory.createEnv03(EPISODE_MAX_TIME, false);
        env.initialize(); //initialize model state
        
        Agent agent = AgentFactory.ppo(agentParams);
        
        RLEnvironment rlEnv = new RLEnvironment(agent, env);
        
        logger.log(Level.INFO, "Training Start. Experiment #{0}", new Object[]{experiment});
        
        for(int i=1;i<=EPISODES;i++){        
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
            result.addResult(agent.getTotalReward(), finishTime-initTime);            

            // reset agent    
            agent.episodeFinished();
            
            if(i%1==0)
                logger.log(Level.INFO, "Episode {0} Terminated. Reward: {1}. Avg-Reward: {2}", new Object[]{i, result.getLastEpisodeReward(), result.getLastAverageReward()});
        }        
        logger.log(Level.INFO, "Training Finalized. Avg-Reward: {0}", new Object[]{result.getLastAverageReward()});
        
        agent.saveModel(resultsFilePath+name+"_");
              
        return result; //Training results    
    }    
}
