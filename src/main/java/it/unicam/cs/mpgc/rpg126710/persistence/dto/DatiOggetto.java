package it.unicam.cs.mpgc.rpg126710.persistence.dto;

/**
 * Rappresentazione grezza di un oggetto letta dal file di configurazione.
 *
 * <p>Le classi di questo package esistono per tenere la libreria di lettura
 * JSON fuori dal modello di dominio: qui i dati sono ancora testo e numeri
 * senza regole, mentre le classi del dominio nascono gia' valide grazie alle
 * verifiche nei loro costruttori. Se un giorno il formato dei contenuti
 * cambiasse, cambierebbero solo queste classi.</p>
 */
public class DatiOggetto {

    private String nome;
    private String descrizione;
    private int valore;
    private String categoria;
    private int potenza;

    public String getNome() {
        return nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public int getValore() {
        return valore;
    }

    /**
     * @return la categoria come testo, da convertire in {@code CategoriaOggetto}
     */
    public String getCategoria() {
        return categoria;
    }

    /**
     * @return il valore numerico dell'effetto: bonus di attacco, di difesa o
     *         punti vita ripristinati a seconda della categoria
     */
    public int getPotenza() {
        return potenza;
    }
}
