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
public class Test3 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new Test3();
    }

    public Test3() {
        Station s1 = new Station(0, 0D, 0D, 3, true, false);
        BlockSection bs11 = new BlockSection(1, 1D, 2000D, 1);
        BlockSection bs12 = new BlockSection(2, 2001D, 4000D, 1);
        BlockSection bs13 = new BlockSection(3, 4001D, 6000D, 1);
        BlockSection bs14 = new BlockSection(4, 6001D, 7999D, 1);
        Station s2 = new Station(5, 8000D, 8000D, 3, false, false);
        BlockSection bs21 = new BlockSection(6, 8001D, 10000D, 1);
        BlockSection bs22 = new BlockSection(7, 10001D, 12000D, 1);
        BlockSection bs23 = new BlockSection(8, 12001D, 13999D, 1);
        Station s3 = new Station(9, 14000D, 14000D, 3, false, true);
        
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
        
        //Train0 Setup
        TimeTableEntry tte01 = new TimeTableEntry(10D, EntryType.DEPARTURE, s1);
        TimeTableEntry tte02 = new TimeTableEntry(16D, EntryType.ARRIBAL, s2);
        TimeTableEntry tte03 = new TimeTableEntry(20D, EntryType.DEPARTURE, s2);
        TimeTableEntry tte04 = new TimeTableEntry(26D, EntryType.ARRIBAL, s3);
        TimeTableEntry tte05 = new TimeTableEntry(32D, EntryType.DEPARTURE, s3);
        TimeTableEntry tte06 = new TimeTableEntry(37D, EntryType.ARRIBAL, s2);
        TimeTableEntry tte07 = new TimeTableEntry(40D, EntryType.DEPARTURE, s2);
        TimeTableEntry tte08 = new TimeTableEntry(43D, EntryType.ARRIBAL, s1);
        
        List<TimeTableEntry> timeTable0Entries = new ArrayList<>();
        timeTable0Entries.add(tte01);
        timeTable0Entries.add(tte02);
        timeTable0Entries.add(tte03);
        timeTable0Entries.add(tte04);        
        timeTable0Entries.add(tte05);        
        timeTable0Entries.add(tte06);        
        timeTable0Entries.add(tte07);        
        timeTable0Entries.add(tte08);        
        
        TimeTable timeTable0 = new TimeTable(timeTable0Entries, 0);
        Train train0 = new Train(0, "train0", 80D, timeTable0);
        
        //Train1 Setup
        TimeTableEntry tte11 = new TimeTableEntry(11D, EntryType.DEPARTURE, s1);
        TimeTableEntry tte12 = new TimeTableEntry(17D, EntryType.ARRIBAL, s2);
        TimeTableEntry tte13 = new TimeTableEntry(21D, EntryType.DEPARTURE, s2);
        TimeTableEntry tte14 = new TimeTableEntry(27D, EntryType.ARRIBAL, s3);
        TimeTableEntry tte15 = new TimeTableEntry(33D, EntryType.DEPARTURE, s3);
        TimeTableEntry tte16 = new TimeTableEntry(38D, EntryType.ARRIBAL, s2);
        TimeTableEntry tte17 = new TimeTableEntry(41D, EntryType.DEPARTURE, s2);
        TimeTableEntry tte18 = new TimeTableEntry(44D, EntryType.ARRIBAL, s1);
        
        List<TimeTableEntry> timeTable1Entries = new ArrayList<>();
        timeTable1Entries.add(tte11);
        timeTable1Entries.add(tte12);
        timeTable1Entries.add(tte13);
        timeTable1Entries.add(tte14);        
        timeTable1Entries.add(tte15);        
        timeTable1Entries.add(tte16);        
        timeTable1Entries.add(tte17);        
        timeTable1Entries.add(tte18);        
        
        TimeTable timeTable1 = new TimeTable(timeTable1Entries, 0);
        Train train1 = new Train(1, "train1", 100D, timeTable1);
        
        List<Train> trains = new ArrayList<>();
        trains.add(train0);
        trains.add(train1);
        
        SingleTrackRailwayEnv env = new SingleTrackRailwayEnv("env", trains, sections);
        env.initialize(); //initialize model state
        DevsSuiteFacade facade = new DevsSuiteFacade(env);
        
        facade.simulateToTime(50);
    }
    
}
