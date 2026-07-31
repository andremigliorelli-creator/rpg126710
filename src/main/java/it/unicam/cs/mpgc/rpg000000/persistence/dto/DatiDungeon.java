package it.unicam.cs.mpgc.rpg000000.persistence.dto;

import java.util.List;

/**
 * Contenuto completo del file di configurazione del dungeon: catalogo degli
 * oggetti, modelli di nemico e stanze.
 */
public class DatiDungeon {

    private String stanzaIniziale;
    private List<DatiOggetto> oggetti;
    private List<DatiNemico> nemici;
    private List<DatiStanza> stanze;

    public String getStanzaIniziale() {
        return stanzaIniziale;
    }

    public List<DatiOggetto> getOggetti() {
        return oggetti == null ? List.of() : oggetti;
    }

    public List<DatiNemico> getNemici() {
        return nemici == null ? List.of() : nemici;
    }

    public List<DatiStanza> getStanze() {
        return stanze == null ? List.of() : stanze;
    }
}
