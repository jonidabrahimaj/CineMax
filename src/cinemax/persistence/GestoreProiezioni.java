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

import cinemax.model.Film;
import cinemax.model.Proiezione;
import cinemax.util.CsvUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GestoreProiezioni {

    /** Percorso predefinito del file delle proiezioni. */
    public static final String PERCORSO_DEFAULT = "data/proiezioni.csv";

    /** Intestazione del file CSV. */
    private static final String INTESTAZIONE =
            "id,titolo,genere,regista,anno,durataMinuti,etaMinima,dataOra,costoBiglietto";

    /** Percorso del file gestito. */
    private final Path percorso;

    /** Elenco delle proiezioni in memoria. */
    private final List<Proiezione> proiezioni;

    /**
     * Crea il gestore usando il percorso predefinito.
     */
    public GestoreProiezioni() {
        this(PERCORSO_DEFAULT);
    }

    /**
     * Crea il gestore su un percorso file specificato.
     *
     * @param percorsoFile percorso del file CSV delle proiezioni
     */
    public GestoreProiezioni(String percorsoFile) {
        this.percorso = Path.of(percorsoFile);
        this.proiezioni = new ArrayList<>();
    }

    /**
     * Carica le proiezioni dal file CSV in memoria. Le righe malformate
     * vengono ignorate segnalandole sullo standard error.
     *
     * @throws IOException se si verifica un errore di lettura del file
     */
    public void carica() throws IOException {
        proiezioni.clear();
        if (!Files.exists(percorso)) {
            return;
        }
        try (BufferedReader br = Files.newBufferedReader(percorso, StandardCharsets.UTF_8)) {
            String intestazione = br.readLine();
            // Riconosce automaticamente il formato dal nome della prima colonna:
            // - formato interno  -> la prima colonna e' "id"
            // - formato docente   -> la prima colonna e' "data_ora_proiezione"
            boolean conId = intestazione != null
                    && intestazione.trim().toLowerCase().startsWith("id,");

            String riga;
            int idAutomatico = 0;
            while ((riga = br.readLine()) != null) {
                if (riga.isBlank()) {
                    continue;
                }
                try {
                    Proiezione p = conId ? daRigaCsvConId(riga)
                                         : daRigaCsvSenzaId(riga, ++idAutomatico);
                    proiezioni.add(p);
                } catch (RuntimeException e) {
                    System.err.println("Riga proiezione ignorata (malformata): " + riga);
                }
            }
        }
    }

    /**
     * Salva le proiezioni sul file CSV, sovrascrivendone il contenuto.
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
            for (Proiezione p : proiezioni) {
                bw.write(aRigaCsv(p));
                bw.newLine();
            }
        }
    }

    /**
     * Ricerca le proiezioni secondo una combinazione di criteri. Ogni criterio
     * {@code null} (o stringa vuota) viene ignorato; passandoli tutti a
     * {@code null} si ottiene l'intero palinsesto.
     *
     * @param titolo   titolo (anche parziale) del film, oppure {@code null}
     * @param genere   genere del film, oppure {@code null}
     * @param dataDa   data minima inclusa della proiezione, oppure {@code null}
     * @param dataA    data massima inclusa della proiezione, oppure {@code null}
     * @param costoMin costo minimo del biglietto, oppure {@code null}
     * @param costoMax costo massimo del biglietto, oppure {@code null}
     * @return la lista delle proiezioni corrispondenti, ordinate per data e ora
     */
    public List<Proiezione> cerca(String titolo, String genere,
                                  LocalDate dataDa, LocalDate dataA,
                                  Double costoMin, Double costoMax) {
        List<Proiezione> risultati = new ArrayList<>();
        for (Proiezione p : proiezioni) {
            if (soddisfaCriteri(p, titolo, genere, dataDa, dataA, costoMin, costoMax)) {
                risultati.add(p);
            }
        }
        risultati.sort(Comparator.comparing(Proiezione::getDataOra));
        return risultati;
    }

    /**
     * Verifica se una proiezione soddisfa tutti i criteri non nulli.
     *
     * @param p        la proiezione da valutare
     * @param titolo   titolo parziale, oppure {@code null}
     * @param genere   genere, oppure {@code null}
     * @param dataDa   data minima inclusa, oppure {@code null}
     * @param dataA    data massima inclusa, oppure {@code null}
     * @param costoMin costo minimo, oppure {@code null}
     * @param costoMax costo massimo, oppure {@code null}
     * @return {@code true} se la proiezione soddisfa tutti i criteri
     */
    private boolean soddisfaCriteri(Proiezione p, String titolo, String genere,
                                    LocalDate dataDa, LocalDate dataA,
                                    Double costoMin, Double costoMax) {
        Film f = p.getFilm();
        if (titolo != null && !titolo.isBlank()
                && !f.getTitolo().toLowerCase().contains(titolo.trim().toLowerCase())) {
            return false;
        }
        if (genere != null && !genere.isBlank()
                && !f.getGenere().toLowerCase().contains(genere.trim().toLowerCase())) {
            return false;
        }
        LocalDate dataProiezione = p.getDataOra().toLocalDate();
        if (dataDa != null && dataProiezione.isBefore(dataDa)) {
            return false;
        }
        if (dataA != null && dataProiezione.isAfter(dataA)) {
            return false;
        }
        if (costoMin != null && p.getCostoBiglietto() < costoMin) {
            return false;
        }
        if (costoMax != null && p.getCostoBiglietto() > costoMax) {
            return false;
        }
        return true;
    }

    /**
     * Cerca una proiezione dato il suo identificativo.
     *
     * @param id l'identificativo della proiezione
     * @return la proiezione corrispondente, oppure {@code null} se non esiste
     */
    public Proiezione trovaPerId(int id) {
        for (Proiezione p : proiezioni) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    /**
     * Aggiunge una nuova proiezione al palinsesto, assegnandole un nuovo
     * identificativo univoco. Viene rifiutata se si sovrappone temporalmente
     * a una proiezione gia' esistente (la sala e' unica).
     *
     * @param film           film da proiettare
     * @param dataOra        data e ora della proiezione
     * @param costoBiglietto costo del biglietto
     * @return la proiezione creata e salvata
     * @throws IllegalStateException se la proiezione si sovrappone a un'altra
     * @throws IOException           se il salvataggio fallisce
     */
    public Proiezione aggiungi(Film film, LocalDateTime dataOra, double costoBiglietto)
            throws IOException {
        Proiezione nuova = new Proiezione(generaNuovoId(), film, dataOra, costoBiglietto);
        Proiezione conflitto = trovaSovrapposizione(nuova);
        if (conflitto != null) {
            throw new IllegalStateException(
                    "La proiezione si sovrappone con: " + conflitto);
        }
        proiezioni.add(nuova);
        salva();
        return nuova;
    }

    /**
     * Aggiorna i dati di una proiezione esistente, verificando che la nuova
     * collocazione temporale non si sovrapponga ad altre proiezioni.
     *
     * @param proiezione la proiezione con i dati gia' modificati
     * @throws IllegalStateException se la nuova collocazione si sovrappone a
     *                               un'altra proiezione
     * @throws IOException           se il salvataggio fallisce
     */
    public void aggiorna(Proiezione proiezione) throws IOException {
        Proiezione conflitto = trovaSovrapposizione(proiezione);
        if (conflitto != null && conflitto.getId() != proiezione.getId()) {
            throw new IllegalStateException(
                    "La proiezione si sovrappone con: " + conflitto);
        }
        // L'oggetto e' gia' presente nella lista (stesso riferimento),
        // quindi e' sufficiente salvare lo stato aggiornato.
        salva();
    }

    /**
     * Elimina una proiezione dato il suo identificativo.
     *
     * @param id l'identificativo della proiezione da eliminare
     * @return {@code true} se la proiezione esisteva ed e' stata rimossa
     * @throws IOException se il salvataggio fallisce
     */
    public boolean elimina(int id) throws IOException {
        Proiezione p = trovaPerId(id);
        if (p == null) {
            return false;
        }
        proiezioni.remove(p);
        salva();
        return true;
    }

    /**
     * Trova, se esiste, una proiezione esistente che si sovrappone
     * temporalmente a quella indicata.
     *
     * @param candidata la proiezione da verificare
     * @return la prima proiezione in conflitto, oppure {@code null}
     */
    private Proiezione trovaSovrapposizione(Proiezione candidata) {
        for (Proiezione p : proiezioni) {
            if (p.getId() != candidata.getId() && p.siSovrappone(candidata)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Calcola un nuovo identificativo univoco (massimo esistente + 1).
     *
     * @return il nuovo identificativo
     */
    public int generaNuovoId() {
        int max = 0;
        for (Proiezione p : proiezioni) {
            if (p.getId() > max) {
                max = p.getId();
            }
        }
        return max + 1;
    }

    /**
     * @return una copia dell'elenco di tutte le proiezioni, ordinate per data
     */
    public List<Proiezione> getTutte() {
        List<Proiezione> copia = new ArrayList<>(proiezioni);
        copia.sort(Comparator.comparing(Proiezione::getDataOra));
        return copia;
    }

    // ----------------------------------------------------------------
    //  Conversione da/verso il formato CSV
    // ----------------------------------------------------------------

    /**
     * Costruisce una proiezione dal formato interno con identificativo:
     * <pre>id,titolo,genere,regista,anno,durataMinuti,etaMinima,dataOra,costoBiglietto</pre>
     *
     * @param riga la riga del file
     * @return la proiezione corrispondente
     */
    private Proiezione daRigaCsvConId(String riga) {
        List<String> c = CsvUtil.parseRiga(riga);
        if (c.size() < 9) {
            throw new IllegalArgumentException("Numero di campi insufficiente");
        }
        int id = Integer.parseInt(c.get(0).trim());
        String titolo = c.get(1);
        String genere = c.get(2);
        String regista = c.get(3);
        int anno = Integer.parseInt(c.get(4).trim());
        int durata = Integer.parseInt(c.get(5).trim());
        int etaMinima = Integer.parseInt(c.get(6).trim());
        LocalDateTime dataOra = parseDataOra(c.get(7).trim());
        double costo = Double.parseDouble(c.get(8).trim().replace(',', '.'));

        Film film = new Film(titolo, genere, regista, anno, durata, etaMinima);
        return new Proiezione(id, film, dataOra, costo);
    }

    /**
     * Costruisce una proiezione dal formato fornito dal docente (senza
     * identificativo, con la data come prima colonna):
     * <pre>data_ora_proiezione,titolo_film,genere,regista,anno,durata_minuti,eta_minima,prezzo_biglietto</pre>
     * L'identificativo viene assegnato automaticamente.
     *
     * @param riga la riga del file
     * @param id   identificativo da assegnare alla proiezione
     * @return la proiezione corrispondente
     */
    private Proiezione daRigaCsvSenzaId(String riga, int id) {
        List<String> c = CsvUtil.parseRiga(riga);
        if (c.size() < 8) {
            throw new IllegalArgumentException("Numero di campi insufficiente");
        }
        LocalDateTime dataOra = parseDataOra(c.get(0).trim());
        String titolo = c.get(1);
        String genere = c.get(2);
        String regista = c.get(3);
        int anno = Integer.parseInt(c.get(4).trim());
        int durata = Integer.parseInt(c.get(5).trim());
        int etaMinima = Integer.parseInt(c.get(6).trim());
        double costo = Double.parseDouble(c.get(7).trim().replace(',', '.'));

        Film film = new Film(titolo, genere, regista, anno, durata, etaMinima);
        return new Proiezione(id, film, dataOra, costo);
    }

    /**
     * Interpreta una data e ora accettando sia il formato con i secondi
     * ({@code yyyy-MM-dd HH:mm:ss}) sia quello senza ({@code yyyy-MM-dd HH:mm}).
     *
     * @param testo il valore testuale della data e ora
     * @return la data e ora corrispondente
     */
    private LocalDateTime parseDataOra(String testo) {
        try {
            return LocalDateTime.parse(testo, Proiezione.FORMATO_DATA_ORA_SEC);
        } catch (java.time.format.DateTimeParseException e) {
            return LocalDateTime.parse(testo, Proiezione.FORMATO_DATA_ORA);
        }
    }

    /**
     * Converte una proiezione in una riga CSV.
     *
     * @param p la proiezione da convertire
     * @return la riga CSV corrispondente
     */
    private String aRigaCsv(Proiezione p) {
        Film f = p.getFilm();
        return CsvUtil.componiRiga(
                String.valueOf(p.getId()),
                f.getTitolo(),
                f.getGenere(),
                f.getRegista(),
                String.valueOf(f.getAnno()),
                String.valueOf(f.getDurataMinuti()),
                String.valueOf(f.getEtaMinima()),
                p.getDataOra().format(Proiezione.FORMATO_DATA_ORA),
                String.format(java.util.Locale.US, "%.2f", p.getCostoBiglietto()));
    }
}
