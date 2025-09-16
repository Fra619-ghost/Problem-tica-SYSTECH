package run;
import models.*;
import utils.ConsoleIO;
import viewModel.AppVM; // Clase que gestiona la lógica entre la vista (consola) y los modelos

import java.time.LocalDate;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        System.out.println(" Sistema de Gestión de Torneos de eSports \n");

        // Instancia ConsoleIO y AppVM
        ConsoleIO io = new ConsoleIO();
        // Instancia del ViewModel que conecta la consola (vista) con la lógica de negocio (modelos)
        AppVM vm = new AppVM(io);

        // Opcional: Cargar algunos datos de ejemplo para probar el menú
        try {
            // Crear categorías y juegos

            // Se crea una categoría llamada "Estrategia" con su descripción
            // y se guarda en la variable "estrategia".
            Categoria estrategia = vm.crearCategoria("Estrategia", "Juegos que requieren planificación táctica.");
            Juego clash = vm.crearJuego("Clash Royale", estrategia); // Se crea el juego "Clash Royale" y se asocia a la categoría "estrategia".
            Juego lol = vm.crearJuego("League of Legends", vm.crearCategoria("MOBA", "Multiplayer Online Battle Arena"));
            // Se crea el juego "League of Legends" y directamente se le pasa
            // una categoría nueva llamada "MOBA", con su descripción,
            // sin necesidad de guardarla en una variable intermedia.

            // Crear un equipo y agregar jugadores
            Equipo fox = vm.crearEquipo("Fox");
            vm.agregarJugadorAEquipo("Fox", "Ana", "AnaX", 1800);
            vm.agregarJugadorAEquipo("Fox", "Luis", "Lucho", 1750);

            // Crear otro equipo y agregar jugador
            Equipo raptors = vm.crearEquipo("Raptors");
            vm.agregarJugadorAEquipo("Raptors", "Sofía", "Sofi", 1820);

            // Crear un torneo y registrar equipos en él
            Torneo clashCup = vm.crearTorneo("Clash Royale Cup", "Supercell", LocalDate.now(), "Clash Royale");
            vm.inscribirEquipoEnTorneo("Clash Royale Cup", "Fox");
            vm.inscribirEquipoEnTorneo("Clash Royale Cup", "Raptors");

            // Crear árbitro y programar una partida dentro del torneo
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
        