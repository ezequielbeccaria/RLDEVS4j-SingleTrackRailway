package rldevs4j.singletrackrailway;

import facade.DevsSuiteFacade;
import org.nd4j.linalg.api.rng.Random;
import rldevs4j.agents.ppo.ContinuousActionActorTest;
import rldevs4j.base.agent.Agent;
import rldevs4j.base.env.Environment;
import rldevs4j.base.env.RLEnvironment;
import rldevs4j.base.env.msg.Step;
import rldevs4j.experiment.Experiment;
import rldevs4j.experiment.ExperimentResult;
import rldevs4j.singletrackrailway.factory.AgentFactory;
import rldevs4j.singletrackrailway.factory.SingleTrackRailwayEnvFactory;
import rldevs4j.utils.CSVUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 *
 * @author Ezequiel Beccaria
 */
public class Env3PPOTest extends Experiment{
    private DevsSuiteFacade facade;
    private final int EPISODES = 1;
    private final double EPISODE_MAX_TIME=3000;
    private final Map<String, Object> agentParams;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Experiment exp = new Env3PPOTest();
        exp.run();

//        System.exit(0);
    }

    public Env3PPOTest() {
        super(0, "Env3PPOTest", 1, false, false, "/home/ezequiel/experiments/SingleTrackRailway/", null);
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

    private void storeTrace(List<Step> trace){
        FileWriter writer;
        try {
            String filename = resultsFilePath+String.valueOf(id)+"-"+name+"-trace";
            writer = new FileWriter(filename);
            //write headers
            List<String> headers = new ArrayList<>();
            headers.add("train0-pos");
            headers.add("train0-speed");
            headers.add("train1-pos");
            headers.add("train1-speed");
            headers.add("bs0-occupation-s0");
            headers.add("bs1-occupation");
            headers.add("bs2-occupation");
            headers.add("bs3-occupation");
            headers.add("bs4-occupation");
            headers.add("bs5-occupation-s1");
            headers.add("bs6-occupation");
            headers.add("bs7-occupation");
            headers.add("bs8-occupation");
            headers.add("bs9-occupation-s2");
            headers.add("bs0-available-s0");
            headers.add("bs1-available");
            headers.add("bs2-available");
            headers.add("bs3-available");
            headers.add("bs4-available");
            headers.add("bs5-available-s1");
            headers.add("bs6-available");
            headers.add("bs7-available");
            headers.add("bs8-available");
            headers.add("bs9-available-s2");
            headers.add("time");
            CSVUtils.writeLine(writer, headers, '|');
            //write data
            for(int j=0;j<trace.size();j++){
                Step step = trace.get(j);
                List<String> line = new ArrayList<>();
                for(int i=0;i<step.observationSize();i++)
                    line.add(formatter.format(step.getFeature(i))); // feature i
                CSVUtils.writeLine(writer, line, '|');
            }
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    private void storeAppliedActions(Map<Double, double[]> actions, int actionDim){
        FileWriter writer;
        try {
            String filename = resultsFilePath+String.valueOf(id)+"-"+name+"-actions";
            writer = new FileWriter(filename);
            //write headers
            List<String> headers = new ArrayList<>();
            headers.add("time");
            for(int i=0;i<actionDim;i++)
                headers.add("action"+i);

            CSVUtils.writeLine(writer, headers, '|');
            //write data
            for(Double time : actions.keySet()){
                double[] action = actions.get(time);
                List<String> line = new ArrayList<>();
                line.add(formatter.format(time)); // feature i
                for(int i=0;i<action.length;i++)
                    line.add(formatter.format(action[i])); // feature i
                CSVUtils.writeLine(writer, line, '|');
            }
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public ExperimentResult experiment(Random rnd, int experiment) {
        ExperimentResult result = new ExperimentResult();
        
        SingleTrackRailwayEnvFactory factory = new SingleTrackRailwayEnvFactory();
        
        Environment env = factory.createEnv03(EPISODE_MAX_TIME, false);
        env.initialize(); //initialize model state
        
        Agent agent = AgentFactory.ppo_test(super.resultsFilePath, (Double) agentParams.get("TAHN_ACTION_LIMIT"));
        
        RLEnvironment rlEnv = new RLEnvironment(agent, env);
        
        logger.log(Level.INFO, "Test Start. Experiment #{0}", new Object[]{experiment});
        
        for(int i=1;i<=EPISODES;i++){        
            //Inititalize environment and simulator
            facade = new DevsSuiteFacade(rlEnv); 
            facade.reset();
            //Episode time start
            long initTime = System.currentTimeMillis();
            //Simulate during "episodeTime" t (seconds of the day)
            facade.simulateToTime(EPISODE_MAX_TIME);
            //Episode time stop
            long finishTime = System.currentTimeMillis();
            //Save episode results            
            result.addResult(agent.getTotalReward(), finishTime-initTime);
            //store test data
            storeTrace(env.getTrace());
            storeAppliedActions(((ContinuousActionActorTest) agent).getAppliedActions(), (Integer) agentParams.get("ACTION_DIM"));
            // reset agent    
            agent.episodeFinished();
            
            if(i%1==0)
                logger.log(Level.INFO, "Episode {0} Terminated. Reward: {1}.", new Object[]{i, result.getLastEpisodeReward()});
        }
        return result; //Test results
    }    
}
