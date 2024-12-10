package org.egibide;

import org.egibide.GUI.V_login;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.*;

public class Cliente {

    public static SSLSocket cliente = null;


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


        }catch (Exception e) {
            System.out.printf("Error: %s\n", e.getMessage());
        }

    }
}
