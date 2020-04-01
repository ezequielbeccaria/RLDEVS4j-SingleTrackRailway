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
public class Test2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new Test2();
    }

    public Test2() {
        Station s1 = new Station(0, 0D, 0D, 2, true, false);
        BlockSection bs11 = new BlockSection(1, 1D, 2000D, 1);
        BlockSection bs12 = new BlockSection(2, 2001D, 4000D, 1);
        BlockSection bs13 = new BlockSection(3, 4001D, 6000D, 1);
        BlockSection bs14 = new BlockSection(4, 6001D, 7999D, 1);
        Station s2 = new Station(5, 8000D, 8000D, 2, false, false);
        BlockSection bs21 = new BlockSection(6, 8001D, 10000D, 1);
        BlockSection bs22 = new BlockSection(7, 10001D, 12000D, 1);
        BlockSection bs23 = new BlockSection(8, 12001D, 13999D, 1);
        Station s3 = new Station(9, 14000D, 14000D, 2, false, true);
        
        BlockSectionTreeMap sections = new BlockSectionTreeMap();
        sections.put(s1);
        sections.put(bs11);
        sections.put(bs12);
        sections.put(bs13);
        sections.put(bs14);
        sections.put(s2);
        sections.put(bs21);
        sections.put(bs22);
        sections.put(bs23);
        sections.put(s3);
        
        TimeTableEntry tte1 = new TimeTableEntry(10D, EntryType.DEPARTURE, s1);
        TimeTableEntry tte2 = new TimeTableEntry(16D, EntryType.ARRIBAL, s2);
        TimeTableEntry tte3 = new TimeTableEntry(20D, EntryType.DEPARTURE, s2);
        TimeTableEntry tte4 = new TimeTableEntry(26D, EntryType.ARRIBAL, s3);
        TimeTableEntry tte5 = new TimeTableEntry(32D, EntryType.DEPARTURE, s3);
        TimeTableEntry tte6 = new TimeTableEntry(37D, EntryType.ARRIBAL, s2);
        TimeTableEntry tte7 = new TimeTableEntry(40D, EntryType.DEPARTURE, s2);
        TimeTableEntry tte8 = new TimeTableEntry(43D, EntryType.ARRIBAL, s1);
        
        List<TimeTableEntry> timeTableEntries = new ArrayList<>();
        timeTableEntries.add(tte1);
        timeTableEntries.add(tte2);
        timeTableEntries.add(tte3);
        timeTableEntries.add(tte4);        
        timeTableEntries.add(tte5);        
        timeTableEntries.add(tte6);        
        timeTableEntries.add(tte7);        
        timeTableEntries.add(tte8);        
        
        TimeTable timeTable = new TimeTable(timeTableEntries, 0);
        Train train1 = new Train(0, "train1", 80D, timeTable);
        
        List<Train> trains = new ArrayList<>();
        trains.add(train1);
        
        SingleTrackRailwayEnv env = new SingleTrackRailwayEnv("env", trains, sections);
        env.initialize(); //initialize model state
        DevsSuiteFacade facade = new DevsSuiteFacade(env);
        
        facade.simulateToTime(50);
    }
    
}
