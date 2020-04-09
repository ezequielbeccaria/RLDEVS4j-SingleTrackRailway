package rldevs4j.singletrackrailway.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Objects;
import model.modeling.DevsInterface;
import model.modeling.content;
import model.modeling.message;
import rldevs4j.base.env.gsmdp.evgen.ExogenousEventActivation;
import rldevs4j.base.env.gsmdp.evgen.ExogenousEventGenerator;
import rldevs4j.utils.DoubleUtils;

/**
 * 
 * @author Ezequiel Beccaria
 */
public class Train extends ExogenousEventGenerator {
    private final Integer id;
    private boolean stop;
    private final Double maxSpeed;   
    private Double currentSpeed;
    private Double position;
    private TimeTable timeTable;
    private Double currentObjPos;   
    private final Double advTimeUnit = 0.1D;
    
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
        this.stop = false;
        this.maxSpeed = maxSpeed*16.667D; //To meters/s
        this.currentSpeed = 0D;        
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
        double diff =  DoubleUtils.round(nDepTime - tL, 2);    
        return diff > advTimeUnit ? diff : advTimeUnit;
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
               
        if(currentSpeed>0 && nextPos<currentObjPos){
            return nextPos;
        }else if(currentSpeed<0 && nextPos>currentObjPos){
            return nextPos;
        }
        return currentObjPos;            
    }
    
    private boolean arribal(){       
        return Objects.equals(position, currentObjPos);            
    }
    
    @Override
    public void deltint() {
        if(this.phaseIs("initial")){
            currentObjPos = timeTable.getNextArribalPos();
            holdIn("passive", getNextDepartureTime());
        } else if(this.phaseIs("passive")){
            holdIn("active", advTimeUnit);                        
            currentSpeed = currentObjPos > position ? maxSpeed : -maxSpeed;
            position = stop?position:nextPosition(sigma);
            timeTable.nextEntry();
        } else if (this.phaseIs("active")){            
            if(arribal()){
                currentSpeed = 0D;                
                if(timeTable.lastOneEntry()){
                    holdIn("final", 0D); 
                }else{
                    timeTable.nextEntry();
                    currentObjPos = timeTable.getNextArribalPos();                                        
                    holdIn("passive", getNextDepartureTime());  
                }                
            }else{
                position = stop?position:nextPosition(advTimeUnit);
                holdIn("active", advTimeUnit);                 
            }
        } else if(this.phaseIs("final")){
            passivate();
        }
    }
    
    @Override
    public message out() {
        message m = new message();    
        if(!phaseIs("initial")){
            content con = makeContent(
            "out", 
            new TrainEvent(
                    id, 
                    phase, 
                    position, 
                    stop?0D:(arribal()?0D:currentSpeed), 
                    timeTable.getCurrentEntry(), 
                    arribal(),
                    nextPosition(advTimeUnit)));
            m.add(con);                     
        }
        return m;
    }    

    @Override
    public void deltext(double e, message x) {
        for (int i = 0; i < x.getLength(); i++) {
            if (messageOnPort(x, "in", i)) {       
                Map<String, Double> content = ((ExogenousEventActivation)x.getValOnPort("in", i)).getIndividualContent(name);
                if(content != null)
                    if(content.get("stop").equals(1D)){     
                        stop = true;
                    } else if(content.get("stop").equals(0D)){
                        stop = false;                      
                    }    
            }     
        }      
        Continue(e);
    }

    @Override
    public double nextSigma() {
        return advTimeUnit;
    }

    public Double getPosition() {
        return position;
    }

    public Double getCurrentSpeed() {
        return currentSpeed;
    }

    public Integer getId() {
        return id;
    }

    public TimeTable getTimeTable() {
        return timeTable;
    }

    @Override
    public ExogenousEventGenerator clone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
