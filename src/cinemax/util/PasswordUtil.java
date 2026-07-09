/*
 * ============================================================================
 *  CineMax - Laboratorio Interdisciplinare A
 *  Autori:
 *    - <Jonida Brahimaj> - Matricola <759037> - Sede <COMO>
 *    - <Renee Angelica Cabigting> - Matricola <756997> - Sede <COMO>
 *  File: Prenotazione.java
 * ============================================================================
 */
package cinemax.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public final class PasswordUtil {

    /** Costruttore privato: la classe espone solo metodi statici. */
    private PasswordUtil() {
    }

    /**
     * Calcola l'impronta SHA-256 di una password e la restituisce come
     * stringa esadecimale (64 caratteri).
     *
     * @param password la password in chiaro da cifrare
     * @return l'hash esadecimale della password
     * @throws IllegalStateException se l'algoritmo SHA-256 non e' disponibile
     */
    public static String cifra(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                int v = b & 0xFF;
                if (v < 0x10) {
                    hex.append('0');
                }
                hex.append(Integer.toHexString(v));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 e' garantito da ogni implementazione standard di Java.
            throw new IllegalStateException("Algoritmo SHA-256 non disponibile", e);
        }
    }

    /**
     * Verifica se una password in chiaro corrisponde a un'impronta memorizzata.
     *
     * @param password    la password digitata dall'utente
     * @param hashSalvato l'impronta esadecimale letta dal file
     * @return {@code true} se la password e' corretta, {@code false} altrimenti
     */
    public static boolean verifica(String password, String hashSalvato) {
        if (password == null || hashSalvato == null) {
            return false;
        }
        return cifra(password).equalsIgnoreCase(hashSalvato);
    }
}
