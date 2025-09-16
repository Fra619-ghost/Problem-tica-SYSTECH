package run;

import models.*;
import services.AuthService;
import utils.ConsoleIO;

import java.time.LocalDate;
import java.util.*;

public class Main {

    // “Persistencia” en memoria
    private static final Map<String, Administrador> ADMINS_BY_USER = new LinkedHashMap<>();
    private static final Map<String, Estudiante> EST_BY_CIF = new LinkedHashMap<>();
    private static final List<Evento> EVENTOS = new ArrayList<>();
    private static final List<Diploma> DIPLOMAS = new ArrayList<>();
    private static final AuthService AUTH = new AuthService(3);

    public static void main(String[] args) {
        ejemplo(); // opcional: crea un admin/estudiante de prueba
        menu();
    }

    private static void ejemplo() {
        // ESTO ES DE EJEMPLO, aun asi se puede crear
        Administrador a = new Administrador("admin001","Admin Demo","admin001","1234");
        ADMINS_BY_USER.put(a.getUsuario(), a);
        // Estudiante de ejemplo (sin id/usuario)
        EST_BY_CIF.put("CIF001", new Estudiante("CIF001","Est Demo","abcd"));
    }

    private static void menu(){
        while (true) {
            System.out.println("\n=== SYSTECH – Diplomas (Consola) ===");
            System.out.println("1) Registrar Administrador");
            System.out.println("2) Registrar Estudiante");
            System.out.println("3) Login Administrador (usuario + contraseña)");
            System.out.println("4) Login Estudiante (CIF + contraseña)");
            System.out.println("0) Salir");
            int op = ConsoleIO.readIntInRange("Opción: ", 0, 4);
            try {
                switch (op) {
                    case 1 -> AUTH.registrarAdmin(ADMINS_BY_USER);
                    case 2 -> AUTH.registrarEstudiante(EST_BY_CIF);
                    case 3 -> menuAdmin(AUTH.loginAdmin(ADMINS_BY_USER));
                    case 4 -> menuEstudiante(AUTH.loginEstudiante(EST_BY_CIF));
                    case 0 -> { System.out.println("Bye!"); return; }
                }
            } catch (Exception ex) {
                System.out.println("❌Error: " + ex.getMessage());
            }
        }
    }

    private static void menuAdmin(Administrador admin) {
        while (true) {
            System.out.println("\n— Admin: " + admin.getNombre());
            System.out.println("1) Crear evento");
            System.out.println("2) Emitir diploma");
            System.out.println("3) Firmar diploma");
            System.out.println("4) Listar diplomas");
            System.out.println("0) Volver");
            int op = ConsoleIO.readIntInRange("Opción: ", 0, 4);
            if (op == 0) return;

            try {
                switch (op) {
                    case 1 -> crearEvento(admin);
                    case 2 -> emitirDiploma(admin);
                    case 3 -> firmarDiploma(admin);
                    case 4 -> DIPLOMAS.forEach(System.out::println);
                }
            } catch (Exception ex) {
                System.out.println("❌  " + ex.getMessage());
            }
            ConsoleIO.pressEnterToContinue();
        }
    }

    private static void crearEvento(Administrador admin) {
        String id = ConsoleIO.readMatching("ID evento: ","[A-Za-z0-9_-]{2,}","ID inválido.");
        String nombre = ConsoleIO.readNonEmpty("Nombre del evento: ");
        var fecha = ConsoleIO.readIsoDate("Fecha");
        double creditos = ConsoleIO.readDouble("Créditos otorgados: ");
        Evento ev = admin.crearEvento(id, nombre, java.sql.Date.valueOf(fecha), creditos);
        EVENTOS.add(ev);
        System.out.println("✔️  Evento creado.");
    }

    private static void emitirDiploma(Administrador admin) {
        Evento ev = pickEvento();
        Estudiante est = pickEstudianteByCif();
        int nuevoId = DIPLOMAS.size() + 1;
        Diploma d = admin.emitirDiploma(est, ev, nuevoId); // valida asistencia
        DIPLOMAS.add(d);
        System.out.println("✔️  Diploma emitido: " + d);
    }

    private static void firmarDiploma(Administrador admin) {
        if (DIPLOMAS.isEmpty()) throw new IllegalStateException("No hay diplomas.");
        for (int i = 0; i < DIPLOMAS.size(); i++) {
            Diploma d = DIPLOMAS.get(i);
            System.out.println((i+1)+") "+d);
        }
        int idx = ConsoleIO.readIntInRange("Seleccione diploma: ", 1, DIPLOMAS.size()) - 1;
        admin.firmarDiploma(DIPLOMAS.get(idx));
        System.out.println("✔️  Diploma firmado.");
    }


    private static void menuEstudiante(Estudiante estudiante) {
        while (true) {
            System.out.println("\n— Estudiante: " + estudiante.getNombre() + " (" + estudiante.getCif() + ")");
            System.out.println("1) Marcar asistencia a un evento");
            System.out.println("2) Consultar mi asistencia en un evento");   // ← NUEVO
            System.out.println("3) Consultar mis diplomas");
            System.out.println("0) Volver");
            int op = ConsoleIO.readIntInRange("Opción: ", 0, 3);
            if (op == 0) return;

            switch (op) {
                case 1 -> {
                    Evento ev = pickEvento();
                    estudiante.marcarAsistencia(ev);
                    System.out.println("✔️  Asistencia marcada.");
                    ConsoleIO.pressEnterToContinue();
                }
                case 2 -> {
                    Evento ev = pickEvento();
                    boolean asistio = ev.consultarAsistencia(estudiante);
                    System.out.println("Estado de asistencia en \"" + ev.getNombreEvento() + "\": " + (asistio ? "Sí" : "No"));
                    ConsoleIO.pressEnterToContinue();
                }
                case 3 -> {
                    DIPLOMAS.stream()
                            .filter(d -> d.getEstudiante().getCif().equals(estudiante.getCif()))
                            .forEach(estudiante::consultarDiploma);
                    ConsoleIO.pressEnterToContinue();
                }
            }
        }
    }


    /* Helpers para escoger objetos */


    //METODOS DE AYUDA
    private static Evento pickEvento() {
        if (EVENTOS.isEmpty()) throw new IllegalStateException("No hay eventos creados.");
        System.out.println("Eventos:");

        //Ciclo para mostrar los eventos
        for (int i=0;i<EVENTOS.size();i++)
        {
            //Obtiene el evento a mostrar
            Evento e = EVENTOS.get(i);
            System.out.println((i+1)+") "+e.getIdEvento()+" - "+e.getNombreEvento());
        }
        int idx = ConsoleIO.readIntInRange("Seleccione: ",1,EVENTOS.size())-1;
        return EVENTOS.get(idx);
    }

    private static Estudiante pickEstudianteByCif() {
        if (EST_BY_CIF.isEmpty()) throw new IllegalStateException("No hay estudiantes.");
        List<Estudiante> list = new ArrayList<>(EST_BY_CIF.values());
        for (int i=0;i<list.size();i++) {
            System.out.println((i+1)+") "+list.get(i).getCif()+" - "+list.get(i).getNombre());
        }
        int idx = ConsoleIO.readIntInRange("Seleccione: ",1,list.size())-1;
        return list.get(idx);
    }

}
