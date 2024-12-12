package org.egibide.Modelo;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.security.PublicKey;

public class Mensaje implements Serializable {
    private TipoMensaje tipomensaje;
    private Usuario usuario;
    private Incidencia incidenciaOriginal;
    private byte[] incidenciaFirmada;
    private PublicKey publicKey;
    private String mensajeError;

    // region Constructores

    public Mensaje() {
    }

    public Mensaje(TipoMensaje tipomensaje) {
        this.tipomensaje = tipomensaje;
    }

    public Mensaje(TipoMensaje tipomensaje, Usuario usuario) {
        this.tipomensaje = tipomensaje;
        this.usuario = usuario;
    }

    public Mensaje(TipoMensaje tipomensaje, Incidencia incidenciaOriginal, byte[] incidenciaFirmada, PublicKey publicKey) {
        this.tipomensaje = tipomensaje;
        this.incidenciaOriginal = incidenciaOriginal;
        this.incidenciaFirmada = incidenciaFirmada;
        this.publicKey = publicKey;
    }

    // endregion

    // region Getters y setters
    public TipoMensaje getTipomensaje() {
        return tipomensaje;
    }

    public void setTipomensaje(TipoMensaje tipomensaje) {
        this.tipomensaje = tipomensaje;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Incidencia getIncidenciaOriginal() {
        return incidenciaOriginal;
    }

    public void setIncidenciaOriginal(Incidencia incidenciaOriginal) {
        this.incidenciaOriginal = incidenciaOriginal;
    }

    public byte[] getIncidenciaFirmada() {
        return incidenciaFirmada;
    }

    public void setIncidenciaFirmada(byte[] incidenciaFirmada) {
        this.incidenciaFirmada = incidenciaFirmada;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public String getMensajeError() {
        return mensajeError;
    }

    public void setMensajeError(String mensajeError) {
        this.mensajeError = mensajeError;
    }

    // endregion


    public byte[] getBytes() {
        return SerializationUtils.serialize(this);
    }

    public static Mensaje toMensaje(byte[] data) {
        return SerializationUtils.deserialize(data);
    }

}
