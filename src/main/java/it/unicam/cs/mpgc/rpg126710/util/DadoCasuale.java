package it.unicam.cs.mpgc.rpg126710.util;

import it.unicam.cs.mpgc.rpg126710.api.Dado;

import java.util.Random;

/**
 * Implementazione di {@link Dado} basata sul generatore pseudocasuale della
 * libreria standard.
 *
 * <p>Il costruttore che accetta un seme permette di riprodurre esattamente la
 * stessa sequenza di lanci, utile per indagare una partita anomala.</p>
 */
public class DadoCasuale implements Dado {

    private final Random generatore;

    /**
     * Crea un dado con seme scelto dal sistema.
     */
    public DadoCasuale() {
        this.generatore = new Random();
    }

    /**
     * Crea un dado con seme fissato, producendo lanci riproducibili.
     *
     * @param seme seme del generatore
     */
    public DadoCasuale(long seme) {
        this.generatore = new Random(seme);
    }

    @Override
    public int lancia(int facce) {
        if (facce <= 0) {
            throw new IllegalArgumentException("Il numero di facce deve essere positivo: " + facce);
        }
        return generatore.nextInt(facce) + 1;
    }
}
