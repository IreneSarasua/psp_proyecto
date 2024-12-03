package org.egibide;

import org.egibide.Modelo.Incidencia;
import org.egibide.Modelo.Usuario;
import org.egibide.utils.General;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.NoSuchAlgorithmException;

public class HiloServidor extends Thread {

    private SSLSocket cliente;
    private Usuario usuario;
    private Incidencia incidencia;

    public HiloServidor(SSLSocket cliente) {
        this.cliente = cliente;
    }

    @Override
    public void run() {
        // Escribir....
        System.out.println("Atendiendo petición");
        try {
            ObjectOutputStream salida = new ObjectOutputStream(cliente.getOutputStream()); // enviamos información
            ObjectInputStream lectura = new ObjectInputStream(cliente.getInputStream()); // recibimos infprmación

            Usuario usuario = (Usuario) lectura.readObject();
            usuario.setPass(General.elHash("SHA-256", usuario.getPassTexto()));

            for (Usuario usu : Servidor.listaUsuarios) {
                if (usu.getUsuario().equals(usuario.getUsuario())) {
                    if (usu.getPass() == usuario.getPass()) {
                        System.out.println("bien");
                    }
                }
            }

            salida.close();
            lectura.close();
            cliente.close();
        } catch (ClassNotFoundException e) {
            System.out.println("Error al cargar el objeto: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error al comprobar la contraseña: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error al cerrar el socket: " + e.getMessage());
        }
    }
}
