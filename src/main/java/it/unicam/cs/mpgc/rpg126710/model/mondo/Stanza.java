package it.unicam.cs.mpgc.rpg126710.model.mondo;

import it.unicam.cs.mpgc.rpg126710.model.oggetto.Oggetto;
import it.unicam.cs.mpgc.rpg126710.model.personaggio.Nemico;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Singolo ambiente del dungeon, con le proprie uscite, i nemici che lo
 * presidiano e i tesori che custodisce.
 *
 * <p>Due stanze sono considerate la stessa stanza quando hanno lo stesso
 * identificatore: l'id e' stabile e assegnato alla creazione della mappa,
 * quindi rappresenta un'identita' chiara ed e' il criterio giusto per
 * {@code equals} e {@code hashCode}. Questo permette di usare le stanze come
 * chiavi di una mappa senza sorprese.</p>
 *
 * <p>Le uscite sono conservate in una {@link EnumMap}: essendo le direzioni un
 * insieme chiuso e noto, e' la struttura piu' compatta ed efficiente
 * disponibile.</p>
 */
public class Stanza {

    private final String id;
    private final String nome;
    private final String descrizione;
    private final Map<Direzione, String> uscite = new EnumMap<>(Direzione.class);
    private final List<Nemico> nemici = new ArrayList<>();
    private final List<Oggetto> tesori = new ArrayList<>();
    private final boolean uscitaDelDungeon;

    private boolean visitata;

    /**
     * @param id               identificatore univoco nella mappa, non vuoto
     * @param nome             nome mostrato all'utente, non vuoto
     * @param descrizione      testo ambientale, non vuoto
     * @param uscitaDelDungeon {@code true} se raggiungerla completa la partita
     * @throws IllegalArgumentException se un parametro non rispetta i vincoli
     */
    public Stanza(String id, String nome, String descrizione, boolean uscitaDelDungeon) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("L'identificatore della stanza non puo' essere vuoto");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Il nome della stanza non puo' essere vuoto");
        }
        if (descrizione == null || descrizione.isBlank()) {
            throw new IllegalArgumentException("La descrizione della stanza non puo' essere vuota");
        }
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.uscitaDelDungeon = uscitaDelDungeon;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    /**
     * @return {@code true} se raggiungere questa stanza conclude vittoriosamente la partita
     */
    public boolean eUscitaDelDungeon() {
        return uscitaDelDungeon;
    }

    public boolean eVisitata() {
        return visitata;
    }

    /**
     * Segna la stanza come gia' esplorata.
     */
    public void segnaComeVisitata() {
        this.visitata = true;
    }

    /**
     * Collega questa stanza a un'altra in una data direzione.
     *
     * @param direzione     direzione del passaggio, non nulla
     * @param idStanzaDestinazione identificatore della stanza raggiunta, non vuoto
     * @throws IllegalArgumentException se un parametro non rispetta i vincoli
     */
    public void collega(Direzione direzione, String idStanzaDestinazione) {
        if (direzione == null) {
            throw new IllegalArgumentException("La direzione del collegamento non puo' essere nulla");
        }
        if (idStanzaDestinazione == null || idStanzaDestinazione.isBlank()) {
            throw new IllegalArgumentException("La destinazione del collegamento non puo' essere vuota");
        }
        uscite.put(direzione, idStanzaDestinazione);
    }

    /**
     * @param direzione direzione da percorrere, non nulla
     * @return l'identificatore della stanza raggiungibile, se il passaggio esiste
     */
    public Optional<String> getDestinazione(Direzione direzione) {
        if (direzione == null) {
            throw new IllegalArgumentException("La direzione non puo' essere nulla");
        }
        return Optional.ofNullable(uscite.get(direzione));
    }

    /**
     * @return le uscite disponibili, in sola lettura
     */
    public Map<Direzione, String> getUscite() {
        return Collections.unmodifiableMap(uscite);
    }

    /**
     * Aggiunge un nemico a guardia della stanza.
     *
     * @param nemico nemico da aggiungere, non nullo
     */
    public void aggiungiNemico(Nemico nemico) {
        if (nemico == null) {
            throw new IllegalArgumentException("Il nemico da aggiungere non puo' essere nullo");
        }
        nemici.add(nemico);
    }

    /**
     * Rimuove un nemico, tipicamente perche' sconfitto o fuggito.
     *
     * @param nemico nemico da rimuovere, non nullo
     * @return {@code true} se il nemico era presente
     */
    public boolean rimuoviNemico(Nemico nemico) {
        if (nemico == null) {
            throw new IllegalArgumentException("Il nemico da rimuovere non puo' essere nullo");
        }
        return nemici.remove(nemico);
    }

    /**
     * @return il primo nemico ancora in vita che presidia la stanza
     */
    public Optional<Nemico> getPrimoNemicoVivo() {
        return nemici.stream().filter(Nemico::eVivo).findFirst();
    }

    /**
     * @return i nemici presenti, in sola lettura
     */
    public List<Nemico> getNemici() {
        return Collections.unmodifiableList(nemici);
    }

    /**
     * @return {@code true} se nella stanza non ci sono piu' nemici in vita
     */
    public boolean eLibera() {
        return getPrimoNemicoVivo().isEmpty();
    }

    /**
     * Nasconde un oggetto nella stanza.
     *
     * @param tesoro oggetto da aggiungere, non nullo
     */
    public void aggiungiTesoro(Oggetto tesoro) {
        if (tesoro == null) {
            throw new IllegalArgumentException("Il tesoro da aggiungere non puo' essere nullo");
        }
        tesori.add(tesoro);
    }

    /**
     * Svuota la stanza dai tesori e li restituisce a chi li raccoglie.
     *
     * @return gli oggetti raccolti, eventualmente lista vuota
     */
    public List<Oggetto> raccogliTesori() {
        List<Oggetto> raccolti = new ArrayList<>(tesori);
        tesori.clear();
        return raccolti;
    }

    /**
     * @return i tesori ancora presenti, in sola lettura
     */
    public List<Oggetto> getTesori() {
        return Collections.unmodifiableList(tesori);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Stanza)) {
            return false;
        }
        Stanza altra = (Stanza) obj;
        return this.id.equals(altra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return nome;
    }
}
