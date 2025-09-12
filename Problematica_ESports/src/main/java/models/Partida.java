/*
* Cada partida se juega entre dos equipos y está asociada a un juego (por
* ejemplo, LoL, CS:GO, etc.).
*  */

package models;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Representa una partida programada entre dos equipos de un mismo torneo y juego.
 *
 * <h2>Responsabilidades</h2>
 * <ul>
 *   <li>Almacenar datos inmutables de la cita (fecha, equipos y juego).</li>
 *   <li>Permitir asignar o cambiar el árbitro responsable.</li>
 *   <li>Exponer getters de solo lectura.</li>
 * </ul>
 *
 * <h2>Invariantes</h2>
 * <ul>
 *   <li>fecha, equipo1, equipo2 y juego no son nulos.</li>
 *   <li>equipo1 y equipo2 son <b>distintos</b>.</li>
 *   <li>El árbitro puede ser null y asignarse después.</li>
 * </ul>
 *
 * <h2>Creación</h2>
 * Usa la fábrica {@link #of(LocalDate, Equipo, Equipo, Juego, Arbitro)} para validar precondiciones.
 */
public class Partida {

    /** Fecha acordada para disputar la partida. */
    private final LocalDate fecha;

    /** Participantes. */
    private final Equipo equipo1;
    private final Equipo equipo2;

    /** Juego al que pertenece la partida (el mismo del torneo que la programa). */
    private final Juego juego;

    /** Árbitro que supervisa la partida (opcional). */
    private Arbitro arbitro;



    public Partida(LocalDate fecha, Equipo equipo1, Equipo equipo2, Juego juego, Arbitro arbitro) {
        this.fecha = fecha;
        this.equipo1 = equipo1;
        this.equipo2 = equipo2;
        this.juego = juego;
        this.arbitro = arbitro;
    }

    /**
     * Fábrica segura para crear partidas.
     *
     * <p><b>Pre:</b> fecha != null, e1 != null, e2 != null, juego != null, e1 != e2</p>
     *
     * @param fecha   fecha de la partida
     * @param e1      primer equipo
     * @param e2      segundo equipo (distinto a e1)
     * @param juego   juego de la partida (el del torneo)
     * @param arbitro árbitro asignado (puede ser null)
     * @return instancia inmutable de Partida
     * @throws NullPointerException     si fecha, e1, e2 o juego son nulos
     * @throws IllegalArgumentException si e1 y e2 son el mismo equipo
     */
    public static Partida of(LocalDate fecha, Equipo e1, Equipo e2, Juego juego, Arbitro arbitro) {
        Objects.requireNonNull(fecha, "fecha");
        Objects.requireNonNull(e1, "equipo1");
        Objects.requireNonNull(e2, "equipo2");
        Objects.requireNonNull(juego, "juego");
        if (e1.equals(e2)) {
            throw new IllegalArgumentException("Una partida requiere dos equipos distintos");
        }
        return new Partida(fecha, e1, e2, juego, arbitro);
    }

    /** @return fecha programada. */
    public LocalDate getFecha() { return fecha; }

    /** @return equipo 1 (no nulo). */
    public Equipo getEquipo1() { return equipo1; }

    /** @return equipo 2 (no nulo y distinto a equipo1). */
    public Equipo getEquipo2() { return equipo2; }

    /** @return juego de la partida. */
    public Juego getJuego() { return juego; }

    /** @return árbitro asignado (puede ser null). */
    public Arbitro getArbitro() { return arbitro; }



    // ------------------------
    // Comportamiento
    // ------------------------

    /**
     * Asigna o cambia el árbitro de la partida.
     * <p><b>Post:</b> si el árbitro no es null, se registra esta partida en su historial.</p>
     *
     * @param arbitro nuevo árbitro (puede ser null para “desasignar” temporalmente)
     */
    public void asignarArbitro(Arbitro arbitro) { // <-- corrige el typo
        this.arbitro = arbitro;
        if (arbitro != null) {
            arbitro.asignarPartida(this);
        }
    }

    @Override
    public String toString() {
        return "Partida{" +
                "fecha=" + fecha +
                ", e1=" + (equipo1 != null ? equipo1.getNombre() : "null") +
                ", e2=" + (equipo2 != null ? equipo2.getNombre() : "null") +
                ", juego=" + (juego != null ? juego.getNombre() : "null") +
                ", arbitro=" + (arbitro != null ? (arbitro.getNombre() + " " + arbitro.getApellido()) : "—") +
                '}';
    }
}
