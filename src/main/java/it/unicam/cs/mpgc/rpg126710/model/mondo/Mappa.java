package it.unicam.cs.mpgc.rpg126710.model.mondo;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Insieme delle stanze che compongono il dungeon e dei collegamenti fra esse.
 *
 * <p>Le stanze sono indicizzate per identificatore in una mappa: raggiungere
 * una stanza a partire dal suo id costa tempo costante, mentre cercarla in una
 * lista richiederebbe di scorrere l'intero dungeon a ogni spostamento.</p>
 *
 * <p>Il costruttore verifica che ogni uscita punti a una stanza esistente: e'
 * preferibile scoprire subito una mappa incoerente piuttosto che ritrovarsi con
 * un passaggio verso il nulla durante la partita.</p>
 */
public class Mappa {

    private final Map<String, Stanza> stanze;
    private final String idStanzaIniziale;

    /**
     * @param stanze           stanze del dungeon indicizzate per id, non vuota
     * @param idStanzaIniziale identificatore della stanza di partenza, presente fra le stanze
     * @throws IllegalArgumentException se la mappa e' incoerente
     */
    public Mappa(Map<String, Stanza> stanze, String idStanzaIniziale) {
        if (stanze == null || stanze.isEmpty()) {
            throw new IllegalArgumentException("Una mappa deve contenere almeno una stanza");
        }
        if (idStanzaIniziale == null || !stanze.containsKey(idStanzaIniziale)) {
            throw new IllegalArgumentException("La stanza iniziale non appartiene alla mappa: " + idStanzaIniziale);
        }
        this.stanze = new LinkedHashMap<>(stanze);
        this.idStanzaIniziale = idStanzaIniziale;
        verificaCoerenzaCollegamenti();
    }

    /**
     * @return la stanza da cui inizia la partita
     */
    public Stanza getStanzaIniziale() {
        return stanze.get(idStanzaIniziale);
    }

    /**
     * @param id identificatore da cercare, non nullo
     * @return la stanza corrispondente, se esiste
     */
    public Optional<Stanza> getStanza(String id) {
        if (id == null) {
            throw new IllegalArgumentException("L'identificatore della stanza non puo' essere nullo");
        }
        return Optional.ofNullable(stanze.get(id));
    }

    /**
     * Restituisce la stanza raggiungibile da quella indicata in una direzione.
     *
     * @param partenza  stanza di partenza, non nulla
     * @param direzione direzione da percorrere, non nulla
     * @return la stanza di arrivo, vuoto se in quella direzione non c'e' passaggio
     */
    public Optional<Stanza> stanzaAdiacente(Stanza partenza, Direzione direzione) {
        if (partenza == null) {
            throw new IllegalArgumentException("La stanza di partenza non puo' essere nulla");
        }
        return partenza.getDestinazione(direzione).map(stanze::get);
    }

    /**
     * @return tutte le stanze del dungeon, in sola lettura
     */
    public Map<String, Stanza> getStanze() {
        return Collections.unmodifiableMap(stanze);
    }

    /**
     * @return il numero di stanze che compongono il dungeon
     */
    public int numeroStanze() {
        return stanze.size();
    }

    /**
     * @return quante stanze sono gia' state visitate
     */
    public long numeroStanzeVisitate() {
        return stanze.values().stream().filter(Stanza::eVisitata).count();
    }

    private void verificaCoerenzaCollegamenti() {
        for (Stanza stanza : stanze.values()) {
            for (Map.Entry<Direzione, String> uscita : stanza.getUscite().entrySet()) {
                if (!stanze.containsKey(uscita.getValue())) {
                    throw new IllegalArgumentException("La stanza '" + stanza.getId() + "' ha un'uscita verso "
                            + uscita.getKey().getEtichetta() + " che punta a una stanza inesistente: "
                            + uscita.getValue());
                }
            }
        }
    }
}
