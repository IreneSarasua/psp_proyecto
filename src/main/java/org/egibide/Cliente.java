package org.egibide;

import org.egibide.GUI.V_login;
import org.egibide.Modelo.Mensaje;
import org.egibide.utils.General;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.PublicKey;

public class Cliente {

    public static SSLSocket cliente = null;
    public static PublicKey serverKey;
    public static KeyPair clientKeys;
    public static SecretKey secretKey;


    public static void main(String[] args) {

        String host = "localhost";
        int puerto = 6001;
        System.out.println("Programa cliente iniciado..");
        System.setProperty("javax.net.ssl.trustStore", "certificados\\AlmacenSSL.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "12345Abcde");
        SSLSocketFactory sfact = (SSLSocketFactory) SSLSocketFactory.getDefault();
        try {
            cliente = (SSLSocket) sfact.createSocket(host, puerto);
            System.out.println("Conexión con el servidor.");
            //Intercambio de claves publicas

            try {
                ObjectInputStream lectura = new ObjectInputStream(cliente.getInputStream());
                serverKey = (PublicKey) lectura.readObject();
                System.out.println("Reciviendo clave asimetrica");

                System.out.println("Generando Par de claves");
                clientKeys = General.generarParClaves("RSA");

                ObjectOutputStream salida = new ObjectOutputStream(cliente.getOutputStream());
                salida.writeObject(clientKeys.getPublic());
                System.out.println("Enviando clave");

                lectura = new ObjectInputStream(cliente.getInputStream());
                byte[] conAsim = (byte[]) lectura.readObject();
                byte[] conAsimDescifrado = General.descifrarConClavePrivada(conAsim, Cliente.clientKeys.getPrivate(), "RSA");
                secretKey = new SecretKeySpec(conAsimDescifrado, 0, conAsimDescifrado.length, "AES");
                System.out.println("Reciviendo clave simetrica");


            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error al leer o esctibir: " + e.getMessage());
            }

            if (!cliente.isClosed() && serverKey != null) {
                try {
                    // Set System L&F
                    UIManager.setLookAndFeel(
                            UIManager.getSystemLookAndFeelClassName());
                } catch (UnsupportedLookAndFeelException ex) {
                    // handle exception
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                }

                JFrame frame = new JFrame("Login");
                frame.setContentPane(new V_login(frame).getPanelPrincipal());
                //frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
                frame.setLocationRelativeTo(null);
            }

        } catch (Exception e) {
            System.out.printf("Error: %s\n", e.getMessage());
        }

    }


}
