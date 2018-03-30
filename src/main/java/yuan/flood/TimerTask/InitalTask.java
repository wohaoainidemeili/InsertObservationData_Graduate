package yuan.flood.TimerTask;

import yuan.flood.Entity.InsertParams;
import yuan.flood.Entity.Sensor;
import yuan.flood.SensorConfigInfo;
import yuan.flood.Until.Decode;
import yuan.flood.Until.Encode;
import yuan.flood.Until.HttpRequestAndPost;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class InitalTask {
    public static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(30);
    public static void start() throws Exception {
        //初始化得到要插入的传感器ID所在文件和属性配置文件，并开启定时插入任务
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("config.txt")));
        String bufferLine = "";
        List<InsertParams> insertParamsList = new ArrayList<InsertParams>();
        while ((bufferLine = bufferedReader.readLine()) != null) {
            String[] eles = bufferLine.split(",");

            if (eles.length!=2) continue;

            String sensorFileName = eles[0];
            String obsFileName = eles[1];
            //循环构建线程问题
            List<InsertParams> params = getInsertParams(sensorFileName, obsFileName);
            insertParamsList.addAll(params);
        }
        //将每个传感器参数构建一个线程，用于定时插入
        for (int i=0;i<insertParamsList.size();i++) {
            scheduledExecutorService.scheduleAtFixedRate(new InsertObservationThread(insertParamsList.get(i)),1000,60000, TimeUnit.MILLISECONDS);
        }
    }

    public static List<InsertParams> getInsertParams(String sensorFileName, String obsFileName) throws Exception {
        List<InsertParams> insertParamsList = new ArrayList<InsertParams>();
        Map<String,Integer> linkedDataMap = new HashMap();
        //首先读取文件的属性名称存放在HashMap中
        BufferedReader obsFileReader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(obsFileName)));
        String bufferLine = "";
        while ((bufferLine = obsFileReader.readLine()) != null) {
            String[] eles = bufferLine.split(",");
            if (eles.length!=2) continue;
            String obsID = eles[0];
            int pos = Integer.valueOf(eles[1]);
            linkedDataMap.put(obsID, pos);
        }
        obsFileReader.close();
        //读取文件名称和属性名称
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(sensorFileName)));
        while ((bufferLine = bufferedReader.readLine()) != null) {
            //获取每一个传感器的信息
            InsertParams insertParams = new InsertParams();
            String[] eles = bufferLine.split(",");
            if (eles.length!=2) continue;
            String sensorID = eles[0];
            String dataFileName = eles[1];
            int dataCount = 0;
            String dataLine = "";
            BufferedReader dataBufferReader = new BufferedReader(new InputStreamReader(new FileInputStream(dataFileName)));
            while ((dataLine = dataBufferReader.readLine()) != null) {
                dataCount++;
            }
            dataBufferReader.close();

            //数据获取
            int propertyNum = linkedDataMap.entrySet().size();
            double[][] data = new double[dataCount][propertyNum];
            BufferedReader dataBufferReader1 = new BufferedReader(new InputStreamReader(new FileInputStream(dataFileName)));
            int currentLine = 0;
            while ((dataLine = dataBufferReader1.readLine()) != null) {
                String[] dataEles = dataLine.split(",");
                if (dataEles.length != propertyNum) throw new Exception("错误的数据，数据长度与属性信息不对等，" +
                        "文件名为："+dataFileName+",行数为:"+currentLine);
                //将数据赋值
                for (int i=0;i<dataEles.length;i++ ) {
                    data[currentLine][i] = Double.valueOf(dataEles[i]);
                }
            }
            dataBufferReader1.close();
            //解析传感器参数信息
            String dsSensorXML= Encode.getDescribeSensorXML(sensorID);
            String url= SensorConfigInfo.getUrl();
            String sensorXML= HttpRequestAndPost.sendPost(url, dsSensorXML);
            Sensor sensor = Decode.decodeDescribeSensor(sensorXML);

            insertParams.setSensor(sensor);
            insertParams.setLinkedDataMap(linkedDataMap);
            insertParams.setSensorData(data);

            insertParamsList.add(insertParams);
        }
        bufferedReader.close();
        return insertParamsList;
    }
}
