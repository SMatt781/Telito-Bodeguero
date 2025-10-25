package com.example.telitobodeguero.daos;

import java.nio.channels.ScatteringByteChannel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class BaseDao {
    
    public Connection getConnection() throws SQLException{
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException ex){
            ex.printStackTrace();
        }

        String user= "root";
        String pass ="12345678";
        String url = "jdbc:mysql://localhost:3306/Bodega-Telito";

        return DriverManager.getConnection(url,user,pass);
    }
    
}
