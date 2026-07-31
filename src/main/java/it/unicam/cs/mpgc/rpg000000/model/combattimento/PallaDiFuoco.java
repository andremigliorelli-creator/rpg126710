package it.unicam.cs.mpgc.rpg000000.model.combattimento;

import it.unicam.cs.mpgc.rpg000000.api.Combattente;
import it.unicam.cs.mpgc.rpg000000.api.Dado;

/**
 * Abilita' speciale del Mago: un danno magico che ignora completamente la
 * difesa del bersaglio.
 *
 * <p>E' il colpo piu' affidabile contro nemici molto corazzati, perche' non
 * viene ridotto dall'armatura.</p>
 */
public class PallaDiFuoco extends AzioneAttacco {

    private static final int DANNO_BASE = 6;
    private static final int FACCE_DADO = 8;

    private final Dado dado;

    /**
     * @param dado sorgente di casualita' per la potenza dell'incantesimo, non nulla
     * @throws IllegalArgumentException se il dado e' nullo
     */
    public PallaDiFuoco(Dado dado) {
        if (dado == null) {
            throw new IllegalArgumentException("Il dado della palla di fuoco non puo' essere nullo");
        }
        this.dado = dado;
    }

    @Override
    public String getNome() {
        return "Palla di fuoco";
    }

    @Override
    public String getDescrizione() {
        return "Un danno magico che ignora l'armatura del bersaglio.";
    }

    @Override
    protected int calcolaDannoInflitto(Combattente attore, Combattente bersaglio) {
        return DANNO_BASE + dado.lancia(FACCE_DADO);
    }

    @Override
    protected String descriviColpo(Combattente attore, Combattente bersaglio, int danno) {
        return attore.getNome() + " evoca una palla di fuoco su " + bersaglio.getNome()
                + " infliggendo " + danno + " danni che ignorano l'armatura.";
    }
}
