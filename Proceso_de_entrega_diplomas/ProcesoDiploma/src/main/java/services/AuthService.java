package services;

import models.Administrador;
import models.Estudiante;
import utils.ConsoleIO;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class AuthService {
    private final int maxIntentos;
    private final AtomicInteger seqAdmin = new AtomicInteger(1);
    private final AtomicInteger seqStudent = new AtomicInteger(1);

    public AuthService() { this(3); }
    public AuthService(int maxIntentos) { this.maxIntentos = Math.max(1, maxIntentos); }

    /* =========================
       LOGIN (solo usuario + password)
       ========================= */

    public Administrador loginAdmin(Map<String, Administrador> adminsByUser) {
        for (int i = 1; i <= maxIntentos; i++) {
            String usr = ConsoleIO.readNonEmpty("Usuario admin: ");
            String pwd = ConsoleIO.readNonEmpty("Contraseña: ");
            Administrador a = adminsByUser.get(usr);
            if (a != null && Objects.equals(a.getPassword(), pwd)) {
                System.out.println("✔️  Autenticación de Administrador exitosa.\n");
                return a;
            }
            System.out.println("❌  Usuario o contraseña inválidos. Intento " + i + "/" + maxIntentos);
        }
        throw new IllegalStateException("Se excedió el número de intentos para administrador.");
    }

    public Estudiante loginEstudiante(Map<String, Estudiante> studentsByUser) {
        for (int i = 1; i <= maxIntentos; i++) {
            String usr = ConsoleIO.readNonEmpty("Usuario estudiante: ");
            String pwd = ConsoleIO.readNonEmpty("Contraseña: ");
            Estudiante e = studentsByUser.get(usr);
            if (e != null && e.getPassword().equals(pwd)) {
                System.out.println("✔️  Autenticación de Estudiante exitosa.\n");
                return e;
            }
            System.out.println("❌  Usuario o contraseña inválidos. Intento " + i + "/" + maxIntentos);
        }
        throw new IllegalStateException("Se excedió el número de intentos para estudiante.");
    }

    /* =========================
       REGISTRO (usuario/ID automáticos)
       ========================= */

    public Administrador registrarAdmin(Map<String, Administrador> adminsByUser) {
        System.out.println("\n=== Registro de Administrador ===");
        String nombre = ConsoleIO.readNonEmpty("Nombre completo: ");
        String usuario;
        while (true) {
            usuario = ConsoleIO.readMatching("Usuario: ","[A-Za-z0-9._-]{3,}","Usuario inválido (mín. 3; letras/números ._-).");
            if (adminsByUser.containsKey(usuario)) {
                System.out.println("⚠️  Ya existe ese usuario. Intente otro.");
            } else break;
        }
        String password = leerPasswordConConfirmacion();
        Administrador nuevo = new Administrador(/*id*/ usuario, nombre, usuario, password);
        adminsByUser.put(usuario, nuevo);
        System.out.println("✔️  Administrador registrado. Usuario: " + usuario + "\n");
        return nuevo;
    }

    public Estudiante registrarEstudiante(Map<String, Estudiante> estudiantesPorCif) {
        System.out.println("\n=== Registro de Estudiante ===");
        String cif;
        while (true) {
            cif = ConsoleIO.readMatching("CIF: ","[A-Za-z0-9_-]{3,}","CIF inválido (mín. 3; letras/números/_-/).");
            if (estudiantesPorCif.containsKey(cif)) {
                System.out.println("⚠️  Ya existe un estudiante con ese CIF. Intente otro.");
            } else break;
        }
        String nombre = ConsoleIO.readNonEmpty("Nombre completo: ");
        String password = leerPasswordConConfirmacion();
        Estudiante e = new Estudiante(cif, nombre, password);
        estudiantesPorCif.put(e.getCif(), e);
        System.out.println("✔️  Estudiante registrado.\n");
        return e;
    }

    /* =========================
       PRIVADAS
       ========================= */
    private String leerPasswordConConfirmacion() {
        String password;
        while (true) {
            password = ConsoleIO.readMatching("Contraseña: ", ".{4,}", "Debe tener al menos 4 caracteres.");
            String confirm = ConsoleIO.readNonEmpty("Confirmar contraseña: ");
            if (password.equals(confirm)) break;
            System.out.println("⚠️  Las contraseñas no coinciden. Intente nuevamente.");
        }
        return password;
    }
}
