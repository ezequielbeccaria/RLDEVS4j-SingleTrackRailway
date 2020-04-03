package rldevs4j.singletrackrailway.entity;

import java.io.Serializable;

/**
 *
 * @author Ezequiel Beccaria
 */
public class TimeTableEntry implements Serializable{
    private final Double time;
    private final EntryType type;
    private final Station station;
    private final boolean last;

    /**
     *
     * @param time
     * @param type
     * @param station
     */
    public TimeTableEntry(Double time, EntryType type, Station station, boolean last) {
        this.time = time;
        this.type = type;
        this.station = station;
        this.last = last;
    }

    public Double getTime() {
        return time;
    }
    
    public EntryType getType(){
        return type;
    }
    
    public Double getPosition(){
        return station.getInitDist();
    }

    public boolean isLast() {
        return last;
    }
}
