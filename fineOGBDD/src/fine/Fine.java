/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fine;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

/**
 *
 * @author Artem
 */
public class Fine {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException, InterruptedException {
        // TODO code application logic here
        Worker wrk = new Worker();
        wrk.starter();
        SQLFunction sqlf = new SQLFunction();
        sqlf.fineSorter();
        for(int ind=0; ind < args.length; ind++){
            if(args[ind].equals("-i"))
                sqlf.fineList(args[ind+1]);
        }
    }
}
