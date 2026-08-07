package it.unicam.cs.mpgc.rpg126710.service;

/**
 * Categorie di eventi che il motore di gioco puo' annunciare.
 *
 * <p>Consentono a un osservatore di reagire in modo diverso a seconda di cosa
 * e' accaduto, per esempio evidenziando in rosso i messaggi di sconfitta.</p>
 */
public enum TipoEvento {

    PARTITA_INIZIATA,
    STANZA_CAMBIATA,
    MOSSA_NON_VALIDA,
    COMBATTIMENTO_INIZIATO,
    AZIONE_ESEGUITA,
    COMBATTIMENTO_TERMINATO,
    TESORO_RACCOLTO,
    LIVELLO_AUMENTATO,
    PARTITA_VINTA,
    PARTITA_PERSA,
    PARTITA_SALVATA,
    PARTITA_CARICATA
}
