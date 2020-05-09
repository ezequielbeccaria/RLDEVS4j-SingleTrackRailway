package rldevs4j.singletrackrailway.entity;

import java.io.Serializable;

/**
 *
 * @author Ezequiel Beccaria
 */
public class TimeTableEntry implements Serializable{
    private Double time;
    private final EntryType type;
    private final Station station;

    /**
     *
     * @param time
     * @param type
     * @param station
     */
    public TimeTableEntry(Double time, EntryType type, Station station) {
        this.time = time;
        this.type = type;
        this.station = station;
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
    
    public void updateTime(double value){
        this.time += value;
    }

    public BlockSection getStation() {
        return station;
    }

    @Override
    public String toString() {
        return "{time:"+time+" , type:"+type+"}";
    }
    
}
