package rldevs4j.singletrackrailway.ppo;

import facade.DevsSuiteFacade;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.nd4j.linalg.api.rng.Random;
import rldevs4j.agents.ppo.ContinuousActionActorTest;
import rldevs4j.agents.ppov2.PPO;
import rldevs4j.agents.ppov2.TestAgent;
import rldevs4j.base.agent.preproc.MinMaxScaler;
import rldevs4j.base.env.Environment;
import rldevs4j.base.env.RLEnvironment;
import rldevs4j.base.env.factory.EnvironmentFactory;
import rldevs4j.base.env.msg.Step;
import rldevs4j.experiment.Experiment;
import rldevs4j.experiment.ExperimentResult;
import rldevs4j.singletrackrailway.factory.AgentFactory;
import rldevs4j.singletrackrailway.factory.SimpleThreeStopsRailwayFactory;
import rldevs4j.utils.CSVUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ezequiel Beccaria
 */
public class SimpleThreeStopsRailwayDelayPPOTest extends Experiment{
    private DevsSuiteFacade facade;
    private final int EPISODES = 1;
    private final double EPISODE_MAX_TIME=3000;
    private final Map<String, Object> agentParams;
    protected UIServer uiServer;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Experiment exp = new SimpleThreeStopsRailwayDelayPPOTest("/home/ezequiel/experiments/SimpleThreeStopsRailway/");
        exp.run();

//        System.exit(0);
    }

    public SimpleThreeStopsRailwayDelayPPOTest(String resultsFilePath) {
        super(0, "PPOTest04", 1, false, false, resultsFilePath, null);
        this.facade = new DevsSuiteFacade();
        this.agentParams = new HashMap<>();
        this.agentParams.put("RESULTS_FILE_PATH", resultsFilePath);
        this.agentParams.put("OBS_DIM", 23);
        this.agentParams.put("LEARNING_RATE", 3e-4);
        this.agentParams.put("HIDDEN_SIZE", 64);
        this.agentParams.put("L2", 1e-3);
        this.agentParams.put("DISCOUNT_RATE", 0.99F);
        this.agentParams.put("LAMBDA_GAE", 0.96F);
        this.agentParams.put("HORIZON", Integer.MAX_VALUE);
        this.agentParams.put("TARGET_KL", 0.05F);
        this.agentParams.put("EPOCHS", 10);
        this.agentParams.put("EPSILON_CLIP", 0.3F);
        this.agentParams.put("ENTROPY_FACTOR", 0.002F);
        float[][] actionSpace = new float[][]{
                {0F, 0F, 0F},
                {960F, 0F, 0F},{240F, 0F, 0F},{60F, 0F, 0F},
                {0F, 960F, 0F},{0F, 240F, 0F},{0F, 60F, 0F},
                {0F, 0F, 960F},{0F, 0F, 240F},{0F, 0F, 60F}};
//        float[][] actionSpace = new float[][]{
//                {0F, 0F, 0F},
//                {960F, 0F, 0F},{480F, 0F, 0F},{240F, 0F, 0F},{120F, 0F, 0F},{60F, 0F, 0F},
//                {0F, 960F, 0F},{0F, 480F, 0F},{0F, 240F, 0F},{0F, 120, 0F},{0F, 60F, 0F},
//                {0F, 0F, 960F},{0F, 0F, 480F},{0F, 0F, 240F},{0F, 0F, 120},{0F, 0F, 60F}};
        this.agentParams.put("ACTION_SPACE", actionSpace);
        this.agentParams.put("ACTION_DIM", actionSpace.length);
        this.agentParams.put("NUMBER_WORKERS", 10 );
        this.agentParams.put("EPISODES_WORKER", 10000);
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
        TestAgent agent = new TestAgent(
                resultsFilePath,
                new MinMaxScaler((double[]) agentParams.get("OBS_MIN"), (double[]) agentParams.get("OBS_MAX")),
                (float[][])agentParams.get("ACTION_SPACE"));

        Environment env = factory.createInstance();
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
            storeTrace(env.getTrace());
            storeAppliedActions(agent.getAppliedActions(), (Integer) agentParams.get("ACTION_DIM"));
            // reset agent
            agent.episodeFinished();

            if (i % 1 == 0)
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

    private void storeAppliedActions(Map<Double, float[]> actions, int actionDim){
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
                float[] action = actions.get(time);
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
}
