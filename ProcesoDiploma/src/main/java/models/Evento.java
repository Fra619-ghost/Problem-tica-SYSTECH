package models;

import java.util.*;
import java.util.stream.Collectors;

public class Evento {

    private String idEvento;
    private String nombreEvento;
    private Date fechaEvento;
    private double creditosOtorgados;
    private Map<String, Boolean> asistencia = new HashMap<>();



    public Evento(String idEvento, String nombreEvento, Date fechaEvento, double creditosOtorgados) {
        this.idEvento = idEvento;
        this.nombreEvento = nombreEvento;
        this.fechaEvento = fechaEvento;
        this.creditosOtorgados = creditosOtorgados;
    }



    public boolean validarAsistencia(Estudiante est) {
        return asistencia.getOrDefault(est.getCif(), false);
    }

    public void registrarAsistencia(Estudiante est, boolean asistio) {
        asistencia.put(est.getCif(), asistio);
        // Si quieres: si true, sumar créditos en el estudiante aquí (o al emitir diploma)
    }

    // CIfs de asistentes (true)
    public Set<String> obtenerCifsAsistentes() {
        return asistencia.entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    // Getters
    public String getIdEvento() { return idEvento; }
    public String getNombreEvento() { return nombreEvento; }
    public Date getFechaEvento() { return fechaEvento; }
    public double getCreditosOtorgados() { return creditosOtorgados; }





}
