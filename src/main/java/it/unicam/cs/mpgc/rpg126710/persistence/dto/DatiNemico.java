package it.unicam.cs.mpgc.rpg126710.persistence.dto;

/**
 * Modello di creatura letto dal file di configurazione.
 *
 * <p>Descrive un <em>tipo</em> di nemico: da un singolo modello il caricatore
 * crea un'istanza distinta per ogni stanza che lo ospita, cosi' che due goblin
 * abbiano punti vita indipendenti.</p>
 */
public class DatiNemico {

    private String id;
    private String nome;
    private int attacco;
    private int difesa;
    private int puntiVita;
    private String strategia;
    private int esperienza;
    private String bottino;

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public int getAttacco() {
        return attacco;
    }

    public int getDifesa() {
        return difesa;
    }

    public int getPuntiVita() {
        return puntiVita;
    }

    /**
     * @return il nome della strategia di combattimento da associare
     */
    public String getStrategia() {
        return strategia;
    }

    public int getEsperienza() {
        return esperienza;
    }

    /**
     * @return il nome dell'oggetto lasciato cadere, eventualmente {@code null}
     */
    public String getBottino() {
        return bottino;
    }
}
