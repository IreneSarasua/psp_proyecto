package org.egibide;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;

public class Servidor {


    /*
    //ruta: C:\Users\9FDAM01\.jdks\openjdk-22.0.2\bin -> keytool.exe
    los comandos para el certificado:
    keytool -genkey -keyalg RSA -alias claveSSL1 -keystore "C:\Users\9FDAM01\Documents\psp\psp_proyecto\certificados\AlmacenSSL.jks" -storepass 12345Abcde

    keytool -export -keystore "C:\Users\9FDAM01\Documents\psp\psp_proyecto\certificados\AlmacenSSL.jks" -alias claveSSL1 -file Certificado.cer

    keytool -import -alias claveSSL1 -file Certificado1.cer -keystore "C:\Users\9FDAM01\Documents\psp\psp_proyecto\certificados\AlmacenSSL.jks" -storepass 12345Abcde

     */
    public static void main(String[] args) {
        int puerto = 6000;

        System.setProperty("javax.net.ssl.keyStore", ".certificados\\AlmacenSSL.jks");
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
