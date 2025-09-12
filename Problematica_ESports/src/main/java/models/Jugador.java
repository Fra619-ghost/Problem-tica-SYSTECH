//Cada jugador tiene un nombre, alias y ranking.

//Un equipo está compuesto por varios jugadores. Si el equipo se elimina, los
//jugadores no se eliminan (pueden ser asignados a otro equipo).



package models;
/**
 * Jugador integrante de un equipo.
 *
 * <h2>Responsabilidades</h2>
 * <ul>
 *   <li>Almacenar identidad del jugador (nombre legal y alias).</li>
 *   <li>Conservar su ranking (métrica editable).</li>
 *   <li>Mantener una referencia al equipo al que pertenece.</li>
 * </ul>
 *
 * <h2>Diseño</h2>
 * <ul>
 *   <li>El setter de equipo es <i>package-private</i> para que solo lo gestione {@link Equipo}.</li>
 *   <li>Alias y nombre son inmutables tras la creación.</li>
 * </ul>
 */
public class Jugador {
    private String nombre;
    private String alias;
    private int ranking;
    private Equipo equipo;

    public Jugador(String nombre, String alias, int ranking) {
        this.nombre = nombre;
        this.alias = alias;
        this.ranking = ranking;
    }

    /// Getters y setters
    public String getNombre() { return nombre; }
    public String getAlias() { return alias; }
    public int getRanking() { return ranking; }
    public void setRanking(int ranking) { this.ranking = ranking; }
    public Equipo getEquipo() { return equipo; }
    /** Solo debe llamarse desde {@link Equipo} para mantener la coherencia del modelo. */
    void setEquipo(Equipo equipo) { this.equipo = equipo; }


    /** @return alias + " (" + nombre + ")" */
    @Override
    public String toString() {
        return alias + " (" + nombre + (equipo != null ? ", " + equipo.getNombre() : "") + ")";
    }
}
