package it.unicam.cs.mpgc.rpg126710.api;

import it.unicam.cs.mpgc.rpg126710.model.personaggio.Statistiche;

/**
 * Entita' che puo' prendere parte a un combattimento subendo danni e curandosi.
 *
 * <p>Il contratto raccoglie esattamente cio' che serve al motore di
 * combattimento e nulla di piu': identita', vitalita', statistiche e capacita'
 * di produrre danno. Nel dominio di questo gioco ogni combattente puo' sia
 * colpire sia essere colpito, quindi nessuna classe e' costretta a implementare
 * metodi che non le servono.</p>
 */
public interface Combattente {

    /**
     * @return il nome con cui il combattente viene mostrato all'utente
     */
    String getNome();

    /**
     * @return i punti vita attuali, sempre compresi fra zero e i punti vita massimi
     */
    int getPuntiVita();

    /**
     * @return la soglia massima di punti vita raggiungibile
     */
    int getPuntiVitaMassimi();

    /**
     * Restituisce le statistiche effettive, gia' comprensive degli eventuali
     * bonus derivanti dall'equipaggiamento indossato.
     *
     * @return le statistiche da usare nei calcoli di combattimento
     */
    Statistiche getStatisticheEffettive();

    /**
     * @return {@code true} se i punti vita attuali sono maggiori di zero
     */
    boolean eVivo();

    /**
     * Riduce i punti vita della quantita' indicata, senza mai scendere sotto zero.
     *
     * @param danno quantita' di danno da applicare, non negativa
     * @return i punti vita effettivamente persi
     * @throws IllegalArgumentException se il danno e' negativo
     */
    int subisciDanno(int danno);

    /**
     * Ripristina punti vita senza mai superare i punti vita massimi.
     *
     * @param puntiVita quantita' da ripristinare, non negativa
     * @return i punti vita effettivamente recuperati
     * @throws IllegalArgumentException se la quantita' e' negativa
     */
    int curati(int puntiVita);

    /**
     * Calcola il danno grezzo che il combattente produce in questo istante,
     * comprensivo della variabilita' del colpo.
     *
     * <p><strong>Aspettative del tipo:</strong> il valore restituito deve
     * essere sempre maggiore o uguale a zero. Un'implementazione che
     * restituisse valori negativi violerebbe il principio di sostituzione di
     * Liskov, costringendo il codice chiamante a controlli aggiuntivi.</p>
     *
     * @return una quantita' di danno maggiore o uguale a zero
     */
    int calcolaDanno();
}
