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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Rappresenta una proiezione: l'evento in cui un determinato {@link Film} viene
 * proiettato in una specifica data e ora, con un costo del biglietto.
 * <p>
 * Il cinema e' monosala con una capacita' fissa di {@value #CAPACITA_SALA}
 * posti; il numero di posti liberi viene calcolato altrove sottraendo i
 * biglietti gia' prenotati per la proiezione.
 *
 * @author CineMax Team
 */
public class Proiezione {

    /** Capacita' della sala unica del cinema (numero totale di posti). */
    public static final int CAPACITA_SALA = 200;

    /** Formato standard usato per data e ora delle proiezioni. */
    public static final DateTimeFormatter FORMATO_DATA_ORA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /** Formato alternativo con i secondi (usato nei file di origine). */
    public static final DateTimeFormatter FORMATO_DATA_ORA_SEC =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** Identificativo univoco della proiezione. */
    private int id;

    /** Film proiettato. */
    private Film film;

    /** Data e ora della proiezione. */
    private LocalDateTime dataOra;

    /** Costo del singolo biglietto in euro. */
    private double costoBiglietto;

    /**
     * Costruisce una nuova proiezione.
     *
     * @param id             identificativo univoco
     * @param film           film proiettato
     * @param dataOra        data e ora della proiezione
     * @param costoBiglietto costo del biglietto in euro
     */
    public Proiezione(int id, Film film, LocalDateTime dataOra, double costoBiglietto) {
        this.id = id;
        this.film = film;
        this.dataOra = dataOra;
        this.costoBiglietto = costoBiglietto;
    }

    /**
     * @return l'identificativo univoco della proiezione
     */
    public int getId() {
        return id;
    }

    /**
     * @param id il nuovo identificativo della proiezione
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return il film proiettato
     */
    public Film getFilm() {
        return film;
    }

    /**
     * @param film il nuovo film proiettato
     */
    public void setFilm(Film film) {
        this.film = film;
    }

    /**
     * @return la data e l'ora della proiezione
     */
    public LocalDateTime getDataOra() {
        return dataOra;
    }

    /**
     * @param dataOra la nuova data e ora della proiezione
     */
    public void setDataOra(LocalDateTime dataOra) {
        this.dataOra = dataOra;
    }

    /**
     * @return il costo del biglietto in euro
     */
    public double getCostoBiglietto() {
        return costoBiglietto;
    }

    /**
     * @param costoBiglietto il nuovo costo del biglietto in euro
     */
    public void setCostoBiglietto(double costoBiglietto) {
        this.costoBiglietto = costoBiglietto;
    }

    /**
     * Calcola l'istante di fine proiezione sommando la durata del film.
     *
     * @return data e ora di fine proiezione
     */
    public LocalDateTime getFine() {
        return dataOra.plusMinutes(film.getDurataMinuti());
    }

    /**
     * Verifica se questa proiezione si sovrappone temporalmente a un'altra.
     * Due proiezioni si sovrappongono se gli intervalli [inizio, fine) hanno
     * intersezione non vuota.
     *
     * @param altra l'altra proiezione da confrontare
     * @return {@code true} se vi e' sovrapposizione temporale
     */
    public boolean siSovrappone(Proiezione altra) {
        LocalDateTime inizioA = this.dataOra;
        LocalDateTime fineA = this.getFine();
        LocalDateTime inizioB = altra.dataOra;
        LocalDateTime fineB = altra.getFine();
        return inizioA.isBefore(fineB) && inizioB.isBefore(fineA);
    }

    /**
     * Restituisce la data e l'ora formattate secondo lo standard del sistema.
     *
     * @return la rappresentazione testuale di data e ora
     */
    public String getDataOraFormattata() {
        return dataOra.format(FORMATO_DATA_ORA);
    }

    /**
     * @return una descrizione compatta della proiezione
     */
    @Override
    public String toString() {
        return String.format("[#%d] %s - %s - %.2f EUR",
                id, film.getTitolo(), getDataOraFormattata(), costoBiglietto);
    }

    /**
     * Due proiezioni sono uguali se hanno lo stesso identificativo.
     *
     * @param o l'oggetto da confrontare
     * @return {@code true} se gli id coincidono
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Proiezione)) {
            return false;
        }
        return id == ((Proiezione) o).id;
    }

    /**
     * @return l'hash code basato sull'identificativo
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
