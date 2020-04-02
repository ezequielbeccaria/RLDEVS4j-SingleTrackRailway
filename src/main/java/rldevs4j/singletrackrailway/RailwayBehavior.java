package rldevs4j.singletrackrailway;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import rldevs4j.base.env.gsmdp.Behavior;
import rldevs4j.base.env.gsmdp.evgen.ExogenousEventActivation;
import rldevs4j.base.env.msg.Event;
import rldevs4j.singletrackrailway.entity.BlockSection;
import rldevs4j.singletrackrailway.entity.BlockSectionTreeMap;
import rldevs4j.singletrackrailway.entity.Train;
import rldevs4j.singletrackrailway.entity.TrainEvent;

/**
 *
 * @author Ezequiel Beccaria
 */
public class RailwayBehavior implements Behavior {
    private final BlockSectionTreeMap sections;
    private final List<Train> trains;
    private final List<Double> trainsXSection; //Number of trains in each section
    private final Map<Integer, BlockSection> trainsInSection; //Section where is each train
    private final Map<Integer, TrainEvent> lastTrainEvents; //Last event for each train
    private final List<Event> actions;

    public RailwayBehavior(BlockSectionTreeMap sections, List<Train> trains) {
        this.sections = sections;
        this.trains = trains;
        this.trainsXSection = new ArrayList<>(sections.size());
        for(int i=0;i<sections.size();i++)
            this.trainsXSection.add(0D);
        this.trainsInSection = new HashMap<>();        
        this.actions = new ArrayList<>();
        this.lastTrainEvents = new HashMap<>();
    }

    @Override
    public void trasition(INDArray state, Event e) {
        TrainEvent tEvent = (TrainEvent) e;
        //Get trains current blocksection based on train position
        BlockSection bs = sections.get(tEvent.getPosition()); 
        if(trainsInSection.containsKey(tEvent.getId())){
            BlockSection prevBs = trainsInSection.get(tEvent.getId());
            trainsXSection.set(prevBs.getId(), trainsXSection.get(prevBs.getId()) - 1D);                         
        }        
        trainsInSection.put(tEvent.getId(), bs);           
        trainsXSection.set(bs.getId(), trainsXSection.get(bs.getId()) + 1D);
        lastTrainEvents.put(e.getId(), tEvent);        
    }

    @Override
    public INDArray observation() {
        List<Double> obs = new ArrayList<>();
        for(Integer k : lastTrainEvents.keySet()){
            TrainEvent te = lastTrainEvents.get(k);
            obs.add(te.getPosition());
            obs.add(te.getSpeed());
        }
        obs.addAll(trainsXSection);    

        System.out.println(obs); //DEBUG

        return Nd4j.create(obs);
    }

    @Override
    public float reward() {
        return 0F;
    }

    @Override
    public boolean done() {
        return false;
    }

    @Override
    public List<Event> enabledActions() {
        return actions;
    }

    @Override
    public ExogenousEventActivation activeEvents() {
        Map<String,Map<String,Double>> content = new HashMap<>();
        Map<String,Double> c = new HashMap<>();
        ExogenousEventActivation eea = new ExogenousEventActivation(content);
        return eea;
    }

    @Override
    public List<Event> getAllActios() {
        return actions;
    }    
    
}
