package rldevs4j.singletrackrailway.entity;

import java.util.Map;
import java.util.Objects;
import model.modeling.DevsInterface;
import model.modeling.content;
import model.modeling.message;
import model.simulation.CoordinatorInterface;
import model.simulation.CoupledCoordinatorInterface;
import rldevs4j.base.env.gsmdp.evgen.ExogenousEventActivation;
import rldevs4j.base.env.gsmdp.evgen.ExogenousEventGenerator;
import rldevs4j.utils.DoubleUtils;

/**
 * 
 * @author Ezequiel Beccaria
 */
public class Train extends ExogenousEventGenerator {
    private final Integer id;
    private final Double speed;   
    private Double direction;   
    private Double position;
    private TimeTable timeTable;
    private BlockSectionTreeMap sections;
    private BlockSection currentSection;
    private Double currentObjPos;   
    private final Double advTimeUnit = 0.5D;
    
    /**
     * 
     * @param id
     * @param name
     * @param maxSpeed: in Km/h
     * @param timeTable 
     */
    public Train(Integer id, String name, Double maxSpeed, TimeTable timeTable, BlockSectionTreeMap sections) {
        super(name, null, "passive", DevsInterface.INFINITY);
        this.id = id;
        this.speed = maxSpeed*16.667D; //To meters/s
        this.direction = 1D;     
        this.timeTable = timeTable.deepCopy();
        this.sections = sections;
        this.position = this.timeTable.getInitPosition();
        this.currentSection = sections.get(this.position);
        this.currentSection.addMe(this);
    }
    
    @Override
    public void initialize() {     
        holdIn("initial", 0D);
        this.timeTable.initialize();
    }
    
    private Double getNextDepartureTime(){
        Double nDepTime = this.timeTable.getNextDepartureTime();
        if (nDepTime == null) return INFINITY;
        
        Double tL = currentGlobalTime();
        double diff =  DoubleUtils.round(nDepTime - tL, 2);    
        return diff > 1D ? diff : 1D; //At least 1 minute for passenger boarding
    }
    
    private Double currentGlobalTime(){
        CoordinatorInterface globalCoordnator = this.getSim().getRootParent();
        CoupledCoordinatorInterface parent = this.getSim().getParent();
        while(globalCoordnator==null){
            globalCoordnator = parent.getRootParent();
            parent = parent.getParent();
        }        
        return globalCoordnator.getTL();
    }
    
    private Double arribalTime(double npos, double cpos, double speed){
        return (npos-cpos)/speed;
    }
    
    @Override
    public void deltint() {
        if(this.phaseIs("initial")){
            intInitial();
        } else if(this.phaseIs("passive")){
            intPassive();                                   
        } else if (this.phaseIs("active")){  
            intActive();
        } else if(this.phaseIs("final")){
            holdIn("final", INFINITY);
        }
    }
    
    private void intInitial(){
        currentObjPos = timeTable.getNextArribalPos();            
        holdIn("passive", getNextDepartureTime());
    }
    
    private void intPassive(){
        direction = currentObjPos > position? 1D : -1D;
        BlockSection nBs = sections.getNextSection(currentSection.getId(), direction);
        if(nBs.isAvailable()){
            if(currentSection.removeMe(this)){
                currentSection = nBs;
                currentSection.addMe(this);
                Double nextPosition = currentSection.getObjDist(direction);
                holdIn("active", arribalTime(
                    nextPosition,
                    position,
                    direction * speed)); 
                position = nextPosition;
            }                
        }else{
            holdIn("passive", advTimeUnit); 
        }
    }
    
    private void intActive(){
        if(currentSection.isStation()){
            timeTable.nextEntry();
            currentObjPos = timeTable.getNextArribalPos();     
            double nextSigma = getNextDepartureTime();
            if(nextSigma == INFINITY)
                holdIn("final", 0D);
            else
                holdIn("passive", nextSigma);
            
        }else{
            holdIn("passive", 0D);
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
                    phaseIs("active")?direction*speed:0D, 
                    timeTable.getCurrentEntryId(), 
                    Objects.equals(currentObjPos, position)) //Arribal?       
            );              
            m.add(con);                     
        }
        return m;
    }    

    @Override
    public void deltext(double e, message x) {                
        for (int i = 0; i < x.getLength(); i++) {
            if (messageOnPort(x, "in", i)) {       
                Map<String, Double> content = ((ExogenousEventActivation)x.getValOnPort("in", i)).getIndividualContent(name);
                if(content != null){
                    this.updateTimeTable(content.get("update"));                    
                }    
            }     
        }      
        if(phaseIs("passive")){
            holdIn("passive", getNextDepartureTime());
        }else{
            Continue(e);
        }
    }

    @Override
    public double nextSigma() {
        return advTimeUnit;
    }

    public Double getPosition() {
        return position;
    }

    public Integer getId() {
        return id;
    }

    public TimeTable getTimeTable() {
        return timeTable;
    }

    public Double getSpeed() {
        return speed;
    }    
    
    public void updateTimeTable(double value){
        this.timeTable.updateTimes(value);
    }

    @Override
    public ExogenousEventGenerator clone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
