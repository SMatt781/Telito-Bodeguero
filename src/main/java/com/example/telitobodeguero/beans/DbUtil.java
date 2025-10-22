package com.example.telitobodeguero.beans;

import java.sql.*;

public class DbUtil {
    private static final String URL  = "jdbc:mysql://127.0.0.1:3306/Bodega-Telito?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "12345678";

    static {
        try { Class.forName("com.mysql.cj.jdbc.Driver"); }
        catch (ClassNotFoundException e) { throw new RuntimeException("No se encontró el driver MySQL", e); }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
