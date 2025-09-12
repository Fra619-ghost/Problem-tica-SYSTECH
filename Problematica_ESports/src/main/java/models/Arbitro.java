/*
* Un árbitro supervisa cada partida. Un árbitro puede supervisar muchas
* partidas, pero una partida tiene un solo árbitro.
*
* */

package models;
import java.util.ArrayList;
import java.util.*;

/**
 * Representa un árbitro que puede supervisar múltiples partidas.
 *
 * <h2>Responsabilidades</h2>
 * <ul>
 *   <li>Almacenar datos de identidad (nombre, apellido).</li>
 *   <li>Llevar el historial de partidas supervisadas.</li>
 * </ul>
 *
 * <h2>Notas</h2>
 * <ul>
 *   <li>El método {@link #asignarPartida(Partida)} es <i>package-private</i> y lo invoca {@link Partida} al asignarse.</li>
 * </ul>
 */
public class Arbitro {

    private String nombre;
    private String apellido;
    private List<Partida> partidasArbitradas = new ArrayList<>();

    public Arbitro(String nombre, String apellido) {
        //Verificaar que no sea null
        if (nombre == null || apellido == null) {throw  new IllegalArgumentException("Los nombres y apellidos no pueden ser nulos");}
        this.nombre = nombre;
        this.apellido = apellido;
    }

    /** @return vista inmutable del historial de partidas supervisadas. */
    public List<Partida> getPartidasSupervisadas() {
        return Collections.unmodifiableList(partidasArbitradas);
    }



    public String getNombre() {
        return nombre;
    }

    public String getApellido(){
        return apellido;
    }


}
