package rldevs4j.singletrackrailway;

import java.util.ArrayList;
import java.util.List;
import org.nd4j.linalg.api.ndarray.INDArray;
import rldevs4j.base.env.Environment;
import rldevs4j.base.env.StateSpaceInfo;
import rldevs4j.base.env.gsmdp.StateObserver;
import rldevs4j.base.env.msg.Event;
import rldevs4j.base.env.msg.Step;
import rldevs4j.singletrackrailway.entity.BlockSectionTreeMap;
import rldevs4j.singletrackrailway.entity.TimeTable;
import rldevs4j.singletrackrailway.entity.Train;

/**
 *
 * @author Ezequiel Beccaria
 */
public class SingleTrackRailwayEnv extends Environment{
    private final StateObserver so;
    
    public SingleTrackRailwayEnv(String name, List<Train> trains, BlockSectionTreeMap sections, boolean debug) {
        super(name);
        List<TimeTable> tTables = new ArrayList<>();        
        RailwayBehavior rb = new RailwayBehavior(sections, trains, tTables);
        so = new StateObserver(rb, debug);

        add(so);               
        for(Train t : trains){
            tTables.add(t.getTimeTable());
            add(t);
            addCoupling(t, "out", so, "event");
            addCoupling(so, "event_genearator", t, "in");
        }        
        
        //add external couplings
        addCoupling(so, "step", this, "step");
        addCoupling(this, "action", so, "event");
    }

    @Override
    public INDArray getInitialState() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    public StateSpaceInfo getStateSpaceInfo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Step> getTrace() {
        return this.so.getTrace();
    }
    
}
