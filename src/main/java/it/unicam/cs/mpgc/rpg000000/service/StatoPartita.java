package it.unicam.cs.mpgc.rpg000000.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Fotografia di una partita in un dato momento, pensata per essere scritta su
 * disco e riletta piu' tardi.
 *
 * <p>Contiene soltanto testo, numeri e collezioni: nessun riferimento a oggetti
 * del dominio. Degli oggetti raccolti si conserva il nome, non l'intera
 * definizione, perche' la definizione vive gia' nel catalogo dei contenuti; in
 * questo modo un salvataggio resta leggibile anche dopo un ribilanciamento
 * delle statistiche di un'arma.</p>
 */
public class StatoPartita {

    private String nomeEroe;
    private String classeEroe;
    private int livello;
    private int esperienza;
    private int puntiVita;
    private String idStanzaCorrente;
    private Map<String, Integer> inventario = new LinkedHashMap<>();
    private List<String> idStanzeVisitate = new ArrayList<>();
    private List<String> idStanzeCompletate = new ArrayList<>();
    private String armaEquipaggiata;
    private String armaturaEquipaggiata;

    /**
     * Costruttore senza argomenti richiesto dalla libreria di lettura JSON.
     */
    public StatoPartita() {
        // I campi vengono valorizzati dalla deserializzazione.
    }

    /**
     * @param nomeEroe         nome del personaggio, non vuoto
     * @param classeEroe       classe del personaggio, non vuota
     * @param livello          livello raggiunto, maggiore di zero
     * @param esperienza       esperienza nel livello corrente, non negativa
     * @param puntiVita        punti vita correnti, non negativi
     * @param idStanzaCorrente stanza in cui si trova l'eroe, non vuota
     * @throws IllegalArgumentException se un parametro non rispetta i vincoli
     */
    public StatoPartita(String nomeEroe,
                        String classeEroe,
                        int livello,
                        int esperienza,
                        int puntiVita,
                        String idStanzaCorrente) {
        if (nomeEroe == null || nomeEroe.isBlank()) {
            throw new IllegalArgumentException("Il nome dell'eroe non puo' essere vuoto");
        }
        if (classeEroe == null || classeEroe.isBlank()) {
            throw new IllegalArgumentException("La classe dell'eroe non puo' essere vuota");
        }
        if (livello <= 0) {
            throw new IllegalArgumentException("Il livello deve essere positivo: " + livello);
        }
        if (esperienza < 0) {
            throw new IllegalArgumentException("L'esperienza non puo' essere negativa: " + esperienza);
        }
        if (puntiVita < 0) {
            throw new IllegalArgumentException("I punti vita non possono essere negativi: " + puntiVita);
        }
        if (idStanzaCorrente == null || idStanzaCorrente.isBlank()) {
            throw new IllegalArgumentException("La stanza corrente non puo' essere vuota");
        }
        this.nomeEroe = nomeEroe;
        this.classeEroe = classeEroe;
        this.livello = livello;
        this.esperienza = esperienza;
        this.puntiVita = puntiVita;
        this.idStanzaCorrente = idStanzaCorrente;
    }

    public String getNomeEroe() {
        return nomeEroe;
    }

    public String getClasseEroe() {
        return classeEroe;
    }

    public int getLivello() {
        return livello;
    }

    public int getEsperienza() {
        return esperienza;
    }

    public int getPuntiVita() {
        return puntiVita;
    }

    public String getIdStanzaCorrente() {
        return idStanzaCorrente;
    }

    /**
     * @return le quantita' possedute, indicizzate per nome dell'oggetto
     */
    public Map<String, Integer> getInventario() {
        return inventario;
    }

    public void aggiungiVoceInventario(String nomeOggetto, int quantita) {
        inventario.put(nomeOggetto, quantita);
    }

    /**
     * @return gli identificatori delle stanze gia' viste dall'eroe
     */
    public List<String> getIdStanzeVisitate() {
        return idStanzeVisitate;
    }

    public void segnaStanzaVisitata(String idStanza) {
        idStanzeVisitate.add(idStanza);
    }

    /**
     * @return gli identificatori delle stanze ripulite da nemici e tesori
     */
    public List<String> getIdStanzeCompletate() {
        return idStanzeCompletate;
    }

    public void segnaStanzaCompletata(String idStanza) {
        idStanzeCompletate.add(idStanza);
    }

    public Optional<String> getArmaEquipaggiata() {
        return Optional.ofNullable(armaEquipaggiata);
    }

    public void setArmaEquipaggiata(String armaEquipaggiata) {
        this.armaEquipaggiata = armaEquipaggiata;
    }

    public Optional<String> getArmaturaEquipaggiata() {
        return Optional.ofNullable(armaturaEquipaggiata);
    }

    public void setArmaturaEquipaggiata(String armaturaEquipaggiata) {
        this.armaturaEquipaggiata = armaturaEquipaggiata;
    }

    /**
     * Verifica che un salvataggio riletto da disco contenga dati sensati.
     *
     * <p>La deserializzazione non passa dal costruttore, quindi un file
     * manomesso o troncato produrrebbe un oggetto incoerente: questo controllo
     * lo intercetta prima che entri nel gioco.</p>
     *
     * @throws IllegalStateException se il salvataggio non e' utilizzabile
     */
    public void verificaCoerenza() {
        if (nomeEroe == null || nomeEroe.isBlank()
                || classeEroe == null || classeEroe.isBlank()
                || idStanzaCorrente == null || idStanzaCorrente.isBlank()
                || livello <= 0 || esperienza < 0 || puntiVita < 0) {
            throw new IllegalStateException("Il salvataggio e' incompleto o danneggiato");
        }
        if (inventario == null) {
            inventario = new LinkedHashMap<>();
        }
        if (idStanzeVisitate == null) {
            idStanzeVisitate = new ArrayList<>();
        }
        if (idStanzeCompletate == null) {
            idStanzeCompletate = new ArrayList<>();
        }
    }
}
