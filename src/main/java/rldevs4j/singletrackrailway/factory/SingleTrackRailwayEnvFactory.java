package rldevs4j.singletrackrailway.factory;

import java.util.ArrayList;
import java.util.List;
import rldevs4j.base.env.Environment;
import rldevs4j.singletrackrailway.SingleTrackRailwayEnv;
import rldevs4j.singletrackrailway.entity.BlockSection;
import rldevs4j.singletrackrailway.entity.BlockSectionTreeMap;
import rldevs4j.singletrackrailway.entity.EntryType;
import rldevs4j.singletrackrailway.entity.Station;
import rldevs4j.singletrackrailway.entity.TimeTable;
import rldevs4j.singletrackrailway.entity.TimeTableEntry;
import rldevs4j.singletrackrailway.entity.Train;

/**
 *
 * @author Ezequiel Beccaria
 */
public class SingleTrackRailwayEnvFactory {
    
    public Environment createEnv01(){
        Station s1 = new Station(0, 0D, 0D, 2, true, false);
        BlockSection bs = new BlockSection(1, 1D, 8000D, 1);
        Station s2 = new Station(2, 8001D, 8001D, 2, false, true);
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
        Train train1 = new Train(0, "train0", 80D, timeTable, bstm);
        List<Train> trains = new ArrayList<>();
        trains.add(train1);
        
        return new SingleTrackRailwayEnv("env", trains, bstm, true);
    }
    
    public Environment createEnv02(){
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
        
        BlockSectionTreeMap bstm = new BlockSectionTreeMap();
        bstm.put(s1);
        bstm.put(bs11);
        bstm.put(bs12);
        bstm.put(bs13);
        bstm.put(bs14);
        bstm.put(s2);
        bstm.put(bs21);
        bstm.put(bs22);
        bstm.put(bs23);
        bstm.put(s3);
        
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
        Train train1 = new Train(0, "train0", 80D, timeTable, bstm);
        
        List<Train> trains = new ArrayList<>();
        trains.add(train1);
        
        return new SingleTrackRailwayEnv("env", trains, bstm, false);
    }
    
    public Environment createEnv03(boolean debug){
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
        
        BlockSectionTreeMap bstm = new BlockSectionTreeMap();
        bstm.put(s1);
        bstm.put(bs11);
        bstm.put(bs12);
        bstm.put(bs13);
        bstm.put(bs14);
        bstm.put(s2);
        bstm.put(bs21);
        bstm.put(bs22);
        bstm.put(bs23);
        bstm.put(s3);
        
        //Train0 Setup
        TimeTableEntry tte01 = new TimeTableEntry(10D, EntryType.DEPARTURE, s1);
        TimeTableEntry tte02 = new TimeTableEntry(16D, EntryType.ARRIBAL, s2);
        TimeTableEntry tte03 = new TimeTableEntry(20D, EntryType.DEPARTURE, s2);
        TimeTableEntry tte04 = new TimeTableEntry(26D, EntryType.ARRIBAL, s3);
        TimeTableEntry tte05 = new TimeTableEntry(30D, EntryType.DEPARTURE, s3);
        TimeTableEntry tte06 = new TimeTableEntry(36D, EntryType.ARRIBAL, s2);
        TimeTableEntry tte07 = new TimeTableEntry(40D, EntryType.DEPARTURE, s2); //TODO chequera pq sale antes de t=40
        TimeTableEntry tte08 = new TimeTableEntry(44D, EntryType.ARRIBAL, s1);
        
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
        Train train0 = new Train(0, "train0", 80D, timeTable0, bstm);
        
        //Train1 Setup
        TimeTableEntry tte11 = new TimeTableEntry(11D, EntryType.DEPARTURE, s1);
        TimeTableEntry tte12 = new TimeTableEntry(17D, EntryType.ARRIBAL, s2);
        TimeTableEntry tte13 = new TimeTableEntry(21D, EntryType.DEPARTURE, s2);
        TimeTableEntry tte14 = new TimeTableEntry(27D, EntryType.ARRIBAL, s3);
        TimeTableEntry tte15 = new TimeTableEntry(29D, EntryType.DEPARTURE, s3);
        TimeTableEntry tte16 = new TimeTableEntry(37D, EntryType.ARRIBAL, s2);
        TimeTableEntry tte17 = new TimeTableEntry(41D, EntryType.DEPARTURE, s2);
        TimeTableEntry tte18 = new TimeTableEntry(48D, EntryType.ARRIBAL, s1);
        
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
        Train train1 = new Train(1, "train1", 70D, timeTable1, bstm);
        
        List<Train> trains = new ArrayList<>();
        trains.add(train0);
        trains.add(train1);
        
        return new SingleTrackRailwayEnv("env", trains, bstm, debug);
    }
    
    /**
     * Environment based on "A Simple Three Stops Railway" study case defined in
     * https://www.sciencedirect.com/science/article/abs/pii/S0191261516000084
     * @param debug
     * @return 
     */
    public Environment createSimpleThreeStopsRailway(boolean debug){
        Station s1 = new Station(0, 0D, 0D, 3, true, false);
        BlockSection bs11 = new BlockSection(1, 1D, 3500D, 1);
        BlockSection bs12 = new BlockSection(2, 3501D, 5800D, 1);
        BlockSection bs13 = new BlockSection(3, 5801D, 8799D, 1);
        Station s2 = new Station(4, 8800D, 8800D, 3, false, false);
        BlockSection bs21 = new BlockSection(5, 8801D, 11800D, 1);
        BlockSection bs22 = new BlockSection(6, 11801D, 14601D, 1);        
        Station s3 = new Station(7, 14602D, 14602D, 3, false, true);
        
        BlockSectionTreeMap bstm = new BlockSectionTreeMap();
        bstm.put(s1);
        bstm.put(bs11);
        bstm.put(bs12);
        bstm.put(bs13);
        bstm.put(s2);
        bstm.put(bs21);
        bstm.put(bs22);
        bstm.put(s3);
        
        //Train0 Setup
        TimeTableEntry tte01 = new TimeTableEntry(7.5D, EntryType.DEPARTURE, s1);
        TimeTableEntry tte02 = new TimeTableEntry(11D, EntryType.ARRIBAL, s2);
        TimeTableEntry tte03 = new TimeTableEntry(13D, EntryType.DEPARTURE, s2);
        TimeTableEntry tte04 = new TimeTableEntry(19D, EntryType.ARRIBAL, s3);
        
        List<TimeTableEntry> timeTable0Entries = new ArrayList<>();
        timeTable0Entries.add(tte01);
        timeTable0Entries.add(tte02);
        timeTable0Entries.add(tte03);
        timeTable0Entries.add(tte04);        
        
        TimeTable timeTable0 = new TimeTable(timeTable0Entries, 0);
        Train train0 = new Train(0, "train0", 70D, timeTable0, bstm);
        
        //Train1 Setup
        TimeTableEntry tte11 = new TimeTableEntry(5D, EntryType.DEPARTURE, s3);
        TimeTableEntry tte12 = new TimeTableEntry(10D, EntryType.ARRIBAL, s2);
        TimeTableEntry tte13 = new TimeTableEntry(16D, EntryType.DEPARTURE, s2);
        TimeTableEntry tte14 = new TimeTableEntry(23D, EntryType.ARRIBAL, s1);
        
        List<TimeTableEntry> timeTable1Entries = new ArrayList<>();
        timeTable1Entries.add(tte11);
        timeTable1Entries.add(tte12);
        timeTable1Entries.add(tte13);
        timeTable1Entries.add(tte14);         
        
        TimeTable timeTable1 = new TimeTable(timeTable1Entries, 0);
        Train train1 = new Train(1, "train1", 100D, timeTable1, bstm);
        
        //Train2 Setup
        TimeTableEntry tte21 = new TimeTableEntry(8D, EntryType.DEPARTURE, s1);
        TimeTableEntry tte22 = new TimeTableEntry(14D, EntryType.ARRIBAL, s2);
        TimeTableEntry tte23 = new TimeTableEntry(16D, EntryType.DEPARTURE, s2);
        TimeTableEntry tte24 = new TimeTableEntry(21D, EntryType.ARRIBAL, s3);
        
        List<TimeTableEntry> timeTable2Entries = new ArrayList<>();
        timeTable2Entries.add(tte21);
        timeTable2Entries.add(tte22);
        timeTable2Entries.add(tte23);
        timeTable2Entries.add(tte24);        
        
        TimeTable timeTable2 = new TimeTable(timeTable2Entries, 0);
        Train train2 = new Train(2, "train2", 100D, timeTable2, bstm);
        
        List<Train> trains = new ArrayList<>();
        trains.add(train0);
        trains.add(train1);
        trains.add(train2);
        
        return new SingleTrackRailwayEnv("env", trains, bstm, debug);
    }
}
