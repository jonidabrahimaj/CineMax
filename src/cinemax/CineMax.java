/*
 * ============================================================================
 *  CineMax - Laboratorio Interdisciplinare A
 *  Autori:
 *    - <Jonida Brahimaj> - Matricola <759037> - Sede <COMO>
 *    - <Renee Angelica Cabigting> - Matricola <756997> - Sede <COMO>
 *  File: Prenotazione.java
 * ============================================================================
 */
package cinemax;

import cinemax.model.Proiezione;
import cinemax.model.Utente;
import cinemax.persistence.GestorePrenotazioni;
import cinemax.persistence.GestoreProiezioni;
import cinemax.persistence.GestoreUtenti;
import cinemax.ui.FunzioniPubbliche;
import cinemax.ui.MenuBigliettaio;
import cinemax.ui.MenuCliente;
import cinemax.ui.MenuProiezionista;
import cinemax.ui.Visualizzatore;
import cinemax.util.InputUtil;

import java.io.IOException;
import java.util.List;


public class CineMax {

    /** Gestore delle proiezioni. */
    private final GestoreProiezioni gestoreProiezioni;

    /** Gestore degli utenti. */
    private final GestoreUtenti gestoreUtenti;

    /** Gestore delle prenotazioni. */
    private final GestorePrenotazioni gestorePrenotazioni;

    /** Visualizzatore per la stampa formattata. */
    private final Visualizzatore visualizzatore;

    /** Funzioni pubbliche (ricerca/selezione/registrazione). */
    private final FunzioniPubbliche funzioniPubbliche;

    /**
     * Costruisce l'applicazione istanziando i gestori e i componenti di
     * interfaccia.
     */
    public CineMax() {
        this.gestoreProiezioni = new GestoreProiezioni();
        this.gestoreUtenti = new GestoreUtenti();
        this.gestorePrenotazioni = new GestorePrenotazioni();
        this.visualizzatore = new Visualizzatore(
                gestoreProiezioni, gestorePrenotazioni, gestoreUtenti);
        this.funzioniPubbliche = new FunzioniPubbliche(
                gestoreProiezioni, gestoreUtenti, visualizzatore);
    }

    /**
     * Punto di ingresso dell'applicazione.
     *
     * @param args argomenti da riga di comando (non utilizzati)
     */
    public static void main(String[] args) {
        CineMax app = new CineMax();
        app.avvia();
    }

    /**
     * Carica i dati da file e avvia il ciclo del menu principale.
     */
    public void avvia() {
        try {
            gestoreUtenti.carica();
            gestoreProiezioni.carica();
            gestorePrenotazioni.carica();
        } catch (IOException e) {
            System.out.println("Errore nel caricamento dei dati: " + e.getMessage());
            return;
        }

        System.out.println("============================================================");
        System.out.println("                 BENVENUTO IN CINEMAX                       ");
        System.out.println("       Cinema monosala - " + Proiezione.CAPACITA_SALA + " posti                        ");
        System.out.println("============================================================");

        menuPrincipale();
        System.out.println("\nArrivederci!");
    }

    /**
     * Mostra e gestisce il menu iniziale fino all'uscita.
     */
    private void menuPrincipale() {
        boolean attivo = true;
        while (attivo) {
            visualizzatore.stampaTitolo("Menu principale");
            System.out.println("  1) Login");
            System.out.println("  2) Registrati come cliente");
            System.out.println("  3) Continua come guest");
            System.out.println("  0) Esci");
            int scelta = InputUtil.leggiInteroNelRange("  Scelta: ", 0, 3);

            switch (scelta) {
                case 1 -> login();
                case 2 -> funzioniPubbliche.registraCliente();
                case 3 -> guest();
                case 0 -> attivo = false;
                default -> { }
            }
        }
    }

    /**
     * Gestisce l'autenticazione e instrada l'utente verso il menu del proprio
     * ruolo.
     */
    private void login() {
        visualizzatore.stampaTitolo("Login");
        String username = InputUtil.leggiStringa("  Username: ");
        String password = InputUtil.leggiStringa("  Password: ");
        Utente utente = gestoreUtenti.login(username, password);
        if (utente == null) {
            System.out.println("\n  ! Credenziali non valide.");
            InputUtil.premiInvioPerContinuare();
            return;
        }
        System.out.println("\n  Accesso effettuato come " + utente.getRuolo() + ".");
        instradaPerRuolo(utente);
    }

    /**
     * Instrada un utente autenticato verso il menu corrispondente al suo ruolo.
     *
     * @param utente l'utente autenticato
     */
    private void instradaPerRuolo(Utente utente) {
        switch (utente.getRuolo()) {
            case CLIENTE -> new MenuCliente(gestoreProiezioni, gestorePrenotazioni,
                    visualizzatore, funzioniPubbliche, utente).esegui();
            case PROIEZIONISTA -> new MenuProiezionista(gestoreProiezioni,
                    gestorePrenotazioni, visualizzatore, utente).esegui();
            case BIGLIETTAIO -> new MenuBigliettaio(gestoreProiezioni,
                    gestorePrenotazioni, gestoreUtenti, visualizzatore, utente).esegui();
            default -> System.out.println("  Ruolo non riconosciuto.");
        }
    }

    /**
     * Gestisce l'accesso come guest: l'utente indica il nome (anche parziale)
     * di un film e puo' consultare le proiezioni e i loro dettagli, oppure
     * effettuare ulteriori ricerche.
     */
    private void guest() {
        visualizzatore.stampaTitolo("Accesso guest");
        String titolo = InputUtil.leggiStringa("  Nome del film (anche parziale): ");
        List<Proiezione> risultati = gestoreProiezioni.cerca(
                titolo, null, null, null, null, null);

        visualizzatore.stampaTitolo("Proiezioni per \"" + titolo + "\" (" + risultati.size() + ")");
        visualizzatore.stampaElencoProiezioni(risultati);
        funzioniPubbliche.visualizzaProiezione(risultati);

        boolean continua = true;
        while (continua) {
            visualizzatore.stampaTitolo("Menu guest");
            System.out.println("  1) Nuova ricerca proiezioni");
            System.out.println("  2) Registrati come cliente");
            System.out.println("  0) Torna al menu principale");
            int scelta = InputUtil.leggiInteroNelRange("  Scelta: ", 0, 2);
            switch (scelta) {
                case 1 -> {
                    List<Proiezione> trovate = funzioniPubbliche.cercaProiezione();
                    funzioniPubbliche.visualizzaProiezione(trovate);
                }
                case 2 -> funzioniPubbliche.registraCliente();
                case 0 -> continua = false;
                default -> { }
            }
        }
    }
}
