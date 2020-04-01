package rldevs4j.singletrackrailway.entity;

import java.io.Serializable;

/**
 *
 * @author Ezequiel Beccaria
 */
public class Station extends BlockSection implements Serializable{
//    private Integer passengerCapacity;
    private final boolean first;
    private final boolean last;
        
    public Station(Integer id, Double initDist, Double endDist, Integer capacity, boolean first, boolean last) {
        super(id, initDist, endDist, capacity);
        this.first = first;
        this.last = last;
    }
    
    public boolean isFirst() {
        return first;
    }

    public boolean isLast() {
        return last;
    }
    
}
