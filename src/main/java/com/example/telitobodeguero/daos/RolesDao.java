package com.example.telitobodeguero.daos;

import com.example.telitobodeguero.beans.Roles;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class RolesDao extends BaseDao{
    public ArrayList<Roles> obtenerListaRoles(){
        ArrayList<Roles> listaRoles = new ArrayList<>();

        try(Connection conn = this.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs= stmt.executeQuery("Select * FROM roles");) {
            while (rs.next()){
                Roles rol = new Roles();
                rol.setIdRoles(rs.getInt(1));
                rol.setNombre(rs.getString(2));
                listaRoles.add(rol);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return listaRoles;
    }
}
