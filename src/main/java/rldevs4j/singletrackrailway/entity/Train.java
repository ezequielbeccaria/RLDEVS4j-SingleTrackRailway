package rldevs4j.singletrackrailway.entity;

import java.util.Map;
import model.modeling.DevsInterface;
import model.modeling.content;
import model.modeling.message;
import model.simulation.CoordinatorInterface;
import model.simulation.CoupledCoordinatorInterface;
import rldevs4j.base.env.gsmdp.evgen.ExogenousEventActivation;
import rldevs4j.base.env.gsmdp.evgen.ExogenousEventGenerator;
import rldevs4j.singletrackrailway.factory.RandomDelayGenerator;
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
    private TimeTable initialTimeTable;
    private TimeTable timeTable;
    private BlockSectionTreeMap sections;
    private BlockSection currentSection;
    private BlockSection objetiveSection;   
    private final Double activeSigma = 1D;
    private boolean randomDelay;
    private final RandomDelayGenerator delayGenerator = RandomDelayGenerator.getInstance();
    private boolean debug;
    
    /**
     * 
     * @param id
     * @param name
     * @param maxSpeed: in Km/h
     * @param timeTable 
     * @param sections 
     */
    public Train(Integer id, String name, Double maxSpeed, TimeTable timeTable, BlockSectionTreeMap sections, boolean randomDelay) {
        super(name, null, "passive", DevsInterface.INFINITY);     
        this.id = id;
        this.speed = Math.floor(maxSpeed/3.6D); //To meters/s
        this.direction = 1D;     
        this.initialTimeTable = timeTable.deepCopy();        
        this.sections = sections;
        this.randomDelay = randomDelay;
        this.debug = false;
        initialize();
    }
    
    @Override
    public void initialize() {             
        this.timeTable = initialTimeTable.deepCopy();
        if(randomDelay)
            this.timeTable.getCurrentEntry().setDelay(delayGenerator.getDelay(id)); //Delay update
        this.position = this.timeTable.getInitPosition();
        this.currentSection = sections.get(this.position);
        this.currentSection.addMe(this);        
        holdIn("initial", 0D);
    }
    
    private Double getNextDepartureTime(){
        Double nDepTime = this.timeTable.getNextDepartureTime();
        if (nDepTime == null) return INFINITY;
        
        Double tL = currentGlobalTime();
        double diff =  DoubleUtils.round(nDepTime - tL, 2);    
        return diff >= 60D ? diff : 60D; //At least 1 minute for passenger boarding
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
    
    private Double nextPos(double cpos, double speed, double t){
        return cpos + speed*t;
    }
    
    @Override
    public void deltint() {
        if(phaseIs("initial")){
            intInitial();
        } else if(phaseIs("passive")){
            intPassive();                                   
        } else if (phaseIs("active") || phaseIs("waiting")){  
            intActive();
        } else if(phaseIs("final")){
            holdIn("final", INFINITY);
        }
    }
    
    private void intInitial(){            
        holdIn("passive", getNextDepartureTime());
    }
    
    private void intPassive(){
        this.objetiveSection = timeTable.getNextObjSection();
        direction = currentSection.getId() < objetiveSection.getId()? 1D : -1D;
        trainMove();        
    }
    
    private void intActive(){
        if(arribal()){
            timeTable.nextEntry();
            double nextSigma = getNextDepartureTime();
            if(nextSigma == INFINITY)
                holdIn("final", 0D);
            else
                holdIn("passive", nextSigma);            
        }else{
            trainMove(); 
        }              
    }     
    
    private void trainMove(){    
        BlockSection nBs = sections.getNextSection(currentSection.getId(), direction);
        if(nBs.isAvailable() || !nBs.inThisSection(nextPos(position, direction * speed, activeSigma))){ 
            //Calc next position
            position = nextPos(position, direction * speed, activeSigma);
            //Check if the train is still in the current section.
            if(!currentSection.inThisSection(position)){
                //is in the next section
                currentSection.removeMe(this);
                currentSection = nBs;
                currentSection.addMe(this);
            }
            holdIn("active", activeSigma); 
        }else{
            holdIn("waiting", activeSigma); 
        }
        
    }
    
    @Override
    public message out() {
        message m = new message();    
        if(phaseIs("initial") || sigma > 0){
            content con = makeContent(
            "out", 
            new TrainEvent(
                    id, 
                    phase, 
                    position,
                    timeTable.getDelay(),
                    phaseIs("active")?direction*speed:0D, 
                    timeTable.getCurrentEntryId(), 
                    arribal())       
            );              
            m.add(con);                     
        }
        return m;
    }    

    @Override
    public void deltext(double e, message x) {                
        for (int i = 0; i < x.getLength(); i++) {
            if (messageOnPort(x, "in", i)) {       
                Map<String, Float> content = ((ExogenousEventActivation)x.getValOnPort("in", i)).getIndividualContent(name);
                if(content != null){
                    double value = content.get("update");
                    if(value>0){
                        this.updateTimeTable(value);
                        if(phaseIs("passive")){
                            holdIn("passive", getNextDepartureTime());
                        }
                    }else{
                        Continue(e);
                    }
                }    
            }
        }
    }

    @Override
    public double nextSigma() {
        return activeSigma;
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
        this.timeTable.updateTimes(value, phase);
        if(debug){            
            System.out.println(timeTable.toString());
        }
    }
    
    public boolean arribal(){
        if(!phaseIs("active")) return false;            
        return currentSection.getId().equals(objetiveSection.getId());
    }

    @Override
    public ExogenousEventGenerator clone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
