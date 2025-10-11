package com.example.telitobodeguero.daos;

import com.example.telitobodeguero.beans.Distritos;
import com.example.telitobodeguero.beans.Zonas;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DistritosDao extends BaseDao {
    public ArrayList<Distritos> listaDistritos(){
        ArrayList<Distritos> listaDistritos = new ArrayList<>();

        try(Connection conn = this.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM distritos");){

            while (rs.next()){
                Distritos distrito = new Distritos();
                distrito.setIdDistritos(rs.getInt(1));
                distrito.setNombre(rs.getString(2));

                listaDistritos.add(distrito);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return listaDistritos;
    }
}
