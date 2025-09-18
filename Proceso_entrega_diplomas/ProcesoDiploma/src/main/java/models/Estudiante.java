package models;

public class Estudiante {


    private String cif;
    private String nombre;
    private String password;
    private double creditosAcumulados; // ← corrige: antes era String

    public Estudiante(String cif, String nombre, String password) {

        this.cif = cif;
        this.nombre = nombre;
        this.password = password;
        this.creditosAcumulados = 0.0;
    }

    // Acción de interfaz: consultar (no muta estado)
    public void consultarDiploma(Diploma diploma) {
        if (diploma == null) {
            System.out.println("No hay diploma disponible.");
            return;
        }
        System.out.println(diploma);
    }



    /**  estudiante marca su asistencia en un evento. */
    public void marcarAsistencia(Evento evento) {
        if (evento == null) throw new IllegalArgumentException("evento null");
        if (!evento.consultarAsistencia(this)) {       // requiere el método en Evento (ver paso 2)
            evento.registrarAsistencia(this, true);    // marca true solo si no estaba
        }
    }

    // Getters/Setters
    public String getCif() { return cif; }
    public String getNombre() { return nombre; }
    public String getPassword() { return password; }
    public double getCreditosAcumulados() { return creditosAcumulados; }
    public void setCreditosAcumulados(double creditosAcumulados) { this.creditosAcumulados = creditosAcumulados; }
}
