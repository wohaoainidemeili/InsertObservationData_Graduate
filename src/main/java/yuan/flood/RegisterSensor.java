package yuan.flood;


import net.opengis.om.x10.ObservationType;
import net.opengis.sensorML.x101.SensorMLDocument;
import net.opengis.sos.x10.ObservationTemplateDocument;
import net.opengis.sos.x10.RegisterSensorDocument;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import yuan.flood.Until.HttpRequestAndPost;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yuan on 2017/4/20.
 */
public class RegisterSensor {
    public static void main(String[] args) throws IOException, XmlException {
        String[] Paths=new String[]{"F:\\硕士论文\\毕业论文试验数据\\洪涝传感器建模文档\\气象传感器建模","F:\\硕士论文\\毕业论文试验数据\\洪涝传感器建模文档\\水位传感器建模"};
//        String[] Paths=new String[]{"F:\\","F:\\"};
        for (int i=0;i<Paths.length;i++) {
            File file = new File(Paths[i]);
            File[] files = file.listFiles();
            for (File file1 : files) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file1), "GB2312"));
                StringBuilder stringBuilder = new StringBuilder();
                String temp = null;
                while ((temp = bufferedReader.readLine()) != null) {
                    stringBuilder.append(temp);
                }
                bufferedReader.close();
                String sensorML = stringBuilder.toString();
                String response= HttpRequestAndPost.sendPost("http://localhost:8080/SOS/sos", sensorML);
                System.out.println(response);
            }
        }

    }
    public static void registerSensor(String sensorML) throws XmlException {
        String sosUrl="http://www.opengis.net/sos/1.0";
        RegisterSensorDocument registerSensorDocument= RegisterSensorDocument.Factory.newInstance();
        RegisterSensorDocument.RegisterSensor registerSensor= registerSensorDocument.addNewRegisterSensor();
        registerSensor.setService("SOS");
        registerSensor.setVersion("1.0.0");
        RegisterSensorDocument.RegisterSensor.SensorDescription sensorDescription= registerSensor.addNewSensorDescription();
        SensorMLDocument sensorMLDocument= SensorMLDocument.Factory.parse(sensorML);
        sensorDescription.set(sensorMLDocument);
        ObservationTemplateDocument.ObservationTemplate observationTemplate= registerSensor.addNewObservationTemplate();
        ObservationType observationType= observationTemplate.addNewObservation();
        observationType.addNewSamplingTime();
        observationType.addNewProcedure();
        observationType.addNewObservedProperty();
        observationType.addNewFeatureOfInterest();
        observationType.addNewResult();

        XmlOptions options=new XmlOptions();
        Map<String,String> nameSpace=new HashMap<String,String>();

        nameSpace.put("http://www.opengis.net/sos/1.0","");
        nameSpace.put("http://www.opengis.net/ows/1.1","ows");
        nameSpace.put("http://www.opengis.net/ogc","ogc");
        nameSpace.put("http://www.opengis.net/om/1.0","om");
        nameSpace.put("http://www.opengis.net/sos/1.0","sos");
        nameSpace.put("http://www.opengis.net/sampling/1.0","sa");
        nameSpace.put("http://www.opengis.net/gml","gml");
        nameSpace.put("http://www.opengis.net/swe/1.0.1","swe");
        nameSpace.put("http://www.w3.org/1999/xlink","xlink");
        nameSpace.put("http://www.w3.org/2001/XMLSchema-instance","xsi");
        options.setSaveSuggestedPrefixes(nameSpace);
        options.setSaveAggressiveNamespaces();
        options.setSavePrettyPrint();
        String registerXML= registerSensorDocument.xmlText(options);
        String response= HttpRequestAndPost.sendPost("http://localhost:8080/SOS/sos", registerXML);
        System.out.println(response);
    }
}
