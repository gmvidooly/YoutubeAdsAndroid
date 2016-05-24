package com.example.gulshan.youtubewebview;

import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
//import net.sourceforge.jtds.jdbc.Driver;

/**
 * Created by gulshan on 12/5/16.
 */
public class ConnectionClass {

    String host = "vidooly-webserver-oregon-snap.cvqiaycslvse.ap-southeast-1.rds.amazonaws.com";
    String classs = "net.sourceforge.jtds.jdbc.Driver";
    String db = "vidooly_v1";
    String un = "tred";
    String password = "7+cCG0";
    public Connection CONN() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL = null;
        try {
            Class.forName(classs);
            ConnURL = "jdbc:jtds:mysql://" + host + ";"
                    + "databaseName=" + db + ";user=" + un + ";password="
                    + password + ";";
            conn = DriverManager.getConnection(ConnURL);
        } catch (SQLException se) {
            se.printStackTrace();
            Log.e("ERRO", se.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.e("ERRO", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERRO", e.getMessage());
        }
        return conn;
    }
}
