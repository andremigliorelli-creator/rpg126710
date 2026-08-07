package it.unicam.cs.mpgc.rpg126710.service;

/**
 * Fase in cui si trova la partita.
 *
 * <p>Determina quali comandi hanno senso in un dato momento: non si puo'
 * camminare mentre si combatte, ne' attaccare mentre si esplora.</p>
 */
public enum StatoGioco {

    /** L'eroe si muove liberamente fra le stanze. */
    ESPLORAZIONE,

    /** L'eroe e' impegnato in uno scontro. */
    COMBATTIMENTO,

    /** L'eroe ha raggiunto l'uscita del dungeon. */
    VITTORIA,

    /** L'eroe e' caduto. */
    SCONFITTA;

    /**
     * @return {@code true} se la partita e' finita e non accetta piu' comandi
     */
    public boolean partitaTerminata() {
        return this == VITTORIA || this == SCONFITTA;
    }
}
