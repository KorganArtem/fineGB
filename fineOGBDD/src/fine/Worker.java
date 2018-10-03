/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fine;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.imageio.ImageIO;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import sun.misc.BASE64Decoder;

/**
 *
 * @author korgan
 */
public class Worker {
    SimpleDateFormat formating = new SimpleDateFormat("HH:mm:ss");
    String sid=null;
    String token = "t524MQjsBPSLYrgms6Dn";
    SQLFunction sqlf;
    Worker() throws ClassNotFoundException{
        sqlf = new SQLFunction();
    }
    public Map<String, String> getAllCarList() throws UnsupportedEncodingException, IOException, ClassNotFoundException, SQLException{
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("https://onlinegibdd.ru/api/partner_auto");
        // Request parameters and other properties.
        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
        params.add(new BasicNameValuePair("token", token));
        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

        //Execute and get the response.
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();
         Map<String, String> carListOGBDD = new HashMap<String, String>();
        if (entity != null) {
            InputStream instream = entity.getContent();
            try {
                JsonObject jo = jsonMaker(EntityUtils.toString(entity));
                JsonObject carList = jo.getAsJsonObject("data").get("autos").getAsJsonObject();
                
                Set<Entry<String, JsonElement>> entrySet = carList.entrySet();
                for(Map.Entry<String,JsonElement> entry : entrySet){
                    String carIdOGBDD = carList.get(entry.getKey()).getAsJsonObject().get("auto_id").getAsString();
                    String carStsOGBDD = carList.get(entry.getKey()).getAsJsonObject().get("auto_cdi").getAsString();
                    carListOGBDD.put(carIdOGBDD, carStsOGBDD);
                }
            } finally {
                instream.close();
            } 
        }
        return carListOGBDD;
    }
    public void addCare(Map<String, String> carData) throws UnsupportedEncodingException, IOException{
        
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("https://onlinegibdd.ru/api/partner_auto/save");
        // Request parameters and other properties.
        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
        params.add(new BasicNameValuePair("token", token));        
        params.add(new BasicNameValuePair("group_id", "1"));
        params.add(new BasicNameValuePair("auto_type", "firstCare"));
        params.add(new BasicNameValuePair("auto_number", carData.get("auto_number")));
        params.add(new BasicNameValuePair("auto_region", carData.get("auto_region")));
        params.add(new BasicNameValuePair("madi", "0"));
        params.add(new BasicNameValuePair("auto_cdi", carData.get("auto_cdi")));
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
    void carFine(String carId, int carIdRTS) throws UnsupportedEncodingException, IOException, ClassNotFoundException, SQLException{
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("https://api.onlinegibdd.ru/v2/partner_fines");
        // Request parameters and other properties.
        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
        params.add(new BasicNameValuePair("token", token));
        params.add(new BasicNameValuePair("autos_ids", carId));         
        params.add(new BasicNameValuePair("status", "all")); 
        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

        //Execute and get the response.
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            InputStream instream = entity.getContent();
            try {
                JsonObject jo = jsonMaker(EntityUtils.toString(entity));
                if(jo.getAsJsonObject("auto_list").size()>0){
                    JsonObject fineList = jo.getAsJsonObject("auto_list").getAsJsonObject("0").getAsJsonObject("offense_list");
                    Set<Entry<String, JsonElement>> entrySet = fineList.entrySet();
                    for(Map.Entry<String,JsonElement> entry : entrySet){
                        //carFine(fineList.get(entry.getKey()).getAsJsonObject().get("auto_id").getAsString());
                        System.out.println(fineList.get(entry.getKey()));
                        sendToSQL(fineList.getAsJsonObject(entry.getKey()), carIdRTS);
                    }
                }
                //parseFine(jo, carID);
            } finally {
                instream.close();
            } 
        }
    }
    private void sendToSQL(JsonObject fineJson, int carId) throws ClassNotFoundException{
        Map<String, String> fine = new HashMap<String, String>();
            fine.put("bill_id", fineJson.get("bill_id").getAsString()); 
        try{ 
            fine.put("gis_status", fineJson.get("gis_status").getAsString());  
            fine.put("pay_bill_date", fineJson.get("pay_bill_date").getAsString());  
            fine.put("last_bill_date", fineJson.get("last_bill_date").getAsString());  
            fine.put("pay_bill_amount", fineJson.get("pay_bill_amount").getAsString());  
            fine.put("gis_discount", fineJson.get("gis_discount").getAsString());  
            fine.put("gis_discount_uptodate", fineJson.get("gis_discount_uptodate").getAsString());  
            fine.put("pay_bill_amount_with_discount", fineJson.get("pay_bill_amount_with_discount").getAsString());  
            fine.put("offense_location", fineJson.get("offense_location").getAsString());  
            fine.put("offense_article", fineJson.get("offense_article").getAsString());  
            fine.put("offense_date", fineJson.get("offense_date").getAsString());  
            fine.put("offense_time", fineJson.get("offense_time").getAsString());  
            fine.put("offense_article_number", fineJson.get("offense_article_number").getAsString());  
            fine.put("json", fineJson.toString());  
            fine.put("carId", carId+""); 
            sqlf.checkAndWrite(fine);
        }
        catch(Exception ex){
            System.out.println("Error in sendToSQL: "+ex.getMessage());
        }
    }
    public void starter() throws ClassNotFoundException, SQLException, IOException, InterruptedException{
        Map<Integer, String> calListRTS = sqlf.getAllCars();
        Map<String, String> carListOGBDD = getAllCarList();
        for (Entry<String, String> entry : carListOGBDD.entrySet()) {
            int carId = sqlf.getCarId(entry.getValue());
            System.out.println("IDOGBDD = " + entry.getKey() + " STS = " + entry.getValue()+ " IDRTS ="+ carId);
            calListRTS.remove(carId);
            try{
                carFine(entry.getKey(), carId);
            }
            catch(Exception ex){
                System.out.println(ex.getMessage());
            }
            Thread.sleep(2000);
        }
        System.out.println(calListRTS.size());
        checkCarsNotMonit(calListRTS);
    }
    private void checkCarsNotMonit(Map<Integer, String> carList ) throws ClassNotFoundException, SQLException, IOException{
        for (Entry<Integer, String> entry : carList.entrySet()) {
            System.out.println(entry.getKey()+"   "+entry.getValue());
            addCare(sqlf.getCarData(entry.getKey().intValue()));
        }
    }
    private JsonObject jsonMaker(String inputString){
        JsonParser parser = new JsonParser();
        JsonObject jsonObj;
        jsonObj = (JsonObject) parser.parse(inputString);
        return jsonObj;
    }
    public void offendPhoto(String bill_id) throws UnsupportedEncodingException, IOException, ClassNotFoundException, SQLException{
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("https://api.onlinegibdd.ru/partner_fines/get_photos");
        // Request parameters and other properties.
        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
        params.add(new BasicNameValuePair("token", token));
        params.add(new BasicNameValuePair("bill_id", bill_id));       
        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            JsonObject jo = jsonMaker(EntityUtils.toString(entity));
            if(jo.has("photos")){
                JsonObject photoList = jo.get("photos").getAsJsonObject();
                Set<Entry<String, JsonElement>> entrySet = photoList.entrySet();
                for(Map.Entry<String,JsonElement> entry : entrySet){
                    String photoNum = entry.getKey();
                    String sourceData = photoList.get(entry.getKey()).getAsString();
                    if(sourceData.length()<10)
                        continue;
                    BufferedImage image = null;
                    byte[] imageByte;
                    BASE64Decoder decoder = new BASE64Decoder();
                    imageByte = decoder.decodeBuffer(sourceData);
                    ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
                    image = ImageIO.read(bis);
                    bis.close();
                    File outputfile = new File("photo/"+bill_id+"_"+photoNum+".png");
                    sqlf.photoWrite(bill_id, outputfile.getAbsoluteFile().toString());
                    ImageIO.write(image, "png", outputfile);
                }
                sqlf.setPhotoGeted(bill_id);
            }
            else{
                System.out.println(jo);
                if(jo.has("errors")){
                    if(jo.get("errors").getAsJsonObject().has("exist"))
                        sqlf.setPhotoGeted(bill_id);
                }
            }
        }
    }
}
