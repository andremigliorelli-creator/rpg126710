package it.unicam.cs.mpgc.rpg126710.api;

import it.unicam.cs.mpgc.rpg126710.model.personaggio.Bonus;

/**
 * Oggetto che, una volta indossato o impugnato, modifica in modo permanente le
 * statistiche di chi lo equipaggia.
 *
 * <p>Contratto distinto da {@link Utilizzabile} perche' un'arma si equipaggia e
 * resta attiva, mentre una pozione si consuma una volta sola: obbligare
 * entrambe a implementare gli stessi metodi significherebbe imporre a ciascuna
 * comportamenti che non le appartengono.</p>
 */
public interface Equipaggiabile {

    /**
     * @return il nome dell'oggetto equipaggiabile
     */
    String getNome();

    /**
     * Restituisce il contributo che l'oggetto offre alle statistiche di chi lo
     * equipaggia.
     *
     * @return il bonus fornito, mai {@code null}
     */
    Bonus getBonus();
}
