package org.egibide.dao;

import org.egibide.Modelo.Usuario;
import org.egibide.utils.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class UsuarioDao implements org.egibide.idao.UsuarioDao {

    @Override
    public int add(Usuario cliente) {
        return 0;
    }

    @Override
    public boolean delete(String username) {
        return false;
    }

    @Override
    public Usuario getUsuario(String username) {
        String query = "select * from usuarios where usuario=?";
        PreparedStatement ps;

        Usuario usuario = null;

        try {
            ps = DatabaseConnection.getInstance().getConnection().prepareStatement(query);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                try {
                    usuario = creaUsuario(rs);
                } catch (SQLException e) {
                    System.out.println("Error de sql: "+e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.out.println("Problema en get usuario: "+e.getMessage());
        }

        return usuario;
    }

    @Override
    public boolean update(Usuario cliente) {
        return false;
    }

    @Override
    public boolean usuarioExists(String username) {
        return false;
    }

    private static Usuario creaUsuario(ResultSet rs) throws SQLException {
        String fecha = rs.getString("fecha_nacimiento");
        LocalDate fecha_nacimiento = LocalDate.parse(fecha);
        return new Usuario(rs.getString("nombre"),
                rs.getString("apellidos"),
                //fecha_nacimiento,
                rs.getString("email"),
                rs.getString("ususario"),
                rs.getString("pass").getBytes());
    }
}
