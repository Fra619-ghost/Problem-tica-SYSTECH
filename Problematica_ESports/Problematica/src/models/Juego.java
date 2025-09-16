package models;

/**
 * Juego (título) sobre el que se organiza un torneo.
 *
 * <h2>Responsabilidades</h2>
 * <ul>
 *   <li>Definir el nombre comercial del juego.</li>
 *   <li>Vincularlo a una {@link Categoria} (ej. MOBA, Shooter, Deportes).</li>
 * </ul>
 *
 * <h2>Invariantes</h2>
 * <ul>
 *   <li>Nombre y categoría no son nulos y no cambian tras la creación.</li>
 * </ul>
 */
public class Juego {

    private String nombre;
    private Categoria categoria;


    //Si el jugador se elimina, se mantiene
    public Juego(String nombre, Categoria categoria) {
        if (nombre == null || nombre.isBlank() || categoria == null)
            throw new IllegalArgumentException("El nombre del juego es obligatorio");
        this.nombre = nombre;
        this.categoria = categoria;
    }

    public String getNombre() { return nombre; }
    public Categoria getCategoria() { return categoria; }


    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    @Override public String toString() {
        return nombre + " [" + categoria + "]";
    }


}
