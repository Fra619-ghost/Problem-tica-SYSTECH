package models;

import java.util.Date;

public class Diploma {

    private int idDiploma;
    private Date fechaEmision;
    private boolean firmaDecano;

    // Asociaciones (no package-private)
    private Evento evento;
    private Estudiante estudiante;

    public Diploma(int idDiploma, Date fechaEmision, Evento evento, Estudiante estudiante) {
        this.idDiploma = idDiploma;
        this.fechaEmision = fechaEmision;
        this.evento = evento;
        this.estudiante = estudiante;
        this.firmaDecano = false;
    }

    public int getIdDiploma() { return idDiploma; }
    public Date getFechaEmision() { return fechaEmision; }
    public boolean isFirmaDecano() { return firmaDecano; }
    public void setFirmaDecano(boolean firmaDecano) { this.firmaDecano = firmaDecano; }
    public Evento getEvento() { return evento; }
    public Estudiante getEstudiante() { return estudiante; }

    @Override
    public String toString() {
        return "Diploma{id=" + idDiploma +
                ", fechaEmision=" + fechaEmision +
                ", firmaDecano=" + firmaDecano +
                ", evento=" + (evento != null ? evento.getNombreEvento() : "-") +
                ", estudiante=" + (estudiante != null ? estudiante.getNombre() : "-") +
                '}';
    }
}
