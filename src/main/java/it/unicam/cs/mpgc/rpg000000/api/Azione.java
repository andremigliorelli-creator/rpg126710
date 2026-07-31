package it.unicam.cs.mpgc.rpg000000.api;

/**
 * Mossa che un combattente puo' compiere durante il proprio turno.
 *
 * <p>Questa astrazione e' il punto in cui il gioco resta aperto all'estensione:
 * per introdurre una nuova mossa basta aggiungere una classe che implementi
 * questo contratto, senza modificare il motore di combattimento che la usa.</p>
 *
 * <p><strong>Aspettative del tipo:</strong> l'esecuzione non deve mai
 * restituire {@code null} e non deve alterare combattenti diversi da attore e
 * bersaglio.</p>
 */
public interface Azione {

    /**
     * @return il nome con cui l'azione viene presentata all'utente
     */
    String getNome();

    /**
     * @return una breve spiegazione di cosa fa l'azione
     */
    String getDescrizione();

    /**
     * Esegue l'azione producendo il resoconto di cio' che e' accaduto.
     *
     * @param attore    chi compie l'azione, non nullo
     * @param bersaglio chi la subisce, non nullo
     * @return l'esito dell'azione, mai {@code null}
     * @throws IllegalArgumentException se attore o bersaglio sono nulli
     */
    EsitoAzione esegui(Combattente attore, Combattente bersaglio);
}
