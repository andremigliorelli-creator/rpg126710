package it.unicam.cs.mpgc.rpg126710.model.combattimento;

import it.unicam.cs.mpgc.rpg126710.api.Azione;
import it.unicam.cs.mpgc.rpg126710.api.Combattente;
import it.unicam.cs.mpgc.rpg126710.api.Dado;
import it.unicam.cs.mpgc.rpg126710.api.EsitoAzione;

/**
 * Tentativo di abbandonare il combattimento.
 *
 * <p>La riuscita non e' garantita: se il tentativo fallisce il turno viene
 * comunque consumato, quindi fuggire e' una scommessa e non una via d'uscita
 * sicura.</p>
 */
public class AzioneFuga implements Azione {

    private static final int FACCE_PROVA_FUGA = 6;
    private static final int SOGLIA_RIUSCITA = 4;

    private final Dado dado;

    /**
     * @param dado sorgente di casualita' per la prova di fuga, non nulla
     * @throws IllegalArgumentException se il dado e' nullo
     */
    public AzioneFuga(Dado dado) {
        if (dado == null) {
            throw new IllegalArgumentException("Il dado della fuga non puo' essere nullo");
        }
        this.dado = dado;
    }

    @Override
    public String getNome() {
        return "Fuggi";
    }

    @Override
    public String getDescrizione() {
        return "Tenta di abbandonare lo scontro. Se fallisci perdi il turno.";
    }

    @Override
    public EsitoAzione esegui(Combattente attore, Combattente bersaglio) {
        if (attore == null) {
            throw new IllegalArgumentException("L'attore dell'azione non puo' essere nullo");
        }
        if (fugaRiuscita()) {
            return EsitoAzione.conInterruzione(attore.getNome() + " riesce a fuggire dallo scontro.");
        }
        return EsitoAzione.senzaEffetto(attore.getNome() + " tenta la fuga ma viene bloccato.");
    }

    private boolean fugaRiuscita() {
        return dado.lancia(FACCE_PROVA_FUGA) >= SOGLIA_RIUSCITA;
    }
}
