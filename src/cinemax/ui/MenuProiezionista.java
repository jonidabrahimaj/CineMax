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
import cinemax.model.Proiezione;
import cinemax.model.Utente;
import cinemax.persistence.GestorePrenotazioni;
import cinemax.persistence.GestoreProiezioni;
import cinemax.util.InputUtil;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class MenuProiezionista {

    /** Gestore delle proiezioni. */
    private final GestoreProiezioni gestoreProiezioni;

    /** Gestore delle prenotazioni (per i vincoli su modifica/eliminazione). */
    private final GestorePrenotazioni gestorePrenotazioni;

    /** Visualizzatore per la stampa formattata. */
    private final Visualizzatore visualizzatore;

    /** Proiezionista attualmente autenticato. */
    private final Utente proiezionista;

    /**
     * Costruisce il menu del proiezionista.
     *
     * @param gestoreProiezioni   gestore delle proiezioni
     * @param gestorePrenotazioni gestore delle prenotazioni
     * @param visualizzatore      visualizzatore per la stampa
     * @param proiezionista       proiezionista autenticato
     */
    public MenuProiezionista(GestoreProiezioni gestoreProiezioni,
                             GestorePrenotazioni gestorePrenotazioni,
                             Visualizzatore visualizzatore,
                             Utente proiezionista) {
        this.gestoreProiezioni = gestoreProiezioni;
        this.gestorePrenotazioni = gestorePrenotazioni;
        this.visualizzatore = visualizzatore;
        this.proiezionista = proiezionista;
    }

    /**
     * Avvia il ciclo del menu del proiezionista fino al logout.
     */
    public void esegui() {
        boolean attivo = true;
        while (attivo) {
            visualizzatore.stampaTitolo("Area proiezionista - " + proiezionista.getNomeCompleto());
            System.out.println("  1) Aggiungi proiezione");
            System.out.println("  2) Modifica proiezione");
            System.out.println("  3) Elimina proiezione");
            System.out.println("  4) Visualizza palinsesto");
            System.out.println("  0) Logout");
            int scelta = InputUtil.leggiInteroNelRange("  Scelta: ", 0, 4);

            switch (scelta) {
                case 1 -> aggiungiProiezione();
                case 2 -> modificaProiezione();
                case 3 -> eliminaProiezione();
                case 4 -> visualizzaPalinsesto();
                case 0 -> attivo = false;
                default -> { }
            }
        }
    }

    /**
     * Inserisce un nuovo film e la relativa proiezione (data e costo del
     * biglietto). L'aggiunta viene rifiutata in caso di sovrapposizione.
     */
    private void aggiungiProiezione() {
        visualizzatore.stampaTitolo("Aggiungi proiezione");
        String titolo = InputUtil.leggiStringa("  Titolo film: ");
        String genere = InputUtil.leggiStringa("  Genere: ");
        String regista = InputUtil.leggiStringa("  Regista: ");
        int anno = InputUtil.leggiInteroNelRange("  Anno: ", 1888, 2100);
        int durata = InputUtil.leggiInteroNelRange("  Durata (min): ", 1, 600);
        int etaMinima = InputUtil.leggiInteroNelRange("  Eta' minima: ", 0, 99);
        LocalDateTime dataOra = InputUtil.leggiDataOra("  Data e ora proiezione");
        double costo = InputUtil.leggiDecimale("  Costo biglietto (EUR): ");

        Film film = new Film(titolo, genere, regista, anno, durata, etaMinima);
        try {
            Proiezione creata = gestoreProiezioni.aggiungi(film, dataOra, costo);
            System.out.println("\n  Proiezione aggiunta con id #" + creata.getId() + ".");
        } catch (IllegalStateException e) {
            System.out.println("\n  ! " + e.getMessage());
        } catch (IOException e) {
            System.out.println("\n  ! Errore di salvataggio: " + e.getMessage());
        }
        InputUtil.premiInvioPerContinuare();
    }

    /**
     * Modifica una proiezione esistente (data/ora e costo), solo se non
     * esistono prenotazioni per quella proiezione.
     */
    private void modificaProiezione() {
        Proiezione p = selezionaProiezione("Modifica proiezione");
        if (p == null) {
            return;
        }
        if (gestorePrenotazioni.esistonoPrenotazioniPer(p.getId())) {
            System.out.println("\n  ! Esistono prenotazioni: la proiezione non puo' essere modificata.");
            InputUtil.premiInvioPerContinuare();
            return;
        }
        LocalDateTime nuovaDataOra = InputUtil.leggiDataOra("  Nuova data e ora");
        double nuovoCosto = InputUtil.leggiDecimale("  Nuovo costo biglietto (EUR): ");

        LocalDateTime vecchiaDataOra = p.getDataOra();
        double vecchioCosto = p.getCostoBiglietto();
        p.setDataOra(nuovaDataOra);
        p.setCostoBiglietto(nuovoCosto);
        try {
            gestoreProiezioni.aggiorna(p);
            System.out.println("\n  Proiezione aggiornata.");
        } catch (IllegalStateException e) {
            // ripristina i valori precedenti in caso di conflitto
            p.setDataOra(vecchiaDataOra);
            p.setCostoBiglietto(vecchioCosto);
            System.out.println("\n  ! " + e.getMessage());
        } catch (IOException e) {
            System.out.println("\n  ! Errore di salvataggio: " + e.getMessage());
        }
        InputUtil.premiInvioPerContinuare();
    }

    /**
     * Elimina una proiezione esistente, solo se non esistono prenotazioni per
     * quella proiezione.
     */
    private void eliminaProiezione() {
        Proiezione p = selezionaProiezione("Elimina proiezione");
        if (p == null) {
            return;
        }
        if (gestorePrenotazioni.esistonoPrenotazioniPer(p.getId())) {
            System.out.println("\n  ! Esistono prenotazioni: la proiezione non puo' essere eliminata.");
            InputUtil.premiInvioPerContinuare();
            return;
        }
        if (InputUtil.leggiConferma("\n  Confermi l'eliminazione della proiezione #" + p.getId() + "?")) {
            try {
                gestoreProiezioni.elimina(p.getId());
                System.out.println("\n  Proiezione eliminata.");
            } catch (IOException e) {
                System.out.println("\n  ! Errore di salvataggio: " + e.getMessage());
            }
        }
        InputUtil.premiInvioPerContinuare();
    }

    /**
     * Mostra l'intero palinsesto ordinato per data.
     */
    private void visualizzaPalinsesto() {
        visualizzatore.stampaTitolo("Palinsesto");
        visualizzatore.stampaElencoProiezioni(gestoreProiezioni.getTutte());
        InputUtil.premiInvioPerContinuare();
    }

    /**
     * Mostra il palinsesto e fa selezionare una proiezione tramite il suo id.
     *
     * @param titolo titolo della schermata
     * @return la proiezione selezionata, oppure {@code null} se non valida
     */
    private Proiezione selezionaProiezione(String titolo) {
        visualizzatore.stampaTitolo(titolo);
        List<Proiezione> tutte = gestoreProiezioni.getTutte();
        if (tutte.isEmpty()) {
            System.out.println("  Nessuna proiezione presente.");
            InputUtil.premiInvioPerContinuare();
            return null;
        }
        visualizzatore.stampaElencoProiezioni(tutte);
        int id = InputUtil.leggiIntero("\n  Id della proiezione: ");
        Proiezione p = gestoreProiezioni.trovaPerId(id);
        if (p == null) {
            System.out.println("\n  ! Nessuna proiezione con id #" + id + ".");
            InputUtil.premiInvioPerContinuare();
        }
        return p;
    }
}
