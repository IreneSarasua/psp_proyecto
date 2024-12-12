package org.egibide.GUI;

import org.egibide.Cliente;
import org.egibide.Modelo.Incidencia;
import org.egibide.Modelo.Mensaje;
import org.egibide.Modelo.TipoMensaje;
import org.egibide.Modelo.Usuario;
import org.egibide.utils.General;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.*;
import java.util.ArrayList;
import java.util.List;


public class V_incidencia extends JDialog {
    // region propiedades ventana
    private JPanel panelPrincipal;
    private JPanel panel_nuevaIncidencia;
    private JTextField tf_asunto;
    private JTextField tf_lugar;
    private JTextArea ta_descripcion;
    private JScrollPane jsp_incidencias;
    private JLabel label_titulo;
    private JLabel label_asunto;
    private JLabel label_lugar;
    private JLabel label_descripcion;
    private JButton verIncidenciasButton;
    private JButton enviarButton;
    private JButton limpiarButton;
    private JPanel panel_botones;
    private JTextField tf_usuario;
    private JLabel label_usuario;

    // endregion

    private ObjectOutputStream salida;
    private ObjectInputStream lectura;

    private List<Incidencia> listaIncidencias = new ArrayList<>();
    private KeyPair keyPair;

    public V_incidencia(Usuario usuario) {
        super((Frame) null, true);
        setContentPane(panelPrincipal);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(500, 160);
        try {
            keyPair = General.generarParClaves("RSA");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al generar las claves.", "Error", JOptionPane.INFORMATION_MESSAGE);
        }

        tablaIncidencias();
        tf_usuario.setText(usuario.toString());


        enviarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tf_asunto.getText().trim().isEmpty() || tf_lugar.getText().trim().isEmpty() || ta_descripcion.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Rellena todos los campos", "Incidencia", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    Incidencia incidencia = new Incidencia(tf_asunto.getText().trim(), tf_lugar.getText().trim(), ta_descripcion.getText().trim(), usuario);
                    //Firmar incidencia
                    byte[] firma = null;
                    Mensaje mensaje = null;
                    try {
                        //Iniciamos firma
                        Signature rsa = Signature.getInstance("SHA1withRSA");
                        rsa.initSign(keyPair.getPrivate());
                        rsa.update(incidencia.getBytes());

                        //FIRMAR
                        firma = rsa.sign();
                        mensaje = new Mensaje(TipoMensaje.INCIDENCIA, incidencia, firma, keyPair.getPublic());
                    } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException ex) {
                        JOptionPane.showMessageDialog(null, "No se pudo firmar el mensaje.", "Incidencia", JOptionPane.INFORMATION_MESSAGE);
                    }
                    if (mensaje != null) {
                        enviarMensaje(mensaje);
                        Mensaje mensajeRecibido = leerMensaje();
                        if (mensajeRecibido != null) {
                            Incidencia incidenciaRecibida = mensajeRecibido.getIncidenciaOriginal();
                            if (incidenciaRecibida != null) {
                                listaIncidencias.add(incidenciaRecibida);
                                //actualizar tabla
                                tablaIncidencias();
                                JOptionPane.showMessageDialog(null, String.format("Incidencia con codigo %d\nClasificación: %s\n%s", incidenciaRecibida.getCodigo(), incidenciaRecibida.getCategoria(), incidenciaRecibida.getEstimacionTiempo()), "Incidencia creada", JOptionPane.INFORMATION_MESSAGE);
                                limpiar();
                            } else if (mensaje.getMensajeError() != null && !mensaje.getMensajeError().isEmpty()) {
                                JOptionPane.showMessageDialog(panelPrincipal, mensaje.getMensajeError(), "Incidencia", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(null, "La incidencia no se pudo enviar correctamente.", "Incidencia", JOptionPane.INFORMATION_MESSAGE);

                            }
                        }else {
                            JOptionPane.showMessageDialog(null, "No se recivio correctamente.", "Incidencia", JOptionPane.INFORMATION_MESSAGE);

                        }
                    }
                }

            }
        });

        limpiarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiar();
            }
        });
    }

    public JPanel getPanelPrincipal() {
        return panelPrincipal;
    }

    private void limpiar() {
        tf_asunto.setText("");
        tf_lugar.setText("");
        ta_descripcion.setText("");
    }
    private void tablaIncidencias() {
        JTable tablaIncidencias = new JTable();
        IncidenciaTableModel incidenciasTableModel = new IncidenciaTableModel(listaIncidencias);
        tablaIncidencias.setModel(incidenciasTableModel);
        jsp_incidencias.setViewportView(tablaIncidencias);
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
