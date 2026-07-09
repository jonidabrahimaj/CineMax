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

import cinemax.model.Ruolo;
import cinemax.model.Utente;
import cinemax.util.CsvUtil;
import cinemax.util.PasswordUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class GestoreUtenti {

    /** Percorso predefinito del file degli utenti. */
    public static final String PERCORSO_DEFAULT = "data/utenti.csv";

    /** Intestazione del file CSV. */
    private static final String INTESTAZIONE =
            "nome,cognome,username,passwordHash,dataNascita,domicilio,ruolo";

    /** Percorso del file gestito. */
    private final Path percorso;

    /** Elenco degli utenti in memoria. */
    private final List<Utente> utenti;

    /**
     * Crea il gestore usando il percorso predefinito.
     */
    public GestoreUtenti() {
        this(PERCORSO_DEFAULT);
    }

    /**
     * Crea il gestore su un percorso file specificato.
     *
     * @param percorsoFile percorso del file CSV degli utenti
     */
    public GestoreUtenti(String percorsoFile) {
        this.percorso = Path.of(percorsoFile);
        this.utenti = new ArrayList<>();
    }

    /**
     * Carica gli utenti dal file CSV in memoria. Le righe malformate vengono
     * ignorate segnalandole sullo standard error.
     *
     * @throws IOException se si verifica un errore di lettura del file
     */
    public void carica() throws IOException {
        utenti.clear();
        if (!Files.exists(percorso)) {
            return; // nessun file: si parte da elenco vuoto
        }
        try (BufferedReader br = Files.newBufferedReader(percorso, StandardCharsets.UTF_8)) {
            String riga;
            boolean primaRiga = true;
            while ((riga = br.readLine()) != null) {
                if (primaRiga) {
                    primaRiga = false;
                    continue; // salta l'intestazione
                }
                if (riga.isBlank()) {
                    continue;
                }
                try {
                    utenti.add(daRigaCsv(riga));
                } catch (RuntimeException e) {
                    System.err.println("Riga utente ignorata (malformata): " + riga);
                }
            }
        }
    }

    /**
     * Salva l'elenco degli utenti sul file CSV, sovrascrivendone il contenuto.
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
            for (Utente u : utenti) {
                bw.write(aRigaCsv(u));
                bw.newLine();
            }
        }
    }

    /**
     * Tenta l'autenticazione di un utente.
     *
     * @param username username inserito
     * @param password password in chiaro inserita
     * @return l'utente autenticato, oppure {@code null} se le credenziali non
     *         sono valide
     */
    public Utente login(String username, String password) {
        Utente u = trovaPerUsername(username);
        if (u == null) {
            return null;
        }
        return PasswordUtil.verifica(password, u.getPasswordHash()) ? u : null;
    }

    /**
     * Registra un nuovo cliente, cifrandone la password e salvando subito su
     * file. Lo username deve essere univoco.
     *
     * @param nome              nome del cliente
     * @param cognome           cognome del cliente
     * @param username          username desiderato (deve essere univoco)
     * @param passwordInChiaro  password in chiaro (verra' cifrata)
     * @param dataNascita       data di nascita (facoltativa, puo' essere {@code null})
     * @param domicilio         luogo del domicilio
     * @return l'utente cliente appena creato
     * @throws IllegalArgumentException se lo username e' gia' in uso
     * @throws IOException              se il salvataggio su file fallisce
     */
    public Utente registraCliente(String nome, String cognome, String username,
                                  String passwordInChiaro, LocalDate dataNascita,
                                  String domicilio) throws IOException {
        if (trovaPerUsername(username) != null) {
            throw new IllegalArgumentException("Username gia' in uso: " + username);
        }
        Utente nuovo = new Utente(nome, cognome, username,
                PasswordUtil.cifra(passwordInChiaro), dataNascita, domicilio, Ruolo.CLIENTE);
        utenti.add(nuovo);
        salva();
        return nuovo;
    }

    /**
     * Cerca un utente dato lo username (confronto case-insensitive).
     *
     * @param username username da cercare
     * @return l'utente corrispondente, oppure {@code null} se non esiste
     */
    public Utente trovaPerUsername(String username) {
        if (username == null) {
            return null;
        }
        for (Utente u : utenti) {
            if (u.getUsername().equalsIgnoreCase(username.trim())) {
                return u;
            }
        }
        return null;
    }

    /**
     * Restituisce gli utenti il cui nome e cognome corrispondono (anche
     * parzialmente, case-insensitive) ai criteri indicati. Un criterio
     * {@code null} o vuoto viene ignorato.
     *
     * @param nome    nome (anche parziale) da cercare, oppure {@code null}
     * @param cognome cognome (anche parziale) da cercare, oppure {@code null}
     * @return la lista degli utenti corrispondenti
     */
    public List<Utente> trovaPerNomeCognome(String nome, String cognome) {
        List<Utente> risultati = new ArrayList<>();
        for (Utente u : utenti) {
            boolean okNome = (nome == null || nome.isBlank())
                    || u.getNome().toLowerCase().contains(nome.trim().toLowerCase());
            boolean okCognome = (cognome == null || cognome.isBlank())
                    || u.getCognome().toLowerCase().contains(cognome.trim().toLowerCase());
            if (okNome && okCognome) {
                risultati.add(u);
            }
        }
        return risultati;
    }

    /**
     * @return una copia dell'elenco di tutti gli utenti
     */
    public List<Utente> getTutti() {
        return new ArrayList<>(utenti);
    }

    // ----------------------------------------------------------------
    //  Conversione da/verso il formato CSV
    // ----------------------------------------------------------------

    /**
     * Costruisce un utente a partire da una riga CSV.
     *
     * @param riga la riga del file
     * @return l'utente corrispondente
     */
    private Utente daRigaCsv(String riga) {
        List<String> c = CsvUtil.parseRiga(riga);
        if (c.size() < 7) {
            throw new IllegalArgumentException("Numero di campi insufficiente");
        }
        String nome = c.get(0);
        String cognome = c.get(1);
        String username = c.get(2);
        String passwordHash = c.get(3);
        LocalDate dataNascita = parseDataNascita(c.get(4));
        String domicilio = c.get(5);
        Ruolo ruolo = Ruolo.daStringa(c.get(6));
        return new Utente(nome, cognome, username, passwordHash, dataNascita, domicilio, ruolo);
    }

    /**
     * Converte un utente in una riga CSV.
     *
     * @param u l'utente da convertire
     * @return la riga CSV corrispondente
     */
    private String aRigaCsv(Utente u) {
        String dataNascita = (u.getDataNascita() == null) ? "" : u.getDataNascita().toString();
        return CsvUtil.componiRiga(
                u.getNome(),
                u.getCognome(),
                u.getUsername(),
                u.getPasswordHash(),
                dataNascita,
                u.getDomicilio(),
                u.getRuolo().name());
    }

    /**
     * Interpreta il campo data di nascita, restituendo {@code null} se vuoto.
     *
     * @param testo il valore del campo
     * @return la data di nascita, oppure {@code null}
     */
    private LocalDate parseDataNascita(String testo) {
        if (testo == null || testo.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(testo.trim());
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
