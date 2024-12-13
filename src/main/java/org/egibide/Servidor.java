package org.egibide;

import org.egibide.Modelo.Usuario;
import org.egibide.utils.General;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;

import java.security.KeyPair;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class Servidor {
    public static HashMap<String, Usuario> usuarios = new HashMap<>();
    // Variable para guardar el numero de incidenci recogidas por el servidor.
    public static int numIncidencia = 0;
    public static KeyPair serverKeys;
    public static SecretKey secretkey;


    public static void main(String[] args) {
        //Usuario de prueba
        /*
        try {
            Usuario usuario = new Usuario();
            usuario.setUsuario("admin");
            usuario.setNombre("Yo");
            usuario.setPass(General.elHash("SHA-256", "1234"));
            usuarios.put(usuario.getUsuario(), usuario);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("No se ha encontrado la implementación del algoritmo.");
        }*/


        int puerto = 6001;

        System.setProperty("javax.net.ssl.keyStore", "certificados\\AlmacenSSL.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "12345Abcde");

        SSLServerSocketFactory sfact = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        SSLServerSocket servidorSSL = null;

        try {
            servidorSSL = (SSLServerSocket) sfact.createServerSocket(puerto);

            System.out.println("servidor preparado");
            //Generar claves
            serverKeys = General.generarParClaves("RSA");

            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            secretkey = keyGenerator.generateKey();


            while (true) {
                SSLSocket clienteConectado = (SSLSocket) servidorSSL.accept();
                HiloServidor mihilo = new HiloServidor(clienteConectado);
                mihilo.start();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());

        }


    }

    public static synchronized void addUsuario(Usuario usuario) {
        usuarios.put(usuario.getUsuario(), usuario);
    }

    public static synchronized int addIncidencia() {
        numIncidencia++;
        return numIncidencia;
    }

}




    /*
    //ruta: C:\Users\9FDAM01\.jdks\openjdk-22.0.2\bin -> keytool.exe
    los comandos que usé para el certificado:
    keytool -genkey -keyalg RSA -alias claveSSL1 -keystore "C:\Users\9FDAM01\Documents\psp\psp_proyecto\certificados\AlmacenSSL.jks" -storepass 12345Abcde

    keytool -export -keystore "C:\Users\9FDAM01\Documents\psp\psp_proyecto\certificados\AlmacenSSL.jks" -alias claveSSL1 -file Certificado.cer

    keytool -import -alias claveSSL1 -file Certificado1.cer -keystore "C:\Users\9FDAM01\Documents\psp\psp_proyecto\certificados\AlmacenSSL.jks" -storepass 12345Abcde

     */
