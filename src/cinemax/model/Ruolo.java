/*
 * ============================================================================
 *  CineMax - Laboratorio Interdisciplinare A
 *  Autori:
 *    - <Jonida Brahimaj> - Matricola <759037> - Sede <COMO>
 *    - <Renee Angelica Cabigting> - Matricola <756997> - Sede <COMO>
 *  File: Prenotazione.java
 * ============================================================================
 */
package cinemax.model;


public enum Ruolo {

    /** Cliente: prenota posti e gestisce le proprie prenotazioni. */
    CLIENTE,

    /** Proiezionista: gestisce il palinsesto delle proiezioni. */
    PROIEZIONISTA,

    /** Bigliettaio: consulta e ricerca le prenotazioni. */
    BIGLIETTAIO;

    /**
     * Converte una stringa (case-insensitive) nel ruolo corrispondente.
     *
     * @param testo il nome del ruolo letto da file o da input
     * @return il ruolo corrispondente
     * @throws IllegalArgumentException se il testo non corrisponde ad alcun ruolo
     */
    public static Ruolo daStringa(String testo) {
        if (testo == null) {
            throw new IllegalArgumentException("Ruolo nullo non valido");
        }
        return Ruolo.valueOf(testo.trim().toUpperCase());
    }
}
