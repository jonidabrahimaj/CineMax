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

import java.time.LocalDate;
import java.util.Objects;

public class Utente {

    /** Nome dell'utente. */
    private String nome;

    /** Cognome dell'utente. */
    private String cognome;

    /** Username univoco usato per il login. */
    private String username;

    /** Impronta (hash SHA-256) della password. */
    private String passwordHash;

    /** Data di nascita (facoltativa, puo' essere {@code null}). */
    private LocalDate dataNascita;

    /** Luogo del domicilio. */
    private String domicilio;

    /** Ruolo dell'utente nel sistema. */
    private Ruolo ruolo;

    /**
     * Costruisce un nuovo utente.
     *
     * @param nome         nome
     * @param cognome      cognome
     * @param username     username univoco
     * @param passwordHash impronta della password (gia' cifrata)
     * @param dataNascita  data di nascita (facoltativa, puo' essere {@code null})
     * @param domicilio    luogo del domicilio
     * @param ruolo        ruolo dell'utente
     */
    public Utente(String nome, String cognome, String username, String passwordHash,
                  LocalDate dataNascita, String domicilio, Ruolo ruolo) {
        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.passwordHash = passwordHash;
        this.dataNascita = dataNascita;
        this.domicilio = domicilio;
        this.ruolo = ruolo;
    }

    /**
     * @return il nome dell'utente
     */
    public String getNome() {
        return nome;
    }

    /**
     * @param nome il nuovo nome
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * @return il cognome dell'utente
     */
    public String getCognome() {
        return cognome;
    }

    /**
     * @param cognome il nuovo cognome
     */
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    /**
     * @return lo username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username il nuovo username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return l'impronta della password
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * @param passwordHash la nuova impronta della password
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * @return la data di nascita, oppure {@code null} se non specificata
     */
    public LocalDate getDataNascita() {
        return dataNascita;
    }

    /**
     * @param dataNascita la nuova data di nascita (puo' essere {@code null})
     */
    public void setDataNascita(LocalDate dataNascita) {
        this.dataNascita = dataNascita;
    }

    /**
     * @return il luogo del domicilio
     */
    public String getDomicilio() {
        return domicilio;
    }

    /**
     * @param domicilio il nuovo luogo del domicilio
     */
    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    /**
     * @return il ruolo dell'utente
     */
    public Ruolo getRuolo() {
        return ruolo;
    }

    /**
     * @param ruolo il nuovo ruolo
     */
    public void setRuolo(Ruolo ruolo) {
        this.ruolo = ruolo;
    }

    /**
     * @return nome e cognome concatenati
     */
    public String getNomeCompleto() {
        return nome + " " + cognome;
    }

    /**
     * @return una descrizione compatta dell'utente
     */
    @Override
    public String toString() {
        return String.format("%s (%s) - %s", getNomeCompleto(), username, ruolo);
    }

    /**
     * Due utenti sono uguali se hanno lo stesso username.
     *
     * @param o l'oggetto da confrontare
     * @return {@code true} se gli username coincidono (ignorando il caso)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Utente)) {
            return false;
        }
        Utente utente = (Utente) o;
        return username != null && username.equalsIgnoreCase(utente.username);
    }

    /**
     * @return l'hash code basato sullo username (in minuscolo)
     */
    @Override
    public int hashCode() {
        return Objects.hash(username == null ? null : username.toLowerCase());
    }
}
