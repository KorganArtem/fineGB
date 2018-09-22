package fine;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author korgan
 */
public class WorkerNU {
    SimpleDateFormat formating = new SimpleDateFormat("HH:mm:ss");
    String sid=null;
    String token = "ffacdefa221c95a97d5ab187e2c5ebef";
    public WorkerNU() throws UnsupportedEncodingException, IOException{
        formating.setTimeZone(TimeZone.getTimeZone("GMT"));
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("http://api.pub.emp.msk.ru:8081/json/v10.0/citizens/auth/loginbymsisdn");
        JsonObject authResult = null;
        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
        params.add(new BasicNameValuePair("token", token));
        params.add(new BasicNameValuePair("msisdn", "79265831376"));
        params.add(new BasicNameValuePair("password", "12345"));
        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            InputStream instream = entity.getContent();
            try {
                authResult = jsonMaker(EntityUtils.toString(entity));
                sid=authResult.get("result").toString().replaceAll("\"", "");
            } finally {
                instream.close();
            }
        }
    }
    public void addvalueToProfile() throws UnsupportedEncodingException, IOException{
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("http://api.pub.emp.msk.ru:8081/json/v10.0/citizens/options/add");
        // Request parameters and other properties.
        JsonArray values = new JsonArray();
        JsonObject value = new JsonObject();
        value.add("type", new JsonPrimitive("vehicle_number"));
        value.add("value", new JsonPrimitive("А722НВ750"));
        values.add(value);
        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
        params.add(new BasicNameValuePair("token", "ffacdefa221c95a97d5ab187e2c5ebef"));
        params.add(new BasicNameValuePair("session_id", sid));
        //params.add(new BasicNameValuePair("option_id", "148"));
        params.add(new BasicNameValuePair("values", values.toString()));
        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

        //Execute and get the response.
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            InputStream instream = entity.getContent();
            try {
                JsonObject jo = jsonMaker(EntityUtils.toString(entity));
            } finally {
                instream.close();
            } 
        }
        JsonObject resVal = new JsonObject();
        JsonElement tk = new JsonPrimitive(token);
        JsonElement si = new JsonPrimitive(sid);
        resVal.add("token", tk);
        resVal.add("session_ids", si);
        resVal.add("values", values);
    }
    public void getAllData() throws UnsupportedEncodingException, IOException{
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("http://api.pub.emp.msk.ru:8081/json/v10.0/citizens/options/getall");
        // Request parameters and other properties.
        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
        params.add(new BasicNameValuePair("token", token));
        params.add(new BasicNameValuePair("session_id", sid));
        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

        //Execute and get the response.
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            InputStream instream = entity.getContent();
            try {
                JsonObject jo = jsonMaker(EntityUtils.toString(entity));
            } finally {
                instream.close();
            } 
        }
    }
    public void setValueDefault() throws UnsupportedEncodingException, IOException{
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("http://api.pub.emp.msk.ru:8081/json/v10.0/citizens/options/setdefaultoption");
        // Request parameters and other properties.
        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
        params.add(new BasicNameValuePair("token", "ffacdefa221c95a97d5ab187e2c5ebef"));
        params.add(new BasicNameValuePair("session_id", sid));
        params.add(new BasicNameValuePair("option_id", "45283367"));
        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

        //Execute and get the response.
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            InputStream instream = entity.getContent();
            try {
                JsonObject jo = jsonMaker(EntityUtils.toString(entity));
            } finally {
                instream.close();
            } 
        }
    }
    private JsonObject jsonMaker(String inputString){
        JsonParser parser = new JsonParser();
        JsonObject jsonObj;
        jsonObj = (JsonObject) parser.parse(inputString);
        return jsonObj;
    }
}
