package viewModel;

import models.*;
import utils.ConsoleIO;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <h1>AppVM (ViewModel de la aplicación)</h1>
 *
 * <p>
 * Capa de orquestación para un sistema de torneos:
 * </p>
 * <ul>
 *   <li>Un <b>Torneo</b> pertenece a <b>un único Juego</b>.</li>
 *   <li>Un <b>Torneo</b> se compone de <b>múltiples Partidas</b> (el Torneo crea y gestiona sus partidas).</li>
 *   <li>Cada <b>Partida</b> se juega entre <b>dos Equipos distintos inscritos</b> en el Torneo.</li>
 *   <li>La <b>Partida</b> toma el <b>Juego</b> desde el Torneo.</li>
 *   <li>Relación <b>Partida → Árbitro</b> es <b>1:1 obligatorio</b> (árbitro no nulo al crear).</li>
 * </ul>
 *
 * <h2>Responsabilidades</h2>
 * <ul>
 *   <li>Proveer una <b>API programática</b> simple para: crear/consultar entidades y ejecutar casos de uso.</li>
 *   <li>Ofrecer un <b>menú interactivo por consola</b> (opcional) usando {@link utils.ConsoleIO}.</li>
 *   <li>Centralizar validaciones de alto nivel (existencia, unicidad por nombre, prerequisitos de flujo).</li>
 * </ul>
 *
 * <h2>Persistencia</h2>
 * <p>Este VM usa <b>almacenamiento en memoria</b> (Map/List). Para BD, reemplaza estas colecciones
 * por repositorios/DAOs manteniendo las mismas firmas públicas.</p>
 *
 * <h2>Ejemplo mínimo (programático) en {@code main}</h2>
 * <pre>{@code
 * ConsoleIO io = new ConsoleIO();
 * AppVM vm = new AppVM(io);
 *
 * // Cat/Juego/Torneo
 * Categoria moba = new Categoria("MOBA");
 * vm.crearJuego("League of Legends", moba);
 * vm.crearTorneo("SYSTECH Cup", "FIA", LocalDate.now(), "League of Legends");
 *
 * // Equipos y jugadores
 * vm.crearEquipo("Fox");
 * vm.crearEquipo("Raptors");
 * vm.agregarJugadorAEquipo("Fox", "Ana", "AnaX", 1800);
 * vm.agregarJugadorAEquipo("Raptors", "Sofía", "Sofi", 1820);
 *
 * // Inscripción y partida (árbitro obligatorio)
 * vm.inscribirEquipoEnTorneo("SYSTECH Cup", "Fox");
 * vm.inscribirEquipoEnTorneo("SYSTECH Cup", "Raptors");
 * Arbitro arb = vm.crearArbitro("Carlos", "Mena");
 * vm.programarPartida("SYSTECH Cup", LocalDate.now().plusDays(1), "Fox", "Raptors", arb);
 * }</pre>
 *
 * <h2>Ejecutar menú interactivo</h2>
 * <pre>{@code
 * new AppVM(new ConsoleIO()).runMenuLoop();
 * }</pre>
 *
 * <h2>Notas</h2>
 * <ul>
 *   <li><b>Thread-safety:</b> no es seguro para concurrencia; sincroniza externamente si lo usas en multihilo.</li>
 *   <li>Unicidad de nombres: se normaliza por <i>lowercase+trim</i> para claves internas.</li>
 *   <li>Excepciones: {@link IllegalArgumentException} para datos inválidos; {@link NoSuchElementException} para “no existe”.</li>
 * </ul>
 */
public class AppVM {

    // ==========================
    // Estado en memoria (mock repos)
    // ==========================

    /** I/O de consola para interacciones y mensajes. */
    private final ConsoleIO io;

    /** Repos de trabajo en memoria (clave: nombre normalizado). */
    private final Map<String, Equipo> equipos   = new LinkedHashMap<>();
    private final Map<String, Torneo> torneos   = new LinkedHashMap<>();
    private final Map<String, Juego>  juegos    = new LinkedHashMap<>();

    /**
     * Crea el VM con una instancia de {@link ConsoleIO}.
     * @param io proveedor de entrada/salida para mensajes y menús.
     */
    public AppVM(ConsoleIO io) {
        this.io = Objects.requireNonNull(io, "io");
    }

    // ==========================
    // API programática (para main/tests)
    // ==========================

    /**
     * Crea y registra un equipo.
     * @param nombre nombre del equipo (no vacío, único)
     * @return equipo creado
     * @throws IllegalArgumentException si el nombre está vacío o ya existe
     */
    public Equipo crearEquipo(String nombre) {
        assertNonBlank(nombre, "nombre de equipo");
        if (equipos.containsKey(key(nombre))) {
            throw new IllegalArgumentException("Ya existe un equipo con ese nombre: " + nombre);
        }
        Equipo e = new Equipo(nombre);
        equipos.put(key(nombre), e);
        return e;
    }

    /**
     * Agrega un jugador a un equipo existente.
     * @param equipoNombre nombre del equipo
     * @param nombre nombre real del jugador
     * @param alias alias público del jugador
     * @param ranking ranking (0..4000 recomendado)
     * @return jugador creado y agregado
     * @throws NoSuchElementException si el equipo no existe
     * @throws IllegalStateException si el jugador ya pertenece a otro equipo (regla de dominio)
     */
    public Jugador agregarJugadorAEquipo(String equipoNombre, String nombre, String alias, int ranking) {
        Equipo e = getEquipoOrThrow(equipoNombre);
        Jugador j = new Jugador(nombre, alias, ranking);
        e.addJugador(j);
        return j;
    }

    /**
     * Crea una categoría simple (entidad ligera sin catálogo).
     */
    public Categoria crearCategoria(String nombre, String descripcion) {
        return new Categoria(nombre, descripcion);
    }

    /**
     * Registra un juego (si ya existe con el mismo nombre, lo retorna).
     * @param nombre nombre del juego
     * @param categoria categoría (no nula)
     * @return juego existente o nuevo
     * @throws IllegalArgumentException si el nombre está vacío
     */
    public Juego crearJuego(String nombre, Categoria categoria) {
        assertNonBlank(nombre, "nombre de juego");
        Objects.requireNonNull(categoria, "categoria");
        if (juegos.containsKey(key(nombre))) {
            return juegos.get(key(nombre));
        }
        Juego j = new Juego(nombre, categoria);
        juegos.put(key(nombre), j);
        return j;
    }

    /**
     * Crea un torneo <b>de un único juego</b>.
     * @param nombre nombre del torneo (único)
     * @param organizador organizador (no vacío)
     * @param fechaInicio fecha de inicio (no nula)
     * @param juegoNombre nombre del juego ya registrado
     * @return torneo creado
     * @throws NoSuchElementException si el juego no existe
     */
    public Torneo crearTorneo(String nombre, String organizador, LocalDate fechaInicio, String juegoNombre) {
        assertNonBlank(nombre, "nombre de torneo");
        assertNonBlank(organizador, "organizador");
        Objects.requireNonNull(fechaInicio, "fechaInicio");
        Juego juego = getJuegoOrThrow(juegoNombre);
        if (torneos.containsKey(key(nombre))) {
            throw new IllegalArgumentException("Ya existe un torneo con ese nombre: " + nombre);
        }
        Torneo t = new Torneo(nombre, organizador, fechaInicio, juego);
        torneos.put(key(nombre), t);
        return t;
    }

    /**
     * Inscribe un equipo en un torneo.
     * @param torneoNombre nombre del torneo
     * @param equipoNombre nombre del equipo
     * @return {@code true} si se inscribió (no estaba), {@code false} si ya estaba
     * @throws NoSuchElementException si torneo o equipo no existen
     */
    public boolean inscribirEquipoEnTorneo(String torneoNombre, String equipoNombre) {
        Torneo t = getTorneoOrThrow(torneoNombre);
        Equipo e = getEquipoOrThrow(equipoNombre);
        return t.agregarEquipo(e);
    }

    /**
     * Programa una partida en un torneo (ambos equipos deben estar inscritos y ser distintos).
     * El <b>árbitro es obligatorio</b>. El <b>juego</b> se toma del Torneo.
     *
     * <p><b>Ejemplo:</b></p>
     * <pre>{@code
     * Arbitro a = vm.crearArbitro("Carla", "Gómez");
     * vm.programarPartida("SYSTECH Cup", LocalDate.parse("2025-10-01"), "Fox", "Raptors", a);
     * }</pre>
     *
     * @throws NoSuchElementException si torneo/equipos no existen
     * @throws IllegalStateException si algún equipo no está inscrito
     * @throws IllegalArgumentException si los equipos son iguales o la fecha es nula
     */
    public Partida programarPartida(
            String torneoNombre, LocalDate fecha, String equipo1, String equipo2, Arbitro arbitro) {
        Torneo t = getTorneoOrThrow(torneoNombre);
        Objects.requireNonNull(fecha, "fecha");
        Equipo e1 = getEquipoOrThrow(equipo1);
        Equipo e2 = getEquipoOrThrow(equipo2);
        return t.programarPartida(fecha, e1, e2, Objects.requireNonNull(arbitro, "arbitro"));
    }

    /**
     * Crea un árbitro (identidad simple).
     */
    public Arbitro crearArbitro(String nombre, String apellido) {
        return new Arbitro(nombre, apellido);
    }

    // ==========================
    // Menú interactivo (opcional)
    // ==========================

    /**
     * <h3>runMenuLoop</h3>
     * Bucle de menú principal (UI de consola). Útil para demos/clases.
     *
     * <p><b>Uso:</b></p>
     * <pre>{@code
     * public static void main(String[] args) {
     *     new AppVM(new ConsoleIO()).runMenuLoop();
     * }
     * }</pre>
     */
    public void runMenuLoop() {
        while (true) {
            io.clear();
            io.banner("Sistema de Torneos");
            io.println("1) Crear equipo");
            io.println("2) Agregar jugador a equipo");
            io.println("3) Crear juego");
            io.println("4) Crear torneo (de un juego)");
            io.println("5) Inscribir equipo en torneo");
            io.println("6) Programar partida");
            io.println("7) Resumen (listar)");
            io.println("0) Salir");
            io.divider();

            int op = io.promptIntInRange("Seleccione una opción", 0, 7);
            switch (op) {
                case 0: io.success("¡Hasta luego!"); return;
                case 1: flujoCrearEquipo(); break;
                case 2: flujoAgregarJugador(); break;
                case 3: flujoCrearJuego(); break;
                case 4: flujoCrearTorneo(); break;
                case 5: flujoInscribirEquipo(); break;
                case 6: flujoProgramarPartida(); break;
                case 7: flujoResumen(); break;
            }
            io.pause();
        }
    }

    // ------- Flujos UI -------

    private void flujoCrearEquipo() {
        io.banner("Crear equipo");
        String nombre = io.promptNonEmpty("Nombre del equipo");
        try {
            Equipo e = crearEquipo(nombre);
            io.success("Equipo creado: " + e.getNombre());
        } catch (Exception ex) { io.error(ex.getMessage()); }
    }

    private void flujoAgregarJugador() {
        io.banner("Agregar jugador a equipo");
        Equipo equipo = elegirEquipo();
        if (equipo == null) return;
        String nombre = io.promptNonEmpty("Nombre del jugador");
        String alias  = io.promptNonEmpty("Alias");
        int ranking   = io.promptIntInRange("Ranking (0-4000)", 0, 4000);
        try {
            agregarJugadorAEquipo(equipo.getNombre(), nombre, alias, ranking);
            io.success("Jugador agregado a " + equipo.getNombre());
        } catch (Exception ex) { io.error(ex.getMessage()); }
    }

    private void flujoCrearJuego() {
        io.banner("Crear juego");
        String nombre = io.promptNonEmpty("Nombre del juego");
        String cat    = io.promptNonEmpty("Categoría (p.ej., MOBA/FPS/Deportes)");
        String desc   = io.promptWithDefault("Descripción", "");
        try {
            Categoria c = crearCategoria(cat, desc.isBlank() ? null : desc);
            Juego j = crearJuego(nombre, c);
            io.success("Juego registrado: " + j.getNombre() + " [" + j.getCategoria() + "]");
        } catch (Exception ex) { io.error(ex.getMessage()); }
    }

    private void flujoCrearTorneo() {
        io.banner("Crear torneo");
        if (juegos.isEmpty()) {
            io.warn("No hay juegos registrados. Cree un juego primero.");
            return;
        }
        Juego juego = elegirJuego();
        String nombre = io.promptNonEmpty("Nombre del torneo");
        String org    = io.promptNonEmpty("Organizador");
        LocalDate f   = io.promptDate("Fecha de inicio (yyyy-MM-dd)", "yyyy-MM-dd");
        try {
            Torneo t = crearTorneo(nombre, org, f, juego.getNombre());
            io.success("Torneo creado: " + t.getNombre() + " / Juego: " + t.getJuego().getNombre());
        } catch (Exception ex) { io.error(ex.getMessage()); }
    }

    private void flujoInscribirEquipo() {
        io.banner("Inscribir equipo en torneo");
        Torneo t = elegirTorneo();
        if (t == null) return;
        Equipo e = elegirEquipo();
        if (e == null) return;
        boolean ok = inscribirEquipoEnTorneo(t.getNombre(), e.getNombre());
        if (ok) io.success("Inscrito " + e.getNombre() + " en " + t.getNombre());
        else    io.warn("El equipo ya estaba inscrito.");
    }

    private void flujoProgramarPartida() {
        io.banner("Programar partida");
        Torneo t = elegirTorneo();
        if (t == null) return;

        // Equipos inscritos
        List<Equipo> inscritos = new ArrayList<>(t.getEquipos());
        if (inscritos.size() < 2) {
            io.warn("El torneo necesita al menos 2 equipos inscritos.");
            return;
        }
        Equipo e1 = io.chooseFrom("Elige equipo 1", inscritos, Equipo::getNombre);
        List<Equipo> candidatos = inscritos.stream().filter(x -> !x.equals(e1)).collect(Collectors.toList());
        Equipo e2 = io.chooseFrom("Elige equipo 2", candidatos, Equipo::getNombre);

        LocalDate fecha = io.promptDate("Fecha (yyyy-MM-dd)", "yyyy-MM-dd");
        String arbNom   = io.promptNonEmpty("Nombre del árbitro");
        String arbApe   = io.promptNonEmpty("Apellido del árbitro");
        Arbitro arbitro = crearArbitro(arbNom, arbApe);

        try {
            Partida p = programarPartida(t.getNombre(), fecha, e1.getNombre(), e2.getNombre(), arbitro);
            io.success("Partida creada: " + p);
        } catch (Exception ex) { io.error(ex.getMessage()); }
    }

    private void flujoResumen() {
        io.banner("Resumen");
        // Juegos
        io.println(ConsoleIO.ANSI_BOLD + "Juegos" + ConsoleIO.ANSI_RESET);
        io.table(List.of("Juego", "Categoría"),
                juegos.values().stream()
                        .map(j -> List.of(j.getNombre(), j.getCategoria().toString()))
                        .collect(Collectors.toList()));

        io.divider();
        // Equipos
        io.println(ConsoleIO.ANSI_BOLD + "Equipos" + ConsoleIO.ANSI_RESET);
        io.table(List.of("Equipo", "#Jugadores"),
                equipos.values().stream()
                        .map(e -> List.of(e.getNombre(), String.valueOf(e.getJugadores().size())))
                        .collect(Collectors.toList()));

        io.divider();
        // Torneos
        io.println(ConsoleIO.ANSI_BOLD + "Torneos" + ConsoleIO.ANSI_RESET);
        io.table(List.of("Torneo", "Juego", "Equipos", "Partidas"),
                torneos.values().stream()
                        .map(t -> List.of(
                                t.getNombre(),
                                t.getJuego().getNombre(),
                                String.valueOf(t.getEquipos().size()),
                                String.valueOf(t.getPartidas().size())
                        )).collect(Collectors.toList()));
    }

    // ==========================
    // Helpers de elección / obtención
    // ==========================

    /** Obtiene un equipo por nombre o lanza excepción si no existe. */
    private Equipo getEquipoOrThrow(String nombre) {
        Equipo e = equipos.get(key(nombre));
        if (e == null) throw new NoSuchElementException("No existe el equipo: " + nombre);
        return e;
    }

    /** Obtiene un torneo por nombre o lanza excepción si no existe. */
    private Torneo getTorneoOrThrow(String nombre) {
        Torneo t = torneos.get(key(nombre));
        if (t == null) throw new NoSuchElementException("No existe el torneo: " + nombre);
        return t;
    }

    /** Obtiene un juego por nombre o lanza excepción si no existe. */
    private Juego getJuegoOrThrow(String nombre) {
        Juego j = juegos.get(key(nombre));
        if (j == null) throw new NoSuchElementException("No existe el juego: " + nombre);
        return j;
    }

    /** Selector UI: equipo. */
    private Equipo elegirEquipo() {
        if (equipos.isEmpty()) { io.warn("No hay equipos registrados."); return null; }
        return io.chooseFrom("Elige un equipo", new ArrayList<>(equipos.values()), Equipo::getNombre);
    }

    /** Selector UI: torneo. */
    private Torneo elegirTorneo() {
        if (torneos.isEmpty()) { io.warn("No hay torneos registrados."); return null; }
        return io.chooseFrom("Elige un torneo", new ArrayList<>(torneos.values()), Torneo::getNombre);
    }

    /** Selector UI: juego. */
    private Juego elegirJuego() {
        if (juegos.isEmpty()) { io.warn("No hay juegos registrados."); return null; }
        return io.chooseFrom("Elige un juego", new ArrayList<>(juegos.values()), Juego::getNombre);
    }

    // ==========================
    // Utilidades
    // ==========================

    /** Normaliza claves de nombre: lowercase + trim. */
    private static String key(String s) { return s.toLowerCase(Locale.ROOT).trim(); }

    /** Valida texto no vacío. */
    private static void assertNonBlank(String s, String campo) {
        if (s == null || s.isBlank()) throw new IllegalArgumentException("Falta " + campo);
    }
}
