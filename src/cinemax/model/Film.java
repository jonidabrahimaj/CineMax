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

public class Film {

    /** Titolo del film. */
    private String titolo;

    /** Genere */
    private String genere;

    /** Nome del regista. */
    private String regista;

    /** Anno di produzione. */
    private int anno;

    /** Durata del film in minuti. */
    private int durataMinuti;

    /** Eta' minima del pubblico ammesso. */
    private int etaMinima;

    /**
     * Costruisce un nuovo film.
     *
     * @param titolo       titolo del film
     * @param genere       genere del film
     * @param regista      regista del film
     * @param anno         anno di produzione
     * @param durataMinuti durata in minuti
     * @param etaMinima    eta' minima del pubblico
     */
    public Film(String titolo, String genere, String regista,
                int anno, int durataMinuti, int etaMinima) {
        this.titolo = titolo;
        this.genere = genere;
        this.regista = regista;
        this.anno = anno;
        this.durataMinuti = durataMinuti;
        this.etaMinima = etaMinima;
    }

    /**
     * @return il titolo del film
     */
    public String getTitolo() {
        return titolo;
    }

    /**
     * @param titolo il nuovo titolo del film
     */
    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    /**
     * @return il genere del film
     */
    public String getGenere() {
        return genere;
    }

    /**
     * @param genere il nuovo genere del film
     */
    public void setGenere(String genere) {
        this.genere = genere;
    }

    /**
     * @return il regista del film
     */
    public String getRegista() {
        return regista;
    }

    /**
     * @param regista il nuovo regista del film
     */
    public void setRegista(String regista) {
        this.regista = regista;
    }

    /**
     * @return l'anno di produzione
     */
    public int getAnno() {
        return anno;
    }

    /**
     * @param anno il nuovo anno di produzione
     */
    public void setAnno(int anno) {
        this.anno = anno;
    }

    /**
     * @return la durata in minuti
     */
    public int getDurataMinuti() {
        return durataMinuti;
    }

    /**
     * @param durataMinuti la nuova durata in minuti
     */
    public void setDurataMinuti(int durataMinuti) {
        this.durataMinuti = durataMinuti;
    }

    /**
     * @return l'eta' minima del pubblico
     */
    public int getEtaMinima() {
        return etaMinima;
    }

    /**
     * @param etaMinima la nuova eta' minima del pubblico
     */
    public void setEtaMinima(int etaMinima) {
        this.etaMinima = etaMinima;
    }

    /**
     * Restituisce una descrizione testuale compatta del film.
     *
     * @return una stringa con titolo, anno e genere
     */
    @Override
    public String toString() {
        return String.format("%s (%d) - %s", titolo, anno, genere);
    }

    /**
     * Due film sono considerati uguali se coincidono titolo, regista e anno.
     *
     * @param o l'oggetto da confrontare
     * @return {@code true} se i film sono equivalenti
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Film)) {
            return false;
        }
        Film film = (Film) o;
        return anno == film.anno
                && Objects.equals(titolo, film.titolo)
                && Objects.equals(regista, film.regista);
    }

    /**
     * @return 
     */
    @Override
    public int hashCode() {
        return Objects.hash(titolo, regista, anno);
    }
}
