/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rldevs4j.singletrackrailway;

import java.util.ArrayList;
import java.util.List;
import rldevs4j.singletrackrailway.entity.BlockSection;
import rldevs4j.singletrackrailway.entity.EntryType;
import rldevs4j.singletrackrailway.entity.Station;
import rldevs4j.singletrackrailway.entity.TimeTable;
import rldevs4j.singletrackrailway.entity.TimeTableEntry;

/**
 *
 * @author ezequiel
 */
public class TimeTableTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new TimeTableTest();
    }

    public TimeTableTest() {
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
        //TimeTable Setup
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
        
        TimeTable tt1 = new TimeTable(timeTable0Entries, 0);
        
        TimeTable tt2 = tt1.deepCopy();
        
        tt2.updateTimes(5);
        
        System.out.println(tt1);
        System.out.println(tt2);
    }
    
        
    
}
