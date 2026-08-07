package it.unicam.cs.mpgc.rpg126710.model.combattimento;

/**
 * Stato in cui si puo' trovare uno scontro.
 */
public enum EsitoCombattimento {

    /** Lo scontro e' ancora aperto. */
    IN_CORSO("Combattimento in corso"),

    /** L'eroe ha abbattuto il nemico. */
    VITTORIA("Hai vinto lo scontro"),

    /** L'eroe e' stato sconfitto. */
    SCONFITTA("Sei stato sconfitto"),

    /** L'eroe e' riuscito ad allontanarsi. */
    FUGA_EROE("Sei fuggito dallo scontro"),

    /** Il nemico si e' dato alla fuga. */
    FUGA_NEMICO("Il nemico e' fuggito");

    private final String messaggio;

    EsitoCombattimento(String messaggio) {
        this.messaggio = messaggio;
    }

    /**
     * @return il messaggio da mostrare all'utente
     */
    public String getMessaggio() {
        return messaggio;
    }

    /**
     * @return {@code true} se lo scontro e' terminato
     */
    public boolean eConcluso() {
        return this != IN_CORSO;
    }

    /**
     * @return {@code true} se il nemico e' stato tolto di mezzo, per morte o fuga
     */
    public boolean nemicoNeutralizzato() {
        return this == VITTORIA || this == FUGA_NEMICO;
    }
}
