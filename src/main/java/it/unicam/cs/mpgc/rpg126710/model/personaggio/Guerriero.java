package it.unicam.cs.mpgc.rpg126710.model.personaggio;

import it.unicam.cs.mpgc.rpg126710.api.Azione;
import it.unicam.cs.mpgc.rpg126710.api.Dado;
import it.unicam.cs.mpgc.rpg126710.model.combattimento.ColpoPossente;

/**
 * Eroe robusto, che regge bene i colpi e combatte in mischia.
 */
public class Guerriero extends Eroe {

    private static final Statistiche STATISTICHE_INIZIALI = new Statistiche(6, 4, 30);

    /**
     * @param nome nome dell'eroe, non vuoto
     * @param dado sorgente di casualita', non nulla
     */
    public Guerriero(String nome, Dado dado) {
        super(nome, STATISTICHE_INIZIALI, dado);
    }

    @Override
    public String getNomeClasse() {
        return "Guerriero";
    }

    @Override
    public Azione abilitaSpeciale() {
        return new ColpoPossente(getDado());
    }
}
