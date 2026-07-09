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

import cinemax.model.Prenotazione;
import cinemax.model.Proiezione;
import cinemax.model.Utente;
import cinemax.persistence.GestorePrenotazioni;
import cinemax.persistence.GestoreProiezioni;
import cinemax.util.InputUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class MenuCliente {

    /** Gestore delle proiezioni. */
    private final GestoreProiezioni gestoreProiezioni;

    /** Gestore delle prenotazioni. */
    private final GestorePrenotazioni gestorePrenotazioni;

    /** Visualizzatore per la stampa formattata. */
    private final Visualizzatore visualizzatore;

    /** Funzioni pubbliche (ricerca/selezione proiezioni). */
    private final FunzioniPubbliche funzioniPubbliche;

    /** Cliente autenticato. */
    private final Utente cliente;

    /**
     * Costruisce il menu del cliente.
     *
     * @param gestoreProiezioni   gestore delle proiezioni
     * @param gestorePrenotazioni gestore delle prenotazioni
     * @param visualizzatore      visualizzatore per la stampa
     * @param funzioniPubbliche   funzioni pubbliche di ricerca
     * @param cliente             cliente autenticato
     */
    public MenuCliente(GestoreProiezioni gestoreProiezioni,
                       GestorePrenotazioni gestorePrenotazioni,
                       Visualizzatore visualizzatore,
                       FunzioniPubbliche funzioniPubbliche,
                       Utente cliente) {
        this.gestoreProiezioni = gestoreProiezioni;
        this.gestorePrenotazioni = gestorePrenotazioni;
        this.visualizzatore = visualizzatore;
        this.funzioniPubbliche = funzioniPubbliche;
        this.cliente = cliente;
    }

    /**
     * Avvia il ciclo del menu del cliente fino al logout.
     */
    public void esegui() {
        boolean attivo = true;
        while (attivo) {
            visualizzatore.stampaTitolo("Area cliente - " + cliente.getNomeCompleto());
            System.out.println("  1) Cerca proiezioni e prenota");
            System.out.println("  2) Le mie prenotazioni");
            System.out.println("  3) Modifica una prenotazione (cambio data)");
            System.out.println("  4) Elimina una prenotazione");
            System.out.println("  0) Logout");
            int scelta = InputUtil.leggiInteroNelRange("  Scelta: ", 0, 4);

            switch (scelta) {
                case 1 -> cercaEPrenota();
                case 2 -> mieiPrenotazioni();
                case 3 -> modificaPrenotazione();
                case 4 -> eliminaPrenotazione();
                case 0 -> attivo = false;
                default -> { }
            }
        }
    }

    /**
     * Cerca le proiezioni, ne permette la selezione e crea una prenotazione,
     * a patto che vi siano posti disponibili sufficienti e la data della
     * proiezione sia successiva alla data odierna.
     */
    private void cercaEPrenota() {
        List<Proiezione> risultati = funzioniPubbliche.cercaProiezione();
        Proiezione p = funzioniPubbliche.visualizzaProiezione(risultati);
        if (p == null) {
            InputUtil.premiInvioPerContinuare();
            return;
        }
        
        // Validazione: la proiezione deve essere futura
        if (!p.getDataOra().isAfter(LocalDateTime.now())) {
            System.out.println("\n  ! Non è possibile prenotare per una proiezione passata.");
            System.out.println("  ! Seleziona una proiezione con data futura.");
            InputUtil.premiInvioPerContinuare();
            return;
        }
        
        if (!InputUtil.leggiConferma("\n  Vuoi prenotare per questa proiezione?")) {
            return;
        }
        int liberi = gestorePrenotazioni.postiLiberi(p.getId());
        if (liberi <= 0) {
            System.out.println("\n  ! Non ci sono posti disponibili per questa proiezione.");
            InputUtil.premiInvioPerContinuare();
            return;
        }
        int biglietti = InputUtil.leggiInteroNelRange(
                "  Numero di biglietti (1-" + liberi + "): ", 1, liberi);
        try {
            Prenotazione pr = gestorePrenotazioni.crea(cliente.getUsername(), p.getId(), biglietti);
            System.out.println("\n  Prenotazione creata. Codice: " + pr.getCodice());
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("\n  ! " + e.getMessage());
        } catch (IOException e) {
            System.out.println("\n  ! Errore di salvataggio: " + e.getMessage());
        }
        InputUtil.premiInvioPerContinuare();
    }

    /**
     * Visualizza le prenotazioni del cliente autenticato.
     *
     * @return la lista delle prenotazioni del cliente
     */
    private List<Prenotazione> mieiPrenotazioni() {
        visualizzatore.stampaTitolo("Le mie prenotazioni");
        List<Prenotazione> mie = gestorePrenotazioni.trovaPerCliente(cliente.getUsername());
        visualizzatore.stampaElencoPrenotazioni(mie);
        if (!mie.isEmpty()
                && InputUtil.leggiConferma("\n  Vuoi vedere il dettaglio di una prenotazione?")) {
            int scelta = InputUtil.leggiInteroNelRange(
                    "  Numero (1-" + mie.size() + "): ", 1, mie.size());
            visualizzatore.stampaDettaglioPrenotazione(mie.get(scelta - 1));
        }
        InputUtil.premiInvioPerContinuare();
        return mie;
    }

    /**
     * Modifica la data di una prenotazione spostandola su un'altra proiezione,
     * a patto che sia la vecchia sia la nuova data di proiezione siano
     * successive alla data odierna.
     */
    private void modificaPrenotazione() {
        visualizzatore.stampaTitolo("Modifica prenotazione (cambio data)");
        List<Prenotazione> mie = gestorePrenotazioni.trovaPerCliente(cliente.getUsername());
        visualizzatore.stampaElencoPrenotazioni(mie);
        if (mie.isEmpty()) {
            InputUtil.premiInvioPerContinuare();
            return;
        }
        int scelta = InputUtil.leggiInteroNelRange(
                "\n  Numero della prenotazione (1-" + mie.size() + "): ", 1, mie.size());
        Prenotazione pr = mie.get(scelta - 1);

        Proiezione vecchia = gestoreProiezioni.trovaPerId(pr.getIdProiezione());
        if (vecchia == null || !vecchia.getDataOra().isAfter(LocalDateTime.now())) {
            System.out.println("\n  ! La data attuale della proiezione deve essere futura.");
            InputUtil.premiInvioPerContinuare();
            return;
        }

        System.out.println("\n  Proiezioni disponibili:");
        List<Proiezione> tutte = gestoreProiezioni.getTutte();
        visualizzatore.stampaElencoProiezioni(tutte);
        int nuovoId = InputUtil.leggiIntero("\n  Id della nuova proiezione: ");
        Proiezione nuova = gestoreProiezioni.trovaPerId(nuovoId);
        if (nuova == null) {
            System.out.println("\n  ! Proiezione inesistente.");
            InputUtil.premiInvioPerContinuare();
            return;
        }
        if (!nuova.getDataOra().isAfter(LocalDateTime.now())) {
            System.out.println("\n  ! Anche la nuova data deve essere futura.");
            InputUtil.premiInvioPerContinuare();
            return;
        }
        try {
            gestorePrenotazioni.cambiaProiezione(pr.getCodice(), nuovoId);
            System.out.println("\n  Prenotazione aggiornata alla nuova data.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("\n  ! " + e.getMessage());
        } catch (IOException e) {
            System.out.println("\n  ! Errore di salvataggio: " + e.getMessage());
        }
        InputUtil.premiInvioPerContinuare();
    }

    /**
     * Elimina una prenotazione del cliente. Secondo le specifiche, la
     * cancellazione e' consentita solo se la data della proiezione e'
     * precedente alla data odierna.
     */
    private void eliminaPrenotazione() {
        visualizzatore.stampaTitolo("Elimina prenotazione");
        List<Prenotazione> mie = gestorePrenotazioni.trovaPerCliente(cliente.getUsername());
        visualizzatore.stampaElencoPrenotazioni(mie);
        if (mie.isEmpty()) {
            InputUtil.premiInvioPerContinuare();
            return;
        }
        int scelta = InputUtil.leggiInteroNelRange(
                "\n  Numero della prenotazione (1-" + mie.size() + "): ", 1, mie.size());
        Prenotazione pr = mie.get(scelta - 1);

        Proiezione p = gestoreProiezioni.trovaPerId(pr.getIdProiezione());
        // Vincolo da specifica: la data della proiezione deve essere precedente a oggi.
        if (p != null && !p.getDataOra().toLocalDate().isBefore(LocalDate.now())) {
            System.out.println("\n  ! La proiezione non e' ancora passata: eliminazione non consentita.");
            InputUtil.premiInvioPerContinuare();
            return;
        }
        if (InputUtil.leggiConferma("\n  Confermi l'eliminazione di " + pr.getCodice() + "?")) {
            try {
                gestorePrenotazioni.elimina(pr.getCodice());
                System.out.println("\n  Prenotazione eliminata.");
            } catch (IOException e) {
                System.out.println("\n  ! Errore di salvataggio: " + e.getMessage());
            }
        }
        InputUtil.premiInvioPerContinuare();
    }
}
