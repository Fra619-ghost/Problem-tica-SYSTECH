//Un equipo está compuesto por varios jugadores. Si el equipo se elimina, los
//jugadores no se eliminan (pueden ser asignados a otro equipo).

//Un equipo puede participar en varios torneos, y un torneo puede tener
//varios equipos.

package models;

import java.util.*;

/**
 * Representa un equipo participante.
 *
 * <h2>Responsabilidades</h2>
 * <ul>
 *   <li>Gestionar la plantilla de jugadores (alta, baja, consulta).</li>
 *   <li>Mantener la relación bidireccional Jugador ↔ Equipo.</li>
 *   <li>Definir identidad por nombre (case-insensitive) para evitar duplicados en sets.</li>
 * </ul>
 *
 * <h2>Reglas</h2>
 * <ul>
 *   <li>No se permiten jugadores nulos.</li>
 *   <li>Un jugador no puede pertenecer a dos equipos a la vez.</li>
 * </ul>
 */
public class Equipo {
    /** Nombre público del equipo (identidad lógica). */
    private final String nombre;

    /** Roster interno; el orden no tiene semántica especial. */
    private final List<Jugador> jugadores = new ArrayList<>();

    public Equipo(String nombre) {
        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("El nombre del equipo es obligatorio");
        this.nombre = nombre.trim();
    }

    /** @return nombre del equipo. */
    public String getNombre() { return nombre; }

    /**
     * Agrega un jugador al equipo.
     * <b>Pre:</b> j != null y (j.getEquipo() == null || j.getEquipo() == this)<br>
     * <b>Post:</b> el jugador pertenece a este equipo y figura en la lista.
     */
    public void addJugador(Jugador j) {
        if (j == null) throw new IllegalArgumentException("Jugador null");
        if (j.getEquipo() != null && j.getEquipo() != this)
            throw new IllegalStateException("El jugador ya pertenece a otro equipo");
        if (!jugadores.contains(j)) {
            jugadores.add(j);
            j.setEquipo(this); // setter package-private en Jugador
        }
    }

    /**
     * Elimina un jugador del equipo (si está).
     * <b>Post:</b> el jugador queda sin equipo (equipo == null).
     */
    public void removeJugador(Jugador j) {
        if (jugadores.remove(j)) {
            j.setEquipo(null);
        }
    }

    /** @return vista inmutable de la plantilla. */
    public List<Jugador> getJugadores() {
        return Collections.unmodifiableList(jugadores);
    }

    // Identidad por nombre para usar en Set<Equipo>
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Equipo)) return false;
        Equipo e = (Equipo) o;
        return nombre.equalsIgnoreCase(e.nombre);
    }
    @Override public int hashCode() { return nombre.toLowerCase().hashCode(); }

    @Override public String toString() {
        return "Equipo{" + nombre + ", jugadores=" + jugadores.size() + '}';
    }
}
