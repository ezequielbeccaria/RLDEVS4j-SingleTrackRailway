package rldevs4j.singletrackrailway.factory;

import org.nd4j.linalg.factory.Nd4j;
import rldevs4j.base.env.Environment;
import rldevs4j.base.env.factory.EnvironmentFactory;
import rldevs4j.singletrackrailway.SingleTrackRailwayEnv;
import rldevs4j.singletrackrailway.entity.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleThreeStopsRailwayFactory implements EnvironmentFactory {
    private double simulationTime;
    private double[] delays;
    private boolean randomDelay;
    private boolean debug;

    public SimpleThreeStopsRailwayFactory(double simulationTime, double[] delays, boolean randomDelay, boolean debug) {
        this.simulationTime = simulationTime;
        this.delays = delays;
        this.randomDelay = randomDelay;
        this.debug = debug;
    }

    /**
     * Environment based on "A Simple Three Stops Railway" study case defined in
     * https://www.sciencedirect.com/science/article/abs/pii/S0191261516000084
     * @param simulationTime
     * @param debug
     * @return
     */
    public Environment createSimpleThreeStopsRailway(double simulationTime, boolean debug, double[] delays){
        Station s1 = new Station(0, 0D, 200D, 3, true, false);
        BlockSection bs11 = new BlockSection(1, 200.1D, 3700D, 1);
        BlockSection bs12 = new BlockSection(2, 3700.1D, 6000D, 1);
        BlockSection bs13 = new BlockSection(3, 6000.1D, 9000D, 1);
        Station s2 = new Station(4, 9000.1D, 9200D, 3, false, false);
        BlockSection bs21 = new BlockSection(5, 9200.1D, 12100D, 1);
        BlockSection bs22 = new BlockSection(6, 12100.1D, 15000D, 1);
        Station s3 = new Station(7, 15000.1D, 15200D, 3, false, true);

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
        TimeTableEntry tte01 = new TimeTableEntry(2D*60D+delays[0], EntryType.DEPARTURE, s1);
        TimeTableEntry tte02 = new TimeTableEntry(11D*60D, EntryType.ARRIVAL, s2);
        TimeTableEntry tte03 = new TimeTableEntry(12D*60D, EntryType.DEPARTURE, s2);
        TimeTableEntry tte04 = new TimeTableEntry(18D*60D, EntryType.ARRIVAL, s3);

        List<TimeTableEntry> timeTable0Entries = new ArrayList<>();
        timeTable0Entries.add(tte01);
        timeTable0Entries.add(tte02);
        timeTable0Entries.add(tte03);
        timeTable0Entries.add(tte04);

        TimeTable timeTable0 = new TimeTable(timeTable0Entries, 0);
        Train train0 = new Train(0, "train0", 70D, timeTable0, bstm);

        //Train1 Setup
        TimeTableEntry tte11 = new TimeTableEntry(5D*60D+delays[1], EntryType.DEPARTURE, s3);
        TimeTableEntry tte12 = new TimeTableEntry(10D*60D, EntryType.ARRIVAL, s2);
        TimeTableEntry tte13 = new TimeTableEntry(16D*60D, EntryType.DEPARTURE, s2);
        TimeTableEntry tte14 = new TimeTableEntry(23D*60D, EntryType.ARRIVAL, s1);

        List<TimeTableEntry> timeTable1Entries = new ArrayList<>();
        timeTable1Entries.add(tte11);
        timeTable1Entries.add(tte12);
        timeTable1Entries.add(tte13);
        timeTable1Entries.add(tte14);

        TimeTable timeTable1 = new TimeTable(timeTable1Entries, 0);
        Train train1 = new Train(1, "train1", 100D, timeTable1, bstm);

        //Train2 Setup
        TimeTableEntry tte21 = new TimeTableEntry(8D*60D+delays[2], EntryType.DEPARTURE, s1);
        TimeTableEntry tte22 = new TimeTableEntry(14D*60D, EntryType.ARRIVAL, s2);
        TimeTableEntry tte23 = new TimeTableEntry(16D*60D, EntryType.DEPARTURE, s2);
        TimeTableEntry tte24 = new TimeTableEntry(21D*60D, EntryType.ARRIVAL, s3);

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

        return new SingleTrackRailwayEnv("env", trains, bstm, simulationTime, debug);
    }

    private void generateRandomDelay(){
        int delayForTrain = Nd4j.getRandom().nextInt(3);
        int delayMin = Nd4j.getRandom().nextInt(0, 10);
        Arrays.fill(delays, 0D);
        delays[delayForTrain] = delayMin;
    }

    @Override
    public Environment createInstance() {
        if(randomDelay){
            generateRandomDelay();
        }
        return this.createSimpleThreeStopsRailway(simulationTime, debug, delays);
    }
}
