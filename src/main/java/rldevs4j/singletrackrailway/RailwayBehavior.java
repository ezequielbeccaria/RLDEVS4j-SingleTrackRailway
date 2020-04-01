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
import rldevs4j.base.env.msg.Event;
import rldevs4j.singletrackrailway.entity.BlockSection;
import rldevs4j.singletrackrailway.entity.BlockSectionTreeMap;
import rldevs4j.singletrackrailway.entity.Train;

/**
 *
 * @author Ezequiel Beccaria
 */
public class RailwayBehavior implements Behavior {
    private final BlockSectionTreeMap sections;
    private final List<Train> trains;
    private Double[] trainsXSection; //Number of trains in each section
    private Map<Integer, BlockSection> trainsInSection; //Section where is each train
    private List<Event> actions;

    public RailwayBehavior(BlockSectionTreeMap sections, List<Train> trains) {
        this.sections = sections;
        this.trains = trains;
        this.trainsXSection = new Double[sections.size()];
        Arrays.fill(trainsXSection, 0D);
        this.trainsInSection = new HashMap<>();        
        this.actions = new ArrayList<>();
    }

    @Override
    public void trasition(INDArray state, Event e) {
        //Get trains current blocksection based on train position
        BlockSection bs = sections.get(e.getValue()); 
        Train train = trains.get(e.getId());
        if(trainsInSection.containsKey(train.getId())){
            BlockSection prevBs = trainsInSection.get(train.getId());
            trainsXSection[prevBs.getId()] -= 1D;             
        }        
        trainsInSection.put(train.getId(), bs);           
        trainsXSection[bs.getId()] += 1D;
    }

    @Override
    public INDArray observation() {
        return Nd4j.zeros(1);
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
