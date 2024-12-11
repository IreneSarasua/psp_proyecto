package org.egibide;

import org.egibide.Modelo.Categoria;
import org.egibide.Modelo.Incidencia;
import org.egibide.Modelo.Mensaje;
import org.egibide.Modelo.Usuario;
import org.egibide.utils.General;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

public class HiloServidor extends Thread {

    private SSLSocket cliente;
    private Usuario usuario;
    private Incidencia incidencia;
    private boolean continuar = true;

    ObjectOutputStream salida;
    ObjectInputStream lectura;

    public HiloServidor(SSLSocket cliente) {
        this.cliente = cliente;

    }

    @Override
    public void run() {
        // Escribir....
        System.out.println("Atendiendo petición");

        while (continuar && !cliente.isClosed()) {
            try {
                System.out.println("Socket closed: " + cliente.isClosed());
                lectura = new ObjectInputStream(cliente.getInputStream()); // recibimos infprmación
                Mensaje mensaje = (Mensaje) lectura.readObject();

                System.out.println("Leyendo mensaje");
                switch (mensaje.getTipomensaje()) {
                    case LOGIN -> login(mensaje);
                    case INCIDENCIA -> incidencia(mensaje);
                    case REGISTRO -> registro(mensaje);
                    case LOGOUT -> continuar = false;
                }
            } catch (IOException | NoSuchAlgorithmException e) {
                System.out.println("Error al leer: " + e.getMessage());
            } catch (ClassNotFoundException e) {
                System.out.println("Error, no se encontro  la clase: " + e.getMessage());
            }
        }
        System.out.println("Saliendo");

        try {
            if (salida != null) {
                salida.close();
            }
            if (lectura != null) {
                lectura.close();
            }
            if (!cliente.isClosed()) {
                cliente.close();
            }
        } catch (IOException e) {
            System.out.println("Error al cerrar el socket: " + e.getMessage());
        }
    }


    public void login(Mensaje mensaje) throws NoSuchAlgorithmException {
        Usuario usuarioRecivido = mensaje.getUsuario();
        usuarioRecivido.setPass(General.elHash("SHA-256", usuarioRecivido.getPassTexto()));

        this.usuario = Servidor.usuarios.get(usuarioRecivido.getUsuario());
        if (usuario != null) {
            if (Arrays.equals(usuario.getPass(), usuarioRecivido.getPass())) {
                mensaje.setUsuario(usuario);
            } else {
                usuario = null;
                mensaje.setUsuario(null);
                mensaje.setMensajeError("Usuario o contraseña incorrectas.");
            }
        } else {
            mensaje.setUsuario(null);
            mensaje.setMensajeError("Usuario o contraseña incorrectas.");
        }

        try {
            salida = new ObjectOutputStream(cliente.getOutputStream());
            salida.writeObject(mensaje);
            System.out.println("Enviando mensaje");
        } catch (IOException e) {
            System.out.println("Error al esctibir: " + e.getMessage());
        }

    }

    public void registro(Mensaje mensaje) {

    }

    public void incidencia(Mensaje mensaje) {
        Incidencia incidencia = mensaje.getIncidencia();
        incidencia.setCodigo(Servidor.addIncidencia());
        Random rand = new Random();
        incidencia.setCategoria(Categoria.values()[rand.nextInt(Categoria.values().length)]);
        incidencia.calcularTiempo();
        mensaje.setIncidencia(incidencia);

        try {
            salida = new ObjectOutputStream(cliente.getOutputStream());
            salida.writeObject(mensaje);
            System.out.println("Enviando mensaje");

            salida.close();
        } catch (IOException e) {
            System.out.println("Error al leer o esctibir: " + e.getMessage());
        }

    }


}
