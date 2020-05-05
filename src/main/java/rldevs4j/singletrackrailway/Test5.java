package rldevs4j.singletrackrailway;

import facade.DevsSuiteFacade;
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
import rldevs4j.utils.CollectionsUtils;

/**
 *
 * @author Ezequiel Beccaria
 */
public class Test5 extends Experiment{
    private DevsSuiteFacade facade;
    private final int EPISODES = 1;        
    private final double EPISODE_MAX_TIME=3000;    
    private final Map<String, Object> agentParams;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Experiment exp = new Test5();
        exp.run();
        
        System.exit(0);
    }

    public Test5() {
        super(0, "Test5", 1, false, false, "/home/ezequiel/experiments/", null);
        this.facade = new DevsSuiteFacade();                
        this.agentParams = new HashMap<>();
        this.agentParams.put("OBS_DIM", 25);
        this.agentParams.put("ACTION_DIM", 2);
        this.agentParams.put("LEARNING_RATE", 1e-4);
        this.agentParams.put("HIDDEN_SIZE", 128);
        this.agentParams.put("TAHN_ACTION_LIMIT", 300D);
        this.agentParams.put("L2", 1e-3);
        this.agentParams.put("DISCOUNT_RATE", 0.99);
        this.agentParams.put("LAMBDA_GAE", 0.96);
        this.agentParams.put("EPSILON_CLIP", 0.2);
        this.agentParams.put("TARGET_KL", 0.02);
        this.agentParams.put("EPOCHS", 3);
        this.agentParams.put("HORIZON", 10);
        this.agentParams.put("ENTROPY_COEF", 0.02);
    }
    
    private void plotTrace(List<Step> trace){
        // create your PlotPanel (you can use it as a JPanel)
        Plot2DPanel plot = new Plot2DPanel();
        
        List<Double> time = new ArrayList<>();
        List<Double> t0pos = new ArrayList<>();
        List<Double> t1pos = new ArrayList<>();
        
        for(int i=0;i<trace.size();i++){
            Step step = trace.get(i);
            INDArray obs = step.getObservation();
            time.add(obs.getDouble(obs.length()-1));
            t0pos.add(obs.getDouble(0));
            t1pos.add(obs.getDouble(2));            
        }
        
        // add a line plot to the PlotPanel                
        plot.addLinePlot("Train0", CollectionsUtils.DoubleToArray(time), CollectionsUtils.DoubleToArray(t0pos));   
        plot.addLinePlot("Train1", CollectionsUtils.DoubleToArray(time), CollectionsUtils.DoubleToArray(t1pos));   
//        plot.addLinePlot("bs1", Color.DARK_GRAY, new double[]{0D, 2000D}, new double[]{EPISODE_MAX_TIME, 2000D});   
//        plot.addLinePlot("bs2", Color.DARK_GRAY, new double[]{0D, 4000D}, new double[]{EPISODE_MAX_TIME, 4000D});   
//        plot.addLinePlot("bs3", Color.DARK_GRAY, new double[]{0D, 6000D}, new double[]{EPISODE_MAX_TIME, 6000D});   
//        plot.addLinePlot("bs4", Color.DARK_GRAY, new double[]{0D, 10000D}, new double[]{EPISODE_MAX_TIME, 10000D});   
//        plot.addLinePlot("bs5", Color.DARK_GRAY, new double[]{0D, 12000D}, new double[]{EPISODE_MAX_TIME, 12000D});   
        
        // put the PlotPanel in a JFrame, as a JPanel
        JFrame frame = new JFrame("Results");
        frame.setSize(600, 600);
        frame.setContentPane(plot);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public ExperimentResult experiment(Random rnd, int experiment) {
        ExperimentResult result = new ExperimentResult();
        
        SingleTrackRailwayEnvFactory factory = new SingleTrackRailwayEnvFactory();
        
        Environment env = factory.createEnv03(false);
        env.initialize(); //initialize model state
        
        Agent agent = AgentFactory.ppo(agentParams);
        
        RLEnvironment rlEnv = new RLEnvironment(agent, env);
        
        facade = new DevsSuiteFacade(rlEnv);      
        
        logger.log(Level.INFO, "Training Start. Experiment #{0}", new Object[]{experiment});
        
        for(int i=1;i<=EPISODES;i++){        
            //Inititalize environment and simulator
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
            
            if(i==EPISODES)
                plotTrace(env.getTrace());
            
            if(i%1==0)
                logger.log(Level.INFO, "Episode {0} Terminated. Reward: {1}. Avg-Reward: {2}", new Object[]{i, result.getLastEpisodeReward(), result.getLastAverageReward()});
        }        
        logger.log(Level.INFO, "Training Finalized. Avg-Reward: {0}", new Object[]{result.getLastAverageReward()});
              
        return result; //Training results    
    }    
}
