package it.unicam.cs.mpgc.rpg000000.api;

import it.unicam.cs.mpgc.rpg000000.service.StatoPartita;

import java.util.Optional;

/**
 * Archivio in cui conservare e da cui recuperare una partita.
 *
 * <p>Il resto dell'applicazione dipende da questo contratto e non da come i
 * dati vengono effettivamente scritti: oggi un file JSON, domani un database,
 * senza che il motore di gioco o l'interfaccia se ne accorgano.</p>
 */
public interface RepositoryPartita {

    /**
     * Conserva lo stato indicato, sostituendo quello eventualmente presente.
     *
     * @param stato fotografia della partita, non nulla
     * @throws IllegalArgumentException se lo stato e' nullo
     * @throws IllegalStateException    se il salvataggio non riesce
     */
    void salva(StatoPartita stato);

    /**
     * Recupera l'ultima partita conservata.
     *
     * @return lo stato salvato, vuoto se non ne esiste alcuno
     * @throws IllegalStateException se il salvataggio esiste ma non e' leggibile
     */
    Optional<StatoPartita> carica();

    /**
     * @return {@code true} se esiste una partita da riprendere
     */
    boolean esisteSalvataggio();
}
