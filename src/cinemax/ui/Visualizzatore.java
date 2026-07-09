/*
 * ============================================================================
 *  CineMax - Laboratorio Interdisciplinare A
 *  Autori:
 *    - <Jonida Brahimaj> - Matricola <759037> - Sede <COMO>
 *    - <Renee Angelica Cabigting> - Matricola <756997> - Sede <COMO>
 *  File: Prenotazione.java
 * ============================================================================
 */
package cinemax.ui;

import cinemax.model.Film;
import cinemax.model.Prenotazione;
import cinemax.model.Proiezione;
import cinemax.model.Utente;
import cinemax.persistence.GestorePrenotazioni;
import cinemax.persistence.GestoreProiezioni;
import cinemax.persistence.GestoreUtenti;

import java.util.List;

public class Visualizzatore {

    /** Larghezza della linea separatrice. */
    private static final String LINEA =
            "------------------------------------------------------------";

    /** Gestore delle prenotazioni (per il calcolo dei posti liberi). */
    private final GestorePrenotazioni gestorePrenotazioni;

    /** Gestore delle proiezioni (per risalire ai dati della proiezione). */
    private final GestoreProiezioni gestoreProiezioni;

    /** Gestore degli utenti (per risalire ai dati del cliente). */
    private final GestoreUtenti gestoreUtenti;

    /**
     * Costruisce il visualizzatore con i gestori necessari.
     *
     * @param gestoreProiezioni  gestore delle proiezioni
     * @param gestorePrenotazioni gestore delle prenotazioni
     * @param gestoreUtenti      gestore degli utenti
     */
    public Visualizzatore(GestoreProiezioni gestoreProiezioni,
                          GestorePrenotazioni gestorePrenotazioni,
                          GestoreUtenti gestoreUtenti) {
        this.gestoreProiezioni = gestoreProiezioni;
        this.gestorePrenotazioni = gestorePrenotazioni;
        this.gestoreUtenti = gestoreUtenti;
    }

    /**
     * Stampa un titolo di sezione racchiuso tra linee separatrici.
     *
     * @param titolo il titolo da stampare
     */
    public void stampaTitolo(String titolo) {
        System.out.println();
        System.out.println(LINEA);
        System.out.println("  " + titolo);
        System.out.println(LINEA);
    }

    /**
     * Stampa un elenco numerato e compatto di proiezioni.
     *
     * @param proiezioni la lista di proiezioni da elencare
     */
    public void stampaElencoProiezioni(List<Proiezione> proiezioni) {
        if (proiezioni.isEmpty()) {
            System.out.println("  Nessuna proiezione trovata.");
            return;
        }
        int i = 1;
        for (Proiezione p : proiezioni) {
            int liberi = gestorePrenotazioni.postiLiberi(p.getId());
            System.out.printf("  %2d) #%d  %-22s %s  %6.2f EUR  [%3d posti liberi]%n",
                    i++, p.getId(), p.getFilm().getTitolo(),
                    p.getDataOraFormattata(), p.getCostoBiglietto(), liberi);
        }
    }

    /**
     * Stampa la scheda dettagliata di una proiezione (caratteristiche del film,
     * data e ora, costo, posti liberi).
     *
     * @param p la proiezione da visualizzare
     */
    public void stampaDettaglioProiezione(Proiezione p) {
        Film f = p.getFilm();
        int liberi = gestorePrenotazioni.postiLiberi(p.getId());
        stampaTitolo("Dettaglio proiezione #" + p.getId());
        System.out.println("  Titolo......: " + f.getTitolo());
        System.out.println("  Genere......: " + f.getGenere());
        System.out.println("  Regista.....: " + f.getRegista());
        System.out.println("  Anno........: " + f.getAnno());
        System.out.println("  Durata......: " + f.getDurataMinuti() + " min");
        System.out.println("  Eta' minima.: " + f.getEtaMinima() + " anni");
        System.out.println("  Data e ora..: " + p.getDataOraFormattata());
        System.out.printf("  Costo.......: %.2f EUR%n", p.getCostoBiglietto());
        System.out.printf("  Posti liberi: %d / %d%n", liberi, Proiezione.CAPACITA_SALA);
        System.out.println(LINEA);
    }

    /**
     * Stampa un elenco numerato e compatto di prenotazioni.
     *
     * @param prenotazioni la lista di prenotazioni da elencare
     */
    public void stampaElencoPrenotazioni(List<Prenotazione> prenotazioni) {
        if (prenotazioni.isEmpty()) {
            System.out.println("  Nessuna prenotazione trovata.");
            return;
        }
        int i = 1;
        for (Prenotazione pr : prenotazioni) {
            Proiezione p = gestoreProiezioni.trovaPerId(pr.getIdProiezione());
            String titolo = (p != null) ? p.getFilm().getTitolo() : "(proiezione rimossa)";
            String quando = (p != null) ? p.getDataOraFormattata() : "-";
            System.out.printf("  %2d) %s  %-20s %s  x%d biglietti%n",
                    i++, pr.getCodice(), titolo, quando, pr.getNumeroBiglietti());
        }
    }

    /**
     * Stampa la scheda dettagliata di una prenotazione (codice, cliente, data e
     * ora della proiezione, numero di biglietti, costo unitario e totale).
     *
     * @param pr la prenotazione da visualizzare
     */
    public void stampaDettaglioPrenotazione(Prenotazione pr) {
        Proiezione p = gestoreProiezioni.trovaPerId(pr.getIdProiezione());
        Utente cliente = gestoreUtenti.trovaPerUsername(pr.getUsernameCliente());
        String nomeCliente = (cliente != null)
                ? cliente.getNomeCompleto() : pr.getUsernameCliente();

        stampaTitolo("Dettaglio prenotazione " + pr.getCodice());
        System.out.println("  Codice......: " + pr.getCodice());
        System.out.println("  Cliente.....: " + nomeCliente);
        if (p != null) {
            System.out.println("  Film........: " + p.getFilm().getTitolo());
            System.out.println("  Data e ora..: " + p.getDataOraFormattata());
            double unitario = p.getCostoBiglietto();
            double totale = pr.calcolaTotale(unitario);
            System.out.println("  Biglietti...: " + pr.getNumeroBiglietti());
            System.out.printf("  Costo unit..: %.2f EUR%n", unitario);
            System.out.printf("  Costo totale: %.2f EUR%n", totale);
        } else {
            System.out.println("  Proiezione..: (non piu' disponibile)");
            System.out.println("  Biglietti...: " + pr.getNumeroBiglietti());
        }
        System.out.println(LINEA);
    }
}
