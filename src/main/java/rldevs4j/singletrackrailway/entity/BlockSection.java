package rldevs4j.singletrackrailway.entity;

import java.io.Serializable;

/**
 *
 * @author Ezequiel Beccaria
 */
public class BlockSection implements Serializable{
    private final Integer id;
    private final Double initDist;
    private final Double endDist;
    private final Integer capacity;

    public BlockSection(Integer id, Double initDist, Double endDist, Integer capacity) {
        this.id = id;
        this.initDist = initDist;
        this.endDist = endDist;
        this.capacity = capacity;
    }

    public Integer getId() {
        return id;
    }

    public Double getInitDist() {
        return initDist;
    }

    public Double getEndDist() {
        return endDist;
    }

    public Integer getCapacity() {
        return capacity;
    }
    
    public boolean isStation(){
        return false;
    }
    
    public boolean inBlock(Double position){
        return initDist <= position && position <= endDist;
    }
}
