package rldevs4j.singletrackrailway2;

import rldevs4j.base.env.Environment;
import rldevs4j.base.env.gsmdp.StateObserver;
import rldevs4j.base.env.gsmdp.evgen.FixedTimeExogenousEventGen;
import rldevs4j.base.env.msg.EventType;
import rldevs4j.base.env.msg.Step;
import rldevs4j.singletrackrailway2.entity.BlockSectionTreeMap;
import rldevs4j.singletrackrailway2.entity.FinalEvent;
import rldevs4j.singletrackrailway2.entity.TimeTable;
import rldevs4j.singletrackrailway2.entity.Train;

import java.util.ArrayList;
import java.util.List;
import rldevs4j.base.env.spaces.Space;
import rldevs4j.singletrackrailway2.entity.RandomDelayGenerator;

/**
 *
 * @author Ezequiel Beccaria
 */
public class SimpleThreeStopsRailwayEnv extends Environment{
    private final StateObserver so;
    private final List<Train> trains;
    private final FixedTimeExogenousEventGen episodeFinishEventGen;
    private final boolean randomDelay;
    private final RandomDelayGenerator delayGenerator = RandomDelayGenerator.getInstance();
    
    public SimpleThreeStopsRailwayEnv(String name, List<Train> trains, BlockSectionTreeMap sections, double simulationTime, boolean randomDelay, boolean test, boolean debug) {
        super(name);
        RailwayBehavior rb = new RailwayBehavior(sections, trains, test);
        so = new StateObserver(rb, debug);
        episodeFinishEventGen = new FixedTimeExogenousEventGen("episode_finish", new FinalEvent(999, "final_event", EventType.exogenous), new Double[]{simulationTime});
        this.trains = trains;
        this.randomDelay = randomDelay;

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
        if(randomDelay)
            delayGenerator.updateDelays();
        trains.forEach((t) -> {
            t.initialize();
        });
    }

    @Override
    public Environment clone() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Space getActionSpace() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Step> getTrace() {
        return this.so.getTrace();
    }

    @Override
    public Space getStateSpace() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
