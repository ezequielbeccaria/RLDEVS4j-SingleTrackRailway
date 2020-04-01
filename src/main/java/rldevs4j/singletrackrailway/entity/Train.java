package rldevs4j.singletrackrailway.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import model.modeling.DevsInterface;
import model.modeling.content;
import model.modeling.message;
import rldevs4j.base.env.gsmdp.evgen.ExogenousEventGenerator;
import rldevs4j.base.env.msg.Event;
import rldevs4j.base.env.msg.EventType;

/**
 * TODO: Probar funcionamiento
 * @author Ezequiel Beccaria
 */
public class Train extends ExogenousEventGenerator {
    private final Integer id;
    private final Double maxSpeed;   
    private Double currentSpeed;
    private Double position;
    private final TimeTable initTimeTable;
    private TimeTable timeTable;
    private Double currentPosObj;   
    
    /**
     * 
     * @param id
     * @param name
     * @param maxSpeed: in Km/h
     * @param timeTable 
     */
    public Train(Integer id, String name, Double maxSpeed, TimeTable timeTable) {
        super(name, null, "passive", DevsInterface.INFINITY);
        this.id = id;
        this.maxSpeed = maxSpeed*16.667D; //To meters/s
        this.currentSpeed = 0D;
        this.initTimeTable = timeTable;
        this.timeTable = timeTable.deepCopy();
        this.position = this.timeTable.getInitPosition();
    }
    
    @Override
    public void initialize() {     
        holdIn("initial", 0D);
    }
    
    private Double getNextDepartureTime(){
        Double nDepTime = this.timeTable.getNextDepartureTime();
        if (nDepTime == null) return INFINITY;
        
        Double tL = currentGlobalTime();
        Double diff =  nDepTime - tL;
        return diff > 1D ? diff : 1D;
    }
    
    private Double currentGlobalTime(){
        return this.getSim().getRootParent().getTL();
    }
    
    /**
     * Method that determines the position of the train for the next delta t
     * @param e
     * @return 
     */
    private Double nextPosition(Double e){
        Double nextPos = position + currentSpeed*e;
        nextPos = new BigDecimal(nextPos).setScale(0, RoundingMode.FLOOR).doubleValue();
        
        
        if(currentSpeed>0 && nextPos<currentPosObj){
            return nextPos;
        }else if(currentSpeed<0 && nextPos>currentPosObj){
            return nextPos;
        }
        return currentPosObj;            
    }
    
    private boolean arribal(Double pos){        
        
        if(currentSpeed>0 && pos<currentPosObj){
            return false;
        }else if(currentSpeed<0 && pos>currentPosObj){
            return false;
        }
        return true;            
    }
    
    @Override
    public void deltint() {
        if(this.phaseIs("initial")){
            holdIn("passive", getNextDepartureTime());
        } else if(this.phaseIs("passive")){
            holdIn("active", 1D);            
            currentPosObj = timeTable.getNextArribalPos();
            currentSpeed = currentPosObj > position ? maxSpeed : -maxSpeed;
            position = nextPosition(sigma);
            timeTable.nextEntry();
        } else if (this.phaseIs("active")){            
            if(arribal(position)){
                holdIn("arribal", 0D);
            }else{
                position = nextPosition(sigma);
            }
        } else if(this.phaseIs("arribal")){
            timeTable.nextEntry();
            holdIn("passive", getNextDepartureTime());   
        }
    }
    
    @Override
    public message out() {
        System.out.println(String.format("Time: %f - Train: %d - Position: %f", currentGlobalTime(), id, position)); //DEBUG
        message m = new message();      
        content con = makeContent(
            "out", 
            new Event(id, "state", EventType.exogenous, position));
        m.add(con);
        return m;
    }
    
    @Override
    public void deltext(double e, message x) {
        Continue(e);
    }

    @Override
    public double nextSigma() {
        return 1D;
    }

    public Double getPosition() {
        return position;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public ExogenousEventGenerator clone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
