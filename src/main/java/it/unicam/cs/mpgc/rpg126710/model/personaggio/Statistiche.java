package it.unicam.cs.mpgc.rpg126710.model.personaggio;

import java.util.Objects;

/**
 * Insieme immutabile dei valori numerici che descrivono le capacita' di un
 * combattente: attacco, difesa e punti vita massimi.
 *
 * <p>La classe e' un <em>value object</em>: una volta creata non puo' essere
 * modificata. Le operazioni di combinazione restituiscono sempre una nuova
 * istanza, evitando che un bonus di equipaggiamento alteri per errore le
 * statistiche di base di un personaggio.</p>
 */
public final class Statistiche {

    private final int attacco;
    private final int difesa;
    private final int puntiVitaMassimi;

    /**
     * Crea un insieme di statistiche validando i valori ricevuti.
     *
     * @param attacco          potenza offensiva, non negativa
     * @param difesa           capacita' di ridurre il danno subito, non negativa
     * @param puntiVitaMassimi soglia massima di vita, maggiore di zero
     * @throws IllegalArgumentException se un valore non rispetta i vincoli
     */
    public Statistiche(int attacco, int difesa, int puntiVitaMassimi) {
        if (attacco < 0) {
            throw new IllegalArgumentException("L'attacco non puo' essere negativo: " + attacco);
        }
        if (difesa < 0) {
            throw new IllegalArgumentException("La difesa non puo' essere negativa: " + difesa);
        }
        if (puntiVitaMassimi <= 0) {
            throw new IllegalArgumentException("I punti vita massimi devono essere positivi: " + puntiVitaMassimi);
        }
        this.attacco = attacco;
        this.difesa = difesa;
        this.puntiVitaMassimi = puntiVitaMassimi;
    }

    public int getAttacco() {
        return attacco;
    }

    public int getDifesa() {
        return difesa;
    }

    public int getPuntiVitaMassimi() {
        return puntiVitaMassimi;
    }

    /**
     * Applica un bonus di equipaggiamento a queste statistiche.
     *
     * <p>I punti vita massimi restano invariati perche' rappresentano una
     * caratteristica del personaggio e non un valore cumulabile: un'arma
     * aumenta l'attacco, non la resistenza fisica di chi la impugna.</p>
     *
     * @param bonus contributo da applicare, non nullo
     * @return una nuova istanza con i valori aggiornati
     * @throws IllegalArgumentException se il bonus e' nullo
     */
    public Statistiche piu(Bonus bonus) {
        if (bonus == null) {
            throw new IllegalArgumentException("Il bonus da applicare non puo' essere nullo");
        }
        return new Statistiche(
                this.attacco + bonus.getAttacco(),
                this.difesa + bonus.getDifesa(),
                this.puntiVitaMassimi
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Statistiche)) {
            return false;
        }
        Statistiche altre = (Statistiche) obj;
        return this.attacco == altre.attacco
                && this.difesa == altre.difesa
                && this.puntiVitaMassimi == altre.puntiVitaMassimi;
    }

    @Override
    public int hashCode() {
        return Objects.hash(attacco, difesa, puntiVitaMassimi);
    }

    @Override
    public String toString() {
        return "ATT " + attacco + " | DIF " + difesa + " | PV max " + puntiVitaMassimi;
    }
}
