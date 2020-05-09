package rldevs4j.singletrackrailway.entity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import rldevs4j.utils.FastByteArrayOutputStream;

/**
 *
 * @author Ezequiel Beccaria
 */
public class TimeTable implements Serializable{
    private List<TimeTableEntry> details;
    private Integer currentEntry;

    public TimeTable(List<TimeTableEntry> details, Integer currentEntry) {
        this.details = details;
        initialize();
    }
    
    public void initialize(){
        this.currentEntry = 0;
    }
    
    public void nextEntry(){
        currentEntry += 2;
        if(currentEntry>=details.size())
            currentEntry = details.size()-1;
    }
    
    public Double getInitPosition(){
        TimeTableEntry tte = details.get(currentEntry);
        return tte.getPosition();
    }
    
    public Double getNextDepartureTime(){
        for(int i=currentEntry;i<details.size();i++){
            TimeTableEntry tte = details.get(i);
            if(EntryType.DEPARTURE.equals(tte.getType()))
                return tte.getTime();
        }
        return null;
    }
    
    public BlockSection getNextObjSection(){
        for(int i=currentEntry;i<details.size();i++){
            TimeTableEntry tte = details.get(i);
            if(EntryType.ARRIVAL.equals(tte.getType()))
                return tte.getStation();
        }
        return details.get(details.size()-1).getStation();
    }
    
    public Double getNextArribalPos(){
        for(int i=currentEntry;i<details.size();i++){
            TimeTableEntry tte = details.get(i);
            if(EntryType.ARRIVAL.equals(tte.getType()))
                return tte.getPosition();
        }
        return details.get(details.size()-1).getPosition();
    }
    
    public TimeTable deepCopy(){
        Object obj = null;
        try {
            // Write the object out to a byte array
            FastByteArrayOutputStream fbos = new FastByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(fbos);
            out.writeObject(this);
            out.flush();
            out.close();

            // Retrieve an input stream from the byte array and read
            // a copy of the object back in. 
            ObjectInputStream in = 
                new ObjectInputStream(fbos.getInputStream());
            obj = in.readObject();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        catch(ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
        return (TimeTable) obj;
    }
    
    public boolean lastOneEntry(){
        return currentEntry>=details.size();           
    }

    public Integer getCurrentEntryId() {
        return currentEntry;
    }

    public List<TimeTableEntry> getDetails() {
        return details;
    }
    
    public TimeTableEntry getCurrentEntry(){
        return details.get(currentEntry);
    }
    
    public TimeTableEntry getNextArribalEntry(int id){
        for(int i=id;i<details.size();i++){
            TimeTableEntry tte = details.get(i);
            if(EntryType.ARRIVAL.equals(tte.getType()))
                return tte;
        }
        return null;
    }
    
    public void updateTimes(double value){
        for(int i=currentEntry;i<details.size();i++){
            details.get(i).updateTime(value);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TT.details: {");
        for(TimeTableEntry tte : details){
            sb.append(tte.toString());
        }
        sb.append("}");
        return sb.toString();
    }
}
