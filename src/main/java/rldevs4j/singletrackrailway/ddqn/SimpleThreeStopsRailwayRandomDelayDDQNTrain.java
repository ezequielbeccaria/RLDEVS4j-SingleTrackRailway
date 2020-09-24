package rldevs4j.singletrackrailway.ddqn;

import facade.DevsSuiteFacade;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.nd4j.linalg.api.rng.Random;
import rldevs4j.agents.ac.A3C;
import rldevs4j.agents.ac.ACWorkerTest;
import rldevs4j.agents.dqn.DDQNTest;
import rldevs4j.agents.dqn.Model;
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

/**
 *
 * @author Ezequiel Beccaria
 */
public class SimpleThreeStopsRailwayRandomDelayDDQNTrain extends Experiment{
    private DevsSuiteFacade facade;
    private final double EPISODE_MAX_TIME=3000;
    private final Map<String, Object> agentParams;
    protected UIServer uiServer;
    private int EPISODES = 50000;
    private double[][] delayTestScenarios = new double[][]{
            {7D*60D,0D,0D},{8D*60D,0D,0D},{9D*60D,0D,0D},{10D*60D,0D,0D},
            {0D,7D*60D,0D},{0D,8D*60D,0D},{0D,9D*60D,0D},{0D,10D*60D,0D},
            {0D,0D,7D*60D},{0D,0D,8D*60D},{0D,0D,9D*60D},{0D,0D,10D*60D}};

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Experiment exp = new SimpleThreeStopsRailwayRandomDelayDDQNTrain();
        exp.run();
        exp.test();
        System.exit(0);
    }

    public SimpleThreeStopsRailwayRandomDelayDDQNTrain() {
        super("DDQN", 5, false, false, "/home/ezequiel/experiments/SimpleThreeStopsRailway/DDQN_RandomDelay/", null);
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

        EnvironmentFactory factory = new SimpleThreeStopsRailwayFactory(EPISODE_MAX_TIME, new double[]{0D,0D,0D}, true, false);
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
            double estimatedTimeMinutes = result.getAverageTime().get(result.size()-1)*(EPISODES-i)/60000;
            int hours = (int) (estimatedTimeMinutes / 60); //since both are ints, you get an int
            int minutes = (int) (estimatedTimeMinutes % 60);
            logger.log(Level.INFO, "Estimated time to complete experiment: {0}:{1} Hs", new Object[]{hours, minutes});
        }
        logger.log(Level.INFO, "Experiment {1} Training Finalized. Avg-Reward: {0}", new Object[]{result.getLastAverageReward(), experiment});
        agent.saveModel(resultsFilePath+name+"_"+experiment);

        return result; //Training results
    }

    @Override
    public void test(){

        ExperimentResult result = new ExperimentResult();

        for(int i=0;i<delayTestScenarios.length;i++) {
            EnvironmentFactory factory = new SimpleThreeStopsRailwayFactory(EPISODE_MAX_TIME, delayTestScenarios[i], false, true, false);
            Environment env = factory.createInstance();
            env.initialize(); //initialize model state

            Model model = new Model(agentParams);
            try {
                model.loadModel(resultsFilePath + name + "_" + 3);
            } catch (IOException e) {
                e.printStackTrace();
            }

            DDQNTest testAgent = new DDQNTest(
                    "DDQNTest",
                    (Preprocessing) agentParams.get("PREPROCESSING"),
                    model,
                    agentParams);

            RLEnvironment rlEnv = new RLEnvironment(testAgent, env);

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
            result.addResult(testAgent.getTotalReward(), finishTime - initTime);
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
