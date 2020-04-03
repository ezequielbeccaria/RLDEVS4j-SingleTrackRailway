package rldevs4j.singletrackrailway.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TreeMap;

/**
 * TreeMap specialization to maintain a get efficiently the BlockSection where a train
 * is positioned.
 * @author Ezequiel Beccaria
 */
public class BlockSectionTreeMap extends TreeMap<Double, BlockSection>{
    private int addCounter;

    public BlockSectionTreeMap() {
        addCounter = 0;
    }
    
    
    
    public void put(BlockSection bs){
        this.put(bs.getInitDist(), bs);
        if(!Objects.equals(bs.getInitDist(), bs.getEndDist()))
            this.put(bs.getEndDist(), null);
        addCounter++;
    }
    
    public BlockSection get(Double key){
        Entry<Double, BlockSection> e = this.floorEntry(key);
        if (e != null && e.getValue() == null) {
            e = this.lowerEntry(key);
        }
        return e == null ? null : e.getValue();
    } 

    @Override
    public int size() {
        return addCounter;
    }

    @Override
    public Collection<BlockSection> values() {
        List<BlockSection> list = new ArrayList<>();
        for(BlockSection bs : super.values()){
            if(bs != null)
                list.add(bs);
        }
        return list; 
    }
    
    public BlockSection getById(Integer id) {
        for(BlockSection bs : super.values())
            if(bs != null && Objects.equals(bs.getId(), id))
                return bs;
        return null;
    } 
}
