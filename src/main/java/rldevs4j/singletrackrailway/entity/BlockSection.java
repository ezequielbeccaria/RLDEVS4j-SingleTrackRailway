package rldevs4j.singletrackrailway.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ezequiel Beccaria
 */
public class BlockSection implements Serializable{
    private final Integer id;
    private final Double initDist;
    private final Double endDist;
    private final Integer capacity;
    private List<Train> trains;

    public BlockSection(Integer id, Double initDist, Double endDist, Integer capacity) {
        this.id = id;
        this.initDist = initDist;
        this.endDist = endDist;
        this.capacity = capacity;
        this.trains = new ArrayList<>();
    }
    
    public void reset(){
        this.trains = new ArrayList<>();
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
    
    public Double getObjDist(Double direction) {
        if(direction==1D) return endDist;
        if(direction==-1D) return initDist;
        return null;
    }

    public Integer getCapacity() {
        return capacity;
    }
    
    public boolean isStation(){
        return false;
    }
    
    public boolean isAvailable(){
        return trains.size()<capacity;
    }    
    
    public boolean addMe(Train t){
        if(isAvailable())
            return trains.add(t);
        return false;
    }
    
    public boolean removeMe(Train t){        
        return trains.remove(t);        
    }
    
    public boolean inThisSection(double position){
        return this.initDist<= position && position <= this.endDist;
    }
}
