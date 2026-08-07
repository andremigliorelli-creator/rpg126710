package it.unicam.cs.mpgc.rpg126710.model.oggetto;

import java.util.Objects;

/**
 * Radice della gerarchia degli oggetti raccoglibili nel dungeon.
 *
 * <p>La classe e' astratta perche' nel dominio non esiste un "oggetto
 * generico": esistono armi, armature e pozioni. Dichiararla astratta impedisce
 * di creare istanze prive di significato e concentra qui soltanto cio' che e'
 * davvero comune a tutti gli oggetti: un nome, una descrizione e un valore.</p>
 *
 * <p>Due oggetti sono considerati lo stesso oggetto quando hanno lo stesso
 * nome: e' il nome a identificarli nel dominio, quindi {@code equals} e
 * {@code hashCode} sono ridefiniti su di esso. Questo permette
 * all'{@link Inventario} di raggruppare correttamente le copie identiche.</p>
 */
public abstract class Oggetto {

    private final String nome;
    private final String descrizione;
    private final int valore;

    /**
     * @param nome        identificatore dell'oggetto nel dominio, non vuoto
     * @param descrizione testo mostrato all'utente, non vuoto
     * @param valore      valore in monete, non negativo
     * @throws IllegalArgumentException se un parametro non rispetta i vincoli
     */
    protected Oggetto(String nome, String descrizione, int valore) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Il nome dell'oggetto non puo' essere vuoto");
        }
        if (descrizione == null || descrizione.isBlank()) {
            throw new IllegalArgumentException("La descrizione dell'oggetto non puo' essere vuota");
        }
        if (valore < 0) {
            throw new IllegalArgumentException("Il valore dell'oggetto non puo' essere negativo: " + valore);
        }
        this.nome = nome;
        this.descrizione = descrizione;
        this.valore = valore;
    }

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
     * Restituisce la categoria dell'oggetto, usata dall'interfaccia grafica per
     * raggrupparlo e dal livello di persistenza per ricostruirlo.
     *
     * <p>Ogni sottoclasse deve dichiarare la propria: non esiste una categoria
     * sensata valida per tutti gli oggetti, per questo il metodo e' astratto.</p>
     *
     * @return la categoria dell'oggetto
     */
    public abstract CategoriaOggetto getCategoria();

    /**
     * @return una riga di riepilogo comprensibile all'utente
     */
    public abstract String riepilogo();

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Oggetto)) {
            return false;
        }
        Oggetto altro = (Oggetto) obj;
        return this.nome.equals(altro.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome);
    }

    @Override
    public String toString() {
        return nome;
    }
}
