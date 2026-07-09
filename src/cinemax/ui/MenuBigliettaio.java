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
import cinemax.persistence.GestoreUtenti;
import cinemax.util.InputUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MenuBigliettaio {

    /** Gestore delle proiezioni. */
    private final GestoreProiezioni gestoreProiezioni;

    /** Gestore delle prenotazioni. */
    private final GestorePrenotazioni gestorePrenotazioni;

    /** Gestore degli utenti. */
    private final GestoreUtenti gestoreUtenti;

    /** Visualizzatore per la stampa formattata. */
    private final Visualizzatore visualizzatore;

    /** Bigliettaio autenticato. */
    private final Utente bigliettaio;

    /**
     * Costruisce il menu del bigliettaio.
     *
     * @param gestoreProiezioni   gestore delle proiezioni
     * @param gestorePrenotazioni gestore delle prenotazioni
     * @param gestoreUtenti       gestore degli utenti
     * @param visualizzatore      visualizzatore per la stampa
     * @param bigliettaio         bigliettaio autenticato
     */
    public MenuBigliettaio(GestoreProiezioni gestoreProiezioni,
                           GestorePrenotazioni gestorePrenotazioni,
                           GestoreUtenti gestoreUtenti,
                           Visualizzatore visualizzatore,
                           Utente bigliettaio) {
        this.gestoreProiezioni = gestoreProiezioni;
        this.gestorePrenotazioni = gestorePrenotazioni;
        this.gestoreUtenti = gestoreUtenti;
        this.visualizzatore = visualizzatore;
        this.bigliettaio = bigliettaio;
    }

    /**
     * Avvia il ciclo del menu del bigliettaio fino al logout.
     */
    public void esegui() {
        boolean attivo = true;
        while (attivo) {
            visualizzatore.stampaTitolo("Area bigliettaio - " + bigliettaio.getNomeCompleto());
            System.out.println("  1) Prenotazioni di oggi");
            System.out.println("  2) Cerca prenotazione");
            System.out.println("  0) Logout");
            int scelta = InputUtil.leggiInteroNelRange("  Scelta: ", 0, 2);

            switch (scelta) {
                case 1 -> prenotazioniDiOggi();
                case 2 -> cercaPrenotazione();
                case 0 -> attivo = false;
                default -> { }
            }
        }
    }

    /**
     * Visualizza tutte le prenotazioni relative a proiezioni in programma nella
     * data odierna.
     */
    private void prenotazioniDiOggi() {
        visualizzatore.stampaTitolo("Prenotazioni di oggi (" + LocalDate.now() + ")");
        Set<Integer> idOggi = new HashSet<>();
        for (Proiezione p : gestoreProiezioni.getTutte()) {
            if (p.getDataOra().toLocalDate().equals(LocalDate.now())) {
                idOggi.add(p.getId());
            }
        }
        List<Prenotazione> risultati = gestorePrenotazioni.trovaPerProiezioni(idOggi);
        visualizzatore.stampaElencoPrenotazioni(risultati);
        selezionaEVisualizza(risultati);
    }

    /**
     * Ricerca una prenotazione secondo il criterio scelto dal bigliettaio.
     */
    private void cercaPrenotazione() {
        visualizzatore.stampaTitolo("Cerca prenotazione");
        System.out.println("  1) Per codice prenotazione");
        System.out.println("  2) Per nome e cognome del cliente");
        System.out.println("  3) Per titolo del film (anche parziale)");
        System.out.println("  4) Per intervallo di date");
        System.out.println("  0) Annulla");
        int scelta = InputUtil.leggiInteroNelRange("  Scelta: ", 0, 4);

        List<Prenotazione> risultati = new ArrayList<>();
        switch (scelta) {
            case 1 -> {
                String codice = InputUtil.leggiStringa("  Codice: ");
                Prenotazione pr = gestorePrenotazioni.trovaPerCodice(codice);
                if (pr != null) {
                    risultati.add(pr);
                }
            }
            case 2 -> {
                String nome = InputUtil.leggiStringaOpzionale("  Nome: ");
                String cognome = InputUtil.leggiStringaOpzionale("  Cognome: ");
                Set<String> username = new HashSet<>();
                for (Utente u : gestoreUtenti.trovaPerNomeCognome(nome, cognome)) {
                    username.add(u.getUsername().toLowerCase());
                }
                for (Prenotazione pr : gestorePrenotazioni.getTutte()) {
                    if (username.contains(pr.getUsernameCliente().toLowerCase())) {
                        risultati.add(pr);
                    }
                }
            }
            case 3 -> {
                String titolo = InputUtil.leggiStringa("  Titolo (anche parziale): ");
                Set<Integer> idProiezioni = idProiezioniPerTitolo(titolo);
                risultati.addAll(gestorePrenotazioni.trovaPerProiezioni(idProiezioni));
            }
            case 4 -> {
                LocalDate dataDa = InputUtil.leggiDataOpzionale("  Data minima");
                LocalDate dataA = InputUtil.leggiDataOpzionale("  Data massima");
                Set<Integer> idProiezioni = idProiezioniPerIntervallo(dataDa, dataA);
                risultati.addAll(gestorePrenotazioni.trovaPerProiezioni(idProiezioni));
            }
            case 0 -> {
                return;
            }
            default -> { }
        }

        visualizzatore.stampaTitolo("Risultati (" + risultati.size() + ")");
        visualizzatore.stampaElencoPrenotazioni(risultati);
        selezionaEVisualizza(risultati);
    }

    /**
     * Restituisce gli id delle proiezioni il cui titolo contiene il testo
     * indicato.
     *
     * @param titolo titolo (anche parziale)
     * @return l'insieme degli id corrispondenti
     */
    private Set<Integer> idProiezioniPerTitolo(String titolo) {
        Set<Integer> id = new HashSet<>();
        for (Proiezione p : gestoreProiezioni.cerca(titolo, null, null, null, null, null)) {
            id.add(p.getId());
        }
        return id;
    }

    /**
     * Restituisce gli id delle proiezioni comprese nell'intervallo di date.
     *
     * @param dataDa data minima inclusa, oppure {@code null}
     * @param dataA  data massima inclusa, oppure {@code null}
     * @return l'insieme degli id corrispondenti
     */
    private Set<Integer> idProiezioniPerIntervallo(LocalDate dataDa, LocalDate dataA) {
        Set<Integer> id = new HashSet<>();
        for (Proiezione p : gestoreProiezioni.cerca(null, null, dataDa, dataA, null, null)) {
            id.add(p.getId());
        }
        return id;
    }

    /**
     * Permette di selezionare una prenotazione dai risultati e ne mostra il
     * dettaglio.
     *
     * @param risultati lista di prenotazioni tra cui scegliere
     */
    private void selezionaEVisualizza(List<Prenotazione> risultati) {
        if (risultati.isEmpty()) {
            InputUtil.premiInvioPerContinuare();
            return;
        }
        if (InputUtil.leggiConferma("\n  Vuoi visualizzare il dettaglio di una prenotazione?")) {
            int scelta = InputUtil.leggiInteroNelRange(
                    "  Numero (1-" + risultati.size() + "): ", 1, risultati.size());
            visualizzatore.stampaDettaglioPrenotazione(risultati.get(scelta - 1));
        }
        InputUtil.premiInvioPerContinuare();
    }
}
