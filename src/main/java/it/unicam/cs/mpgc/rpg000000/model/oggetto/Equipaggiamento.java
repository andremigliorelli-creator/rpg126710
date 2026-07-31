package it.unicam.cs.mpgc.rpg000000.model.oggetto;

import it.unicam.cs.mpgc.rpg000000.model.personaggio.Bonus;

import java.util.Optional;

/**
 * Ci&ograve; che un eroe ha attualmente indosso: al piu' un'arma e al piu'
 * un'armatura.
 *
 * <p>La classe ha una sola responsabilita': sapere cosa e' equipaggiato e
 * quale bonus complessivo ne deriva. Non decide quando equipaggiare, ne'
 * verifica che l'oggetto sia posseduto: sono compiti dell'eroe.</p>
 */
public class Equipaggiamento {

    private Arma arma;
    private Armatura armatura;

    /**
     * Impugna l'arma indicata, sostituendo quella eventualmente gia' presente.
     *
     * @param arma arma da equipaggiare, non nulla
     * @return l'arma precedentemente equipaggiata, se c'era
     * @throws IllegalArgumentException se l'arma e' nulla
     */
    public Optional<Arma> equipaggia(Arma arma) {
        if (arma == null) {
            throw new IllegalArgumentException("L'arma da equipaggiare non puo' essere nulla");
        }
        Optional<Arma> precedente = Optional.ofNullable(this.arma);
        this.arma = arma;
        return precedente;
    }

    /**
     * Indossa l'armatura indicata, sostituendo quella eventualmente presente.
     *
     * @param armatura armatura da equipaggiare, non nulla
     * @return l'armatura precedentemente equipaggiata, se c'era
     * @throws IllegalArgumentException se l'armatura e' nulla
     */
    public Optional<Armatura> equipaggia(Armatura armatura) {
        if (armatura == null) {
            throw new IllegalArgumentException("L'armatura da equipaggiare non puo' essere nulla");
        }
        Optional<Armatura> precedente = Optional.ofNullable(this.armatura);
        this.armatura = armatura;
        return precedente;
    }

    public Optional<Arma> getArma() {
        return Optional.ofNullable(arma);
    }

    public Optional<Armatura> getArmatura() {
        return Optional.ofNullable(armatura);
    }

    /**
     * Somma i bonus di tutti i pezzi attualmente equipaggiati.
     *
     * @return il bonus complessivo, neutro se non c'e' nulla equipaggiato
     */
    public Bonus getBonusTotale() {
        Bonus totale = Bonus.nessuno();
        if (arma != null) {
            totale = totale.piu(arma.getBonus());
        }
        if (armatura != null) {
            totale = totale.piu(armatura.getBonus());
        }
        return totale;
    }

    /**
     * @return una descrizione leggibile di cio' che e' equipaggiato
     */
    public String riepilogo() {
        String descrizioneArma = getArma().map(Arma::riepilogo).orElse("nessuna arma");
        String descrizioneArmatura = getArmatura().map(Armatura::riepilogo).orElse("nessuna armatura");
        return descrizioneArma + " | " + descrizioneArmatura;
    }
}
