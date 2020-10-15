package rldevs4j.singletrackrailway2.entity;

import rldevs4j.base.env.msg.Event;
import rldevs4j.base.env.msg.EventType;

/**
 *
 * @author Ezequiel Beccaria
 */
public class DelayEvent extends Event{
    private int train;
    private double value;

    public DelayEvent(int id, String name, EventType type, int train, double value) {
        super(id, name, type);
        this.train = train;
        this.value = value;
    }
    
}
