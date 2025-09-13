package models;

import java.time.LocalDate;
import java.util.*;

/**
 * Representa un torneo oficial de un <b>único</b> juego.
 *
 * <h2>Responsabilidades</h2>
 * <ul>
 *   <li>Mantener la identidad y metadatos del torneo (nombre, organizador, fecha de inicio y juego).</li>
 *   <li>Gestionar la inscripción de equipos (alta/baja y consulta de inscritos).</li>
 *   <li>Programar partidas entre equipos <i>inscritos</i> del mismo torneo.</li>
 *   <li>Exponer vistas de solo lectura de equipos y partidas para proteger invariantes.</li>
 * </ul>
 *
 * <h2>Invariantes de dominio</h2>
 * <ul>
 *   <li>El torneo pertenece a un <b>único</b> {@link Juego} (no puede cambiarse tras su creación).</li>
 *   <li>Los equipos inscritos no se repiten (estructura {@link Set} + {@link #equals(Object)} de Equipo).</li>
 *   <li>Solo se pueden programar partidas entre dos <b>equipos distintos</b> que ya estén inscritos.</li>
 * </ul>
 *
 * <h2>Notas de diseño</h2>
 * <ul>
 *   <li>Las colecciones expuestas son inmutables (wrappers unmodifiable) para evitar modificaciones externas.</li>
 *   <li>No es thread-safe por diseño (pensado para uso en capa de aplicación/servicio). Sincronizar externamente si procede.</li>
 *   <li>Complejidad: inscripción/consulta media O(1) sobre {@link HashSet}; almacenamiento de partidas en {@link ArrayList} (append O(1) amortizado).</li>
 * </ul>
 *
 * <h2>Ejemplo de uso</h2>
 * <pre>{@code
 * Categoria cat = new Categoria("MOBA");
 * Juego lol = new Juego("League of Legends", cat);
 *
 * Torneo torneo = new Torneo("SYSTECH Cup", "FIA", LocalDate.now(), lol);
 * Equipo fox = new Equipo("Fox");   // Equipo y jugadores gestionados en EquipoService
 * Equipo raptors = new Equipo("Raptors");
 *
 * torneo.agregarEquipo(fox);
 * torneo.agregarEquipo(raptors);
 *
 * Arbitro arbitro = new Arbitro("Carlos", "Mena");
 * Partida p = torneo.programarPartida(LocalDate.now().plusDays(1), fox, raptors, arbitro);
 * }</pre>
 */
public class Torneo {

    /** Nombre comercial del torneo (identidad humana). */
    private final String nombre;

    /** Entidad/organización responsable del evento. */
    private final String organizador;

    /** Fecha de inicio oficial del torneo (no implica primera partida). */
    private final LocalDate fechaInicio;

    /** Juego único al que pertenece el torneo (invariante). */
    private final Juego juego;

    /** Conjunto de equipos inscritos (sin duplicados). */
    private final Set<Equipo> equipos = new HashSet<>();

    /** Calendario/agenda de partidas del torneo. */
    private final List<Partida> partidas = new ArrayList<>();

    /**
     * Crea un torneo asociado a un único juego.
     *
     * @param nombre       nombre del torneo (no nulo ni en blanco)
     * @param organizador  organizador (no nulo ni en blanco)
     * @param fechaInicio  fecha de inicio (no nula)
     * @param juego        juego único del torneo (no nulo)
     * @throws IllegalArgumentException si algún texto está en blanco
     * @throws NullPointerException     si algún argumento requerido es nulo
     */
    public Torneo(String nombre, String organizador, LocalDate fechaInicio, Juego juego) {
        this.nombre = requireNonBlank(nombre, "nombre");
        this.organizador = requireNonBlank(organizador, "organizador");
        this.fechaInicio = Objects.requireNonNull(fechaInicio, "fechaInicio");
        this.juego = Objects.requireNonNull(juego, "juego");
    }

    // ------------------------
    // Getters (solo lectura)
    // ------------------------

    /** @return nombre del torneo. */
    public String getNombre() { return nombre; }

    /** @return nombre del organizador. */
    public String getOrganizador() { return organizador; }

    /** @return fecha de inicio del torneo. */
    public LocalDate getFechaInicio() { return fechaInicio; }

    /** @return juego único al que pertenece el torneo. */
    public Juego getJuego() { return juego; }

    /**
     * Vista inmutable de los equipos inscritos.
     * @return set no modificable de equipos
     */
    public Set<Equipo> getEquipos() {
        return Collections.unmodifiableSet(equipos);
    }

    /**
     * Vista inmutable de las partidas programadas.
     * @return lista no modificable de partidas
     */
    public List<Partida> getPartidas() {
        return Collections.unmodifiableList(partidas);
    }

    // ------------------------
    // Inscripción de equipos
    // ------------------------

    /**
     * Inscribe un equipo en el torneo.
     *
     * <p><b>Precondiciones:</b> equipo != null</p>
     * <p><b>Postcondiciones:</b> si el equipo no estaba inscrito, se añade al conjunto y se devuelve true.</p>
     *
     * @param equipo equipo a inscribir
     * @return true si se inscribió (no estaba), false si ya existía
     * @throws NullPointerException si equipo es nulo
     */
    public boolean agregarEquipo(Equipo equipo) {
        Objects.requireNonNull(equipo, "equipo");
        return equipos.add(equipo);
    }

    /**
     * Retira (desinscribe) un equipo del torneo.
     *
     * @param equipo equipo a retirar (ignora nulos)
     * @return true si estaba inscrito y se eliminó; false en caso contrario
     */
    public boolean retirarEquipo(Equipo equipo) {
        if (equipo == null) return false;
        return equipos.remove(eequipoSafeKey(equipo));
    }

    /**
     * Verifica si un equipo está inscrito.
     *
     * @param equipo equipo a consultar
     * @return true si está en el conjunto de inscritos; false en caso contrario o si equipo es null
     */
    public boolean estaInscrito(Equipo equipo) {
        if (equipo == null) return false;
        return equipos.contains(eequipoSafeKey(equipo));
    }

    // ------------------------
    // Gestión de partidas
    // ------------------------

    /**
     * Programa una partida entre dos equipos inscritos del torneo, para el juego del torneo.
     *
     * <p><b>Precondiciones:</b></p>
     * <ul>
     *   <li>{@code fecha != null}</li>
     *   <li>{@code e1 != null && e2 != null}</li>
     *   <li>{@code !e1.equals(e2)} (equipos distintos)</li>
     *   <li>Ambos equipos deben estar previamente inscritos en el torneo</li>
     * </ul>
     *
     * <p><b>Postcondiciones:</b> se crea y almacena una {@link Partida} en la lista interna y se retorna.</p>
     *
     * @param fecha   fecha de la partida (no nula)
     * @param e1      primer equipo
     * @param e2      segundo equipo (distinto a e1)
     * @param arbitro árbitro asignado (puede ser null si aún no se define)
     * @return la partida creada y agregada a la agenda del torneo
     * @throws NullPointerException     si fecha o algún equipo es nulo
     * @throws IllegalArgumentException si los equipos son iguales
     * @throws IllegalStateException    si algún equipo no está inscrito
     */
    public Partida programarPartida(LocalDate fecha, Equipo e1, Equipo e2, Arbitro arbitro) {
        Objects.requireNonNull(arbitro, "arbitro"); // 1:1 obligatorio
        if (e1.equals(e2)) throw new IllegalArgumentException("Una partida requiere equipos distintos");
        if (!estaInscrito(e1) || !estaInscrito(e2))
            throw new IllegalStateException("Ambos equipos deben estar inscritos en el torneo");

        Partida p = Partida.of(this, fecha, e1, e2, arbitro);
        partidas.add(p);
        return p;
    }



    /**
     * Cancela una partida previamente programada.
     *
     * @param partida partida a eliminar (ignora null)
     * @return true si existía y fue eliminada; false en caso contrario
     */
    public boolean cancelarPartida(Partida partida) {
        if (partida == null) return false;
        return partidas.remove(partida);
    }

    // ------------------------
    // Utilidades privadas
    // ------------------------/
    /**
     * Valida que un texto no sea nulo ni en blanco.
     */
    private static String requireNonBlank(String value, String field) {
        Objects.requireNonNull(value, field);
        if (value.isBlank())
            throw new IllegalArgumentException("El campo '" + field + "' no puede estar en blanco");
        return value;
    }

    /**
     * Normaliza la clave de un equipo para búsquedas en el Set.
     * <p>Si Equipo implementa equals/hashCode por nombre case-insensitive,
     * esta utilidad puede no ser necesaria; se deja por claridad y como punto para
     * customizar políticas de identidad (por ID, por ejemplo).</p>
     */
    private static Equipo eequipoSafeKey(Equipo equipo) {
        return equipo;
    }

    // ------------------------
    // Representación
    // ------------------------

    @Override
    public String toString() {
        return "Torneo{" +
                "nombre='" + nombre + '\'' +
                ", organizador='" + organizador + '\'' +
                ", fechaInicio=" + fechaInicio +
                ", juego=" + (juego != null ? juego.getNombre() : "null") +
                ", equipos=" + equipos.size() +
                ", partidas=" + partidas.size() +
                '}';
    }
}
