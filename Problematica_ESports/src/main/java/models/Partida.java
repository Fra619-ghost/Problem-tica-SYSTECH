/*
* Cada partida se juega entre dos equipos y está asociada a un juego (por
* ejemplo, LoL, CS:GO, etc.).
*  */

package models;
import java.time.LocalDate;

public class Partida {

    private LocalDate fecha;
    private Equipo equipo1;  //La clase Partida declara atributos q son pertenecientes a la clase Equipo.
    private Equipo equipo2; // ''
    private Juego juego;
    private Arbitro arbitro;



    public Partida(LocalDate fecha, Equipo equipo1, Equipo equipo2, Juego juego, Arbitro arbitro) {
        this.fecha = fecha;
        this.equipo1 = equipo1;
        this.equipo2 = equipo2;
        this.juego = juego;
        this.arbitro = arbitro;
    }

    public LocalDate getFecha() {
        return fecha;
    }
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
    public Equipo getEquipo1() {
        return equipo1;
    }
    public void setEquipo1(Equipo equipo1) {
        this.equipo1 = equipo1;
    }
    public Equipo getEquipo2() {
        return equipo2;
    }


    public void setEquipo2(Equipo equipo2) {
        this.equipo2 = equipo2;
    }
    public Juego getJuego() {
        return juego;
    }
    public void setJuego(Juego juego) {
        this.juego = juego;
    }
    public Arbitro getArbitro() {
        return arbitro;
    }


    public void asignarArbirtro(Arbitro arbitro) {
        this.arbitro = arbitro;

    }
}
