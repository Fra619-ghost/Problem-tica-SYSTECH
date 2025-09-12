/* Un torneo está compuesto por varias partidas (matches). Si el torneo se
 elimina, sus partidas también.*/

package models;

import java.util.Date;

import java.util.*;

public class Torneo {

    private String nombre;
    private String organizador;
    private Date fechaInicio;

    // Este es un array de objetos Partida porque un torneo puede tener varias partidas.
    private List<Partida> partidas = new ArrayList<>();
    private Set<Equipo> equipos = new HashSet<>(); //HAY QUE EVITAR DUPLICADOS jiji

    public Torneo(String nombre, String organizador, Date fechaInicio) {
        this.nombre = nombre;
        this.organizador = organizador;
        this.fechaInicio = fechaInicio;
    }

    public boolean agregarEquipo(Equipo equipo) {
        if (equipo == null) {
            return false;
        }
        return equipos.add(equipo); // add() ya de por si devuelve false si el equipo ya existe
    }

    public boolean retirarEquipo(Equipo equipo) {
        if (equipo == null) return false;
        boolean removed = equipos.remove(equipo);
        if (removed) equipo.(this); // sincroniza reverso
        return removed;
    }


    public List<Equipo> getEquipos() {
        return equipos;
    }

    public void setEquipos(List<Equipo> equipos) {
        this.equipos = equipos;
    }

    public List<Partida> getPartidas() {
        return partidas;
    }

    public void setPartidas(List<Partida> partidas) {
        this.partidas = partidas;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getOrganizador() {
        return organizador;
    }

    public void setOrganizador(String organizador) {
        this.organizador = organizador;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }



}
