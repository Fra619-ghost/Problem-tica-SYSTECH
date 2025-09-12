package run;
import models.*;
import utils.ConsoleIO;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        System.out.println(" Sistema de Gestión de Torneos de eSports \n");

        Categoria estrategia = new Categoria("Estrategia");
        Juego clash = new Juego("Clash Royale", estrategia);
        
        Jugador j1 = new Jugador("Pedro Morales", "Halcón", 1500);
    }

}
