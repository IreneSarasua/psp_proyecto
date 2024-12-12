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
import java.security.*;
import java.util.Arrays;

import java.util.Random;

public class HiloServidor extends Thread {

    private SSLSocket cliente;
    private Usuario usuario;
    private boolean continuar = true;
    private PublicKey clientKey = null;

    ObjectOutputStream salida;
    ObjectInputStream lectura;

    public HiloServidor(SSLSocket cliente) {
        this.cliente = cliente;

    }

    @Override
    public void run() {

        System.out.println("Atendiendo petición");
        //Intercambio de claves publicas

        try {
            salida = new ObjectOutputStream(cliente.getOutputStream());
            salida.writeObject(Servidor.serverKeys.getPublic());
            System.out.println("Enviando clave asimetrica");

            lectura = new ObjectInputStream(cliente.getInputStream());
            clientKey = (PublicKey) lectura.readObject();
            System.out.println("Reciviendo clave cliente");

            salida = new ObjectOutputStream(cliente.getOutputStream());
            byte[] conAsim = General.cifrarConClavePublica(Servidor.secretkey.getEncoded() ,clientKey, "RSA");
            salida.writeObject(conAsim);
            System.out.println("Enviando clave simetrica");

        } catch (Exception e) {
            System.out.println("Error al leer o esctibir: " + e.getMessage());
        }

        if (clientKey != null) {
            while (continuar && !cliente.isClosed()) {
                try {

                    Mensaje mensaje = leerMensaje();

                    System.out.println("Leyendo mensaje");
                    if (mensaje != null) {
                        //divido las acciones dependiendo del tipo de mensaje
                        switch (mensaje.getTipomensaje()) {
                            case LOGIN -> login(mensaje);
                            case INCIDENCIA -> incidencia(mensaje);
                            case REGISTRO -> registro(mensaje);
                            case LOGOUT -> continuar = false;
                        }
                    }
                } catch (NoSuchAlgorithmException e) {
                    System.out.println("Error al leer: " + e.getMessage());
                } catch (Exception e) {
                    System.out.println("Error al cifrar o descifrar: " + e.getMessage());
                }
            }
            System.out.println("Saliendo");
        }
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


    /**
     * Se recoje el usuario y si existe y la contraseña es correcta se sevuelve el mensaje con todos los datos del usuario.
     * <br/>En caso contrario, se devuelve el mensaje con el usuarío a null y un mensaje de error.
     *
     * @param mensaje objeto que contiene la informacion
     * @throws NoSuchAlgorithmException si no encuenta el algoritmo para el hash de la contraseña
     */
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

        enviarMensaje(mensaje);

    }

    public void registro(Mensaje mensaje) {

    }

    /**
     * Recoge la incidencia, verifica la firma.<br/> En caso de firma verificada: le asigna el código, la categoría y llama al metodo para calcular el tiempo de arreglo.
     * <br/>Firma no verificada: Asigna el mesaje de error.
     * Después se devuelve el mensaje con todos los datos de la incidencia.
     *
     * @param mensaje objeto que contiene la informacion
     */
    public void incidencia(Mensaje mensaje) {
        Incidencia incidencia = mensaje.getIncidenciaOriginal();
        //Inicar verificación
        boolean check = false;
        try {
            Signature verificar = Signature.getInstance("SHA1withRSA");
            verificar.initVerify(mensaje.getPublicKey());
            verificar.update(incidencia.getBytes());

            check = verificar.verify(mensaje.getIncidenciaFirmada());
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            System.out.println("Error al verificar.");
        }

        if (check) {
            System.out.println("Firma verificada con clave pública");
            incidencia.setCodigo(Servidor.addIncidencia());
            Random rand = new Random();
            incidencia.setCategoria(Categoria.values()[rand.nextInt(Categoria.values().length)]);
            incidencia.calcularTiempo();
            mensaje.setIncidenciaOriginal(incidencia);
        } else {
            System.out.println("Firma no verificada");
            mensaje.setMensajeError("Firma no verificada.");
            mensaje.setIncidenciaOriginal(null);
            mensaje.setIncidenciaFirmada(null);
            mensaje.setPublicKey(null);
        }
        enviarMensaje(mensaje);

    }

    private Mensaje leerMensaje() {
        try {
            lectura = new ObjectInputStream(cliente.getInputStream()); // recibimos infprmación

            byte[] recivido = (byte[]) lectura.readObject();
            //byte[] recividoDescifrado = General.descifrarConClavePrivada(recivido, Servidor.serverKeys.getPrivate(), "RSA");
            byte[] conSim = General.descifrarObjeto(recivido, Servidor.secretkey, "AES");

            return Mensaje.toMensaje(conSim);
        } catch (IOException e) {
            System.out.println("Error al leer: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error al descifrar: " + e.getMessage());
        }
        return null;
    }

    private void enviarMensaje(Mensaje mensaje) {
        try {
            salida = new ObjectOutputStream(cliente.getOutputStream());
            //Cifrar
            byte[] conSim = General.cifrarObjeto(mensaje.getBytes(), Servidor.secretkey, "AES");
            //byte[] envio = General.cifrarConClavePublica(conSim, clientKey, "RSA");
            salida.writeObject(conSim);
            System.out.println("Enviando mensaje");
        } catch (IOException e) {
            System.out.println("Error al escribir: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error al cifrar: " + e.getMessage());
        }
    }
}
