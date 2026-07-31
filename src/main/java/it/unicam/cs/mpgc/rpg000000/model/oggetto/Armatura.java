package it.unicam.cs.mpgc.rpg000000.model.oggetto;

import it.unicam.cs.mpgc.rpg000000.api.Equipaggiabile;
import it.unicam.cs.mpgc.rpg000000.model.personaggio.Bonus;

/**
 * Oggetto che, indossato, riduce il danno subito da chi lo equipaggia.
 */
public final class Armatura extends Oggetto implements Equipaggiabile {

    private final int bonusDifesa;

    /**
     * @param nome        nome dell'armatura, non vuoto
     * @param descrizione testo mostrato all'utente, non vuoto
     * @param valore      valore in monete, non negativo
     * @param bonusDifesa incremento di difesa fornito, maggiore di zero
     * @throws IllegalArgumentException se un parametro non rispetta i vincoli
     */
    public Armatura(String nome, String descrizione, int valore, int bonusDifesa) {
        super(nome, descrizione, valore);
        if (bonusDifesa <= 0) {
            throw new IllegalArgumentException("Un'armatura deve fornire un bonus di difesa positivo: " + bonusDifesa);
        }
        this.bonusDifesa = bonusDifesa;
    }

    public int getBonusDifesa() {
        return bonusDifesa;
    }

    @Override
    public Bonus getBonus() {
        return new Bonus(0, bonusDifesa);
    }

    @Override
    public CategoriaOggetto getCategoria() {
        return CategoriaOggetto.ARMATURA;
    }

    @Override
    public String riepilogo() {
        return getNome() + " (+" + bonusDifesa + " DIF)";
    }
}
