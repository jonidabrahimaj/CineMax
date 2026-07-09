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

import cinemax.model.Proiezione;
import cinemax.persistence.GestoreProiezioni;
import cinemax.persistence.GestoreUtenti;
import cinemax.util.InputUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;


public class FunzioniPubbliche {

    /** Gestore delle proiezioni. */
    private final GestoreProiezioni gestoreProiezioni;

    /** Gestore degli utenti. */
    private final GestoreUtenti gestoreUtenti;

    /** Visualizzatore per la stampa formattata. */
    private final Visualizzatore visualizzatore;

    /**
     * Costruisce il gestore delle funzioni pubbliche.
     *
     * @param gestoreProiezioni gestore delle proiezioni
     * @param gestoreUtenti     gestore degli utenti
     * @param visualizzatore    visualizzatore per la stampa
     */
    public FunzioniPubbliche(GestoreProiezioni gestoreProiezioni,
                             GestoreUtenti gestoreUtenti,
                             Visualizzatore visualizzatore) {
        this.gestoreProiezioni = gestoreProiezioni;
        this.gestoreUtenti = gestoreUtenti;
        this.visualizzatore = visualizzatore;
    }

    /**
     * Esegue una ricerca interattiva di proiezioni combinando i criteri
     * (titolo, genere, intervallo di date, intervallo di costo). I criteri
     * lasciati vuoti vengono ignorati.
     *
     * @return la lista delle proiezioni trovate (ordinate per data)
     */
    public List<Proiezione> cercaProiezione() {
        visualizzatore.stampaTitolo("Ricerca proiezioni");
        System.out.println("  Lasciare vuoto un criterio per ignorarlo.\n");

        String titolo = InputUtil.leggiStringaOpzionale("  Titolo (anche parziale): ");
        String genere = InputUtil.leggiStringaOpzionale("  Genere: ");
        LocalDate dataDa = InputUtil.leggiDataOpzionale("  Data minima");
        LocalDate dataA = InputUtil.leggiDataOpzionale("  Data massima");
        Double costoMin = InputUtil.leggiDecimaleOpzionale("  Costo minimo");
        Double costoMax = InputUtil.leggiDecimaleOpzionale("  Costo massimo");

        List<Proiezione> risultati = gestoreProiezioni.cerca(
                titolo, genere, dataDa, dataA, costoMin, costoMax);

        visualizzatore.stampaTitolo("Risultati (" + risultati.size() + ")");
        visualizzatore.stampaElencoProiezioni(risultati);
        return risultati;
    }

    /**
     * Permette di selezionare una proiezione da una lista (tramite numero di
     * elenco) e ne visualizza il dettaglio.
     *
     * @param risultati la lista di proiezioni tra cui scegliere
     * @return la proiezione selezionata, oppure {@code null} se la lista e'
     *         vuota o l'utente annulla
     */
    public Proiezione visualizzaProiezione(List<Proiezione> risultati) {
        if (risultati.isEmpty()) {
            return null;
        }
        if (!InputUtil.leggiConferma("\n  Vuoi visualizzare il dettaglio di una proiezione?")) {
            return null;
        }
        int scelta = InputUtil.leggiInteroNelRange(
                "  Numero della proiezione (1-" + risultati.size() + "): ",
                1, risultati.size());
        Proiezione p = risultati.get(scelta - 1);
        visualizzatore.stampaDettaglioProiezione(p);
        return p;
    }

    /**
     * Registra un nuovo cliente raccogliendo tutti i suoi dati da terminale.
     *
     * @return {@code true} se la registrazione e' andata a buon fine
     */
    public boolean registraCliente() {
        visualizzatore.stampaTitolo("Registrazione nuovo cliente");
        String nome = InputUtil.leggiStringa("  Nome: ");
        String cognome = InputUtil.leggiStringa("  Cognome: ");
        String username = InputUtil.leggiStringa("  Username: ");
        String password = InputUtil.leggiStringa("  Password: ");
        LocalDate dataNascita = InputUtil.leggiDataOpzionale("  Data di nascita");
        String domicilio = InputUtil.leggiStringa("  Domicilio: ");

        try {
            gestoreUtenti.registraCliente(
                    nome, cognome, username, password, dataNascita, domicilio);
            System.out.println("\n  Registrazione completata. Ora puoi effettuare il login.");
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println("\n  ! " + e.getMessage());
            return false;
        } catch (IOException e) {
            System.out.println("\n  ! Errore durante il salvataggio: " + e.getMessage());
            return false;
        }
    }
}
