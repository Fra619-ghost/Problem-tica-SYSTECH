//Un equipo está compuesto por varios jugadores. Si el equipo se elimina, los
//jugadores no se eliminan (pueden ser asignados a otro equipo).

//Un equipo puede participar en varios torneos, y un torneo puede tener
//varios equipos.

package models;

import java.util.ArrayList;
import java.util.List;

public class Equipo {

    private String nombre;
    private List<Jugador> jugadores = new ArrayList<>(); //Un equipo está compuesto por varios jugadores. Si el equipo se elimina, los jugadores no se eliminan (pueden ser asignados a otro equipo).
    //Un equipo puede participar en varios torneos, y un torneo puede tener varios equipos.

    public Equipo(String nombre, Jugador[] jugadores) {
        this.nombre = nombre;
    }

    public Equipo(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void AgregarJugador(Jugador jugador) {
        jugadores.add(jugador);
    }

    public void EliminarJugador(Jugador jugador) {
        jugadores.remove(jugador);
    }

    public ArrayList<Jugador> getJugadores() {
        return (ArrayList<Jugador>) jugadores;
    }

    @Override
    public String toString() {
        return nombre + " - Jugadores: " + jugadores.size();
    }
}
