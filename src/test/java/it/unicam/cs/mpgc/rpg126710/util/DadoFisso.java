package it.unicam.cs.mpgc.rpg126710.util;

import it.unicam.cs.mpgc.rpg126710.api.Dado;

/**
 * Dado di prova che restituisce sempre lo stesso valore.
 *
 * <p>Sostituisce il generatore pseudocasuale nei test: senza di esso l'esito di
 * un combattimento cambierebbe a ogni esecuzione e le asserzioni non sarebbero
 * ripetibili. E' possibile usarlo proprio perche' il codice di produzione
 * dipende dall'astrazione {@link Dado} e non da {@code java.util.Random}.</p>
 */
public class DadoFisso implements Dado {

    private final int valore;

    /**
     * @param valore risultato restituito da ogni lancio, maggiore di zero
     * @throws IllegalArgumentException se il valore non e' positivo
     */
    public DadoFisso(int valore) {
        if (valore <= 0) {
            throw new IllegalArgumentException("Il valore fisso deve essere positivo: " + valore);
        }
        this.valore = valore;
    }

    @Override
    public int lancia(int facce) {
        if (facce <= 0) {
            throw new IllegalArgumentException("Il numero di facce deve essere positivo: " + facce);
        }
        return Math.min(valore, facce);
    }
}
