package rldevs4j.singletrackrailway;

import facade.DevsSuiteFacade;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.swing.JFrame;
import org.math.plot.Plot2DPanel;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.rng.Random;
import rldevs4j.base.agent.preproc.NoPreprocessing;
import rldevs4j.base.env.Environment;
import rldevs4j.base.env.RLEnvironment;
import rldevs4j.base.env.msg.Step;
import rldevs4j.experiment.Experiment;
import rldevs4j.experiment.ExperimentResult;
import rldevs4j.agents.dummy.DummyAgent;
import rldevs4j.singletrackrailway.factory.SingleTrackRailwayEnvFactory;
import rldevs4j.utils.CollectionsUtils;

/**
 *
 * @author Ezequiel Beccaria
 */
public class Test4 extends Experiment{
    private DevsSuiteFacade facade;
    private final int EPISODES = 5;        
    private final double EPISODE_MAX_TIME=3000;    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Experiment exp = new Test4();
        exp.run();
        
        System.exit(0);
    }

    public Test4() {
        super(0, "Test4", 1, false, false, "/home/ezequiel/experiments/", null);
        this.facade = new DevsSuiteFacade();        
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
        
        Environment env = factory.createEnv03(true);
        env.initialize(); //initialize model state
        
        DummyAgent agent = new DummyAgent("dummy_agent", new NoPreprocessing());
        
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
            plotTrace(env.getTrace());
            if(i%1==0)
                logger.log(Level.INFO, "Episode {0} Terminated. Reward: {1}. Avg-Reward: {2}", new Object[]{i, result.getLastEpisodeReward(), result.getLastAverageReward()});
        }
        
        logger.log(Level.INFO, "Training Finalized. Avg-Reward: {0}", new Object[]{result.getLastAverageReward()});
              
        return result; //Training results    
    }    
}
