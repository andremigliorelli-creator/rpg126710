package it.unicam.cs.mpgc.rpg000000.api;

import it.unicam.cs.mpgc.rpg000000.model.personaggio.Statistiche;

/**
 * Entita' che puo' prendere parte a un combattimento subendo danni e curandosi.
 *
 * <p>Il contratto descrive soltanto la capacita' di <em>stare</em> in un
 * combattimento. La capacita' di infliggere danno e' descritta separatamente da
 * {@link Attaccante}: esistono infatti bersagli che possono essere colpiti ma
 * non colpire, e questa separazione evita di imporre metodi inutili a chi li
 * implementa.</p>
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
}
