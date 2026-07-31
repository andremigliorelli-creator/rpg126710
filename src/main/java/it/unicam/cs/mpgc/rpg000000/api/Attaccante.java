package it.unicam.cs.mpgc.rpg000000.api;

/**
 * Entita' capace di produrre una quantita' di danno.
 *
 * <p>Il contratto e' volutamente minimo: chi lo implementa deve saper fare una
 * cosa sola, cioe' dichiarare quanto danno grezzo e' in grado di generare in
 * questo istante. Come tale danno venga poi ridotto dalla difesa del bersaglio
 * non riguarda l'attaccante.</p>
 *
 * <p><strong>Aspettative del tipo:</strong> il valore restituito deve essere
 * sempre maggiore o uguale a zero. Un'implementazione che restituisse valori
 * negativi violerebbe il principio di sostituzione di Liskov, perche'
 * costringerebbe il codice chiamante a controlli aggiuntivi.</p>
 */
public interface Attaccante {

    /**
     * Calcola il danno grezzo prodotto in questo momento.
     *
     * @return una quantita' di danno maggiore o uguale a zero
     */
    int calcolaDanno();
}
