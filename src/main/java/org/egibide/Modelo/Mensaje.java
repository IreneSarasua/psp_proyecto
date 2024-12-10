package org.egibide.Modelo;

import java.io.Serializable;

public class Mensaje implements Serializable {
    private TipoMensaje tipomensaje;
    private Usuario usuario;
    private Incidencia incidencia;
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

    public Mensaje(TipoMensaje tipomensaje, Incidencia incidencia) {
        this.tipomensaje = tipomensaje;
        this.incidencia = incidencia;
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

    public Incidencia getIncidencia() {
        return incidencia;
    }

    public void setIncidencia(Incidencia incidencia) {
        this.incidencia = incidencia;
    }

    public String getMensajeError() {
        return mensajeError;
    }

    public void setMensajeError(String mensajeError) {
        this.mensajeError = mensajeError;
    }

    // endregion


}
