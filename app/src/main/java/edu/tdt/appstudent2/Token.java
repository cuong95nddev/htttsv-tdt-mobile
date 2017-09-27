package edu.tdt.appstudent2;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by Anonymous Coder on 8/31/2016.
 */
public class Token {
    /* Trang service get Token của trường mình là http://sso.tdt.edu.vn/AuthService.asmx?WSDL
    Mình quan tâm đến cái <s:element name="Authenticate"> để request lấy token thôi
        <s:element minOccurs="0" maxOccurs="1" name="UserName" type="s:string"/>
        <s:element minOccurs="0" maxOccurs="1" name="Password" type="s:string"/>
    Dữ liệu trả về là 
        <s:element minOccurs="0" maxOccurs="1" name="MSSV" type="s:string"/>
        <s:element minOccurs="0" maxOccurs="1" name="MatKhau" type="s:string"/>
        <s:element minOccurs="1" maxOccurs="1" name="IsQuaTienDoDaoTao" type="s:boolean"/>
        <s:element minOccurs="0" maxOccurs="1" name="Token" type="s:string"/>
        <s:element minOccurs="0" maxOccurs="1" name="Password" type="s:string"/>
    */

    private static final String NAMESPACE = "http://tempuri.org/";
    private static final String URL = "http://sso.tdt.edu.vn/AuthService.asmx";
    private static final String SOAP_ACTION = "http://tempuri.org/Authenticate";
    private static final String METHOD_NAME = "Authenticate";


    public static String getToken(String username, String password){
        String token = "";
        SoapSerializationEnvelope soapClent = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        soapClent.implicitTypes = true;
        soapClent.dotNet = true;

        SoapObject soapObject = new SoapObject(NAMESPACE, METHOD_NAME);
        soapObject.addProperty("UserName", username);
        soapObject.addProperty("Password", password);

        soapClent.setOutputSoapObject(soapObject);

        HttpTransportSE httpTransport = new HttpTransportSE(URL, 60000);
        try{
            httpTransport.call(SOAP_ACTION, soapClent);
            SoapObject get = (SoapObject) soapClent.getResponse();
            token = get.getPrimitivePropertyAsString("Token");
        }catch (Exception e) {
            e.printStackTrace();
        }

        return token;
    }
}
