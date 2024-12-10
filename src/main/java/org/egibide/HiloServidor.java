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
        try {
            //salida = new ObjectOutputStream(cliente.getOutputStream());
            lectura = new ObjectInputStream(cliente.getInputStream()); // recibimos infprmación
        } catch (IOException e) {
            System.out.println("Error al leer o esctibir: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        // Escribir....
        System.out.println("Atendiendo petición");

        try {

            while (continuar) {
                salida = new ObjectOutputStream(cliente.getOutputStream()); // enviamos información

                //ObjectInputStream lectura = new ObjectInputStream(cliente.getInputStream()); // recibimos infprmación
                Mensaje mensaje = (Mensaje) lectura.readObject();

                System.out.println("Leyendo mensaje");

                switch (mensaje.getTipomensaje()) {
                    case LOGIN -> login(mensaje);
                    case INCIDENCIA -> incidencia(mensaje);
                    case REGISTRO -> registro(mensaje);
                    case LOGOUT -> continuar = false;
                }

            }
            System.out.println("Saliendo");
            salida.close();
            lectura.close();
            cliente.close();
        } catch (ClassNotFoundException e) {
            System.out.println("Error al cargar el objeto: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error al comprobar la contraseña: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }


    public void login(Mensaje mensaje) throws NoSuchAlgorithmException, IOException {
        Usuario usuarioRecivido = mensaje.getUsuario();
        usuarioRecivido.setPass(General.elHash("SHA-256", usuarioRecivido.getPassTexto()));
//        ObjectOutputStream salida = new ObjectOutputStream(cliente.getOutputStream()); // enviamos información

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
        salida.writeObject(mensaje);
        System.out.println("Enviando mensaje");

//        salida.close();
    }

    public void registro(Mensaje mensaje) {

    }

    public void incidencia(Mensaje mensaje) throws IOException {
        Incidencia incidencia = mensaje.getIncidencia();
        incidencia.setCodigo(Servidor.addIncidencia());
        Random rand = new Random();
        incidencia.setCategoria(Categoria.values()[rand.nextInt(Categoria.values().length)]);
        incidencia.calcularTiempo();
        mensaje.setIncidencia(incidencia);

        salida.writeObject(mensaje);

    }


}
