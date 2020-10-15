package rldevs4j.singletrackrailway2.ppo;

import facade.DevsSuiteFacade;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.rng.Random;
import rldevs4j.agents.ppov2.PPO;
import rldevs4j.agents.ppov2.PPOWorkerTest;
import rldevs4j.base.agent.Agent;
import rldevs4j.base.agent.preproc.NoPreprocessing;
import rldevs4j.base.agent.preproc.Preprocessing;
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
public class SimpleThreeStopsRailwayRandomDelayPPOTrain extends Experiment{
    private final double EPISODE_MAX_TIME=3000;
    private final Map<String, Object> agentParams;
    protected UIServer uiServer;
    private double[][] delayTestScenarios = new double[][]{
            {7D*60D,0D,0D},{8D*60D,0D,0D},{9D*60D,0D,0D},{10D*60D,0D,0D},
            {0D,7D*60D,0D},{0D,8D*60D,0D},{0D,9D*60D,0D},{0D,10D*60D,0D},
            {0D,0D,7D*60D},{0D,0D,8D*60D},{0D,0D,9D*60D},{0D,0D,10D*60D}};

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Experiment exp = new SimpleThreeStopsRailwayRandomDelayPPOTrain();
        exp.run();
        exp.test();
        System.exit(0);
    }

    public SimpleThreeStopsRailwayRandomDelayPPOTrain() {
        super("PPO", 5, false, false, "/home/ezequiel/experiments/SimpleThreeStopsRailway/PPO_RandomDelay/", null);
        this.agentParams = new HashMap<>();
        this.agentParams.put("RESULTS_FILE_PATH", resultsFilePath);
        this.agentParams.put("OBS_DIM", 23);
        this.agentParams.put("LEARNING_RATE_ACTOR", 1e-6);
        this.agentParams.put("LEARNING_RATE_CRITIC", 1e-6);
        this.agentParams.put("HIDDEN_SIZE", 1024);
        this.agentParams.put("HIDDEN_ACT", Activation.TANH);
        this.agentParams.put("L2", 1e-4);
        this.agentParams.put("DISCOUNT_RATE", 0.95F);
        this.agentParams.put("LAMBDA_GAE", 0.9F);
        this.agentParams.put("HORIZON", Integer.MAX_VALUE);
        this.agentParams.put("TARGET_KL", 0.003F);
        this.agentParams.put("EPOCHS", 3);
        this.agentParams.put("EPSILON_CLIP", 0.4F);
        this.agentParams.put("ENTROPY_FACTOR", 2F);
//        float[][] actionSpace = new float[][]{
//                {0F, 0F, 0F},
//                {960F, 0F, 0F},{240F, 0F, 0F},{60F, 0F, 0F},
//                {0F, 960F, 0F},{0F, 240F, 0F},{0F, 60F, 0F},
//                {0F, 0F, 960F},{0F, 0F, 240F},{0F, 0F, 60F}};
        float[][] actionSpace = new float[][]{
                {0F, 0F, 0F},
                {960F, 0F, 0F},{480F, 0F, 0F},{240F, 0F, 0F},{120F, 0F, 0F},{60F, 0F, 0F},
                {0F, 960F, 0F},{0F, 480F, 0F},{0F, 240F, 0F},{0F, 120, 0F},{0F, 60F, 0F},
                {0F, 0F, 960F},{0F, 0F, 480F},{0F, 0F, 240F},{0F, 0F, 120},{0F, 0F, 60F}};
        this.agentParams.put("ACTION_SPACE", actionSpace);
        this.agentParams.put("ACTION_DIM", actionSpace.length);
        this.agentParams.put("NUMBER_WORKERS", 5 );
        this.agentParams.put("EPISODES_WORKER", 10000);
        this.agentParams.put("SIMULATION_TIME", EPISODE_MAX_TIME);
        this.agentParams.put("DEBUG", true);
        double[] minFeatureValues = {0D, -27D, 0D, -27D, 0D, -27D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D};
        this.agentParams.put("OBS_MIN", minFeatureValues);
        double[] maxFeatureValues = {15200D, 27D, 15200D, 27D, 15200D, 27D, 3D, 3D, 3D, 3D, 3D, 3D, 3D, 3D, 1D, 1D, 1D, 1D, 1D, 1D, 1D, 1D, 3000};
        this.agentParams.put("OBS_MAX", maxFeatureValues);
//        this.agentParams.put("PREPROCESSING", new MinMaxScaler(minFeatureValues, maxFeatureValues));
        this.agentParams.put("PREPROCESSING", new NoPreprocessing());
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

        EnvironmentFactory factory = new SimpleThreeStopsRailwayFactory(EPISODE_MAX_TIME, new double[]{0D,0D,0D}, true,false);
        
        PPO global = AgentFactory.ppoDiscrete(agentParams, factory);

        logger.log(Level.INFO, "Training Start. Experiment #{0}", new Object[]{experiment});

        try {
            logger.log(Level.INFO, "Training Start. Experiment #{0}", new Object[]{experiment});
            global.startTraining((Integer) agentParams.getOrDefault("NUMBER_WORKERS", 1));
            logger.log(Level.INFO, "Training Finalized. Avg-Reward: {0}", new Object[]{global.getResults().getLastAverageReward()});

            global.saveModel(resultsFilePath+name+"_"+experiment);
        } catch (InterruptedException | IOException ex) {
            Logger.getGlobal().severe(ex.getLocalizedMessage());
        }
        return global.getResults(); //Training results
    }

    @Override
    public void test(){

        ExperimentResult result = new ExperimentResult();

        for(int i=0;i<delayTestScenarios.length;i++) {
            EnvironmentFactory factory = new SimpleThreeStopsRailwayFactory(EPISODE_MAX_TIME, delayTestScenarios[i], false, true, false);
            Environment env = factory.createInstance();
            env.initialize(); //initialize model state

            PPO agent = AgentFactory.ppoDiscrete(agentParams, factory);
            try {
                agent.loadModel(resultsFilePath + name + "_" + 5);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Agent ppoWorkerTest = new PPOWorkerTest(
                    1,
                    agent.getActor(),
                    (Preprocessing) agentParams.get("PREPROCESSING"),
                    (float[][]) agentParams.get("ACTION_SPACE"));

            RLEnvironment rlEnv = new RLEnvironment(ppoWorkerTest, env);

            logger.log(Level.INFO, "TEST START.");

            //Inititalize environment and simulator
            DevsSuiteFacade facade = new DevsSuiteFacade(rlEnv);
            facade.reset();
            //Episode time start
            long initTime = System.currentTimeMillis();
            //Simulate during "episodeTime" t (minuts of the day)
            facade.simulateToTime(EPISODE_MAX_TIME);
            //Episode time stop
            long finishTime = System.currentTimeMillis();
            //Save episode results
            result.addResult(ppoWorkerTest.getTotalReward(), finishTime - initTime);
            storeTrace(env.getTrace(), i);
            logger.log(Level.INFO, "Test {0} Terminated. Reward: {1}. Avg-Reward: {2}", new Object[]{i, result.getLastEpisodeReward(), result.getLastAverageReward()});
        }
        //Write test results
        super.writeResults(result, 0, super.resultsFilePath+"/"+name+"_test.csv");
    }

    private void storeTrace(List<Step> trace, int scenario){
        FileWriter writer;
        try {
            String filename = super.resultsFilePath+"/test_"+name+"_"+scenario+"_trace.csv";
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
