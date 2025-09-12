package models;

public class Juego {

    private String nombre;
    private Categoria categoria;


    //Si el jugador se elimina, se mantiene
    public Juego(String nombre, Categoria categoria) {
        this.nombre = nombre;
        this.categoria = categoria;
    }

    public String getNombre() { return nombre; }
    public Categoria getCategoria() { return categoria; }


    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    

}
