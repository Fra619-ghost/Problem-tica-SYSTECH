package run;
import models.*;
import utils.ConsoleIO;
import viewModel.AppVM; // Importa tu AppVM

import java.time.LocalDate;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        System.out.println(" Sistema de Gestión de Torneos de eSports \n");

        // Instancia ConsoleIO y AppVM
        ConsoleIO io = new ConsoleIO();
        AppVM vm = new AppVM(io);

        // Opcional: Cargar algunos datos de ejemplo para probar el menú
        try {
            Categoria estrategia = vm.crearCategoria("Estrategia", "Juegos que requieren planificación táctica.");
            Juego clash = vm.crearJuego("Clash Royale", estrategia);
            Juego lol = vm.crearJuego("League of Legends", vm.crearCategoria("MOBA", "Multiplayer Online Battle Arena"));

            Equipo fox = vm.crearEquipo("Fox");
            vm.agregarJugadorAEquipo("Fox", "Ana", "AnaX", 1800);
            vm.agregarJugadorAEquipo("Fox", "Luis", "Lucho", 1750);

            Equipo raptors = vm.crearEquipo("Raptors");
            vm.agregarJugadorAEquipo("Raptors", "Sofía", "Sofi", 1820);

            Torneo clashCup = vm.crearTorneo("Clash Royale Cup", "Supercell", LocalDate.now(), "Clash Royale");
            vm.inscribirEquipoEnTorneo("Clash Royale Cup", "Fox");
            vm.inscribirEquipoEnTorneo("Clash Royale Cup", "Raptors");

            Arbitro arb1 = vm.crearArbitro("Carlos", "Mena");
            vm.programarPartida("Clash Royale Cup", LocalDate.now().plusDays(7), "Fox", "Raptors", arb1);

            io.success("Datos de ejemplo cargados.");
        } catch (Exception e) {
            io.error("Error al cargar datos de ejemplo: " + e.getMessage());
        }

        // Inicia el bucle del menú interactivo
        vm.runMenuLoop();
    }
}
        