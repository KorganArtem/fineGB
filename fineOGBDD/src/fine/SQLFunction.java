/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author korgan
 */
public class SQLFunction {
    public String url;
    public String login;
    public String pass;
    public Connection con;
    Map config;
    boolean iscon;
    public SQLFunction() throws ClassNotFoundException{
        ConfigurationReader cr = new ConfigurationReader();
        config=cr.readFile();
        url="jdbc:mysql://"+config.get("dbhost")+":"+config.get("dbport")+"/"+config.get("dbname")+"?useUnicode=true&characterEncoding=UTF-8";
        try
        {
            Class.forName("com.mysql.jdbc.Driver"); 
            login=config.get("dbuser").toString();
            pass=config.get("dbpassword").toString();
            con = DriverManager.getConnection(url, login, pass);
            iscon = true;
        }
        catch(SQLException ex)
        {
            System.out.println("Mysql ERROR: "+ex.getMessage());
        }
        System.out.println(url);
    }
    public Map<Integer, String> getAllCars() throws SQLException{
        Map<Integer, String> cars = new HashMap();
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM cars");
        while(rs.next()){
            cars.put(rs.getInt("id"), rs.getString("number")+"/"+rs.getString("sts"));
        }
        rs.close();
        st.close();
        return cars;
    }
    public void writefine(Map<String, String> fineData) throws SQLException{
        Statement st = con.createStatement();
        st.execute("INSERT INTO `fine` SET fineUis='"+fineData.get("fineUis")+"', "
                + "fineSum='"+fineData.get("fineSum")+"', "
                + "fineDate='"+fineData.get("fineDate")+"', "
                + "fineReason='"+fineData.get("fineReason")+"', "
                + "finePlace='"+fineData.get("finePlace")+"', "
                + "fineState='"+fineData.get("fineState")+"', "
                + "fineOffender='"+fineData.get("fineOffender")+"', "
                + "fineOffenderType='"+fineData.get("fineOffenderType")+"', "
                + "fineDatePay='"+fineData.get("fineDatePay")+"', "
                + "fineCar='"+fineData.get("fineCar")+"', "
                + "fineJson='"+fineData.get("fineJson")+"', "
                + "fineDescription='"+fineData.get("fineDescription")+"' ON DUPLICATE KEY UPDATE "
                + "fineState='"+fineData.get("fineState")+"', "
                + "fineDatePay='"+fineData.get("fineDatePay")+"'");
        st.close();
    }
    public int getCarId(String sts) throws SQLException{
        int carId = 0;
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM cars WHERE sts like '%"+sts+"%'");
        if(rs.next()){
            carId = rs.getInt("id");
        }
        return carId;
    }
    public Map<String, String> getCarData(int carId) throws SQLException{
        Map<String, String> carData = new HashMap<String, String>();
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM cars WHERE `id`="+carId);
        if(rs.next()){
            String reg = rs.getString("number").substring(rs.getString("number").length()-3);
            String numb = rs.getString("number").substring(0, rs.getString("number").length()-3);
            carData.put("auto_number", numb);
            carData.put("auto_region", reg);
            carData.put("auto_cdi", rs.getString("sts"));
        }
        return carData;
    }
    public void checkAndWrite(Map<String, String> fine) throws SQLException{
        System.out.println("Will check fine "+fine.get("bill_id"));
        if(chechFine(fine.get("bill_id"))){
            
        }
        else{
            Statement st = con.createStatement();
            String query ="INSERT INTO `offenses` SET `bill_id`='"+fine.get("bill_id")+"', "
                                        +"`gis_status`='"+fine.get("gis_status")+"', "  
                                        +"`pay_bill_date`='"+fine.get("pay_bill_date")+"', "  
                                        +"`last_bill_date`='"+fine.get("last_bill_date")+"', "  
                                        +"`pay_bill_amount`='"+fine.get("pay_bill_amount")  +"', "
                                        +"`gis_discount`='"+fine.get("gis_discount")  +"', "
                                        +"`gis_discount_uptodate`='"+fine.get("gis_discount_uptodate")  +"', "
                                        +"`pay_bill_amount_with_discount`='"+fine.get("pay_bill_amount_with_discount")  +"', "
                                        +"`offense_location`='"+fine.get("offense_location")  +"', "
                                        +"`offense_article`='"+fine.get("offense_article")  +"', "
                                        +"`offense_date`='"+fine.get("offense_date")  +"', "
                                        +"`offense_time`='"+fine.get("offense_time")  +"', "
                                        +"`offense_article_number`='"+fine.get("offense_article_number")  +"', "
                                        +"`json`='"+fine.get("json")  +"', "
                                        +"`carId`='"+fine.get("carId")+"' "  ;
            System.out.println(query);
            st.execute(query);
        }
    }
    public boolean chechFine(String billId) throws SQLException{
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT `bill_id` FROM `offenses` WHERE `bill_id`='"+billId+"'");
        if(rs.next()){
            rs.close();
            st.close();
            return true;
        } else{
            rs.close();
            st.close();
            return false;
        }
    }
}
