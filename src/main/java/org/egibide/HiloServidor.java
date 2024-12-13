package org.egibide;

import org.egibide.Modelo.*;
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
            byte[] conAsim = General.cifrarConClavePublica(Servidor.secretkey.getEncoded(), clientKey, "RSA");
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
     */
    public void login(Mensaje mensaje) {
        Usuario usuarioRecivido = mensaje.getUsuario();
        //usuarioRecivido.setPass(General.elHash("SHA-256", usuarioRecivido.getPassTexto()));

        Usuario usuario = Servidor.usuarios.get(usuarioRecivido.getUsuario());
        if (usuario != null && usuarioRecivido.getPass() != null) {
            if (Arrays.equals(usuario.getPass(), usuarioRecivido.getPass())) {
                mensaje.setUsuario(usuario);
            } else {
                mensaje.setUsuario(null);
                mensaje.setMensajeError("Usuario o contraseña incorrectas.");
            }
        } else {
            mensaje.setUsuario(null);
            mensaje.setMensajeError("Usuario o contraseña incorrectas.");
        }

        enviarMensaje(mensaje);

    }

    /**
     * Recoge la información, comprueba si existe un usuario con ese username y en caso de que
     * no exista, genera la contraseña y lo guarda en el hashmap. <br/>
     * Devuelve el mensaje con el usuario y si no se ha creado envia el mensaje.
     *
     * @param mensaje objeto que contiene la informacion
     */
    public void registro(Mensaje mensaje) {
        Usuario usuarioRecivido = mensaje.getUsuario();

        if (usuarioRecivido != null && Servidor.usuarios.containsKey(usuarioRecivido.getUsuario())) {
            mensaje = new Mensaje(TipoMensaje.REGISTRO);
            mensaje.setMensajeError("Nombre de usuario ya existente, prueba con otro nombre.");
        } else if (usuarioRecivido != null && usuarioRecivido.getPass() != null) {

            Servidor.usuarios.put(usuarioRecivido.getUsuario(), usuarioRecivido);
            mensaje.setUsuario(usuarioRecivido);

        } else {
            mensaje = new Mensaje(TipoMensaje.REGISTRO);
            mensaje.setMensajeError("Error en el envio.");
        }

        enviarMensaje(mensaje);
        System.out.println("Enviando respuesta registro");


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
