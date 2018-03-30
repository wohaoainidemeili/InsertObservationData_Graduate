package yuan.flood.StartFeeder;

import javax.xml.soap.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Yuan on 2017/3/26.
 */
public class FeederSensor {
    public static void main(String[] args) throws IOException, SOAPException, InterruptedException {
        //addSOS in Feeder
        //setLoadSOS("http://localhost:8080/SOS/sos");
        Thread.sleep(10000);
        //set sensors enable
        BufferedReader sensorReader=new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("Sensors.txt")));
        String eles;
        while ((eles=sensorReader.readLine())!=null){
            setSensorFeederTrue(eles);
        }
    }
    public static void setSensorFeederTrue(String sensorID) throws SOAPException, MalformedURLException {
        SOAPConnectionFactory soapConnectionFactory=SOAPConnectionFactory.newInstance();
        SOAPConnection connection=soapConnectionFactory.createConnection();

        //������Ϣ����
        MessageFactory messageFactory=MessageFactory.newInstance();
        SOAPMessage message=messageFactory.createMessage();
        message.setProperty(SOAPMessage.CHARACTER_SET_ENCODING,"utf-8");

        //������Ϣ����
        SOAPPart soapPart=message.getSOAPPart();
        SOAPEnvelope envelope=soapPart.getEnvelope();
        SOAPBody soapBody=envelope.getBody();
        SOAPElement bodyEle=soapBody.addChildElement("usedSensors");
        bodyEle.addChildElement("sensor").addTextNode(sensorID);
        message.saveChanges();
        //message= getSoapMessageFromString(soapxml);//��ȡsoapxmlת��Ϊ��soapmessage����
        URL url = new URL("http://localhost:8083/52n-sos-ses-feeder-1.0.0/SosSesFeeder");
        SOAPMessage reply=connection.call(message,url);

    }
    private static void setLoadSOS(String sosUrl) throws SOAPException, MalformedURLException {
        SOAPConnectionFactory soapConnectionFactory=SOAPConnectionFactory.newInstance();
        SOAPConnection connection=soapConnectionFactory.createConnection();

        //������Ϣ����
        MessageFactory messageFactory=MessageFactory.newInstance();
        SOAPMessage message=messageFactory.createMessage();
        message.setProperty(SOAPMessage.CHARACTER_SET_ENCODING,"utf-8");

        //������Ϣ����
        SOAPPart soapPart=message.getSOAPPart();
        SOAPEnvelope envelope=soapPart.getEnvelope();
        SOAPBody soapBody=envelope.getBody();
        SOAPElement bodyEle=soapBody.addChildElement("addSOS");
        bodyEle.addChildElement("sos").addTextNode(sosUrl);
        message.saveChanges();
        //message= getSoapMessageFromString(soapxml);//��ȡsoapxmlת��Ϊ��soapmessage����
        URL url = new URL("http://localhost:8083/52n-sos-ses-feeder-1.0.0/SosSesFeeder");
        SOAPMessage reply=connection.call(message,url);
    }
}

