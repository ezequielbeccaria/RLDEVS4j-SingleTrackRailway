package rldevs4j.singletrackrailway;

import java.util.ArrayList;
import java.util.List;
import org.nd4j.linalg.api.ndarray.INDArray;
import rldevs4j.base.env.Environment;
import rldevs4j.base.env.StateSpaceInfo;
import rldevs4j.base.env.gsmdp.StateObserver;
import rldevs4j.base.env.gsmdp.evgen.FixedTimeExogenousEventGen;
import rldevs4j.base.env.msg.Event;
import rldevs4j.base.env.msg.EventType;
import rldevs4j.base.env.msg.Step;
import rldevs4j.singletrackrailway.entity.BlockSectionTreeMap;
import rldevs4j.singletrackrailway.entity.FinalEvent;
import rldevs4j.singletrackrailway.entity.TimeTable;
import rldevs4j.singletrackrailway.entity.Train;

/**
 *
 * @author Ezequiel Beccaria
 */
public class SingleTrackRailwayEnv extends Environment{
    private final StateObserver so;
    private final List<Train> trains;
    private final FixedTimeExogenousEventGen episodeFinishEventGen;
    
    public SingleTrackRailwayEnv(String name, List<Train> trains, BlockSectionTreeMap sections, double simulationTime, boolean debug) {
        super(name);
        List<TimeTable> tTables = new ArrayList<>();       
        for(Train t : trains)
            tTables.add(t.getTimeTable().deepCopy());
        RailwayBehavior rb = new RailwayBehavior(sections, trains, tTables);
        so = new StateObserver(rb, debug);
        episodeFinishEventGen = new FixedTimeExogenousEventGen("episode_finish", new FinalEvent(999, "final_event", EventType.exogenous), new Double[]{simulationTime});
        this.trains = trains;
        
        add(so);        
        add(episodeFinishEventGen); //This generator is responsible for sending an event at the end of the simulation.
        addCoupling(episodeFinishEventGen, "out", so, "event");
        for(Train t : trains){            
            add(t);
            addCoupling(t, "out", so, "event");
            addCoupling(so, "event_genearator", t, "in");
        }        
        
        //add external couplings
        addCoupling(so, "step", this, "step");
        addCoupling(this, "action", so, "event");
    }

    @Override
    public void initialize() {
        super.initialize();
        so.initialize();
        trains.forEach((t) -> {
            t.initialize();
        });
    }

    @Override
    public Environment clone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Event> getActionSpace() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Step> getTrace() {
        return this.so.getTrace();
    }
    
}
