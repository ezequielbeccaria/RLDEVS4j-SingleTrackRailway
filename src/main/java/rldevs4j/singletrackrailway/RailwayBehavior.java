package rldevs4j.singletrackrailway;

import java.util.ArrayList;
import java.util.Arrays;
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
import rldevs4j.singletrackrailway.entity.EntryType;
import rldevs4j.singletrackrailway.entity.FinalEvent;
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
    private List<Double> trainsXSection; //Number of trains in each section
    private Map<Integer, BlockSection> trainsInSection; //Section where is each train
    private Map<Integer, TrainEvent> lastTrainEvents; //Last event for each train
    private final List<TimeTable> timeTables; //Last event for each train
    private final List<Train> trains;
    private Continuous action;
    private Double clock;
    private Double nextNotifTime;
    private Double notifInterval = 300D;
    //every time an arrival happens, the value for the arrival delay is updated
    private Map<Integer, List<Float>> trainsArribals; //Used to calc reward at the end of the episode
    private int[] trainsArrivalCount;
    private boolean finalEvent;
    private boolean test;

    public RailwayBehavior(BlockSectionTreeMap sections, List<Train> trains, List<TimeTable> timeTables, boolean test) {
        this.sections = sections;                        
        this.timeTables = timeTables;
        this.trains = trains;
        this.test = test;
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
        trainsArribals = new HashMap<>();
        for(Train t : trains){            
            TrainEvent te = new TrainEvent(t.getId(), "initial", t.getPosition(), 0D, t.getTimeTable().getDelay(),0, false);
            lastTrainEvents.put(t.getId(), te);
            this.trasition(te, 0D);
            //Trains arribals 
            TimeTable tt = timeTables.get(t.getId());
            List<Float> arribalTimes = new ArrayList<>();
            for(TimeTableEntry tte : tt.getDetails()){
                if(EntryType.ARRIVAL.equals(tte.getType())){
                    arribalTimes.add(-1000F); //Max default delay value
                }
            }
            trainsArribals.put(t.getId(), arribalTimes);
        }
        trainsArrivalCount = new int[trains.size()];
        Arrays.fill(trainsArrivalCount, 0);
        finalEvent = false;
        nextNotifTime = 0D;
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
        }   
        if(e instanceof Continuous){
            action = (Continuous) e;           
        }
        if(e instanceof FinalEvent){
            finalEvent = true;         
        }        
        if(clock == null || time > clock)
            clock = time;
    }
    
    @Override
    public List<Double> observation(){
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
        return obs;
    }
    
    
    public INDArray observationINDArray() {
        return Nd4j.create(this.observation());
    }

    @Override
    /**
     * The reward is emited only at the end of the training episode.
     */
    public float reward() {
        float reward = 0F;
        for(TrainEvent te : lastTrainEvents.values()){
            if(te.isArrival() && !te.isComputed()){
                trainsArrivalCount[te.getId()] = trainsArrivalCount[te.getId()]+1;
                TimeTableEntry tte = timeTables.get(te.getId()).getNextArribalEntry(te.getTTEntryId());
                reward += test ? tte.getTime() - clock : 0F;
                trainsArribals.get(te.getId()).set(trainsArrivalCount[te.getId()]-1, 0F);
//                trainsArribals.get(te.getId()).set(trainsArrivalCount[te.getId()]-1, new Float(tte.getTime() - clock));

                te.computed();
            }
        }
        if(action!=null && !test)
            reward -= sum(action.getValue());
        reward += finalEvent?calcFinalReward():0F;
        return reward;
    }

    private float calcFinalReward(){
        float reward = 0F;
        for(List<Float> l : trainsArribals.values()){
            for(Float r : l){
                reward += r;
            }
        }
        return reward;
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
            Map<String,Map<String,Float>> content = new HashMap<>();
            for(int i=0;i<lastTrainEvents.size();i++){
                Map<String,Float> c = new HashMap<>();
                c.put("update", action.getValue()[i]);                
                content.put(lastTrainEvents.get(i).getName(), c);
            }    
            ExogenousEventActivation eea = new ExogenousEventActivation(content);        
            return eea;
        }         
        return null;
    }

    @Override
    public List<Event> getAllActios() {
        return null;
    }

    @Override
    public boolean notifyAgent() {
        if(action != null){ //the last event was an action
            action = null;
            return false;
        }

        if(clock==0D){
            nextNotifTime = clock + notifInterval;
            return true;
        }


        if(finalEvent){ //the last event generates the reward
            nextNotifTime = clock + notifInterval;
            return true;
        }

        //if some of the last events was a train arrival
        for(TrainEvent te : lastTrainEvents.values()){
            if(te.isArrival()) {
                te.setArribal(false); // to avoid computing reward twise
                nextNotifTime = clock + notifInterval;
                return true;
            }
        }

        if(clock >= nextNotifTime) {
            nextNotifTime = clock + notifInterval;
            return true;
        }

        return false; //do not notify the agent
    }

    private float sum(float[] a){
        float sum = 0F;
        for(int i=0;i<a.length;i++)
            sum += a[i];
        return sum;
    }
}
