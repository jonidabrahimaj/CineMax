/*
 * ============================================================================
 *  CineMax - Laboratorio Interdisciplinare A
 *  Autori:
 *    - <Jonida Brahimaj> - Matricola <759037> - Sede <COMO>
 *    - <Renee Angelica Cabigting> - Matricola <756997> - Sede <COMO>
 *  File: Prenotazione.java
 * ============================================================================
 */
package cinemax.persistence;

import cinemax.model.Prenotazione;
import cinemax.model.Proiezione;
import cinemax.util.CsvUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GestorePrenotazioni {

    /** Percorso predefinito del file delle prenotazioni. */
    public static final String PERCORSO_DEFAULT = "data/prenotazioni.csv";

    /** Intestazione del file CSV. */
    private static final String INTESTAZIONE =
            "codice,usernameCliente,idProiezione,numeroBiglietti";

    /** Prefisso usato per i codici di prenotazione. */
    private static final String PREFISSO_CODICE = "PRN-";

    /** Percorso del file gestito. */
    private final Path percorso;

    /** Elenco delle prenotazioni in memoria. */
    private final List<Prenotazione> prenotazioni;

    /**
     * Crea il gestore usando il percorso predefinito.
     */
    public GestorePrenotazioni() {
        this(PERCORSO_DEFAULT);
    }

    /**
     * Crea il gestore su un percorso file specificato.
     *
     * @param percorsoFile percorso del file CSV delle prenotazioni
     */
    public GestorePrenotazioni(String percorsoFile) {
        this.percorso = Path.of(percorsoFile);
        this.prenotazioni = new ArrayList<>();
    }

    /**
     * Carica le prenotazioni dal file CSV in memoria. Le righe malformate
     * vengono ignorate segnalandole sullo standard error.
     *
     * @throws IOException se si verifica un errore di lettura del file
     */
    public void carica() throws IOException {
        prenotazioni.clear();
        if (!Files.exists(percorso)) {
            return;
        }
        try (BufferedReader br = Files.newBufferedReader(percorso, StandardCharsets.UTF_8)) {
            String riga;
            boolean primaRiga = true;
            while ((riga = br.readLine()) != null) {
                if (primaRiga) {
                    primaRiga = false;
                    continue;
                }
                if (riga.isBlank()) {
                    continue;
                }
                try {
                    prenotazioni.add(daRigaCsv(riga));
                } catch (RuntimeException e) {
                    System.err.println("Riga prenotazione ignorata (malformata): " + riga);
                }
            }
        }
    }

    /**
     * Salva le prenotazioni sul file CSV, sovrascrivendone il contenuto.
     *
     * @throws IOException se si verifica un errore di scrittura del file
     */
    public void salva() throws IOException {
        if (percorso.getParent() != null) {
            Files.createDirectories(percorso.getParent());
        }
        try (BufferedWriter bw = Files.newBufferedWriter(percorso, StandardCharsets.UTF_8)) {
            bw.write(INTESTAZIONE);
            bw.newLine();
            for (Prenotazione p : prenotazioni) {
                bw.write(aRigaCsv(p));
                bw.newLine();
            }
        }
    }

    /**
     * Calcola il numero di posti gia' prenotati per una determinata proiezione.
     *
     * @param idProiezione identificativo della proiezione
     * @return la somma dei biglietti prenotati per quella proiezione
     */
    public int postiPrenotati(int idProiezione) {
        int totale = 0;
        for (Prenotazione p : prenotazioni) {
            if (p.getIdProiezione() == idProiezione) {
                totale += p.getNumeroBiglietti();
            }
        }
        return totale;
    }

    /**
     * Calcola il numero di posti ancora liberi per una proiezione, in base
     * alla capacita' della sala.
     *
     * @param idProiezione identificativo della proiezione
     * @return i posti liberi (capacita' sala meno posti prenotati)
     */
    public int postiLiberi(int idProiezione) {
        return Proiezione.CAPACITA_SALA - postiPrenotati(idProiezione);
    }

    /**
     * Verifica se esiste almeno una prenotazione per una data proiezione (utile
     * per impedire modifica o eliminazione della proiezione stessa).
     *
     * @param idProiezione identificativo della proiezione
     * @return {@code true} se esiste almeno una prenotazione
     */
    public boolean esistonoPrenotazioniPer(int idProiezione) {
        for (Prenotazione p : prenotazioni) {
            if (p.getIdProiezione() == idProiezione) {
                return true;
            }
        }
        return false;
    }

    /**
     * Crea una nuova prenotazione, a patto che vi siano posti sufficienti.
     * Genera automaticamente un codice univoco e salva su file.
     *
     * @param usernameCliente username del cliente
     * @param idProiezione    identificativo della proiezione
     * @param numeroBiglietti numero di posti richiesti (maggiore di zero)
     * @return la prenotazione creata
     * @throws IllegalArgumentException se il numero di biglietti non e' valido
     * @throws IllegalStateException    se i posti disponibili non sono sufficienti
     * @throws IOException              se il salvataggio fallisce
     */
    public Prenotazione crea(String usernameCliente, int idProiezione, int numeroBiglietti)
            throws IOException {
        if (numeroBiglietti <= 0) {
            throw new IllegalArgumentException("Il numero di biglietti deve essere positivo");
        }
        int liberi = postiLiberi(idProiezione);
        if (numeroBiglietti > liberi) {
            throw new IllegalStateException(
                    "Posti insufficienti: richiesti " + numeroBiglietti + ", liberi " + liberi);
        }
        Prenotazione nuova = new Prenotazione(
                generaCodice(), usernameCliente, idProiezione, numeroBiglietti);
        prenotazioni.add(nuova);
        salva();
        return nuova;
    }

    /**
     * Sposta una prenotazione su una nuova proiezione (cambio data),
     * verificando la disponibilita' di posti sulla nuova proiezione.
     *
     * @param codice           codice della prenotazione
     * @param nuovoIdProiezione identificativo della nuova proiezione
     * @throws IllegalArgumentException se la prenotazione non esiste
     * @throws IllegalStateException    se i posti sulla nuova proiezione non bastano
     * @throws IOException              se il salvataggio fallisce
     */
    public void cambiaProiezione(String codice, int nuovoIdProiezione) throws IOException {
        Prenotazione p = trovaPerCodice(codice);
        if (p == null) {
            throw new IllegalArgumentException("Prenotazione inesistente: " + codice);
        }
        int liberi = postiLiberi(nuovoIdProiezione);
        if (p.getNumeroBiglietti() > liberi) {
            throw new IllegalStateException(
                    "Posti insufficienti sulla nuova proiezione: liberi " + liberi);
        }
        p.setIdProiezione(nuovoIdProiezione);
        salva();
    }

    /**
     * Elimina una prenotazione dato il suo codice.
     *
     * @param codice codice della prenotazione
     * @return {@code true} se la prenotazione esisteva ed e' stata rimossa
     * @throws IOException se il salvataggio fallisce
     */
    public boolean elimina(String codice) throws IOException {
        Prenotazione p = trovaPerCodice(codice);
        if (p == null) {
            return false;
        }
        prenotazioni.remove(p);
        salva();
        return true;
    }

    /**
     * Cerca una prenotazione dato il suo codice univoco.
     *
     * @param codice il codice da cercare
     * @return la prenotazione corrispondente, oppure {@code null}
     */
    public Prenotazione trovaPerCodice(String codice) {
        if (codice == null) {
            return null;
        }
        for (Prenotazione p : prenotazioni) {
            if (p.getCodice().equalsIgnoreCase(codice.trim())) {
                return p;
            }
        }
        return null;
    }

    /**
     * Restituisce tutte le prenotazioni di un determinato cliente.
     *
     * @param usernameCliente username del cliente
     * @return la lista delle prenotazioni del cliente
     */
    public List<Prenotazione> trovaPerCliente(String usernameCliente) {
        List<Prenotazione> risultati = new ArrayList<>();
        if (usernameCliente == null) {
            return risultati;
        }
        for (Prenotazione p : prenotazioni) {
            if (p.getUsernameCliente().equalsIgnoreCase(usernameCliente.trim())) {
                risultati.add(p);
            }
        }
        return risultati;
    }

    /**
     * Restituisce tutte le prenotazioni relative a un insieme di proiezioni
     * (utile per le ricerche per titolo o per intervallo di date, dove le
     * proiezioni vengono prima filtrate dal gestore delle proiezioni).
     *
     * @param idProiezioni insieme degli identificativi di proiezione
     * @return la lista delle prenotazioni corrispondenti
     */
    public List<Prenotazione> trovaPerProiezioni(Set<Integer> idProiezioni) {
        List<Prenotazione> risultati = new ArrayList<>();
        if (idProiezioni == null || idProiezioni.isEmpty()) {
            return risultati;
        }
        for (Prenotazione p : prenotazioni) {
            if (idProiezioni.contains(p.getIdProiezione())) {
                risultati.add(p);
            }
        }
        return risultati;
    }

    /**
     * @return una copia dell'elenco di tutte le prenotazioni
     */
    public List<Prenotazione> getTutte() {
        return new ArrayList<>(prenotazioni);
    }

    /**
     * Genera un nuovo codice di prenotazione univoco nella forma
     * {@code PRN-XXXX} (numero progressivo basato sul massimo gia' presente).
     *
     * @return il nuovo codice univoco
     */
    private String generaCodice() {
        int max = 0;
        for (Prenotazione p : prenotazioni) {
            String codice = p.getCodice();
            if (codice != null && codice.startsWith(PREFISSO_CODICE)) {
                try {
                    int n = Integer.parseInt(codice.substring(PREFISSO_CODICE.length()));
                    if (n > max) {
                        max = n;
                    }
                } catch (NumberFormatException ignored) {
                    // codice in formato non standard: ignorato nel calcolo
                }
            }
        }
        return String.format("%s%04d", PREFISSO_CODICE, max + 1);
    }

    // ----------------------------------------------------------------
    //  Conversione da/verso il formato CSV
    // ----------------------------------------------------------------

    /**
     * Costruisce una prenotazione da una riga CSV.
     *
     * @param riga la riga del file
     * @return la prenotazione corrispondente
     */
    private Prenotazione daRigaCsv(String riga) {
        List<String> c = CsvUtil.parseRiga(riga);
        if (c.size() < 4) {
            throw new IllegalArgumentException("Numero di campi insufficiente");
        }
        String codice = c.get(0);
        String username = c.get(1);
        int idProiezione = Integer.parseInt(c.get(2).trim());
        int numeroBiglietti = Integer.parseInt(c.get(3).trim());
        return new Prenotazione(codice, username, idProiezione, numeroBiglietti);
    }

    /**
     * Converte una prenotazione in una riga CSV.
     *
     * @param p la prenotazione da convertire
     * @return la riga CSV corrispondente
     */
    private String aRigaCsv(Prenotazione p) {
        return CsvUtil.componiRiga(
                p.getCodice(),
                p.getUsernameCliente(),
                String.valueOf(p.getIdProiezione()),
                String.valueOf(p.getNumeroBiglietti()));
    }
}
