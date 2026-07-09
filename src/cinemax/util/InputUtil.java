/*
 * ============================================================================
 *  CineMax - Laboratorio Interdisciplinare A
 *  Autori:
 *    - <Jonida Brahimaj> - Matricola <759037> - Sede <COMO>
 *    - <Renee Angelica Cabigting> - Matricola <756997> - Sede <COMO>
 *  File: Prenotazione.java
 * ============================================================================
 */
package cinemax.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public final class InputUtil {

    /** Scanner condiviso sullo standard input. */
    private static final Scanner SCANNER = new Scanner(System.in);

    /** Formato accettato per le date (giorno). */
    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /** Formato accettato per data e ora. */
    private static final DateTimeFormatter FORMATO_DATA_ORA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /** Costruttore privato: la classe espone solo metodi statici. */
    private InputUtil() {
    }

    /**
     * Legge una riga di testo non vuota.
     *
     * @param messaggio il messaggio da mostrare
     * @return il testo inserito (privo di spazi iniziali/finali)
     */
    public static String leggiStringa(String messaggio) {
        while (true) {
            System.out.print(messaggio);
            String riga = SCANNER.nextLine().trim();
            if (!riga.isEmpty()) {
                return riga;
            }
            System.out.println("  ! Il valore non puo' essere vuoto.");
        }
    }

    /**
     * Legge una riga di testo che puo' anche essere vuota (per criteri di
     * ricerca facoltativi).
     *
     * @param messaggio il messaggio da mostrare
     * @return il testo inserito, eventualmente vuoto
     */
    public static String leggiStringaOpzionale(String messaggio) {
        System.out.print(messaggio);
        return SCANNER.nextLine().trim();
    }

    /**
     * Legge un numero intero.
     *
     * @param messaggio il messaggio da mostrare
     * @return l'intero inserito
     */
    public static int leggiIntero(String messaggio) {
        while (true) {
            System.out.print(messaggio);
            String riga = SCANNER.nextLine().trim();
            try {
                return Integer.parseInt(riga);
            } catch (NumberFormatException e) {
                System.out.println("  ! Inserire un numero intero valido.");
            }
        }
    }

    /**
     * Legge un numero intero compreso tra due estremi inclusi.
     *
     * @param messaggio il messaggio da mostrare
     * @param min       valore minimo accettato
     * @param max       valore massimo accettato
     * @return l'intero inserito nell'intervallo
     */
    public static int leggiInteroNelRange(String messaggio, int min, int max) {
        while (true) {
            int valore = leggiIntero(messaggio);
            if (valore >= min && valore <= max) {
                return valore;
            }
            System.out.printf("  ! Inserire un numero tra %d e %d.%n", min, max);
        }
    }

    /**
     * Legge un numero decimale (accetta sia il punto sia la virgola).
     *
     * @param messaggio il messaggio da mostrare
     * @return il valore decimale inserito
     */
    public static double leggiDecimale(String messaggio) {
        while (true) {
            System.out.print(messaggio);
            String riga = SCANNER.nextLine().trim().replace(',', '.');
            try {
                return Double.parseDouble(riga);
            } catch (NumberFormatException e) {
                System.out.println("  ! Inserire un numero valido (es. 8.50).");
            }
        }
    }

    /**
     * Legge una data nel formato {@code yyyy-MM-dd}.
     *
     * @param messaggio il messaggio da mostrare
     * @return la data inserita
     */
    public static LocalDate leggiData(String messaggio) {
        while (true) {
            System.out.print(messaggio + " (formato yyyy-MM-dd): ");
            String riga = SCANNER.nextLine().trim();
            try {
                return LocalDate.parse(riga, FORMATO_DATA);
            } catch (DateTimeParseException e) {
                System.out.println("  ! Data non valida. Esempio: 2026-05-20");
            }
        }
    }

    /**
     * Legge una data facoltativa: una riga vuota restituisce {@code null}.
     *
     * @param messaggio il messaggio da mostrare
     * @return la data inserita, oppure {@code null} se l'utente preme solo Invio
     */
    public static LocalDate leggiDataOpzionale(String messaggio) {
        System.out.print(messaggio + " (yyyy-MM-dd, vuoto per saltare): ");
        String riga = SCANNER.nextLine().trim();
        if (riga.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(riga, FORMATO_DATA);
        } catch (DateTimeParseException e) {
            System.out.println("  ! Data non valida, criterio ignorato.");
            return null;
        }
    }

    /**
     * Legge una data e un'ora nel formato {@code yyyy-MM-dd HH:mm}.
     *
     * @param messaggio il messaggio da mostrare
     * @return la data e ora inserite
     */
    public static LocalDateTime leggiDataOra(String messaggio) {
        while (true) {
            System.out.print(messaggio + " (formato yyyy-MM-dd HH:mm): ");
            String riga = SCANNER.nextLine().trim();
            try {
                return LocalDateTime.parse(riga, FORMATO_DATA_ORA);
            } catch (DateTimeParseException e) {
                System.out.println("  ! Data/ora non valida. Esempio: 2026-05-20 21:00");
            }
        }
    }

    /**
     * Legge un numero decimale facoltativo: una riga vuota restituisce
     * {@code null} (utile per i filtri di costo nella ricerca).
     *
     * @param messaggio il messaggio da mostrare
     * @return il valore inserito, oppure {@code null}
     */
    public static Double leggiDecimaleOpzionale(String messaggio) {
        System.out.print(messaggio + " (vuoto per saltare): ");
        String riga = SCANNER.nextLine().trim().replace(',', '.');
        if (riga.isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(riga);
        } catch (NumberFormatException e) {
            System.out.println("  ! Valore non valido, criterio ignorato.");
            return null;
        }
    }

    /**
     * Pone una domanda con risposta sì/no.
     *
     * @param messaggio il messaggio da mostrare
     * @return {@code true} se l'utente risponde affermativamente
     */
    public static boolean leggiConferma(String messaggio) {
        while (true) {
            System.out.print(messaggio + " (s/n): ");
            String riga = SCANNER.nextLine().trim().toLowerCase();
            if (riga.equals("s") || riga.equals("si") || riga.equals("sì")) {
                return true;
            }
            if (riga.equals("n") || riga.equals("no")) {
                return false;
            }
            System.out.println("  ! Rispondere con 's' o 'n'.");
        }
    }

    /**
     * Mette in pausa l'esecuzione fino alla pressione del tasto Invio.
     */
    public static void premiInvioPerContinuare() {
        System.out.print("\nPremere Invio per continuare...");
        SCANNER.nextLine();
    }
}
