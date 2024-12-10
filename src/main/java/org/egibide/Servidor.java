package org.egibide;

import org.egibide.Modelo.Usuario;
import org.egibide.utils.General;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Servidor {
    public static HashMap<String, Usuario> usuarios = new HashMap<>();

    public static void main(String[] args) {
        try {
            Usuario usuario = new Usuario();
            usuario.setUsuario("admin");
            usuario.setPass(General.elHash("SHA-256", "1234"));
            usuarios.put(usuario.getUsuario(), usuario);
        } catch (NoSuchAlgorithmException e){
            System.out.println("No se ha encontrado la implementación del algoritmo.");
        }



        int puerto = 6001;

        System.setProperty("javax.net.ssl.keyStore", "certificados\\AlmacenSSL.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "12345Abcde");

        SSLServerSocketFactory sfact = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        SSLServerSocket servidorSSL = null;

        try {
            servidorSSL = (SSLServerSocket) sfact.createServerSocket(puerto);

            System.out.println("servidor preparado");

            while (true) {
                SSLSocket clienteConectado = (SSLSocket) servidorSSL.accept();
                HiloServidor mihilo = new HiloServidor(clienteConectado);
                mihilo.start();
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }


    }

}




    /*
    //ruta: C:\Users\9FDAM01\.jdks\openjdk-22.0.2\bin -> keytool.exe
    los comandos para el certificado:
    keytool -genkey -keyalg RSA -alias claveSSL1 -keystore "C:\Users\9FDAM01\Documents\psp\psp_proyecto\certificados\AlmacenSSL.jks" -storepass 12345Abcde

    keytool -export -keystore "C:\Users\9FDAM01\Documents\psp\psp_proyecto\certificados\AlmacenSSL.jks" -alias claveSSL1 -file Certificado.cer

    keytool -import -alias claveSSL1 -file Certificado1.cer -keystore "C:\Users\9FDAM01\Documents\psp\psp_proyecto\certificados\AlmacenSSL.jks" -storepass 12345Abcde

     */
