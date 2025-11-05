package com.example.telitobodeguero.daos;
import com.example.telitobodeguero.utils.HashUtil;

import com.example.telitobodeguero.beans.Distritos;
import com.example.telitobodeguero.beans.Roles;
import com.example.telitobodeguero.beans.Usuarios;

import java.sql.*;
import java.util.ArrayList;

public class UsuariosDao extends BaseDao{
    public Usuarios validarUsuario(String correo, String contrasenha){
        Usuarios usuario = null;

        String sql = "SELECT u.idUsuarios, u.nombre, u.apellido, u.correo, u.contrasenha, " +
                "       u.Distritos_idDistritos, u.activo, r.idRoles AS rol_id, r.nombre AS rol_nombre " +
                "FROM Usuarios u " +
                "INNER JOIN Roles r ON u.Roles_idRoles = r.idRoles " +
                "WHERE u.correo = ? AND u.contrasenha = ? AND u.activo = true";

        try (Connection conn = this.getConnection();
             PreparedStatement pstm = conn.prepareStatement(sql)) {

            // 1. hashear la contraseña que llegó del login
            String hashIngresado = HashUtil.sha256(contrasenha);

            // 2. enviar el correo y el hash al query (no el texto plano)
            pstm.setString(1, correo);
            pstm.setString(2, hashIngresado);

            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuarios();
                    usuario.setIdUsuarios(rs.getInt("idUsuarios"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setApellido(rs.getString("apellido"));
                    usuario.setCorreo(rs.getString("correo"));
                    usuario.setContrasenha(rs.getString("contrasenha")); // opcional guardarlo en sesión
                    usuario.setDistritos_idDistritos(rs.getInt("Distritos_idDistritos"));
                    usuario.setActivo(rs.getBoolean("activo"));

                    Roles rol = new Roles();
                    rol.setIdRoles(rs.getInt("rol_id"));
                    rol.setNombre(rs.getString("rol_nombre"));
                    usuario.setRol(rol);
                }
            }
        } catch (SQLException e) {
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
                            "       u.distritos_idDistritos, u.activo, r.idRoles AS rol_id, r.nombre AS rol_nombre, d.idDistritos AS distrito_id, d.nombre AS distrito_nombre " +
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
                usuario.setActivo(rs.getBoolean("activo"));
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

        try (Connection conn = this.getConnection()) {
            String sql = "INSERT INTO Usuarios " +
                    "(nombre, apellido, correo, contrasenha, Roles_idRoles, Distritos_idDistritos, activo) " +
                    "VALUES (?,?,?,?,?,?,true)";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, nombre);
                pstmt.setString(2, apellido);
                pstmt.setString(3, correo);

                // hash antes de guardar
                String hash = HashUtil.sha256(contrasenha);
                pstmt.setString(4, hash);

                pstmt.setInt(5, rolID);
                pstmt.setInt(6, distritoId);

                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public Usuarios buscarUsuario(int idUsuario){
        Usuarios usuario = null;
        try (Connection conn = this.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT * FROM usuarios WHERE idUsuarios = ?")) {
            pstmt.setInt(1, idUsuario);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuarios();
                    usuario.setIdUsuarios(rs.getInt(1));
                    usuario.setNombre(rs.getString(2));
                    usuario.setApellido(rs.getString(3));
                    usuario.setCorreo(rs.getString(4));
                    usuario.setContrasenha(rs.getString(5));
                    usuario.setActivo(rs.getBoolean("activo"));

                    int rolId  = rs.getInt(6);
                    if (!rs.wasNull()) {
                        Roles rol = new Roles();
                        rol.setIdRoles(rolId);
                        usuario.setRol(rol);
                    }

                    int distId = rs.getInt(7);
                    if (!rs.wasNull()) {
                        Distritos d = new Distritos();
                        d.setIdDistritos(distId);
                        usuario.setDistrito(d);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuario;
    }

    public void actualizarUsuario(int idUsuario,String nombre, String apellido, String correo, String contrasenha, int distritoId, int rolID){

        try (Connection conn = this.getConnection()) {

            boolean cambiaPass = contrasenha != null && !contrasenha.trim().isEmpty();

            String base = "UPDATE Usuarios SET nombre=?, apellido=?, correo=?, Distritos_idDistritos=?, Roles_idRoles=?";
            String sql = cambiaPass
                    ? base + ", contrasenha=? WHERE idUsuarios=?"
                    : base + " WHERE idUsuarios=?";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                int i = 1;
                pstmt.setString(i++, nombre);
                pstmt.setString(i++, apellido);
                pstmt.setString(i++, correo);
                pstmt.setInt(i++, distritoId);
                pstmt.setInt(i++, rolID);

                if (cambiaPass) {
                    String hashNueva = HashUtil.sha256(contrasenha);
                    pstmt.setString(i++, hashNueva);
                }

                pstmt.setInt(i, idUsuario);

                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void borrarUsuario(int idUsuario){

        try (Connection conn = this.getConnection();) {
                String sql = "UPDATE Usuarios SET activo = 0 WHERE idUsuarios = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, idUsuario);
                    pstmt.executeUpdate();
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
        }

    public void activarUsuario(int idUsuario){
        String sql = "UPDATE Usuarios SET activo = 1 WHERE idUsuarios = ?";
        try (Connection c = this.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, idUsuario); p.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public int contarUsuarios() {
        int total = 0;
        String sql = "SELECT COUNT(*) AS total FROM Usuarios";

        try (Connection conn = this.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                total = rs.getInt("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return total;
    }

    public ArrayList<Usuarios> listarUsuariosPaginado(int limit, int offset) {
        ArrayList<Usuarios> lista = new ArrayList<>();

        String sql = "SELECT u.idUsuarios, u.nombre, u.apellido, u.correo, " +
                "u.contrasenha, u.Distritos_idDistritos, u.activo, " +
                "r.idRoles AS rol_id, r.nombre AS rol_nombre " +
                "FROM Usuarios u " +
                "INNER JOIN Roles r ON u.Roles_idRoles = r.idRoles " +
                "ORDER BY u.idUsuarios ASC " +
                "LIMIT ? OFFSET ?";

        try (Connection conn = this.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);   // normalmente 10
            pstmt.setInt(2, offset);  // por ej. 0, 10, 20, ...

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Usuarios usuario = new Usuarios();
                    usuario.setIdUsuarios(rs.getInt("idUsuarios"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setApellido(rs.getString("apellido"));
                    usuario.setCorreo(rs.getString("correo"));
                    usuario.setContrasenha(rs.getString("contrasenha"));

                    usuario.setDistritos_idDistritos(rs.getInt("Distritos_idDistritos"));
                    usuario.setActivo(rs.getBoolean("activo"));

                    Roles rol = new Roles();
                    rol.setIdRoles(rs.getInt("rol_id"));
                    rol.setNombre(rs.getString("rol_nombre"));
                    usuario.setRol(rol);

                    lista.add(usuario);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public Usuarios buscarPorCorreo(String correo) {
        Usuarios usuario = null;
        String sql = "SELECT * FROM usuarios WHERE correo = ?";
        try (Connection conn = this.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, correo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuarios();
                    usuario.setIdUsuarios(rs.getInt("idUsuarios"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setApellido(rs.getString("apellido"));
                    usuario.setCorreo(rs.getString("correo"));
                    usuario.setContrasenha(rs.getString("contrasenha"));
                    usuario.setActivo(rs.getBoolean("activo"));

                    int rolId = rs.getInt("Roles_idRoles");
                    if (!rs.wasNull()) {
                        Roles rol = new Roles();
                        rol.setIdRoles(rolId);
                        usuario.setRol(rol);
                    }

                    int distId = rs.getInt("Distritos_idDistritos");
                    if (!rs.wasNull()) {
                        Distritos d = new Distritos();
                        d.setIdDistritos(distId);
                        usuario.setDistrito(d);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuario;
    }

}
