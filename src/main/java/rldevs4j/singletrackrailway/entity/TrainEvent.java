package rldevs4j.singletrackrailway.entity;

import rldevs4j.base.env.msg.Event;
import rldevs4j.base.env.msg.EventType;

/**
 *
 * @author Ezequiel Beccaria
 */
public class TrainEvent extends Event{
    private final String phase;
    private final Double position;
    private final Double speed;
    private final Double delay;
    private final Integer tteId;    
    private boolean arribal;
    private boolean computed;
    
    public TrainEvent(int id, String phase, Double position, Double speed, Double delay, Integer tteId, boolean arribal) {
        super(id, "train"+id, EventType.exogenous);
        this.position = position;
        this.speed = speed;
        this.delay = delay;
        this.phase = phase;
        this.tteId = tteId;
        this.arribal = arribal;
        this.computed = false;
    }

    public Double getPosition() {
        return position;
    }

    public Double getSpeed() {
        return speed;
    }

    @Override
    public double getDelay() {
        return delay;
    }

    public Integer getTTEntryId(){
        return tteId;
    }
    
    public String getPhase(){
        return phase;
    }

    public boolean isArrival() {
        return arribal;
    }

    public void setArribal(boolean arribal) {
        this.arribal = arribal;
    }

    public boolean isComputed() {
        return computed;
    }

    public void computed(){
        this.computed = true;
    }
}
