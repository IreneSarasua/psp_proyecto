package org.egibide.GUI;

import org.egibide.Cliente;
import org.egibide.Modelo.Usuario;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

public class V_login {
    private JPanel panelPrincipal;
    private JTextField tf_username;
    private JPasswordField passf;
    private JButton btn_acceder;
    private JButton btn_registrarse;
    private JLabel label_titulo;


    public V_login() {


        btn_acceder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Usuario u = new Usuario();
                u.setUsuario(tf_username.getText());
                u.setPassTexto(Arrays.toString(passf.getPassword()));
                try {
                    ObjectOutputStream salida = new ObjectOutputStream(Cliente.cliente.getOutputStream());
                    salida.writeObject(u);

                    Cliente.cliente.close();
                } catch (IOException ex) {
                    System.out.printf("Error: %s", ex.getMessage());
                }

            }
        });
        btn_registrarse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    public JPanel getPanelPrincipal() {
        return panelPrincipal;
    }
}
