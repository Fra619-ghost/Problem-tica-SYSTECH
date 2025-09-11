package services;

import models.Administrador;
import models.Estudiante;
import utils.ConsoleIO;

import java.util.List;
import java.util.Map;

public class AuthService {

    private final int maxIntentos;

    public AuthService() {
        this(3);
    }

    public AuthService(int maxIntentos) {
        this.maxIntentos = Math.max(1, maxIntentos);
    }

    /** Login de administrador por usuario+password con reintentos */
    public Administrador loginAdmin(List<Administrador> admins) {
        for (int intento = 1; intento <= maxIntentos; intento++) {
            String usr = ConsoleIO.readNonEmpty("Usuario admin: ");
            String pwd = ConsoleIO.readNonEmpty("Contraseña: ");

            for (Administrador a : admins) {
                if (a.getUsuario().equals(usr) && a.getPassword().equals(pwd)) {
                    System.out.println("✔️  Autenticación de Administrador exitosa.\n");
                    return a;
                }
            }
            System.out.println("❌  Credenciales inválidas. Intento " + intento + "/" + maxIntentos);
        }
        throw new IllegalStateException("Se excedió el número de intentos de autenticación de administrador.");
    }

    /** Login de estudiante por CIF+password con reintentos */
    public Estudiante loginEstudiante(Map<String, Estudiante> estudiantesPorCif) {
        for (int intento = 1; intento <= maxIntentos; intento++) {
            String cif = ConsoleIO.readMatching(
                    "CIF: ",
                    "[A-Za-z0-9_-]{3,}",
                    "CIF inválido (use letras/números/_-/ y al menos 3 caracteres)."
            );
            String pwd = ConsoleIO.readNonEmpty("Contraseña: ");

            Estudiante e = estudiantesPorCif.get(cif);
            if (e != null && e.getPassword().equals(pwd)) {
                System.out.println("✔️  Autenticación de Estudiante exitosa.\n");
                return e;
            }
            System.out.println("❌  CIF o contraseña incorrecta. Intento " + intento + "/" + maxIntentos);
        }
        throw new IllegalStateException("Se excedió el número de intentos de autenticación de estudiante.");
    }
}
