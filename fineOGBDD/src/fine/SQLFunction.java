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
    }
    public Map getAllCars() throws SQLException{
        Map<Integer, String> cars = new HashMap();
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM cars LIMIT 10");
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
}
