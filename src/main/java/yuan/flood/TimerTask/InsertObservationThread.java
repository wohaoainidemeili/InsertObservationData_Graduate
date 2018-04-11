package yuan.flood.TimerTask;

import org.apache.log4j.Logger;
import yuan.flood.Entity.InsertParams;
import yuan.flood.Entity.ObservedProperty;
import yuan.flood.Entity.SOSWrapper;
import yuan.flood.Entity.Sensor;
import yuan.flood.SensorConfigInfo;
import yuan.flood.Until.Encode;
import yuan.flood.Until.HttpRequestAndPost;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class InsertObservationThread implements Runnable {
    private static final Logger log = Logger.getLogger(InsertObservationThread.class);
    InsertParams insertParams;
    private int Count=0;

    public InsertObservationThread(InsertParams insertParams) {
        this.insertParams = insertParams;
    }
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + "," + Count);
        double[][] data = insertParams.getSensorData();
        Map<String, Integer> linkedData = insertParams.getLinkedDataMap();
        Sensor sensor = insertParams.getSensor();
        String sensorID = sensor.getSensorID();
        Double lat = sensor.getLat();
        Double lon = sensor.getLon();
        String sos = SensorConfigInfo.getUrl();

        for (int i=0;i<sensor.getObservedProperties().size();i++) {
            ObservedProperty obs = sensor.getObservedProperties().get(i);
            obs.setDataValue(String.valueOf(data[Count][linkedData.get(obs.getPropertyID())]));
        }
        //当前事件
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        String t= simpleDateFormat.format(new Date());
        String timeStr= t.replace("+0800", "+08:00");
        SOSWrapper sosWrapper = new SOSWrapper(sensorID, timeStr, lon, lat, sos, sensor.getObservedProperties());
        sosWrapper.setSrid(4326);
        String insertXML = Encode.getInserObservationXML(sosWrapper);
        String responseXML = HttpRequestAndPost.sendPost(sos, insertXML);
        System.out.println(timeStr+","+responseXML);
        Count++;

    }
}
