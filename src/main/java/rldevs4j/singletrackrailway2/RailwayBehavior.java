package rldevs4j.singletrackrailway2;

import rldevs4j.base.env.gsmdp.Behavior;
import rldevs4j.base.env.gsmdp.evgen.ExogenousEventActivation;
import rldevs4j.base.env.msg.DiscreteEvent;
import rldevs4j.base.env.msg.Event;
import rldevs4j.singletrackrailway2.entity.*;

import java.util.*;
import rldevs4j.base.env.msg.EventType;

/**
 *
 * @author Ezequiel Beccaria
 */
public class RailwayBehavior implements Behavior {
    private final BlockSectionTreeMap sections;
    private List<Double> trainsXSection; //Number of trains in each section
    private Map<Integer, BlockSection> trainsInSection; //Section where is each train
    private Map<Integer, TrainEvent> lastTrainEvents; //Last event for each train
    private final List<Train> trains;
    private DiscreteEvent action;
    private Double clock;
    private double notifInterval = 1D;
    //every time an arrival happens, the value for the arrival delay is updated
    private int trainsArrivalCount;
    private boolean finalEvent;
    private boolean test;
    private Map<Integer, DiscreteEvent> action2Event;

    public RailwayBehavior(BlockSectionTreeMap sections, List<Train> trains, boolean test) {
        this.sections = sections;                        
        this.trains = trains;
        this.test = test;
        
        action2Event = new HashMap<>();
        int[] timeToAdd = {60, 120, 240, 480, 960}; 
        for(int i=0;i<trains.size();i++){
            for(int j=0;j<timeToAdd.length;j++){
                action2Event.put(i+j+1, new DiscreteEvent(i+j+1, "train"+i, EventType.action, timeToAdd[j]));
            }
        }
        
        initialize();
    }
    
    @Override
    public void initialize(){
        for(BlockSection bs : sections.values()){
            if(bs != null)
                bs.reset();
        }
        this.trainsInSection = new HashMap<>();        
        //trainsXSection initialization
        this.trainsXSection = new ArrayList<>(sections.size());
        for(int i=0;i<sections.size();i++)
            this.trainsXSection.add(0D);
        //train events initialization
        lastTrainEvents = new HashMap<>();
        for(Train t : trains){            
            TrainEvent te = new TrainEvent(t.getId(), "initial", t.getPosition(), 0D, t.getTimeTable().getDelay(),0, false);
            lastTrainEvents.put(t.getId(), te);
            this.trasition(te, 0D);
        }
        trainsArrivalCount = 0;
        finalEvent = false;
        clock = 0D;
    }

    @Override
    public void trasition(Event e, double time) {
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
            lastTrainEvents.put(tEvent.getId(), tEvent);       
            if(tEvent.isArrival())
                trainsArrivalCount += 1;
        }   
        if(e instanceof DiscreteEvent){
            action = (DiscreteEvent) e;
            if(action.getValue() == 0){
                action = null;
            }else{
                action = action2Event.get(action.getValue());
            }
        }
        if(e instanceof FinalEvent){
            finalEvent = true;         
        }        
        if(clock == null || time > clock)
            clock = time;
    }
    
    @Override
    public Map<String, List<Double>> observation(){
        Map<String, List<Double>> obs = new HashMap<>();
        List<Double> pos = new ArrayList<>();
        List<Double> speed = new ArrayList<>();
  
        for(Integer k : lastTrainEvents.keySet()){
            TrainEvent te = lastTrainEvents.get(k);
            pos.add(te.getPosition());
            speed.add(te.getSpeed());
        }
        
        List<BlockSection> bsList = (List<BlockSection>) sections.values();
        List<Double> sectionAvailable = new ArrayList<>();
        for(int i=0;i<bsList.size();i++){
            sectionAvailable.add(bsList.get(i).isAvailable()?1D:0D);
        }

        List<Double> time = new ArrayList<>();
        time.add(clock);
        
        obs.put("pos", pos);
        obs.put("speed", speed);
        obs.put("block_occupation", trainsXSection);
        obs.put("block_available", sectionAvailable);
        obs.put("time", time);
        
        return obs;
    }

    @Override
    /**
     * The reward is emited only at the end of the training episode.
     */
    public float reward() {
        int reward = 0;
        if(action!=null && !test)
            reward -= action.getValue();
        reward += finalEvent?calcFinalReward():0F;
        return reward;
    }

    private float calcFinalReward(){
        return (6 - trainsArrivalCount) * -1000;
    }

    @Override
    public boolean done() {
        return finalEvent;
    }

    @Override
    public List<Event> enabledActions() {
        return null;
    }

    @Override
    public ExogenousEventActivation activeEvents() {          
        if(action != null){
            Map<String,Map<String, Float>> content = new HashMap<>();
            Map<String, Float> c = new HashMap<>();
            c.put("update", Float.valueOf(action.getValue()));                
            content.put(action.getName(), c);            
            ExogenousEventActivation eea = new ExogenousEventActivation(content);        
            return eea;
        }         
        return null;
    }

    @Override
    public List<Event> getAllActions() {
        return null;
    }

    @Override
    public double getSigma() {
        return notifInterval;
    }

    private DiscreteEvent discreteActionToEvent(int value) {
        return action2Event.get(value);
    }
}
