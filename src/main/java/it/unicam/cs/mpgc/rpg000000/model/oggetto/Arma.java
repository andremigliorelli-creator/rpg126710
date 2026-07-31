package it.unicam.cs.mpgc.rpg000000.model.oggetto;

import it.unicam.cs.mpgc.rpg000000.api.Equipaggiabile;
import it.unicam.cs.mpgc.rpg000000.model.personaggio.Bonus;

/**
 * Oggetto che, impugnato, aumenta la potenza offensiva di chi lo equipaggia.
 */
public final class Arma extends Oggetto implements Equipaggiabile {

    private final int bonusAttacco;

    /**
     * @param nome         nome dell'arma, non vuoto
     * @param descrizione  testo mostrato all'utente, non vuoto
     * @param valore       valore in monete, non negativo
     * @param bonusAttacco incremento di attacco fornito, maggiore di zero
     * @throws IllegalArgumentException se un parametro non rispetta i vincoli
     */
    public Arma(String nome, String descrizione, int valore, int bonusAttacco) {
        super(nome, descrizione, valore);
        if (bonusAttacco <= 0) {
            throw new IllegalArgumentException("Un'arma deve fornire un bonus di attacco positivo: " + bonusAttacco);
        }
        this.bonusAttacco = bonusAttacco;
    }

    public int getBonusAttacco() {
        return bonusAttacco;
    }

    @Override
    public Bonus getBonus() {
        return new Bonus(bonusAttacco, 0);
    }

    @Override
    public CategoriaOggetto getCategoria() {
        return CategoriaOggetto.ARMA;
    }

    @Override
    public String riepilogo() {
        return getNome() + " (+" + bonusAttacco + " ATT)";
    }
}
