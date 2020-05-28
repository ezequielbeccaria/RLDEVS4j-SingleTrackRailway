package rldevs4j.singletrackrailway.factory;

import java.util.ArrayList;
import java.util.List;
import rldevs4j.base.env.Environment;
import rldevs4j.base.env.factory.EnvironmentFactory;
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
public class SingleTrackRailwayEnvFactory implements EnvironmentFactory {
    
    public Environment createEnv01(double simulationTime){
        Station s1 = new Station(0, 0D, 50D, 2, true, false);
        BlockSection bs = new BlockSection(1, 51D, 8050D, 1);
        Station s2 = new Station(2, 8051D, 8100D, 2, false, true);
        BlockSectionTreeMap bstm = new BlockSectionTreeMap();
        bstm.put(s1);
        bstm.put(bs);
        bstm.put(s2);        
        
        TimeTableEntry tte1 = new TimeTableEntry(600D, EntryType.DEPARTURE, s1);
        TimeTableEntry tte2 = new TimeTableEntry(960D, EntryType.ARRIVAL, s2);
        TimeTableEntry tte3 = new TimeTableEntry(1200D, EntryType.DEPARTURE, s2);
        TimeTableEntry tte4 = new TimeTableEntry(1560D, EntryType.ARRIVAL, s1);
        List<TimeTableEntry> timeTableEntries = new ArrayList<>();
        timeTableEntries.add(tte1);
        timeTableEntries.add(tte2);
        timeTableEntries.add(tte3);
        timeTableEntries.add(tte4);        
        
        TimeTable timeTable = new TimeTable(timeTableEntries, 0);
        Train train1 = new Train(0, "train0", 80D, timeTable, bstm);
        List<Train> trains = new ArrayList<>();
        trains.add(train1);
        
        return new SingleTrackRailwayEnv("env", trains, bstm, simulationTime, true);
    }
    
    public Environment createEnv02(double simulationTime){
        Station s1 = new Station(0, 0D, 50D, 2, true, false);
        BlockSection bs11 = new BlockSection(1, 51D, 2000D, 1);
        BlockSection bs12 = new BlockSection(2, 2001D, 4000D, 1);
        BlockSection bs13 = new BlockSection(3, 4001D, 6000D, 1);
        BlockSection bs14 = new BlockSection(4, 6001D, 8000D, 1);
        Station s2 = new Station(5, 8001D, 8050D, 2, false, false);
        BlockSection bs21 = new BlockSection(6, 8051D, 10000D, 1);
        BlockSection bs22 = new BlockSection(7, 10001D, 12000D, 1);
        BlockSection bs23 = new BlockSection(8, 12001D, 14000D, 1);
        Station s3 = new Station(9, 14001D, 14050D, 2, false, true);
        
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
        
        TimeTableEntry tte1 = new TimeTableEntry(10D*60D, EntryType.DEPARTURE, s1);
        TimeTableEntry tte2 = new TimeTableEntry(16D*60D, EntryType.ARRIVAL, s2);
        TimeTableEntry tte3 = new TimeTableEntry(20D*60D, EntryType.DEPARTURE, s2);
        TimeTableEntry tte4 = new TimeTableEntry(26D*60D, EntryType.ARRIVAL, s3);
        TimeTableEntry tte5 = new TimeTableEntry(32D*60D, EntryType.DEPARTURE, s3);
        TimeTableEntry tte6 = new TimeTableEntry(37D*60D, EntryType.ARRIVAL, s2);
        TimeTableEntry tte7 = new TimeTableEntry(40D*60D, EntryType.DEPARTURE, s2);
        TimeTableEntry tte8 = new TimeTableEntry(43D*60D, EntryType.ARRIVAL, s1);
        
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
        
        return new SingleTrackRailwayEnv("env", trains, bstm, simulationTime, true);
    }
    
    public Environment createEnv03(double simulationTime, boolean debug){

        double delay = 550;

        Station s1 = new Station(0, 0D, 50D, 3, true, false);
        BlockSection bs11 = new BlockSection(1, 51D, 2000D, 1);
        BlockSection bs12 = new BlockSection(2, 2001D, 4000D, 1);
        BlockSection bs13 = new BlockSection(3, 4001D, 6000D, 1);
        BlockSection bs14 = new BlockSection(4, 6001D, 8000D, 1);
        Station s2 = new Station(5, 8001D, 8050D, 3, false, false);
        BlockSection bs21 = new BlockSection(6, 8051D, 10000D, 1);
        BlockSection bs22 = new BlockSection(7, 10001D, 12000D, 1);
        BlockSection bs23 = new BlockSection(8, 12001D, 14000D, 1);
        Station s3 = new Station(9, 14001D, 14050D, 3, false, true);
        
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
        TimeTableEntry tte02 = new TimeTableEntry(450D, EntryType.ARRIVAL, s2);
        TimeTableEntry tte03 = new TimeTableEntry(600D, EntryType.DEPARTURE, s2);
        TimeTableEntry tte04 = new TimeTableEntry(900D, EntryType.ARRIVAL, s3);
        TimeTableEntry tte05 = new TimeTableEntry(1200D, EntryType.DEPARTURE, s3);
        TimeTableEntry tte06 = new TimeTableEntry(1500D, EntryType.ARRIVAL, s2);
        TimeTableEntry tte07 = new TimeTableEntry(1600D, EntryType.DEPARTURE, s2);
        TimeTableEntry tte08 = new TimeTableEntry(2000D, EntryType.ARRIVAL, s1);
        
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
        Train train0 = new Train(0, "train0", 70D, timeTable0, bstm);
        
        //Train1 Setup
        TimeTableEntry tte11 = new TimeTableEntry(10D+delay, EntryType.DEPARTURE, s3);
        TimeTableEntry tte12 = new TimeTableEntry(400D, EntryType.ARRIVAL, s2);
        TimeTableEntry tte13 = new TimeTableEntry(500D, EntryType.DEPARTURE, s2);
        TimeTableEntry tte14 = new TimeTableEntry(800D, EntryType.ARRIVAL, s1);
        TimeTableEntry tte15 = new TimeTableEntry(1100D, EntryType.DEPARTURE, s1);
        TimeTableEntry tte16 = new TimeTableEntry(1400D, EntryType.ARRIVAL, s2);
        TimeTableEntry tte17 = new TimeTableEntry(1650D, EntryType.DEPARTURE, s2);
        TimeTableEntry tte18 = new TimeTableEntry(2000D, EntryType.ARRIVAL, s3);
        
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
        Train train1 = new Train(1, "train1", 80D, timeTable1, bstm);
        
        List<Train> trains = new ArrayList<>();
        trains.add(train0);
        trains.add(train1);
        
        return new SingleTrackRailwayEnv("env", trains, bstm, simulationTime, debug);
    }

    @Override
    public Environment createInstance() {
        return createEnv03(3000D, false);
    }
}
