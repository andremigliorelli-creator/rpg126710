package it.unicam.cs.mpgc.rpg000000.model.personaggio;

import it.unicam.cs.mpgc.rpg000000.api.Azione;
import it.unicam.cs.mpgc.rpg000000.api.Dado;
import it.unicam.cs.mpgc.rpg000000.model.combattimento.ColpoFurtivo;

/**
 * Eroe agile, capace di trasformare un attacco normale in un colpo critico.
 */
public class Ladro extends Eroe {

    private static final Statistiche STATISTICHE_INIZIALI = new Statistiche(5, 3, 26);

    /**
     * @param nome nome dell'eroe, non vuoto
     * @param dado sorgente di casualita', non nulla
     */
    public Ladro(String nome, Dado dado) {
        super(nome, STATISTICHE_INIZIALI, dado);
    }

    @Override
    public String getNomeClasse() {
        return "Ladro";
    }

    @Override
    public Azione abilitaSpeciale() {
        return new ColpoFurtivo(getDado());
    }
}
