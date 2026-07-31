package it.unicam.cs.mpgc.rpg000000.api;

/**
 * Criterio con cui un combattente non controllato dall'utente decide la propria
 * mossa.
 *
 * <p>Separare la strategia dal personaggio permette di cambiare il
 * comportamento di un nemico senza toccarne la classe: due nemici identici
 * possono combattere in modo diverso semplicemente ricevendo strategie diverse
 * nel costruttore.</p>
 */
public interface StrategiaCombattimento {

    /**
     * Sceglie l'azione da eseguire nel turno corrente.
     *
     * @param attore    chi deve agire, non nullo
     * @param bersaglio chi subira' l'azione, non nullo
     * @return l'azione scelta, mai {@code null}
     */
    Azione scegliAzione(Combattente attore, Combattente bersaglio);
}
