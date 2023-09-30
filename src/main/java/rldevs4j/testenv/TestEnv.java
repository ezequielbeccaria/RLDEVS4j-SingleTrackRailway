package rldevs4j.testenv;

import rldevs4j.base.env.Environment;
import rldevs4j.base.env.gsmdp.StateObserver;
import rldevs4j.base.env.gsmdp.evgen.FixedTimeExogenousEventGen;
import rldevs4j.base.env.msg.Event;
import rldevs4j.base.env.msg.EventType;
import rldevs4j.base.env.msg.Step;

import java.util.List;
import rldevs4j.base.env.spaces.Space;

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
    public Environment clone() {
        return null;
    }

    @Override
    public Space getActionSpace() {
        return null;
    }

    @Override
    public List<Step> getTrace() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Space getStateSpace() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
