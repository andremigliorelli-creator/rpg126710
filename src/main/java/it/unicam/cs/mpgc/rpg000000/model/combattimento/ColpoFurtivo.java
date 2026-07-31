package it.unicam.cs.mpgc.rpg000000.model.combattimento;

import it.unicam.cs.mpgc.rpg000000.api.Combattente;
import it.unicam.cs.mpgc.rpg000000.api.Dado;

/**
 * Abilita' speciale del Ladro: un attacco normale che, se va a segno nel punto
 * giusto, diventa un colpo critico.
 */
public class ColpoFurtivo extends AzioneAttacco {

    private static final int FACCE_PROVA_CRITICO = 6;
    private static final int SOGLIA_CRITICO = 5;
    private static final int DANNO_AGGIUNTIVO_CRITICO = 8;

    private final Dado dado;
    private boolean ultimoColpoCritico;

    /**
     * @param dado sorgente di casualita' per la prova di critico, non nulla
     * @throws IllegalArgumentException se il dado e' nullo
     */
    public ColpoFurtivo(Dado dado) {
        if (dado == null) {
            throw new IllegalArgumentException("Il dado del colpo furtivo non puo' essere nullo");
        }
        this.dado = dado;
    }

    @Override
    public String getNome() {
        return "Colpo furtivo";
    }

    @Override
    public String getDescrizione() {
        return "Un attacco rapido che puo' trasformarsi in un colpo critico devastante.";
    }

    @Override
    protected int calcolaDannoInflitto(Combattente attore, Combattente bersaglio) {
        int danno = super.calcolaDannoInflitto(attore, bersaglio);
        ultimoColpoCritico = dado.lancia(FACCE_PROVA_CRITICO) >= SOGLIA_CRITICO;
        if (ultimoColpoCritico) {
            danno += DANNO_AGGIUNTIVO_CRITICO;
        }
        return danno;
    }

    @Override
    protected String descriviColpo(Combattente attore, Combattente bersaglio, int danno) {
        if (ultimoColpoCritico) {
            return attore.getNome() + " trova un punto scoperto di " + bersaglio.getNome()
                    + ": colpo critico da " + danno + " danni!";
        }
        return attore.getNome() + " colpisce di sorpresa " + bersaglio.getNome()
                + " infliggendo " + danno + " danni.";
    }
}
