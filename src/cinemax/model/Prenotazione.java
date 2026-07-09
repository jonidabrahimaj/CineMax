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

import java.util.Objects;

public class Prenotazione {

    /** Codice univoco della prenotazione */
    private String codice;

    /** Username del cliente che ha effettuato la prenotazione */
    private String usernameCliente;

    /** Identificativo della proiezione prenotata */
    private int idProiezione;

    /** Numero di biglietti (posti) prenotati */
    private int numeroBiglietti;

    /**
     * Costruisce una nuova prenotazione
     *
     * @param codice          codice univoco della prenotazione
     * @param usernameCliente username del cliente
     * @param idProiezione    identificativo della proiezione
     * @param numeroBiglietti numero di biglietti prenotati
     */
    public Prenotazione(String codice, String usernameCliente,
                        int idProiezione, int numeroBiglietti) {
        this.codice = codice;
        this.usernameCliente = usernameCliente;
        this.idProiezione = idProiezione;
        this.numeroBiglietti = numeroBiglietti;
    }

    /**
     * @return il codice univoco della prenotazione
     */
    public String getCodice() {
        return codice;
    }

    /**
     * @param codice il nuovo codice della prenotazione
     */
    public void setCodice(String codice) {
        this.codice = codice;
    }

    /**
     * @return lo username del cliente
     */
    public String getUsernameCliente() {
        return usernameCliente;
    }

    /**
     * @param usernameCliente il nuovo username del cliente
     */
    public void setUsernameCliente(String usernameCliente) {
        this.usernameCliente = usernameCliente;
    }

    /**
     * @return l'identificativo della proiezione
     */
    public int getIdProiezione() {
        return idProiezione;
    }

    /**
     * @param idProiezione il nuovo identificativo della proiezione
     */
    public void setIdProiezione(int idProiezione) {
        this.idProiezione = idProiezione;
    }

    /**
     * @return il numero di biglietti prenotati
     */
    public int getNumeroBiglietti() {
        return numeroBiglietti;
    }

    /**
     * @param numeroBiglietti il nuovo numero di biglietti
     */
    public void setNumeroBiglietti(int numeroBiglietti) {
        this.numeroBiglietti = numeroBiglietti;
    }

    /**
     * Calcola il costo totale della prenotazione dato il costo unitario
     *
     * @param costoUnitario costo del singolo biglietto
     * @return il costo totale (costo unitario per numero di biglietti)
     */
    public double calcolaTotale(double costoUnitario) {
        return costoUnitario * numeroBiglietti;
    }

    /**
     * @return una descrizione compatta della prenotazione
     */
    @Override
    public String toString() {
        return String.format("Prenotazione %s - cliente %s - proiezione #%d - %d biglietti",
                codice, usernameCliente, idProiezione, numeroBiglietti);
    }

    /**
     * Due prenotazioni sono uguali se hanno lo stesso codice
     *
     * @param o l'oggetto da confrontare
     * @return {@code true} se i codici coincidono
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Prenotazione)) {
            return false;
        }
        return Objects.equals(codice, ((Prenotazione) o).codice);
    }

    /**
     * @return l'hash code basato sul codice
     */
    @Override
    public int hashCode() {
        return Objects.hash(codice);
    }
}
