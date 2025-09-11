package run;
import java.util.Scanner;
import services.AuthService;
import utils.ConsoleIO;
import models.*;

public class Main {

    public static void main(String[] args) {


    }

    public static void menuAdmin(Administrador admin) {
        System.out.println("Bienvenido, " + admin.getNombre() + " (Admin)");
        // Aquí iría el menú y lógica para el administrador
    }

    public static void menuEstudiante(Estudiante estudiante) {
        System.out.println("Bienvenido, " + estudiante.getNombre() + " (Estudiante)");
        // Aquí iría el menú y lógica para el estudiante
    }

    public static void menu(){
        
    }


}
