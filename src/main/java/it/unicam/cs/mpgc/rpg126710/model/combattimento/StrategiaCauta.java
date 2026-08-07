package it.unicam.cs.mpgc.rpg126710.model.combattimento;

import it.unicam.cs.mpgc.rpg126710.api.Azione;
import it.unicam.cs.mpgc.rpg126710.api.Combattente;
import it.unicam.cs.mpgc.rpg126710.api.Dado;
import it.unicam.cs.mpgc.rpg126710.api.StrategiaCombattimento;

/**
 * Comportamento di chi attacca finche' e' in forze e tenta la fuga quando la
 * situazione diventa disperata.
 *
 * <p>E' la strategia di creature come i goblin, che preferiscono salvarsi la
 * pelle piuttosto che combattere fino all'ultimo.</p>
 */
public class StrategiaCauta implements StrategiaCombattimento {

    /** Frazione di vita sotto la quale la creatura preferisce scappare. */
    private static final double SOGLIA_RITIRATA = 0.25;

    private final Azione attacco = new AzioneAttacco();
    private final Azione fuga;

    /**
     * @param dado sorgente di casualita' usata dal tentativo di fuga, non nulla
     * @throws IllegalArgumentException se il dado e' nullo
     */
    public StrategiaCauta(Dado dado) {
        this.fuga = new AzioneFuga(dado);
    }

    @Override
    public Azione scegliAzione(Combattente attore, Combattente bersaglio) {
        if (attore == null) {
            throw new IllegalArgumentException("L'attore della strategia non puo' essere nullo");
        }
        return inFinDiVita(attore) ? fuga : attacco;
    }

    private boolean inFinDiVita(Combattente combattente) {
        double vitaResidua = (double) combattente.getPuntiVita() / combattente.getPuntiVitaMassimi();
        return vitaResidua < SOGLIA_RITIRATA;
    }
}
