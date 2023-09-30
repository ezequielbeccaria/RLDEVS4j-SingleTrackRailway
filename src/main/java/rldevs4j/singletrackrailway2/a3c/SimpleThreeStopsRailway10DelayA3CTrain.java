package rldevs4j.singletrackrailway2.a3c;

import facade.DevsSuiteFacade;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.rng.Random;
import rldevs4j.base.agent.Agent;
import rldevs4j.base.agent.preproc.NoPreprocessing;
import rldevs4j.base.agent.preproc.Preprocessing;
import rldevs4j.base.env.Environment;
import rldevs4j.base.env.RLEnvironment;
import rldevs4j.base.env.factory.EnvironmentFactory;
import rldevs4j.base.env.msg.Step;
import rldevs4j.experiment.Experiment;
import rldevs4j.experiment.ExperimentResult;
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
import rldevs4j.agents.ac.A3C;
import rldevs4j.agents.ac.ACWorkerTest;

/**
 *
 * @author Ezequiel Beccaria
 */
public class SimpleThreeStopsRailway10DelayA3CTrain extends Experiment{
    private final double EPISODE_MAX_TIME=3000;
    private final Map<String, Object> agentParams;
    protected UIServer uiServer;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Experiment exp = new SimpleThreeStopsRailway10DelayA3CTrain();
        exp.run();
        exp.test();
//        System.exit(0);
    }

    public SimpleThreeStopsRailway10DelayA3CTrain() {
        super("A3C", 5, false, true, "/home/ezequiel/experiments/SimpleThreeStopsRailway2/A3C_10Delay/", null);
        this.agentParams = new HashMap<>();
        this.agentParams.put("RESULTS_FILE_PATH", resultsFilePath);
        this.agentParams.put("OBS_DIM", 23);
        this.agentParams.put("LEARNING_RATE_ACTOR", 1e-6);
        this.agentParams.put("LEARNING_RATE_CRITIC", 1e-6);
        this.agentParams.put("HIDDEN_SIZE", 1024);
        this.agentParams.put("HIDDEN_ACT", Activation.TANH);
        this.agentParams.put("L2", 1e-6);
        this.agentParams.put("DISCOUNT_RATE", 0.95D);
        this.agentParams.put("HORIZON", Integer.MAX_VALUE);
        this.agentParams.put("ENTROPY_FACTOR", 2F);
        float[][] actionSpace = new float[][]{
                {0F, 0F, 0F},
                {960F, 0F, 0F},{480F, 0F, 0F},{240F, 0F, 0F},{120F, 0F, 0F},{60F, 0F, 0F},
                {-960F, 0F, 0F},{-480F, 0F, 0F},{-240F, 0F, 0F},{-120F, 0F, 0F},{-60F, 0F, 0F},
                {0F, 960F, 0F},{0F, 480F, 0F},{0F, 240F, 0F},{0F, 120F, 0F},{0F, 60F, 0F},
                {0F, -960F, 0F},{0F, -480F, 0F},{0F, -240F, 0F},{0F, -120F, 0F},{0F, -60F, 0F},
                {0F, 0F, 960F},{0F, 0F, 480F},{0F, 0F, 240F},{0F, 0F, 120},{0F, 0F, 60F},
                {0F, 0F, -960F},{0F, 0F, -480F},{0F, 0F, -240F},{0F, 0F, -120},{0F, 0F, -60F}};
        this.agentParams.put("ACTION_SPACE", actionSpace);
        this.agentParams.put("ACTION_DIM", actionSpace.length);
        this.agentParams.put("NUMBER_WORKERS", 5 );
        this.agentParams.put("EPISODES_WORKER", 10000);
        this.agentParams.put("SIMULATION_TIME", EPISODE_MAX_TIME);
        this.agentParams.put("DEBUG", true);
        double[] minFeatureValues = {0D, -27D, 0D, 0D, -27D, 0D, 0D, -27D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D};
        this.agentParams.put("OBS_MIN", minFeatureValues);
        double[] maxFeatureValues = {15200D, 27D, 10D, 15200D, 27D, 10D, 15200D, 27D, 10D, 3D, 3D, 3D, 3D, 3D, 3D, 3D, 3D, 1D, 1D, 1D, 1D, 1D, 1D, 1D, 1D, 3000};
        this.agentParams.put("OBS_MAX", maxFeatureValues);
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

        EnvironmentFactory factory = new SimpleThreeStopsRailwayFactory(EPISODE_MAX_TIME, new double[]{10D*60D,0D,0D}, false,false);
        
        A3C global = this.a3cDiscrete(agentParams, factory);

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

        EnvironmentFactory factory = new SimpleThreeStopsRailwayFactory(EPISODE_MAX_TIME, new double[]{10D*60D,0D,0D}, false, false);
        Environment env = factory.createInstance();
        env.initialize(); //initialize model state

        A3C agent = this.a3cDiscrete(agentParams, factory);
        try {
            agent.loadModel(resultsFilePath+name+"_"+5);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Agent workerTest = new ACWorkerTest(
                1,
                agent.getActor(),
                (Preprocessing) agentParams.get("PREPROCESSING"),
                (float[][]) agentParams.get("ACTION_SPACE"));

        RLEnvironment rlEnv = new RLEnvironment(workerTest, env);

        logger.log(Level.INFO, "Training Start.");

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
        result.addResult(workerTest.getTotalReward(), finishTime - initTime);
        storeTrace(env.getTrace(), 1);
        logger.log(Level.INFO, "Test Terminated. Reward: {1}. Avg-Reward: {2}", new Object[]{result.getLastEpisodeReward(), result.getLastAverageReward()});
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

    private static rldevs4j.agents.ac.A3C a3cDiscrete(Map<String,Object> params, EnvironmentFactory envFactory){
        rldevs4j.agents.ac.ACCritic critic = new rldevs4j.agents.ac.FFCritic(
                (int) params.get("OBS_DIM"),
                (double) params.get("LEARNING_RATE_CRITIC"),
                (double) params.getOrDefault("L2", 0.001D),
                (int) params.get("HIDDEN_SIZE"),
                (Activation) params.get("HIDDEN_ACT"),
                (StatsStorage) params.get("STATS_STORAGE"));
        rldevs4j.agents.ac.DiscreteACActor actor = new rldevs4j.agents.ac.FFDiscreteActor(
                (int) params.get("OBS_DIM"),
                (int) params.get("ACTION_DIM"),
                (double) params.get("LEARNING_RATE_ACTOR"),
                (double) params.getOrDefault("L2", 0.001D),
                (float) params.getOrDefault("ENTROPY_FACTOR", 0.001F),
                (int) params.get("HIDDEN_SIZE"),
                (Activation) params.get("HIDDEN_ACT"),
                (StatsStorage) params.get("STATS_STORAGE"));
        return new rldevs4j.agents.ac.A3C(actor, critic, (Preprocessing) params.get("PREPROCESSING"), envFactory, params);
    }
}
