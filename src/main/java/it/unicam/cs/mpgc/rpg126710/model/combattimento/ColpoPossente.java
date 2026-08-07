package it.unicam.cs.mpgc.rpg126710.model.combattimento;

import it.unicam.cs.mpgc.rpg126710.api.Combattente;
import it.unicam.cs.mpgc.rpg126710.api.Dado;

/**
 * Abilita' speciale del Guerriero: un colpo caricato che raddoppia il danno.
 *
 * <p>Riusa la logica di {@link AzioneAttacco} ridefinendo soltanto il calcolo
 * del danno e la frase di resoconto: tutto il resto, compresa la validazione
 * dei partecipanti, resta scritto una volta sola nella classe base.</p>
 */
public class ColpoPossente extends AzioneAttacco {

    private static final int MOLTIPLICATORE = 2;
    private static final int FACCE_PROVA_RIUSCITA = 6;
    private static final int SOGLIA_RIUSCITA = 3;

    private final Dado dado;

    /**
     * @param dado sorgente di casualita' per la prova di riuscita, non nulla
     * @throws IllegalArgumentException se il dado e' nullo
     */
    public ColpoPossente(Dado dado) {
        if (dado == null) {
            throw new IllegalArgumentException("Il dado del colpo possente non puo' essere nullo");
        }
        this.dado = dado;
    }

    @Override
    public String getNome() {
        return "Colpo possente";
    }

    @Override
    public String getDescrizione() {
        return "Un attacco caricato che raddoppia il danno, ma rischia di mancare il bersaglio.";
    }

    @Override
    protected int calcolaDannoInflitto(Combattente attore, Combattente bersaglio) {
        if (!provaRiuscita()) {
            return DANNO_MINIMO;
        }
        return super.calcolaDannoInflitto(attore, bersaglio) * MOLTIPLICATORE;
    }

    @Override
    protected String descriviColpo(Combattente attore, Combattente bersaglio, int danno) {
        return attore.getNome() + " carica un colpo possente su " + bersaglio.getNome()
                + " infliggendo " + danno + " danni.";
    }

    private boolean provaRiuscita() {
        return dado.lancia(FACCE_PROVA_RIUSCITA) >= SOGLIA_RIUSCITA;
    }
}
