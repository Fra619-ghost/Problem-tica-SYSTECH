package models;

import java.util.Date;

public class Administrador {

    private String idAdministrador;
    private String nombre;
    private String usuario;
    private String password;

    public Administrador(String idAdministrador, String nombre, String usuario, String password) {
        this.idAdministrador = idAdministrador;
        this.nombre = nombre;
        this.usuario = usuario;
        this.password = password;
    }

    // Crea un evento (asociación, no composición)
    public Evento crearEvento(String id, String nombre, Date fecha, double creditosOtorgados) {
        return new Evento(id, nombre, fecha, creditosOtorgados);
    }

    // Marca asistencia (delegando en Evento)
    public void marcarAsistencia(Evento evento, Estudiante estudiante, boolean asistio) {
        evento.registrarAsistencia(estudiante, asistio);
    }

    // Emite el diploma si el estudiante tiene asistencia válida; retorna el Diploma
    public Diploma emitirDiploma(Estudiante estudiante, Evento evento, int nuevoIdDiploma) {
        if (!evento.validarAsistencia(estudiante)) {
            throw new IllegalStateException("El estudiante no tiene asistencia válida para este evento.");
        }
        Diploma d = new Diploma(nuevoIdDiploma, new Date(), evento, estudiante);
        // Política: sumar créditos al emitir
        estudiante.setCreditosAcumulados(estudiante.getCreditosAcumulados() + evento.getCreditosOtorgados());
        return d;
    }

    // Firma "decano" (simple: bandera)
    public void firmarDiploma(Diploma diploma) {
        diploma.setFirmaDecano(true);
        // Si quieres: guardar fecha/usuario firmante en futuras versiones
    }

    // Getters básicos si los necesitas
    public String getIdAdministrador() { return idAdministrador; }
    public String getNombre() { return nombre; }
    public String getUsuario() { return usuario; }
    public String getPassword() { return password; }
}
