/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fine;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

/**
 *
 * @author korgan
 */
public class Worker {
    SimpleDateFormat formating = new SimpleDateFormat("HH:mm:ss");
    String sid=null;
    String token = "t524MQjsBPSLYrgms6Dn";
    public Map<String, Map> getAllCarList() throws UnsupportedEncodingException, IOException, ClassNotFoundException, SQLException{
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("https://onlinegibdd.ru/api/partner_auto");
        // Request parameters and other properties.
        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
        params.add(new BasicNameValuePair("token", token));
        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

        //Execute and get the response.
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            InputStream instream = entity.getContent();
            try {
                JsonObject jo = jsonMaker(EntityUtils.toString(entity));
                System.out.println(jo);
                //parseFine(jo, carID);
            } finally {
                instream.close();
            } 
        }
        return null;
    }
    private JsonObject jsonMaker(String inputString){
        JsonParser parser = new JsonParser();
        JsonObject jsonObj;
        jsonObj = (JsonObject) parser.parse(inputString);
        return jsonObj;
    }
    public void addCare() throws UnsupportedEncodingException, IOException{
        
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("https://onlinegibdd.ru/api/partner_auto/save");
        // Request parameters and other properties.
        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
        params.add(new BasicNameValuePair("token", token));        
        params.add(new BasicNameValuePair("group_id", "1"));
        params.add(new BasicNameValuePair("auto_type", "firstCare"));
        params.add(new BasicNameValuePair("auto_number", "Н639СВ"));
        params.add(new BasicNameValuePair("auto_region", "750"));
        params.add(new BasicNameValuePair("madi", "0"));
        params.add(new BasicNameValuePair("auto_cdi", "5058821218"));
        params.add(new BasicNameValuePair("inn", "7723390010"));
        params.add(new BasicNameValuePair("kpp", "772701001"));
        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

        //Execute and get the response.
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            InputStream instream = entity.getContent();
            try {
                JsonObject jo = jsonMaker(EntityUtils.toString(entity));
                System.out.println(jo);
                //parseFine(jo, carID);
            } finally {
                instream.close();
            } 
        }
    }

    void getAllFine() throws IOException, ClassNotFoundException, SQLException {
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("https://api.onlinegibdd.ru/v2/partner_fines");
        // Request parameters and other properties.
        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
        params.add(new BasicNameValuePair("token", token));
        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

        //Execute and get the response.
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            InputStream instream = entity.getContent();
            try {
                JsonObject jo = jsonMaker(EntityUtils.toString(entity));
                System.out.println(jo);
                parseFine(jo);
                //parseFine(jo, carID);
            } finally {
                instream.close();
            } 
        }
    }
    private void parseFine(JsonObject fineCar) throws ClassNotFoundException, SQLException{
        File fl = new File("C:/out.json");
        
        System.out.println("--->>>"+fineCar.getAsJsonObject("auto_list").get("0"));
        System.out.println("--->>>"+fineCar.getAsJsonObject("auto_list").get("1"));
        System.out.println("--->>>"+fineCar.getAsJsonObject("auto_list").get("2"));
        System.out.println("--->>>"+fineCar.getAsJsonObject("auto_list").get("3"));
        System.out.println("--->>>"+fineCar.getAsJsonObject("auto_list").get("4"));
//        JsonArray allFine = fineCar.getAsJsonArray("auto_list").get(0).getAsJsonArray();
//        for(int i=0; i<allFine.size(); i++){
//            JsonObject oneFine = allFine.get(i).getAsJsonObject();
//            System.out.println(oneFine);
//        }
            
    }
}
