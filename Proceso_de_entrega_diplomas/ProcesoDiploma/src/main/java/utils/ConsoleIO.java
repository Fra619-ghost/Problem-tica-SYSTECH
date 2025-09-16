package utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public final class ConsoleIO {

    private static final Scanner SC = new Scanner(System.in);
    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private ConsoleIO() {}

    public static String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = SC.nextLine().trim();
            if (!s.isEmpty()) return s;
            System.out.println("⚠️  Entrada vacía. Intente nuevamente.");
        }
    }

    public static String readMatching(String prompt, String regex, String errorMsg) {
        while (true) {
            System.out.print(prompt);
            String s = SC.nextLine().trim();
            if (s.matches(regex)) return s;
            System.out.println("⚠️  " + errorMsg);
        }
    }

    public static int readIntInRange(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String s = SC.nextLine().trim();
            try {
                int v = Integer.parseInt(s);
                if (v < min || v > max) throw new NumberFormatException();
                return v;
            } catch (NumberFormatException ex) {
                System.out.println("⚠️  Ingrese un entero entre " + min + " y " + max + ".");
            }
        }
    }

    public static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = SC.nextLine().trim();
            try {
                return Double.parseDouble(s.replace(",", "."));
            } catch (NumberFormatException ex) {
                System.out.println("⚠️  Número inválido. Intente de nuevo.");
            }
        }
    }

    public static boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(prompt + " (s/n): ");
            String s = SC.nextLine().trim().toLowerCase();
            if (s.equals("s") || s.equals("si") || s.equals("sí")) return true;
            if (s.equals("n") || s.equals("no")) return false;
            System.out.println("⚠️  Responda 's' o 'n'.");
        }
    }

    /** Devuelve LocalDate con formato yyyy-MM-dd */
    public static LocalDate readIsoDate(String prompt) {
        while (true) {
            System.out.print(prompt + " (yyyy-MM-dd): ");
            String s = SC.nextLine().trim();
            try {
                return LocalDate.parse(s, ISO_DATE);
            } catch (DateTimeParseException ex) {
                System.out.println("⚠️  Fecha inválida. Ejemplo: 2025-09-11");
            }
        }
    }

    /** Pausa simple */
    public static void pressEnterToContinue() {
        System.out.println("Presione ENTER para continuar…");
        SC.nextLine();
    }
}
