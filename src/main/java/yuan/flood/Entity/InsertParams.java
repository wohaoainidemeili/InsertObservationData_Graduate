package yuan.flood.Entity;

import java.util.HashMap;
import java.util.Map;

public class InsertParams {
    private Sensor sensor;
    private double[][] sensorData;
    private Map<String, Integer> linkedDataMap = new HashMap<String, Integer>();

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public double[][] getSensorData() {
        return sensorData;
    }

    public void setSensorData(double[][] sensorData) {
        this.sensorData = sensorData;
    }

    public Map<String, Integer> getLinkedDataMap() {
        return linkedDataMap;
    }

    public void setLinkedDataMap(Map<String, Integer> linkedDataMap) {
        this.linkedDataMap = linkedDataMap;
    }
}
