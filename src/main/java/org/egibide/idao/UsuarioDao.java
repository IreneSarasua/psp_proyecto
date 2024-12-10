package org.egibide.idao;

import org.egibide.Modelo.Usuario;

import java.util.List;

public interface UsuarioDao {
    int add(Usuario cliente);
    boolean delete(String username);
    Usuario getUsuario(String username);
    boolean update(Usuario cliente);
    boolean usuarioExists(String username);


}
