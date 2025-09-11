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

    // Si quieres atajo desde estudiante (delegación)
    public void registrarEvento(Evento evento, boolean asistio) {
        evento.registrarAsistencia(this, asistio);
    }

    // Getters/Setters
    public String getCif() { return cif; }
    public String getNombre() { return nombre; }
    public String getPassword() { return password; }
    public double getCreditosAcumulados() { return creditosAcumulados; }
    public void setCreditosAcumulados(double creditosAcumulados) { this.creditosAcumulados = creditosAcumulados; }
}
