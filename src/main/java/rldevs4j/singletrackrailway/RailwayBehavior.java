package rldevs4j.singletrackrailway;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import rldevs4j.base.env.gsmdp.Behavior;
import rldevs4j.base.env.gsmdp.evgen.ExogenousEventActivation;
import rldevs4j.base.env.msg.Continuous;
import rldevs4j.base.env.msg.Event;
import rldevs4j.singletrackrailway.entity.BlockSection;
import rldevs4j.singletrackrailway.entity.BlockSectionTreeMap;
import rldevs4j.singletrackrailway.entity.TimeTable;
import rldevs4j.singletrackrailway.entity.TimeTableEntry;
import rldevs4j.singletrackrailway.entity.Train;
import rldevs4j.singletrackrailway.entity.TrainEvent;

/**
 *
 * @author Ezequiel Beccaria
 */
public class RailwayBehavior implements Behavior {
    private final BlockSectionTreeMap sections;
    private final List<Double> trainsXSection; //Number of trains in each section
    private final Map<Integer, BlockSection> trainsInSection; //Section where is each train
    private final Map<Integer, TrainEvent> lastTrainEvents; //Last event for each train
    private final List<TimeTable> timeTables; //Last event for each train
    private final List<Event> actions;
    private Continuous action;
    private Double clock;

    public RailwayBehavior(BlockSectionTreeMap sections, List<Train> trains, List<TimeTable> timeTables) {
        this.sections = sections;        
        this.trainsXSection = new ArrayList<>(sections.size());
        for(int i=0;i<sections.size();i++)
            this.trainsXSection.add(0D);
        this.trainsInSection = new HashMap<>();        
        this.actions = new ArrayList<>();
        this.lastTrainEvents = new HashMap<>();
        this.timeTables = timeTables;
        for(Train t : trains){            
            TrainEvent te = new TrainEvent(t.getId(), "Initial", t.getPosition(), t.getSpeed(), 0, false);
            lastTrainEvents.put(t.getId(), te);
            this.trasition(null, te);
        }
    }

    @Override
    public void trasition(INDArray state, Event e) {
        if(e instanceof TrainEvent){
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
        if(e instanceof Continuous){
            action = (Continuous) e;           
        }
        clock = state!=null?state.getDouble(state.columns()-1):0D;
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
        
        List<BlockSection> bsList = (List<BlockSection>) sections.values();
        for(int i=0;i<bsList.size();i++){
            obs.add(bsList.get(i).isAvailable()?1D:0D);
        }        
        obs.add(clock); // add clock

        return Nd4j.create(obs);
    }

    @Override
    public float reward() {
        float reward = 0F;
        for(TrainEvent te : lastTrainEvents.values()){
            if(te.isArribal()){
                TimeTableEntry tte = timeTables.get(te.getId()).getCurrentEntry();
                if(te.getPosition().equals(tte.getPosition())){ //If the train if in the arribal position
                    reward += tte.getTime() - clock;                
                }
                te.setArribal(false); // to avoid computing reward twise
            }
        }
        return reward;
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
        if(action != null){
            for(int i=0;i<action.getValue().length;i++){
                Map<String,Double> c = new HashMap<>();   
                c.put("update", action.getValue()[i]);                
                content.put(lastTrainEvents.get(i).getName(), c);
            }    
        }
        ExogenousEventActivation eea = new ExogenousEventActivation(content);
        action = null;
        return eea;
    }

    @Override
    public List<Event> getAllActios() {
        return actions;
    }
}
