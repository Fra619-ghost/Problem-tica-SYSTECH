//Un equipo está compuesto por varios jugadores. Si el equipo se elimina, los
//jugadores no se eliminan (pueden ser asignados a otro equipo).

//Un equipo puede participar en varios torneos, y un torneo puede tener
//varios equipos.

package models;


public class Equipo {

    String nombre;
    Jugador[] jugadores; //Un equipo está compuesto por varios jugadores. Si el equipo se elimina, los jugadores no se eliminan (pueden ser asignados a otro equipo).
    Torneo[] torneos; //Un equipo puede participar en varios torneos, y un torneo puede tener varios equipos.

    public Equipo(String nombre, Jugador[] jugadores) {
        this.nombre = nombre;
        this.jugadores = jugadores;
    }

    
}
