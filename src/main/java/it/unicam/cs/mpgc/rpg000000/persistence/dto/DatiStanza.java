package it.unicam.cs.mpgc.rpg000000.persistence.dto;

import java.util.List;
import java.util.Map;

/**
 * Rappresentazione grezza di una stanza letta dal file di configurazione.
 *
 * <p>I collegamenti vengono dichiarati una sola volta: il caricatore si occupa
 * di renderli percorribili anche nel verso opposto, cosi' il file dei contenuti
 * resta compatto e non puo' contenere passaggi incoerenti a senso unico.</p>
 */
public class DatiStanza {

    private String id;
    private String nome;
    private String descrizione;
    private boolean uscitaDelDungeon;
    private Map<String, String> uscite;
    private List<String> nemici;
    private List<String> tesori;

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public boolean isUscitaDelDungeon() {
        return uscitaDelDungeon;
    }

    /**
     * @return le uscite come coppie direzione/identificatore di stanza
     */
    public Map<String, String> getUscite() {
        return uscite == null ? Map.of() : uscite;
    }

    /**
     * @return gli identificatori dei modelli di nemico che presidiano la stanza
     */
    public List<String> getNemici() {
        return nemici == null ? List.of() : nemici;
    }

    /**
     * @return i nomi degli oggetti custoditi nella stanza
     */
    public List<String> getTesori() {
        return tesori == null ? List.of() : tesori;
    }
}
