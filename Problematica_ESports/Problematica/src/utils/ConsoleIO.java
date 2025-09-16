package utils;

import java.io.InputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Function;

/**
 * <h2> Clase para la entrada y salida de datos por consola </h1>
 * Utilidad para E/S por consola con validaciones y UX mejorada.
 * Centraliza prompts, reintentos, selección de opciones, fechas, números y mensajes con estilo.
 *
 * Uso típico:
 *   ConsoleIO io = new ConsoleIO(); // System.in/out
 *   int opcion = io.promptIntInRange("Seleccione una opción", 1, 5);
 *   String nombre = io.promptNonEmpty("Nombre del equipo");
 *   boolean ok = io.promptYesNo("¿Confirmar?");
 *   LocalDate f = io.promptDate("Fecha de inicio (yyyy-MM-dd)", "yyyy-MM-dd");
 *   Equipo elegido = io.chooseFrom("Elige un equipo", equipos, Equipo::getNombre);
 */
public class ConsoleIO {

    // ===== Estilos ANSI (pueden desactivarse) =====
    public static final String ANSI_RESET  = "\u001B[0m";
    public static final String ANSI_BOLD   = "\u001B[1m";
    public static final String ANSI_RED    = "\u001B[31m";
    public static final String ANSI_GREEN  = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE   = "\u001B[34m";
    public static final String ANSI_CYAN   = "\u001B[36m";
    public static final String ANSI_GRAY   = "\u001B[90m";

    private final Scanner scanner;
    private final PrintStream out;
    private final boolean ansiEnabled;

    // ===== Constructores =====

    /** Usa System.in/out y habilita ANSI por defecto. */
    public ConsoleIO() {
        this(System.in, System.out, true);
    }

    /** Inyectable para tests, con control de ANSI. */
    public ConsoleIO(InputStream in, PrintStream out, boolean ansiEnabled) {
        this.scanner = new Scanner(Objects.requireNonNull(in)).useDelimiter("\n");
        this.out = Objects.requireNonNull(out);
        this.ansiEnabled = ansiEnabled;
    }

    // ===== Impresión básica =====

    public void println() { out.println(); }

    public void println(String s) { out.println(safe(s)); }

    public void printf(String fmt, Object... args) { out.printf(safe(fmt), args); }

    public void info(String msg)    { println(color("[i] "  + msg, ANSI_CYAN)); }
    public void success(String msg) { println(color("[✓] "  + msg, ANSI_GREEN)); }
    public void warn(String msg)    { println(color("[!] "  + msg, ANSI_YELLOW)); }
    public void error(String msg)   { println(color("[x] "  + msg, ANSI_RED)); }

    /** Título centrado con líneas. */
    public void banner(String title) {
        String t = stripAnsi(title);
        int width = Math.max(30, Math.min(70, t.length() + 10));
        String line = "─".repeat(width);
        println(color(line, ANSI_GRAY));
        int pad = (width - t.length()) / 2;
        println(" ".repeat(Math.max(0, pad)) + color(ANSI_BOLD + t + ANSI_RESET, ANSI_BLUE));
        println(color(line, ANSI_GRAY));
    }

    /** Separador. */
    public void divider() { println(color("─".repeat(40), ANSI_GRAY)); }

    /** “Limpia” pantalla (aprox). */
    public void clear() {
        if (ansiEnabled) {
            out.print("\033[H\033[2J");
            out.flush();
        } else {
            println("\n".repeat(50));
        }
    }

    /** Pausa hasta ENTER. */
    public void pause() { promptLine(color("Presione ENTER para continuar...", ANSI_GRAY)); }

    // ===== Prompts de texto =====

    /** Lee una línea (puede ser vacía). */
    public String promptLine(String prompt) {
        out.print(formatPrompt(prompt));
        String line = scanner.hasNext() ? scanner.next() : "";
        return line.replace("\r", "").trim();
    }

    /** Lee una línea no vacía (reintenta). */
    public String promptNonEmpty(String prompt) {
        while (true) {
            String s = promptLine(prompt);
            if (!s.isBlank()) return s;
            warn("El valor no puede estar vacío.");
        }
    }

    /** Lee una línea, usa default si el usuario solo presiona ENTER. */
    public String promptWithDefault(String prompt, String defaultValue) {
        String s = promptLine(prompt + " [" + defaultValue + "]");
        return s.isBlank() ? defaultValue : s;
    }

    // ===== Prompts sí/no =====

    /** Acepta: s, si, y, yes, n, no (insensible a may/min). */
    public boolean promptYesNo(String prompt) {
        while (true) {
            String s = promptNonEmpty(prompt + " (s/n)");
            s = s.toLowerCase(Locale.ROOT);
            if (s.equals("s") || s.equals("si") || s.equals("y") || s.equals("yes")) return true;
            if (s.equals("n") || s.equals("no")) return false;
            warn("Responda 's' o 'n'.");
        }
    }

    // ===== Números =====

    public int promptInt(String prompt) {
        while (true) {
            String s = promptNonEmpty(prompt);
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                warn("Ingrese un entero válido.");
            }
        }
    }

    public int promptIntInRange(String prompt, int min, int max) {
        if (min > max) throw new IllegalArgumentException("min > max");
        while (true) {
            int v = promptInt(prompt + " [" + min + "–" + max + "]");
            if (v < min || v > max) {
                warn("El valor debe estar entre " + min + " y " + max + ".");
            } else return v;
        }
    }

    public double promptDouble(String prompt) {
        while (true) {
            String s = promptNonEmpty(prompt);
            try {
                return Double.parseDouble(s.replace(",", "."));
            } catch (NumberFormatException e) {
                warn("Ingrese un número válido (use '.' como separador decimal).");
            }
        }
    }

    public double promptDoubleInRange(String prompt, double min, double max) {
        if (min > max) throw new IllegalArgumentException("min > max");
        while (true) {
            double v = promptDouble(prompt + " [" + min + "–" + max + "]");
            if (v < min || v > max) {
                warn("El valor debe estar entre " + min + " y " + max + ".");
            } else return v;
        }
    }

    // ===== Fechas =====

    /** Lee una fecha con el formato indicado (ej: yyyy-MM-dd). */
    public LocalDate promptDate(String prompt, String pattern) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(pattern);
        while (true) {
            String s = promptNonEmpty(prompt);
            try {
                return LocalDate.parse(s, fmt);
            } catch (DateTimeParseException e) {
                warn("Fecha inválida. Formato esperado: " + pattern);
            }
        }
    }

    /** Lee una fecha permitiendo default (ENTER usa defaultDate). */
    public LocalDate promptDateOrDefault(String prompt, String pattern, LocalDate defaultDate) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(pattern);
        String s = promptWithDefault(prompt, defaultDate.format(fmt));
        try {
            return LocalDate.parse(s, fmt);
        } catch (DateTimeParseException e) {
            warn("Fecha inválida, se usará el valor por defecto.");
            return defaultDate;
        }
    }

    // ===== Enum =====

    /** Pide un valor de un Enum por nombre o índice (se muestra un menú numerado). */
    public <E extends Enum<E>> E promptEnum(Class<E> enumClass, String prompt) {
        E[] values = enumClass.getEnumConstants();
        if (values == null || values.length == 0) throw new IllegalArgumentException("Enum vacío");
        // Mostrar opciones
        println(ANSI_BOLD + prompt + ANSI_RESET);
        for (int i = 0; i < values.length; i++) {
            println(color(String.format("  %d) %s", i + 1, values[i].name()), ANSI_GRAY));
        }
        while (true) {
            String s = promptNonEmpty("Elige por número o nombre");
            // por número
            try {
                int idx = Integer.parseInt(s);
                if (idx >= 1 && idx <= values.length) return values[idx - 1];
            } catch (NumberFormatException ignored) { /* probar por nombre */ }
            // por nombre
            for (E v : values) if (v.name().equalsIgnoreCase(s)) return v;
            warn("Opción inválida.");
        }
    }

    // ===== Selección de listas =====

    /**
     * Menú para elegir un elemento de una lista (1..N). Muestra etiquetas con {@code labeler}.
     * @throws IllegalArgumentException si la lista está vacía.
     */
    public <T> T chooseFrom(String prompt, List<T> items, Function<T, String> labeler) {
        if (items == null || items.isEmpty()) throw new IllegalArgumentException("No hay elementos para elegir");
        println(ANSI_BOLD + prompt + ANSI_RESET);
        for (int i = 0; i < items.size(); i++) {
            String label = labeler != null ? labeler.apply(items.get(i)) : String.valueOf(items.get(i));
            println(color(String.format("  %d) %s", i + 1, label), ANSI_GRAY));
        }
        int opt = promptIntInRange("Seleccione una opción", 1, items.size());
        return items.get(opt - 1);
    }

    // ===== Tablas simples =====

    /** Imprime una tabla simple con ancho ajustado automáticamente. */
    public void table(List<String> headers, List<List<String>> rows) {
        if (headers == null || headers.isEmpty()) return;
        int cols = headers.size();
        int[] w = new int[cols];
        for (int c = 0; c < cols; c++) w[c] = Math.max(3, headers.get(c).length());
        if (rows != null) {
            for (List<String> r : rows) {
                for (int c = 0; c < cols && c < r.size(); c++) {
                    w[c] = Math.max(w[c], String.valueOf(r.get(c)).length());
                }
            }
        }
        // header
        StringBuilder sb = new StringBuilder();
        for (int c = 0; c < cols; c++) {
            sb.append(pad(headers.get(c), w[c])).append(c == cols - 1 ? "" : "  ");
        }
        println(ANSI_BOLD + sb + ANSI_RESET);
        // rows
        if (rows != null) {
            for (List<String> r : rows) {
                sb.setLength(0);
                for (int c = 0; c < cols; c++) {
                    String cell = c < r.size() ? String.valueOf(r.get(c)) : "";
                    sb.append(pad(cell, w[c])).append(c == cols - 1 ? "" : "  ");
                }
                println(sb.toString());
            }
        }
    }

    // ===== Utilidades internas =====

    private String formatPrompt(String prompt) {
        return (ansiEnabled ? ANSI_BOLD : "") + safe(prompt) + (ansiEnabled ? ANSI_RESET : "") + ": ";
    }

    private String color(String s, String ansi) {
        return ansiEnabled ? (ansi + safe(s) + ANSI_RESET) : safe(s);
    }

    private static String pad(String s, int w) {
        String t = s == null ? "" : s;
        if (t.length() >= w) return t;
        return t + " ".repeat(w - t.length());
    }

    private static String safe(String s) { return s == null ? "" : s; }

    private static String stripAnsi(String s) {
        return s == null ? "" : s.replaceAll("\\u001B\\[[;\\d]*m", "");
    }
}
