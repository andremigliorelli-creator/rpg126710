package it.unicam.cs.mpgc.rpg126710.api;

import it.unicam.cs.mpgc.rpg126710.model.mondo.Mappa;

/**
 * Sorgente da cui nasce il dungeon di una partita.
 *
 * <p>Il motore di gioco dipende da questa astrazione e non da una specifica
 * implementazione: la mappa puo' arrivare da un file JSON, essere generata in
 * modo procedurale o essere costruita a mano in un test, e il resto del sistema
 * non cambia di una riga.</p>
 *
 * <p><strong>Aspettative del tipo:</strong> la mappa restituita deve essere
 * gia' coerente, cioe' con tutte le uscite che puntano a stanze esistenti.</p>
 */
public interface GeneratoreMappa {

    /**
     * Costruisce una nuova mappa pronta per essere giocata.
     *
     * @return la mappa generata, mai {@code null}
     * @throws IllegalStateException se la sorgente dei dati non e' utilizzabile
     */
    Mappa genera();
}
