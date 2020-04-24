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
    private final Integer tteId;    
    private boolean arribal;
    
    public TrainEvent(int id, String phase, Double position, Double speed, Integer tteId, boolean arribal) {
        super(id, "train"+id, EventType.exogenous);
        this.position = position;
        this.speed = speed;
        this.phase = phase;
        this.tteId = tteId;
        this.arribal = arribal;
    }

    public Double getPosition() {
        return position;
    }

    public Double getSpeed() {
        return speed;
    }
    
    public Integer getTTEntryId(){
        return tteId;
    }
    
    public String getPhase(){
        return phase;
    }

    public boolean isArribal() {
        return arribal;
    }

    public void setArribal(boolean arribal) {
        this.arribal = arribal;
    }
}
