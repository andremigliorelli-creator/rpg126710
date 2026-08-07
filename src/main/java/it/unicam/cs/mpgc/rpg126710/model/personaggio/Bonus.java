package it.unicam.cs.mpgc.rpg126710.model.personaggio;

import java.util.Objects;

/**
 * Contributo immutabile che un oggetto equipaggiato offre alle statistiche di
 * un personaggio.
 *
 * <p>E' un tipo distinto da {@link Statistiche} perche' rappresenta un concetto
 * diverso: un bonus somma attacco e difesa, ma non definisce quanta vita ha un
 * personaggio. Tenerli separati evita di dover inventare un valore fittizio di
 * punti vita ogni volta che si descrive un'arma.</p>
 */
public final class Bonus {

    private static final Bonus NESSUNO = new Bonus(0, 0);

    private final int attacco;
    private final int difesa;

    /**
     * Crea un bonus validando i valori ricevuti.
     *
     * @param attacco incremento di attacco, non negativo
     * @param difesa  incremento di difesa, non negativo
     * @throws IllegalArgumentException se un valore e' negativo
     */
    public Bonus(int attacco, int difesa) {
        if (attacco < 0) {
            throw new IllegalArgumentException("Il bonus di attacco non puo' essere negativo: " + attacco);
        }
        if (difesa < 0) {
            throw new IllegalArgumentException("Il bonus di difesa non puo' essere negativo: " + difesa);
        }
        this.attacco = attacco;
        this.difesa = difesa;
    }

    /**
     * @return l'elemento neutro, utile quando non c'e' nulla di equipaggiato
     */
    public static Bonus nessuno() {
        return NESSUNO;
    }

    public int getAttacco() {
        return attacco;
    }

    public int getDifesa() {
        return difesa;
    }

    /**
     * Somma questo bonus con un altro.
     *
     * @param altro bonus da sommare, non nullo
     * @return una nuova istanza con i valori sommati
     * @throws IllegalArgumentException se l'altro bonus e' nullo
     */
    public Bonus piu(Bonus altro) {
        if (altro == null) {
            throw new IllegalArgumentException("Il bonus da sommare non puo' essere nullo");
        }
        return new Bonus(this.attacco + altro.attacco, this.difesa + altro.difesa);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Bonus)) {
            return false;
        }
        Bonus altro = (Bonus) obj;
        return this.attacco == altro.attacco && this.difesa == altro.difesa;
    }

    @Override
    public int hashCode() {
        return Objects.hash(attacco, difesa);
    }

    @Override
    public String toString() {
        return "+" + attacco + " ATT, +" + difesa + " DIF";
    }
}
