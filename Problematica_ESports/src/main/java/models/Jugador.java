//Cada jugador tiene un nombre, alias y ranking.

//Un equipo está compuesto por varios jugadores. Si el equipo se elimina, los
//jugadores no se eliminan (pueden ser asignados a otro equipo).

package models;

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

    public Torneo inscribirseEnTorneo(Torneo torneo) {
        if (torneo == null || torneo.getEquipos().contains(this.equipo)) {
            return null;
        } else {
            this.equipo.AgregarJugador(this);
            return torneo;
        }
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
