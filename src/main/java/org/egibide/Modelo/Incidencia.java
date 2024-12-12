package org.egibide.Modelo;

import org.apache.commons.lang3.SerializationUtils;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Random;

public class Incidencia implements Serializable {
    private String asunto;
    private String descripcion;
    private String lugar;
    private Usuario usuario;

    // Para rellenar por el servidor
    private int codigo;
    private Categoria categoria;
    private String estimacionTiempo;


    // region Constructores

    public Incidencia() {
    }

    public Incidencia(String asunto, String descripcion, String lugar, Usuario usuario) {
        this.asunto = asunto;
        this.descripcion = descripcion;
        this.lugar = lugar;
        this.usuario = usuario;
    }
    // endregion


    // region Getters y setters

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String getEstimacionTiempo() {
        return estimacionTiempo;
    }

    public void setEstimacionTiempo(String estimacionTiempo) {
        this.estimacionTiempo = estimacionTiempo;
    }
    // endregion

    public void calcularTiempo() {
        String texto = "";
        int num1 = 0;
        int num2 = 0;
        Random rand = new Random();
        if (categoria != null) {
            switch (categoria) {
                case LEVE -> {
                    num1 = rand.nextInt(4);
                    num2 = num1 + rand.nextInt(1, 9);
                    texto = String.format("Estimación de tiempos: de %d a %d meses", num1, num2);
                }
                case MODERADA -> {
                    num1 = rand.nextInt(4);
                    num2 = num1 + rand.nextInt(1, 10);
                    texto = String.format("Estimación de tiempos: de %d a %d semanas", num1, num2);

                }
                case URGENTE -> {
                    num1 = rand.nextInt(4);
                    num2 = num1 + rand.nextInt(1, 49);
                    texto = String.format("Estimación de tiempos: de %d a %d horas", num1, num2);
                }
                default -> {
                    texto = "No se pudo establecer una estimación de tiempo, se le comunicara en cunto sea posible.";
                }
            }
            this.estimacionTiempo = texto;
        }
    }


    public byte[] getBytes() {
        //String texto = String.format("%s-%s-%s-%s", asunto, lugar, descripcion, usuario.getUsuario());
        return SerializationUtils.serialize(this);
    }

    public static Incidencia toIncidencia(byte[] data) {
        return SerializationUtils.deserialize(data);
    }
}
