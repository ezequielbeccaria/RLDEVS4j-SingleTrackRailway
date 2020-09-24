package rldevs4j.singletrackrailway.factory;

import org.nd4j.linalg.factory.Nd4j;

import java.io.Serializable;
import java.util.Arrays;

public class RandomDelayGenerator implements Serializable {
    private static RandomDelayGenerator instance;
    private double[] delays;

    private RandomDelayGenerator(){
        delays = new double[3];
        Arrays.fill(delays, 0D);
    }

    //static block initialization for exception handling
    static{
        try{
            instance = new RandomDelayGenerator();
        }catch(Exception e){
            throw new RuntimeException("Exception occured in creating RandomDelayGenerator instance");
        }
    }

    public static RandomDelayGenerator getInstance(){
        return instance;
    }

    public void updateDelays(){
        Arrays.fill(delays, 0D);
        int delayForTrain = Nd4j.getRandom().nextInt(3);
        int delayMin = Nd4j.getRandom().nextInt(7,11) ;
        delays[delayForTrain] = delayMin * 60D;
    }

    public double getDelay(int trainId){
        return delays[trainId];
    }

}
