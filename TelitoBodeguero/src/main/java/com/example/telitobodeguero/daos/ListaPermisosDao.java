package com.example.telitobodeguero.daos;

import com.example.telitobodeguero.beans.Permisos;
import com.example.telitobodeguero.beans.Roles;
import com.example.telitobodeguero.beans.Roles_has_Permisos;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class ListaPermisosDao {
    public Set<Integer> permisosActivosDeRol(int rolId) {
        Set<Integer> set = new HashSet<>();
        try {
            String user = "root";
            String pass = "12345678";
            String url = "jdbc:mysql://localhost:3306/bodega-telito";

            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, pass);
                 PreparedStatement ps = conn.prepareStatement(
                         "SELECT Permisos_idPermisos " +
                                 "FROM Roles_has_Permisos " +
                                 "WHERE Roles_idRoles=? AND activacion=1")) {
                ps.setInt(1, rolId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        set.add(rs.getInt("Permisos_idPermisos"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return set;
    }

    public ArrayList<Roles_has_Permisos> obtenerListaPermisos(){
        ArrayList<Roles_has_Permisos> listaPermisos=new ArrayList<>();
        try {
            String user = "root";
            String pass = "12345678";
            String url = "jdbc:mysql://localhost:3306/bodega-telito";

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, pass);
            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT r.idRoles AS rol_id, r.nombre AS rol_nombre, " +
                    "       p.idPermisos AS permiso_id, p.nombre AS permiso_nombre, " +
                    "       p.descripcion AS permiso_desc, rp.activacion AS activacion " +
                    "FROM Roles r " +
                    "INNER JOIN Roles_has_Permisos rp ON r.idRoles = rp.Roles_idRoles " +
                    "INNER JOIN Permisos p ON rp.Permisos_idPermisos = p.idPermisos " +
                    "ORDER BY r.idRoles");

            while (rs.next()) {

                Roles rol = new Roles();
                rol.setIdRoles(rs.getInt("rol_id"));
                rol.setNombre(rs.getString("rol_nombre"));

                Permisos permisos = new Permisos();
                permisos.setIdPermisos(rs.getInt("permiso_id"));
                permisos.setNombre(rs.getString("permiso_nombre"));
                permisos.setDescripcion(rs.getString("permiso_desc"));


                Roles_has_Permisos rhp = new Roles_has_Permisos();
                rhp.setRoles_idRoles(rol.getIdRoles());
                rhp.setPermisos_idPermisos(permisos.getIdPermisos());
                rhp.setRol(rol);
                rhp.setPermiso(permisos);
                rhp.setActivacion(rs.getBoolean("activacion"));

                listaPermisos.add(rhp);
            }
        }catch(ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return listaPermisos;
    }

    public void actualizarActivacion(int rolId, int permisoId, boolean activo) {
        try {
            String user = "root";
            String pass = "12345678";
            String url  = "jdbc:mysql://localhost:3306/bodega-telito";

            String sql = "UPDATE Roles_has_Permisos " +
                "SET activacion = ? " +
                "WHERE Roles_idRoles = ? AND Permisos_idPermisos = ?";


            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, pass);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setBoolean(1, activo);
                pstmt.setInt(2, rolId);
                pstmt.setInt(3, permisoId);
                pstmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
