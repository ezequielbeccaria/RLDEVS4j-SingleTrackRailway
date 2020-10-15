package rldevs4j.singletrackrailway2.entity;

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
        // only applies the update if the delay is equal to 0
        // or the value is positive
        // a delayed train can't depart earlier but can depart later
        if(delay==0 || value > 0)
            this.time += value;
    }   

    public Double getDelay() {
        return delay;
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
