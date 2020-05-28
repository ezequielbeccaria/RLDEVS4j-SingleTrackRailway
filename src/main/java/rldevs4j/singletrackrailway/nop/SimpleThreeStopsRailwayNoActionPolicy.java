package rldevs4j.singletrackrailway.nop;

import facade.DevsSuiteFacade;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.nd4j.linalg.api.rng.Random;
import rldevs4j.base.agent.preproc.NoPreprocessing;
import rldevs4j.base.env.Environment;
import rldevs4j.base.env.RLEnvironment;
import rldevs4j.base.env.factory.EnvironmentFactory;
import rldevs4j.base.env.msg.Step;
import rldevs4j.experiment.Experiment;
import rldevs4j.experiment.ExperimentResult;
import rldevs4j.agents.dummy.DummyAgent;
import rldevs4j.singletrackrailway.factory.SimpleThreeStopsRailwayFactory;
import rldevs4j.singletrackrailway.factory.SingleTrackRailwayEnvFactory;
import rldevs4j.utils.CSVUtils;

/**
 *
 * @author Ezequiel Beccaria
 */
public class SimpleThreeStopsRailwayNoActionPolicy extends Experiment{
    private DevsSuiteFacade facade;
    private final int EPISODES = 10;
    private final double EPISODE_MAX_TIME=2000;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Experiment exp = new SimpleThreeStopsRailwayNoActionPolicy();
        exp.run();

        System.exit(0);
    }

    public SimpleThreeStopsRailwayNoActionPolicy() {
        super(0, "SimpleThreeStopsRailwayNoActionPolicy", 1, false, false, "/home/ezequiel/experiments/SimpleThreeStopsRailway/", null);
    }

    @Override
    public ExperimentResult experiment(Random rnd, int experiment) {
        ExperimentResult result = new ExperimentResult();

        EnvironmentFactory factory = new SimpleThreeStopsRailwayFactory(EPISODE_MAX_TIME, new double[]{0D,0D,0D}, false);
        Environment env = factory.createInstance();
        env.initialize(); //initialize model state
        
        DummyAgent agent = new DummyAgent("dummy_agent", new NoPreprocessing(), 3);
        
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
            storeTrace(env.getTrace());
            // reset agent
            agent.episodeFinished();

            if(i%1==0)
                logger.log(Level.INFO, "Episode {0} Terminated. Reward: {1}. Avg-Reward: {2}", new Object[]{i, result.getLastEpisodeReward(), result.getLastAverageReward()});
        }
        
        logger.log(Level.INFO, "Training Finalized. Avg-Reward: {0}", new Object[]{result.getLastAverageReward()});
              
        return result; //Training results    
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
            headers.add("train2-pos");
            headers.add("train2-speed");
            headers.add("bs0-occupation-s0");
            headers.add("bs1-occupation");
            headers.add("bs2-occupation");
            headers.add("bs3-occupation");
            headers.add("bs4-occupation-s1");
            headers.add("bs5-occupation");
            headers.add("bs6-occupation");
            headers.add("bs7-occupation-s2");
            headers.add("bs0-available-s0");
            headers.add("bs1-available");
            headers.add("bs2-available");
            headers.add("bs3-available");
            headers.add("bs4-available-s1");
            headers.add("bs5-available");
            headers.add("bs6-available");
            headers.add("bs7-available-s2");
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
}
