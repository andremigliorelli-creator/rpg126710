package it.unicam.cs.mpgc.rpg000000.model.personaggio;

import it.unicam.cs.mpgc.rpg000000.api.Azione;
import it.unicam.cs.mpgc.rpg000000.api.Dado;
import it.unicam.cs.mpgc.rpg000000.model.combattimento.PallaDiFuoco;

/**
 * Eroe fragile ma capace di colpire con una magia che ignora le armature.
 */
public class Mago extends Eroe {

    private static final Statistiche STATISTICHE_INIZIALI = new Statistiche(4, 2, 22);

    /**
     * @param nome nome dell'eroe, non vuoto
     * @param dado sorgente di casualita', non nulla
     */
    public Mago(String nome, Dado dado) {
        super(nome, STATISTICHE_INIZIALI, dado);
    }

    @Override
    public String getNomeClasse() {
        return "Mago";
    }

    @Override
    public Azione abilitaSpeciale() {
        return new PallaDiFuoco(getDado());
    }
}
