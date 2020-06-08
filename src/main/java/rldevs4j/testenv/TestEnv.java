package rldevs4j.testenv;

import org.nd4j.linalg.api.ndarray.INDArray;
import rldevs4j.base.env.Environment;
import rldevs4j.base.env.StateSpaceInfo;
import rldevs4j.base.env.gsmdp.StateObserver;
import rldevs4j.base.env.gsmdp.evgen.FixedTimeExogenousEventGen;
import rldevs4j.base.env.msg.Event;
import rldevs4j.base.env.msg.EventType;
import rldevs4j.base.env.msg.Step;

import java.util.List;

public class TestEnv extends Environment {
    private final StateObserver so;
    private final TestEnvBehavior behavior;
    private final FixedTimeExogenousEventGen eventGen;

    public TestEnv() {
        super("TestEnv");
        this.behavior = new TestEnvBehavior();
        this.so = new StateObserver(behavior, false);
        this.eventGen = new FixedTimeExogenousEventGen(
                "event_gen",
                new Event(99, "decision", EventType.exogenous),
                new Double[]{1D},
                true);

        add(so);
        add(eventGen);

        addCoupling(eventGen, "out", so, "event");

        //add external couplings
        addCoupling(so, "step", this, "step");
        addCoupling(this, "action", so, "event");
    }

    @Override
    public void initialize() {
        super.initialize();
        so.initialize();
        eventGen.initialize();
        behavior.initialize();
    }

    @Override
    public INDArray getInitialState() {
        return null;
    }

    @Override
    public Environment clone() {
        return null;
    }

    @Override
    public List<Event> getActionSpace() {
        return null;
    }

    @Override
    public StateSpaceInfo getStateSpaceInfo() {
        return null;
    }

    @Override
    public List<Step> getTrace() {
        return null;
    }
}
