package org.egibide.Modelo;

public class Incidencia {
    private String asunto;
    private String descripcion;
    private String lugar;
    private Usuario ususario;

    // Para rellenar por el servidor
    private Categoria codigo;
    private String estimacionTiempo;


    // region Constructores

    public Incidencia() {
    }

    public Incidencia(String asunto, String descripcion, String lugar, Usuario ususario) {
        this.asunto = asunto;
        this.descripcion = descripcion;
        this.lugar = lugar;
        this.ususario = ususario;
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

    public Usuario getUsusario() {
        return ususario;
    }

    public void setUsusario(Usuario ususario) {
        this.ususario = ususario;
    }

    public Categoria getCodigo() {
        return codigo;
    }

    public void setCodigo(Categoria codigo) {
        this.codigo = codigo;
    }

    public String getEstimacionTiempo() {
        return estimacionTiempo;
    }

    public void setEstimacionTiempo(String estimacionTiempo) {
        this.estimacionTiempo = estimacionTiempo;
    }
    // endregion





}
