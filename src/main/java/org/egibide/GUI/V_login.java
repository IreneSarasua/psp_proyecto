package org.egibide.GUI;

import org.egibide.Cliente;
import org.egibide.Modelo.Mensaje;
import org.egibide.Modelo.TipoMensaje;
import org.egibide.Modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class V_login {
    // region propiedades ventana

    private JPanel panelPrincipal;
    private JTextField tf_username;
    private JPasswordField passf;
    private JButton btn_acceder;
    private JButton btn_registrarse;
    private JLabel label_titulo;
    private JPanel panel_login;
    private JPanel panel_registro;
    private JTextField textField1;
    private JPasswordField passwordField1;
    private JTextField textField2;
    private JSpinner spinner1;
    private JTextField textField3;
    private JTextField textField4;
    private JButton guardarButton;
    private JButton volverButton;



    // endregion

    ObjectOutputStream salida ;
    ObjectInputStream lectura ;
    V_login yo;


    public V_login(JFrame vActual) {
        System.out.println("Ventana login");
        yo = this;
        panel_registro.setVisible(false);
        panel_login.setVisible(true);
        try {
            salida = new ObjectOutputStream(Cliente.cliente.getOutputStream());
            lectura = new ObjectInputStream(Cliente.cliente.getInputStream()); // recibimos infprmación
        } catch (IOException e) {
            System.out.println("Error al leer o esctibir: " + e.getMessage());
        }


        vActual.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
               Mensaje mensaje = new Mensaje(TipoMensaje.LOGOUT);
                try {
                    salida.writeObject(mensaje);
                    lectura.close();
                    salida.close();
                } catch (IOException e) {
                    System.out.printf("Error al cerrar: %s", e.getMessage());
                }

                vActual.dispose();
            }
        });

        btn_acceder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Usuario u = new Usuario();
                u.setUsuario(tf_username.getText());
                u.setPassTexto(new String(passf.getPassword()));
                Mensaje mensaje = new Mensaje(TipoMensaje.LOGIN, u);
                try {

                    salida.writeObject(mensaje);

                    mensaje = (Mensaje) lectura.readObject();
                    System.out.println(mensaje.getUsuario());
                    if (mensaje.getUsuario()!=null) {
                        limpiar();
                        V_incidencia wizard = new V_incidencia(mensaje.getUsuario());
                        wizard.setSize(380, 430);
                        wizard.setLocationRelativeTo(yo.panelPrincipal);
                        wizard.setVisible(true);


                    } else if(!mensaje.getMensajeError().isEmpty()){
                        JOptionPane.showMessageDialog(panelPrincipal, mensaje.getMensajeError(), "Login", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(panelPrincipal, "No se pudo conectar, intentelo más tarde.", "Login", JOptionPane.INFORMATION_MESSAGE);

                    }

                } catch (ClassNotFoundException ex) {
                    System.out.printf("Error al leer la respuesta: %s", ex.getMessage());
                } catch (IOException ex) {
                    System.out.printf("Error: %s", ex.getMessage());
                }

            }
        });
        btn_registrarse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel_registro.setVisible(true);
                panel_login.setVisible(false);
                vActual.pack();
            }
        });
        volverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiar();
                panel_registro.setVisible(false);
                panel_login.setVisible(true);
                vActual.pack();
            }
        });

        guardarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    private void limpiar() {
        tf_username.setText("");
        passf.setText("");
    }

    public JPanel getPanelPrincipal() {
        return panelPrincipal;
    }
}
