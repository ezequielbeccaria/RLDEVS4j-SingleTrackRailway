package rldevs4j.singletrackrailway.entity;

import rldevs4j.base.env.msg.Event;
import rldevs4j.base.env.msg.EventType;

/**
 *
 * @author Ezequiel Beccaria
 */
public class TrainEvent extends Event{
    private final Double position;
    private final Double speed;
    private final String phase;
    
    public TrainEvent(int id, String phase, double position, double speed, double objPos) {
        super(id, "train"+id, EventType.exogenous, objPos);
        this.position = position;
        this.speed = speed;
        this.phase = phase;
    }

    public Double getPosition() {
        return position;
    }

    public Double getSpeed() {
        return speed;
    }
    
    public Double getObjPos(){
        return getValue();
    }
    
    public String getPhase(){
        return phase;
    }
}
