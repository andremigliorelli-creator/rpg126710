package it.unicam.cs.mpgc.rpg000000.model.combattimento;

import it.unicam.cs.mpgc.rpg000000.api.Azione;
import it.unicam.cs.mpgc.rpg000000.api.Combattente;
import it.unicam.cs.mpgc.rpg000000.api.StrategiaCombattimento;

/**
 * Comportamento di chi attacca sempre, senza mai ritirarsi.
 *
 * <p>E' la strategia delle creature prive di istinto di sopravvivenza, come
 * scheletri e costrutti.</p>
 */
public class StrategiaAggressiva implements StrategiaCombattimento {

    private final Azione attacco = new AzioneAttacco();

    @Override
    public Azione scegliAzione(Combattente attore, Combattente bersaglio) {
        return attacco;
    }
}
