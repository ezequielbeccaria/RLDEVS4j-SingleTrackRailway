package rldevs4j.singletrackrailway.entity;

import rldevs4j.base.env.msg.Event;
import rldevs4j.base.env.msg.EventType;

/**
 *
 * @author Ezequiel Beccaria
 */
public class FinalEvent extends Event{
    
    public FinalEvent(int id, String name, EventType type) {
        super(id, name, type);
    }
    
}
