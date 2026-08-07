package it.unicam.cs.mpgc.rpg126710.api;

/**
 * Sorgente di casualita' del gioco.
 *
 * <p>Il combattimento non usa direttamente {@link java.util.Random}: dipende da
 * questa astrazione. In questo modo il motore di gioco resta invariato mentre i
 * test possono iniettare un dado prevedibile e verificare gli esiti, senza
 * dover accettare risultati diversi a ogni esecuzione.</p>
 *
 * <p><strong>Aspettative del tipo:</strong> il valore restituito deve sempre
 * essere compreso fra 1 e il numero di facce richiesto, estremi inclusi.</p>
 */
public interface Dado {

    /**
     * Lancia un dado con il numero di facce indicato.
     *
     * @param facce numero di facce, maggiore di zero
     * @return un valore fra 1 e {@code facce}
     * @throws IllegalArgumentException se il numero di facce non e' positivo
     */
    int lancia(int facce);
}
