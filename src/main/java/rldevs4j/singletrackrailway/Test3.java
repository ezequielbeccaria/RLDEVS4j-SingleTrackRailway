package rldevs4j.singletrackrailway;

import facade.DevsSuiteFacade;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import org.math.plot.Plot2DPanel;
import org.nd4j.linalg.api.ndarray.INDArray;
import rldevs4j.base.env.Environment;
import rldevs4j.base.env.msg.Step;
import rldevs4j.singletrackrailway.factory.SingleTrackRailwayEnvFactory;
import rldevs4j.utils.CollectionsUtils;

/**
 *
 * @author Ezequiel Beccaria
 */
public class Test3 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new Test3();
    }

    public Test3() {
        SingleTrackRailwayEnvFactory factory = new SingleTrackRailwayEnvFactory();
        
        Environment env = factory.createEnv03();
        env.initialize(); //initialize model state
        DevsSuiteFacade facade = new DevsSuiteFacade(env);
        
        facade.simulateToTime(50);
        
        plotTrace(env.getTrace());
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
        plot.addLinePlot("bs1", Color.DARK_GRAY, new double[]{0D, 2000D}, new double[]{50D, 2000D});   
        plot.addLinePlot("bs2", Color.DARK_GRAY, new double[]{0D, 4000D}, new double[]{50D, 4000D});   
        plot.addLinePlot("bs3", Color.DARK_GRAY, new double[]{0D, 6000D}, new double[]{50D, 6000D});   
        plot.addLinePlot("bs4", Color.DARK_GRAY, new double[]{0D, 10000D}, new double[]{50D, 10000D});   
        plot.addLinePlot("bs5", Color.DARK_GRAY, new double[]{0D, 12000D}, new double[]{50D, 12000D});   
        
        // put the PlotPanel in a JFrame, as a JPanel
        JFrame frame = new JFrame("Results");
        frame.setSize(600, 600);
        frame.setContentPane(plot);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
}
