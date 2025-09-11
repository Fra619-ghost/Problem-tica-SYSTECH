/* Un torneo está compuesto por varias partidas (matches). Si el torneo se
 elimina, sus partidas también.*/

package models;

import java.util.Date;

public class Torneo {

    private String nombre;
    private String Organizador;
    private Date fecha;

    // Un torneo está compuesto por varias partidas. COMPOSICIÓN
    private Partida[] partidas;
    private Juego juego;
    private Equipo[] equipos;
    private Arbitro arbitro;


    public Torneo(String nombre, String organizador, Date fecha, Partida[] partidas) {
        this.nombre = nombre;
        Organizador = organizador;
        this.fecha = fecha;
        this.partidas = partidas;
    }



}
