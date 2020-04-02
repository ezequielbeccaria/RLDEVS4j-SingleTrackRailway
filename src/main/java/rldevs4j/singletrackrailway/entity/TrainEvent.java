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
    
    public TrainEvent(int id, double position, double speed) {
        super(id, "train"+id, EventType.exogenous, 0);
        this.position = position;
        this.speed = speed;
    }

    public Double getPosition() {
        return position;
    }

    public Double getSpeed() {
        return speed;
    }
}
