package rldevs4j.singletrackrailway;

import java.util.List;
import org.nd4j.linalg.api.ndarray.INDArray;
import rldevs4j.base.env.Environment;
import rldevs4j.base.env.StateSpaceInfo;
import rldevs4j.base.env.gsmdp.StateObserver;
import rldevs4j.base.env.msg.Event;
import rldevs4j.base.env.msg.Step;
import rldevs4j.singletrackrailway.entity.BlockSectionTreeMap;
import rldevs4j.singletrackrailway.entity.Train;

/**
 *
 * @author Ezequiel Beccaria
 */
public class SingleTrackRailwayEnv extends Environment{
    private StateObserver so;
    

    public SingleTrackRailwayEnv(String name, List<Train> trains, BlockSectionTreeMap sections) {
        super(name);
        RailwayBehavior rb = new RailwayBehavior(sections, trains);
        so = new StateObserver(rb, true);
        add(so);       
        
        for(Train t : trains){
            add(t);
            addCoupling(t, "out", so, "event");
            addCoupling(so, "event_genearator", t, "in");
        }        
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
