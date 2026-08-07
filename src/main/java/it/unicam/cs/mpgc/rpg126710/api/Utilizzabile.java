package it.unicam.cs.mpgc.rpg126710.api;

/**
 * Oggetto che produce un effetto immediato su un combattente e che, una volta
 * usato, viene consumato.
 *
 * <p><strong>Aspettative del tipo:</strong> l'effetto non deve mai portare i
 * punti vita del bersaglio sotto zero ne' oltre il proprio massimo, e la
 * descrizione restituita deve essere sempre presentabile all'utente.</p>
 */
public interface Utilizzabile {

    /**
     * @return il nome dell'oggetto utilizzabile
     */
    String getNome();

    /**
     * Applica l'effetto dell'oggetto sul bersaglio indicato.
     *
     * @param bersaglio destinatario dell'effetto, non nullo
     * @return l'esito leggibile dell'utilizzo, mai {@code null}
     * @throws IllegalArgumentException se il bersaglio e' nullo
     */
    EsitoAzione applicaEffetto(Combattente bersaglio);
}
