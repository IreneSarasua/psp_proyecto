package org.egibide.GUI;

import org.egibide.Cliente;
import org.egibide.Modelo.Incidencia;
import org.egibide.Modelo.Mensaje;
import org.egibide.Modelo.TipoMensaje;
import org.egibide.Modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

    ObjectOutputStream salida;
    ObjectInputStream lectura;

    List<Incidencia> listaIncidencias= new ArrayList<>();

    public V_incidencia(Usuario usuario) {
        super((Frame) null, true);
        setContentPane(panelPrincipal);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(500, 160);

        tf_usuario.setText(usuario.toString());

        try {
            salida = new ObjectOutputStream(Cliente.cliente.getOutputStream());
            lectura = new ObjectInputStream(Cliente.cliente.getInputStream()); // recibimos infprmación
        } catch (IOException e) {
            System.out.println("Error al leer o esctibir: " + e.getMessage());
        }


        enviarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tf_asunto.getText().trim().isEmpty() || tf_lugar.getText().trim().isEmpty() || ta_descripcion.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Rellena todos los campos", "Incidencia", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    Incidencia incidencia = new Incidencia(tf_asunto.getText().trim(), tf_lugar.getText().trim(), ta_descripcion.getText().trim(), usuario);
                    Mensaje mensaje = new Mensaje(TipoMensaje.INCIDENCIA, incidencia);
                    // encriptar y escribir
                    try {
                        salida.writeObject(mensaje);

                        Mensaje mensajeRecibido = (Mensaje) lectura.readObject();
                        Incidencia incidenciaRecibida = mensajeRecibido.getIncidencia();
                        if (incidenciaRecibida != null) {
                            listaIncidencias.add(incidenciaRecibida);
                            //actualizar tabla
                            JOptionPane.showMessageDialog(null, String.format("Incidencia con codigo %d\nClasificación: %s\n%s", incidenciaRecibida.getCodigo(), incidenciaRecibida.getCategoria(), incidenciaRecibida.getEstimacionTiempo()), "Incidencia creada", JOptionPane.INFORMATION_MESSAGE);

                        } else {
                            JOptionPane.showMessageDialog(null, "La incidencia no se pudo envar correctamente.", "Incidencia", JOptionPane.INFORMATION_MESSAGE);

                        }

                    } catch (IOException ex) {
                        System.out.println("Error al escribir: " + ex.getMessage());
                    } catch (ClassNotFoundException ex) {
                        throw new RuntimeException(ex);
                    }
                }

            }
        });

        limpiarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tf_asunto.setText("");
                tf_lugar.setText("");
                ta_descripcion.setText("");
            }
        });
        verIncidenciasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    public JPanel getPanelPrincipal() {
        return panelPrincipal;
    }
}
