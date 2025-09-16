package models;

import java.util.ArrayList;
import java.util.List;

/**
 * Categoría o género de un juego (p.ej., MOBA, FPS, Deportes).
 *
 * <h2>Notas</h2>
 * <ul>
 *   <li>Se modela como entidad simple; si más adelante requieres un catálogo fijo,
 *       puedes convertirlo en enum o mantener una tabla en BD.</li>
 * </ul>
 */
public class Categoria {
    private String nombre;
    private String descripcion; // opcional

    public Categoria(String nombre) {
        this.nombre = nombre;
    }

    public Categoria(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }



    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return nombre + (descripcion != null ? " (" + descripcion + ")" : "");
    }
}