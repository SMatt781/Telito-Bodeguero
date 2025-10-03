package com.example.telitobodeguero.daos;

import com.example.telitobodeguero.beans.Distritos;
import com.example.telitobodeguero.beans.Roles;
import com.example.telitobodeguero.beans.Usuarios;

import java.sql.*;
import java.util.ArrayList;

public class UsuariosDao extends BaseDao{
    public Usuarios validarUsuario(String correo, String contrasenha){
        Usuarios usuario = null;

        String sql = "SELECT u.idUsuarios, u.nombre, u.apellido, u.correo, u.contrasenha, " +
               "       u.Distritos_idDistritos, r.idRoles AS rol_id, r.nombre AS rol_nombre " +
               "FROM Usuarios u " +
               "INNER JOIN Roles r ON u.Roles_idRoles = r.idRoles " +
               "WHERE u.correo = ? AND u.contrasenha = ?";


        try (Connection conn = this.getConnection();
             PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, correo);
            pstm.setString(2, contrasenha);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuarios();
                    usuario.setIdUsuarios(rs.getInt("idUsuarios"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setApellido(rs.getString("apellido"));
                    usuario.setCorreo(rs.getString("correo"));
                    usuario.setContrasenha(rs.getString("contrasenha"));
                    usuario.setDistritos_idDistritos(rs.getInt("Distritos_idDistritos"));

                    Roles rol = new Roles();
                    rol.setIdRoles(rs.getInt("rol_id"));
                    rol.setNombre(rs.getString("rol_nombre"));
                    usuario.setRol(rol);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();

        }
        return usuario;
    }
    public ArrayList<Usuarios> obtenerUsuarios(){
        ArrayList<Usuarios> listaUsuarios=new ArrayList<>();
        try{

            Connection conn = this.getConnection();
            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery(
                    "SELECT u.idUsuarios, u.nombre, u.apellido, u.correo," +
                            "       u.distritos_idDistritos, r.idRoles AS rol_id, r.nombre AS rol_nombre, d.idDistritos AS distrito_id, d.nombre AS distrito_nombre " +
                            "FROM usuarios u " +
                            "INNER JOIN roles r ON u.Roles_idRoles = r.idRoles INNER JOIN distritos d ON u.distritos_idDistritos = d.idDistritos ORDER BY u.idUsuarios ASC"
            );
            while(rs.next()){
                Usuarios usuario=new Usuarios();
                usuario.setIdUsuarios(rs.getInt("idUsuarios"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setApellido(rs.getString("apellido"));
                usuario.setCorreo(rs.getString("correo"));
                //usuario.setRoles_idRoles(rs.getInt("rol_id"));
                usuario.setDistritos_idDistritos(rs.getInt("distritos_idDistritos"));

                Roles rol = new Roles();
                rol.setIdRoles(rs.getInt("rol_id"));
                rol.setNombre(rs.getString("rol_nombre"));
                usuario.setRol(rol);

                Distritos distrito = new Distritos();
                distrito.setIdDistritos(rs.getInt("distrito_id"));
                distrito.setNombre(rs.getString("distrito_nombre"));
                usuario.setDistrito(distrito);

                listaUsuarios.add(usuario);

            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return listaUsuarios;
    }

    public void crearUsuario(String nombre, String apellido, String correo, int distritoId, String contrasenha, int rolID){

            try(Connection conn= this.getConnection();){
                String sql=
                        "INSERT INTO Usuarios (nombre,apellido,correo,contrasenha,Roles_idRoles,Distritos_idDistritos) " +
                        "VALUES (?,?,?,?,?,?)";
                try(PreparedStatement pstmt = conn.prepareStatement(sql)){
                    pstmt.setString(1,nombre);
                    pstmt.setString(2,apellido);
                    pstmt.setString(3,correo);
                    pstmt.setString(4,contrasenha);
                    pstmt.setInt(5, rolID);
                    pstmt.setInt(6, distritoId);
                    pstmt.executeUpdate();
                }
            }catch ( SQLException e){
                e.printStackTrace();
            }
    }
    public Usuarios buscarUsuario(int idUsuario){
        Usuarios usuario = null;

            try(Connection conn = this.getConnection();
                PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM usuarios WHERE idUsuarios = ?");){
                pstmt.setInt(1, idUsuario);

                try(ResultSet rs = pstmt.executeQuery()){
                    if (rs.next()){
                        usuario = new Usuarios();
                        usuario.setIdUsuarios(rs.getInt(1));
                        usuario.setNombre(rs.getString(2));
                        usuario.setApellido(rs.getString(3));
                        usuario.setCorreo(rs.getString(4));
                        usuario.setContrasenha(rs.getString(5));
                        usuario.setRoles_idRoles(rs.getInt(6));
                        usuario.setDistritos_idDistritos(rs.getInt(7));
                    }
                }
            }catch ( SQLException e) {
                e.printStackTrace();
            }
        return usuario;
    }

    public void actualizarUsuario(int idUsuario,String nombre, String apellido, String correo, String contrasenha, int distritoId, int rolID){

            try(Connection conn = this.getConnection();){
                boolean cambiaPass = contrasenha != null && !contrasenha.trim().isEmpty();

                String base = "UPDATE Usuarios SET nombre=?, apellido=?, correo=?, Distritos_idDistritos=?, Roles_idRoles=?";
                String sql = cambiaPass ? base + ", contrasenha=? WHERE idUsuarios=?"
                        : base + " WHERE idUsuarios=?";
                try(PreparedStatement pstmt = conn.prepareStatement(sql)){
                    int i = 1;
                    pstmt.setString(i++,nombre);
                    pstmt.setString(i++,apellido);
                    pstmt.setString(i++,correo);
                    pstmt.setInt(i++,distritoId);
                    pstmt.setInt(i++,rolID);
                    if (cambiaPass){
                        pstmt.setString(i++, contrasenha);
                    }
                    pstmt.setInt(i, idUsuario);
                    pstmt.executeUpdate();
                }
            }catch (SQLException e) {
                e.printStackTrace();

            }
    }

    public void borrarUsuario(int idUsuario){

        try (Connection conn = this.getConnection();) {
                String sql = "DELETE FROM Usuarios WHERE idUsuarios = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, idUsuario);
                    pstmt.executeUpdate();
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
        }

}
