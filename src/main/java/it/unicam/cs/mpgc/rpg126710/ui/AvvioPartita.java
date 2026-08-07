package it.unicam.cs.mpgc.rpg126710.ui;

import it.unicam.cs.mpgc.rpg126710.service.MotoreGioco;

/**
 * Richiamo con cui la schermata iniziale comunica all'applicazione che una
 * partita e' pronta a partire.
 *
 * <p>Serve a tenere la schermata iniziale indipendente dalla classe che
 * gestisce le finestre: la schermata sa che qualcuno vuole essere avvisato, non
 * chi sia.</p>
 */
@FunctionalInterface
public interface AvvioPartita {

    /**
     * Segnala che la partita indicata puo' essere avviata.
     *
     * @param motore         partita pronta, non nulla
     * @param partitaRipresa {@code true} se proviene da un salvataggio
     */
    void avvia(MotoreGioco motore, boolean partitaRipresa);
}
