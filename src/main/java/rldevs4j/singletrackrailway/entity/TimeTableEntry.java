package rldevs4j.singletrackrailway.entity;

import java.io.Serializable;

/**
 *
 * @author Ezequiel Beccaria
 */
public class TimeTableEntry implements Serializable{
    private Double time;
    private Double delay;
    private final EntryType type;
    private final Station station;

    /**
     *
     * @param time
     * @param type
     * @param station
     */
    public TimeTableEntry(Double time, Double delay, EntryType type, Station station) {
        this.time = time;
        this.delay = delay;
        this.type = type;
        this.station = station;
    }

    public Double getTime() {
        return time+delay;
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
    public void setDelay(double value) {
        this.delay = value;
    }

    public BlockSection getStation() {
        return station;
    }

    @Override
    public String toString() {
        return "{time:"+time+", delay:"+delay+", type:"+type+"}";
    }
    
}
