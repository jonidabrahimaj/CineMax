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

import java.util.ArrayList;
import java.util.List;


public final class CsvUtil {

    /** Carattere separatore dei campi. */
    public static final char SEPARATORE = ',';

    /** Costruttore privato: la classe espone solo metodi statici. */
    private CsvUtil() {
    }

    /**
     * Suddivide una riga CSV nei suoi campi, rispettando le regole di
     * quoting (campi racchiusi tra doppi apici).
     *
     * @param riga la riga di testo da analizzare (senza il carattere a-capo)
     * @return la lista ordinata dei campi presenti nella riga
     */
    public static List<String> parseRiga(String riga) {
        List<String> campi = new ArrayList<>();
        if (riga == null) {
            return campi;
        }

        StringBuilder corrente = new StringBuilder();
        boolean dentroApici = false;

        for (int i = 0; i < riga.length(); i++) {
            char c = riga.charAt(i);

            if (dentroApici) {
                if (c == '"') {
                    // Doppio apice raddoppiato -> apice letterale
                    if (i + 1 < riga.length() && riga.charAt(i + 1) == '"') {
                        corrente.append('"');
                        i++;
                    } else {
                        dentroApici = false;
                    }
                } else {
                    corrente.append(c);
                }
            } else {
                if (c == '"') {
                    dentroApici = true;
                } else if (c == SEPARATORE) {
                    campi.add(corrente.toString());
                    corrente.setLength(0);
                } else {
                    corrente.append(c);
                }
            }
        }
        campi.add(corrente.toString());
        return campi;
    }

    /**
     * Compone una riga CSV a partire da un insieme di campi, applicando il
     * quoting solo dove necessario (campi contenenti separatore, apici o
     * a-capo).
     *
     * @param campi i valori da scrivere nei singoli campi
     * @return la riga CSV pronta per essere salvata su file
     */
    public static String componiRiga(String... campi) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < campi.length; i++) {
            if (i > 0) {
                sb.append(SEPARATORE);
            }
            sb.append(quotaSeNecessario(campi[i]));
        }
        return sb.toString();
    }

    /**
     * Applica il quoting a un singolo campo solo se contiene caratteri
     * speciali.
     *
     * @param campo il valore del campo (puo' essere {@code null})
     * @return il campo eventualmente racchiuso tra apici e con gli apici
     *         interni raddoppiati
     */
    private static String quotaSeNecessario(String campo) {
        String valore = (campo == null) ? "" : campo;
        boolean serve = valore.indexOf(SEPARATORE) >= 0
                || valore.indexOf('"') >= 0
                || valore.indexOf('\n') >= 0
                || valore.indexOf('\r') >= 0;
        if (!serve) {
            return valore;
        }
        return "\"" + valore.replace("\"", "\"\"") + "\"";
    }
}
