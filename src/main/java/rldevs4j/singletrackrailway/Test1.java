package rldevs4j.singletrackrailway;

import facade.DevsSuiteFacade;
import java.util.ArrayList;
import java.util.List;
import rldevs4j.singletrackrailway.entity.BlockSection;
import rldevs4j.singletrackrailway.entity.BlockSectionTreeMap;
import rldevs4j.singletrackrailway.entity.EntryType;
import rldevs4j.singletrackrailway.entity.Station;
import rldevs4j.singletrackrailway.entity.TimeTable;
import rldevs4j.singletrackrailway.entity.TimeTableEntry;
import rldevs4j.singletrackrailway.entity.Train;

/**
 *
 * @author ezequiel
 */
public class Test1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new Test1();
    }

    public Test1() {
        Station s1 = new Station(0, 0D, 0D, 2, true, false);
        BlockSection bs = new BlockSection(1, 1D, 8000D, 1);
        Station s2 = new Station(2, 8000D, 8000D, 2, false, true);
        BlockSectionTreeMap bstm = new BlockSectionTreeMap();
        bstm.put(s1);
        bstm.put(bs);
        bstm.put(s2);        
        
        TimeTableEntry tte1 = new TimeTableEntry(10D, EntryType.DEPARTURE, s1);
        TimeTableEntry tte2 = new TimeTableEntry(16D, EntryType.ARRIBAL, s2);
        TimeTableEntry tte3 = new TimeTableEntry(20D, EntryType.DEPARTURE, s2);
        TimeTableEntry tte4 = new TimeTableEntry(26D, EntryType.ARRIBAL, s1);
        List<TimeTableEntry> timeTableEntries = new ArrayList<>();
        timeTableEntries.add(tte1);
        timeTableEntries.add(tte2);
        timeTableEntries.add(tte3);
        timeTableEntries.add(tte4);        
        
        TimeTable timeTable = new TimeTable(timeTableEntries, 0);
        Train train1 = new Train(1, "train1", 80D, timeTable);
        List<Train> trains = new ArrayList<>();
        trains.add(train1);
        
        SingleTrackRailwayEnv env = new SingleTrackRailwayEnv("env", trains, bstm);
        
        DevsSuiteFacade facade = new DevsSuiteFacade(env);
        
        facade.simulateToTime(50);
    }
    
}
