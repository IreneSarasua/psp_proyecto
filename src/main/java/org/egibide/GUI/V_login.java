package org.egibide.GUI;

import org.egibide.Cliente;
import org.egibide.Modelo.Mensaje;
import org.egibide.Modelo.TipoMensaje;
import org.egibide.Modelo.Usuario;
import org.egibide.utils.General;

import javax.swing.*;
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
    private JTextField tf_username2;
    private JPasswordField passf_2;
    private JTextField tf_apellido;
    private JSpinner sp_edad;
    private JTextField tf_email;
    private JTextField tf_nombre;
    private JButton btn_guardar;
    private JButton btn_volver;
    private JLabel label_username;
    private JLabel label_password;
    private JLabel label_username2;
    private JLabel label_nombre;
    private JLabel label_apellidos;
    private JLabel label_edad;
    private JLabel label_email;
    private JLabel label_titulo2;
    private JLabel label_password2;
    private JLabel label_anio;


    // endregion

    private ObjectOutputStream salida;
    private ObjectInputStream lectura;
    private final V_login yo;

    public V_login(JFrame vActual) {
        System.out.println("Ventana login");
        yo = this;
        panel_registro.setVisible(false);
        panel_login.setVisible(true);

        vActual.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                Mensaje mensaje = new Mensaje(TipoMensaje.LOGOUT);
                try {
                    enviarMensaje(mensaje);
                    lectura.close();
                    salida.close();
                    Cliente.cliente.close();
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

                //Enviamos mensaje
                enviarMensaje(mensaje);

                //Recivimos respuesta
                mensaje = leerMensaje();
                System.out.println("Leyendo mensaje");

                if (mensaje != null && mensaje.getUsuario() != null) {
                    limpiar();
                    V_incidencia wizard = new V_incidencia(mensaje.getUsuario());
                    wizard.setSize(380, 430);
                    wizard.setLocationRelativeTo(yo.panelPrincipal);
                    wizard.setVisible(true);


                } else if (mensaje != null && mensaje.getMensajeError() != null && !mensaje.getMensajeError().isEmpty()) {
                    JOptionPane.showMessageDialog(panelPrincipal, mensaje.getMensajeError(), "Login", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(panelPrincipal, "No se pudo conectar, intentelo más tarde.", "Login", JOptionPane.INFORMATION_MESSAGE);

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
        btn_volver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiar();
                panel_registro.setVisible(false);
                panel_login.setVisible(true);
                vActual.pack();
            }
        });

        btn_guardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validar()) {
                    System.out.println("valido");
                    Usuario u = new Usuario(tf_nombre.getText(), tf_apellido.getText(), (Integer) sp_edad.getModel().getValue(), tf_email.getText(), tf_username2.getText(), new String(passf.getPassword()));

                    Mensaje mensaje = new Mensaje(TipoMensaje.REGISTRO, u);
                    enviarMensaje(mensaje);
                    mensaje = leerMensaje();

                    if (mensaje != null && mensaje.getUsuario() != null) {
                        JOptionPane.showMessageDialog(panelPrincipal, "Usuario creado", "Registro", JOptionPane.INFORMATION_MESSAGE);
                        limpiar();
                        panel_registro.setVisible(false);
                        panel_login.setVisible(true);
                        vActual.pack();


                    } else if (mensaje != null && mensaje.getMensajeError() != null && !mensaje.getMensajeError().isEmpty()) {
                        JOptionPane.showMessageDialog(panelPrincipal, mensaje.getMensajeError(), "Registro", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(panelPrincipal, "No se pudo conectar, intentelo más tarde.", "Login", JOptionPane.INFORMATION_MESSAGE);
                    }

                } else {
                    JOptionPane.showMessageDialog(panelPrincipal, """
                                    Se deben rellenar todos los campos correctamente.
                                    - Edad: Mayor o igual a 16 años.
                                    - Email: con un @
                                    - Contraseña: minimo 6 caracteres, debe contener minusculas, mayusculas y numeros. 
                                                  Puede contener los caracteres especiales (, ?, y !.
                                    """
                            , "Registro", JOptionPane.INFORMATION_MESSAGE);

                }
            }
        });
    }


    private boolean validar() {

        return false;
    }

    private void limpiar() {
        tf_username.setText("");
        passf.setText("");

        tf_username2.setText("");
        tf_nombre.setText("");
        tf_apellido.setText("");
        cargarSpinner();
        tf_email.setText("");
        passf_2.setText("");
    }

    public JPanel getPanelPrincipal() {
        return panelPrincipal;
    }

    // Los usuarios tienen que tener minimo 16 años.
    private void cargarSpinner() {
        SpinnerNumberModel model = new SpinnerNumberModel(16, 16, 130, 1);
        sp_edad.setModel(model);
    }

    private void enviarMensaje(Mensaje mensaje) {
        try {
            salida = new ObjectOutputStream(Cliente.cliente.getOutputStream());
            //Cifrar
            byte[] conSim = General.cifrarObjeto(mensaje.getBytes(), Cliente.secretKey, "AES");
            //byte[] envio = General.cifrarConClavePublica(conSim, Cliente.serverKey, "RSA");
            salida.writeObject(conSim);
            System.out.println("Enviando mensaje");
        } catch (IOException e) {
            System.out.println("Error al escribir: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error al cifrar: " + e.getMessage());
        }
    }

    private Mensaje leerMensaje() {
        try {
            lectura = new ObjectInputStream(Cliente.cliente.getInputStream()); // recibimos infprmación

            byte[] recivido = (byte[]) lectura.readObject();
            //byte[] recividoDescifrado = General.descifrarConClavePrivada(recivido, Cliente.clientKeys.getPrivate(), "RSA");
            byte[] conSim = General.descifrarObjeto(recivido, Cliente.secretKey, "AES");
            return Mensaje.toMensaje(conSim);
        } catch (IOException e) {
            System.out.println("Error al leer: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error al descifrar: " + e.getMessage());
        }
        return null;
    }
}
