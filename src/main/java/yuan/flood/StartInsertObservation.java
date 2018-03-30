package yuan.flood;

import yuan.flood.TimerTask.InitalTask;

public class StartInsertObservation {
    public static void main(String[] args) throws Exception {
        SensorConfigReader.reader();
        InitalTask.start();
    }
}
