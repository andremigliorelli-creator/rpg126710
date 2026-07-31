package it.unicam.cs.mpgc.rpg000000.model.combattimento;

import it.unicam.cs.mpgc.rpg000000.api.Azione;
import it.unicam.cs.mpgc.rpg000000.api.Combattente;
import it.unicam.cs.mpgc.rpg000000.api.EsitoAzione;

/**
 * Colpo base disponibile a qualunque combattente.
 *
 * <p>Il danno inflitto e' quello prodotto dall'attore ridotto dalla difesa del
 * bersaglio, con un minimo garantito: anche il colpo peggiore contro l'armatura
 * migliore lascia il segno, cosi' che un combattimento non possa mai bloccarsi
 * in una situazione di stallo perpetuo.</p>
 */
public class AzioneAttacco implements Azione {

    /** Danno minimo che un attacco riesce sempre a infliggere. */
    protected static final int DANNO_MINIMO = 1;

    @Override
    public String getNome() {
        return "Attacca";
    }

    @Override
    public String getDescrizione() {
        return "Colpisci il nemico con l'arma equipaggiata.";
    }

    @Override
    public EsitoAzione esegui(Combattente attore, Combattente bersaglio) {
        richiediPartecipantiValidi(attore, bersaglio);
        int danno = calcolaDannoInflitto(attore, bersaglio);
        int dannoEffettivo = bersaglio.subisciDanno(danno);
        return EsitoAzione.conDanno(descriviColpo(attore, bersaglio, dannoEffettivo), dannoEffettivo);
    }

    /**
     * Calcola quanto danno arriva effettivamente al bersaglio.
     *
     * <p>Le sottoclassi ridefiniscono questo metodo per realizzare colpi
     * speciali senza dover riscrivere la gestione dell'esito.</p>
     *
     * @param attore    chi colpisce
     * @param bersaglio chi subisce
     * @return il danno da applicare, sempre almeno pari a {@link #DANNO_MINIMO}
     */
    protected int calcolaDannoInflitto(Combattente attore, Combattente bersaglio) {
        int dannoGrezzo = attore.calcolaDanno();
        int difesa = bersaglio.getStatisticheEffettive().getDifesa();
        return Math.max(DANNO_MINIMO, dannoGrezzo - difesa);
    }

    /**
     * Compone la frase mostrata nel diario di combattimento.
     *
     * @param attore    chi colpisce
     * @param bersaglio chi subisce
     * @param danno     danno effettivamente inflitto
     * @return la descrizione leggibile del colpo
     */
    protected String descriviColpo(Combattente attore, Combattente bersaglio, int danno) {
        return attore.getNome() + " attacca " + bersaglio.getNome() + " infliggendo " + danno + " danni.";
    }

    /**
     * Verifica che i partecipanti all'azione siano validi.
     *
     * @param attore    chi compie l'azione
     * @param bersaglio chi la subisce
     * @throws IllegalArgumentException se uno dei due e' nullo
     */
    protected void richiediPartecipantiValidi(Combattente attore, Combattente bersaglio) {
        if (attore == null) {
            throw new IllegalArgumentException("L'attore dell'azione non puo' essere nullo");
        }
        if (bersaglio == null) {
            throw new IllegalArgumentException("Il bersaglio dell'azione non puo' essere nullo");
        }
    }
}
