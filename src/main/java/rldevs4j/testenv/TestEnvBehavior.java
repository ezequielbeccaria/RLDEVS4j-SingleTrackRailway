package rldevs4j.testenv;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import rldevs4j.base.env.gsmdp.Behavior;
import rldevs4j.base.env.gsmdp.evgen.ExogenousEventActivation;
import rldevs4j.base.env.msg.Continuous;
import rldevs4j.base.env.msg.Event;

import java.util.List;

public class TestEnvBehavior implements Behavior {
    private double currentTime;
    private int currentPos;
    private int minPos = 0;
    private int maxPos = 8;
    private int[] actionSpace = new int[]{-1,0,1,-1,0,1,-1,0,1};
//    private int[] actionSpace = new int[]{-1,0,1};
    private int counter;
    private Event lastEvent;

    @Override
    public void initialize() {
        currentPos = Nd4j.getRandom().nextInt(1, 7);
        counter = 0;
        currentTime = 0D;
    }

    @Override
    public void trasition(Event e, double time) {
        lastEvent = e;
        currentTime = time;
        if(e.getId()!=99){
            int a = actionSpace[e.getId()];
            if(a==-1 && currentPos>minPos)
                currentPos--;
            else if(a==1 && currentPos<maxPos)
                currentPos++;
            counter++;
        }
    }

    @Override
    public INDArray observation() {
        INDArray obs = Nd4j.zeros(maxPos+2);
        obs.putScalar(currentPos, 1);
        obs.putScalar(maxPos+1, currentTime);
        return obs;
    }

    @Override
    public float reward() {
        if(currentPos==0)
            return 0.1F;
        if(currentPos==8)
            return 1F;
        return 0F;
    }

    @Override
    public boolean done() {
        return counter==100;
    }

    @Override
    public List<Event> enabledActions() {
        return null;
    }

    @Override
    public ExogenousEventActivation activeEvents() {
        return null;
    }

    @Override
    public List<Event> getAllActios() {
        return null;
    }

    @Override
    public boolean notifyAgent() {
        return lastEvent.getId()==99 || counter==100;
    }
}
