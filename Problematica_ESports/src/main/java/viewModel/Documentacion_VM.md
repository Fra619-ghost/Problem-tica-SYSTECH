
---

# AppVM – Referencia de métodos

> Paquete sugerido: `vm`
> Depende de: `models` (Torneo, Equipo, Jugador, Juego, Categoria, Arbitro) y `utils.ConsoleIO`.

---

## `AppVM(ConsoleIO io)`

**Funcionalidad**
Crea el ViewModel con un canal de I/O para el menú y mensajes en consola.

**Parámetros**

* `io: ConsoleIO` – proveedor de entrada/salida (no nulo).

**Retorno**

* *(Constructor)*

**Excepciones**

* `NullPointerException` si `io` es nulo.

**Ejemplo**

```java
ConsoleIO io = new ConsoleIO();
AppVM vm = new AppVM(io);
```

---

## `Equipo crearEquipo(String nombre)`

**Funcionalidad**
Crea y registra un nuevo equipo en memoria (clave por nombre).

**Parámetros**

* `nombre: String` – nombre único del equipo (no vacío).

**Retorno**

* `Equipo` – instancia creada.

**Excepciones**

* `IllegalArgumentException` si `nombre` está vacío o ya existe.

**Ejemplo**

```java
Equipo fox = vm.crearEquipo("Fox");
```

---

## `Jugador agregarJugadorAEquipo(String equipoNombre, String nombre, String alias, int ranking)`

**Funcionalidad**
Crea un jugador y lo agrega al equipo indicado, conservando la relación bidireccional.

**Parámetros**

* `equipoNombre: String` – nombre del equipo destino.
* `nombre: String` – nombre real del jugador (no vacío).
* `alias: String` – alias público (no vacío).
* `ranking: int` – ranking sugerido (0–4000 típico).

**Retorno**

* `Jugador` – instancia creada y agregada.

**Excepciones**

* `NoSuchElementException` si el equipo no existe.
* `IllegalStateException` si el jugador ya pertenece a otro equipo (regla de dominio).
* `IllegalArgumentException` si `nombre/alias` vacíos.

**Ejemplo**

```java
vm.agregarJugadorAEquipo("Fox", "Ana", "AnaX", 1800);
```

---

## `Categoria crearCategoria(String nombre, String descripcion)`

**Funcionalidad**
Crea una categoría simple (entidad ligera; útil para etiquetar juegos).

**Parámetros**

* `nombre: String` – nombre de la categoría (no vacío).
* `descripcion: String` – texto opcional (puede ser `null` o vacío).

**Retorno**

* `Categoria` – instancia creada.

**Excepciones**

* `IllegalArgumentException` si `nombre` está vacío.

**Ejemplo**

```java
Categoria moba = vm.crearCategoria("MOBA", "Multiplayer Online Battle Arena");
```

---

## `Juego crearJuego(String nombre, Categoria categoria)`

**Funcionalidad**
Registra un juego; si ya existe un juego con el mismo nombre, retorna el existente.

**Parámetros**

* `nombre: String` – nombre del juego (no vacío).
* `categoria: Categoria` – categoría asociada (no nula).

**Retorno**

* `Juego` – instancia creada o existente.

**Excepciones**

* `IllegalArgumentException` si `nombre` está vacío.
* `NullPointerException` si `categoria` es nula.

**Ejemplo**

```java
Juego lol = vm.crearJuego("League of Legends", moba);
```

---

## `Torneo crearTorneo(String nombre, String organizador, LocalDate fechaInicio, String juegoNombre)`

**Funcionalidad**
Crea un torneo **de un único juego** y lo registra en memoria.

**Parámetros**

* `nombre: String` – nombre del torneo (no vacío, único).
* `organizador: String` – entidad responsable (no vacío).
* `fechaInicio: LocalDate` – fecha de inicio (no nula).
* `juegoNombre: String` – nombre de un juego ya registrado.

**Retorno**

* `Torneo` – instancia creada.

**Excepciones**

* `NoSuchElementException` si el juego no existe.
* `IllegalArgumentException` si `nombre/organizador` vacíos o si el torneo ya existe.
* `NullPointerException` si `fechaInicio` es nula.

**Ejemplo**

```java
Torneo cup = vm.crearTorneo("SYSTECH Cup", "FIA", LocalDate.now(), "League of Legends");
```

---

## `boolean inscribirEquipoEnTorneo(String torneoNombre, String equipoNombre)`

**Funcionalidad**
Inscribe un equipo en un torneo (sin duplicados).

**Parámetros**

* `torneoNombre: String` – nombre del torneo.
* `equipoNombre: String` – nombre del equipo.

**Retorno**

* `boolean` – `true` si se inscribió, `false` si ya estaba.

**Excepciones**

* `NoSuchElementException` si torneo o equipo no existen.

**Ejemplo**

```java
boolean ok = vm.inscribirEquipoEnTorneo("SYSTECH Cup", "Fox");
```

---

## `Partida programarPartida(String torneoNombre, LocalDate fecha, String equipo1, String equipo2, Arbitro arbitro)`

**Funcionalidad**
Programa una partida en el torneo indicado.
Reglas:

* Ambos equipos deben estar **inscritos** y ser **distintos**.
* **Árbitro obligatorio** (1:1).
* La partida **toma el juego del torneo** automáticamente.

**Parámetros**

* `torneoNombre: String` – torneo donde se programa.
* `fecha: LocalDate` – fecha de la partida (no nula).
* `equipo1: String` – nombre del primer equipo.
* `equipo2: String` – nombre del segundo equipo (distinto).
* `arbitro: Arbitro` – árbitro asignado (no nulo).

**Retorno**

* `Partida` – instancia creada y agregada al torneo.

**Excepciones**

* `NoSuchElementException` si torneo o equipos no existen.
* `IllegalStateException` si algún equipo no está inscrito.
* `IllegalArgumentException` si los equipos son iguales.
* `NullPointerException` si `fecha` o `arbitro` son nulos.

**Ejemplo**

```java
Arbitro a = vm.crearArbitro("Carla", "Gómez");
Partida p = vm.programarPartida("SYSTECH Cup", LocalDate.parse("2025-10-01"), "Fox", "Raptors", a);
```

---

## `Arbitro crearArbitro(String nombre, String apellido)`

**Funcionalidad**
Crea un árbitro (identidad simple).

**Parámetros**

* `nombre: String` – nombre (no vacío).
* `apellido: String` – apellido (no vacío).

**Retorno**

* `Arbitro` – instancia creada.

**Excepciones**

* `IllegalArgumentException` si nombre o apellido están vacíos.

**Ejemplo**

```java
Arbitro a = vm.crearArbitro("Carlos", "Mena");
```

---

## `void runMenuLoop()`

**Funcionalidad**
Inicia el menú interactivo en consola usando `ConsoleIO`.
Incluye: crear equipo, agregar jugador, crear juego, crear torneo, inscribir equipo, programar partida y ver resumen.

**Parámetros**

* *(ninguno)*

**Retorno**

* `void`

**Excepciones**

* Las mismas que los flujos internos (propagadas si no se capturan).

**Ejemplo**

```java
new AppVM(new ConsoleIO()).runMenuLoop();
```

---

> **Notas generales**
>
> * Normalización de claves por `lowercase+trim` para nombres.
> * Al cambiar a persistencia real, conserva estas firmas y redirige a tus DAOs/repos.
> * Las entidades de `models` ya validan las reglas de dominio (equipo ↔ jugador, torneo → partidas, etc.).
