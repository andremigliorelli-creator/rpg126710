package it.unicam.cs.mpgc.rpg126710.model.personaggio;

import it.unicam.cs.mpgc.rpg126710.api.Combattente;
import it.unicam.cs.mpgc.rpg126710.api.Dado;

/**
 * Base comune a tutti gli esseri che popolano il dungeon.
 *
 * <p>La classe e' astratta perche' nel dominio non esiste un "personaggio
 * generico": esistono eroi controllati dall'utente e nemici governati da una
 * strategia. Raccoglie qui soltanto cio' che entrambi condividono davvero:
 * nome, statistiche di base, punti vita correnti e la sorgente di casualita'
 * usata per variare i colpi.</p>
 *
 * <p>Volutamente <strong>non</strong> ridefinisce {@code equals}: due goblin
 * con lo stesso nome sono due creature distinte, quindi l'identita' coincide
 * con quella dell'oggetto. Ridefinire l'uguaglianza sul nome renderebbe
 * impossibile tenere due nemici uguali nella stessa stanza.</p>
 */
public abstract class Personaggio implements Combattente {

    private final String nome;
    private final Dado dado;
    private Statistiche statisticheBase;
    private int puntiVita;

    /**
     * @param nome            nome del personaggio, non vuoto
     * @param statisticheBase statistiche iniziali, non nulle
     * @param dado            sorgente di casualita' per la variabilita' dei colpi, non nulla
     * @throws IllegalArgumentException se un parametro non rispetta i vincoli
     */
    protected Personaggio(String nome, Statistiche statisticheBase, Dado dado) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Il nome del personaggio non puo' essere vuoto");
        }
        if (statisticheBase == null) {
            throw new IllegalArgumentException("Le statistiche del personaggio non possono essere nulle");
        }
        if (dado == null) {
            throw new IllegalArgumentException("Il dado del personaggio non puo' essere nullo");
        }
        this.nome = nome;
        this.statisticheBase = statisticheBase;
        this.dado = dado;
        this.puntiVita = statisticheBase.getPuntiVitaMassimi();
    }

    @Override
    public String getNome() {
        return nome;
    }

    @Override
    public int getPuntiVita() {
        return puntiVita;
    }

    @Override
    public int getPuntiVitaMassimi() {
        return getStatisticheEffettive().getPuntiVitaMassimi();
    }

    /**
     * Restituisce le statistiche effettive del personaggio.
     *
     * <p>Nella versione di base coincidono con quelle iniziali; le sottoclassi
     * che possiedono equipaggiamento ridefiniscono il metodo per aggiungere i
     * relativi bonus.</p>
     *
     * @return le statistiche da usare nei calcoli di combattimento
     */
    @Override
    public Statistiche getStatisticheEffettive() {
        return statisticheBase;
    }

    /**
     * @return le statistiche prive di qualsiasi bonus
     */
    public Statistiche getStatisticheBase() {
        return statisticheBase;
    }

    @Override
    public boolean eVivo() {
        return puntiVita > 0;
    }

    @Override
    public int subisciDanno(int danno) {
        if (danno < 0) {
            throw new IllegalArgumentException("Il danno subito non puo' essere negativo: " + danno);
        }
        int dannoEffettivo = Math.min(danno, puntiVita);
        puntiVita -= dannoEffettivo;
        return dannoEffettivo;
    }

    @Override
    public int curati(int puntiVitaDaRipristinare) {
        if (puntiVitaDaRipristinare < 0) {
            throw new IllegalArgumentException(
                    "I punti vita da ripristinare non possono essere negativi: " + puntiVitaDaRipristinare);
        }
        int recuperoPossibile = getPuntiVitaMassimi() - puntiVita;
        int recuperoEffettivo = Math.min(puntiVitaDaRipristinare, recuperoPossibile);
        puntiVita += recuperoEffettivo;
        return recuperoEffettivo;
    }

    /**
     * Calcola il danno grezzo del colpo sommando l'attacco effettivo alla
     * variabilita' introdotta dal dado.
     *
     * @return una quantita' di danno sempre positiva
     */
    @Override
    public int calcolaDanno() {
        return getStatisticheEffettive().getAttacco() + dado.lancia(FACCE_DADO_DANNO);
    }

    /**
     * Numero di facce del dado usato per variare il danno di un colpo.
     */
    protected static final int FACCE_DADO_DANNO = 6;

    /**
     * @return la sorgente di casualita' associata al personaggio
     */
    protected Dado getDado() {
        return dado;
    }

    /**
     * Sostituisce le statistiche di base, ad esempio in seguito a un avanzamento
     * di livello, e adegua i punti vita correnti al nuovo massimo.
     *
     * @param nuoveStatistiche statistiche aggiornate, non nulle
     * @throws IllegalArgumentException se le statistiche sono nulle
     */
    protected void aggiornaStatisticheBase(Statistiche nuoveStatistiche) {
        if (nuoveStatistiche == null) {
            throw new IllegalArgumentException("Le nuove statistiche non possono essere nulle");
        }
        this.statisticheBase = nuoveStatistiche;
        this.puntiVita = Math.min(this.puntiVita, getPuntiVitaMassimi());
    }

    /**
     * Imposta direttamente i punti vita correnti, usato quando si ricostruisce
     * un personaggio da un salvataggio.
     *
     * @param puntiVita valore da impostare, fra zero e i punti vita massimi
     * @throws IllegalArgumentException se il valore e' fuori dall'intervallo
     */
    public void impostaPuntiVita(int puntiVita) {
        if (puntiVita < 0 || puntiVita > getPuntiVitaMassimi()) {
            throw new IllegalArgumentException(
                    "Punti vita fuori intervallo [0, " + getPuntiVitaMassimi() + "]: " + puntiVita);
        }
        this.puntiVita = puntiVita;
    }

    /**
     * @return una riga di stato leggibile, del tipo "Nome 12/20 PV"
     */
    public String riepilogoStato() {
        return nome + " " + puntiVita + "/" + getPuntiVitaMassimi() + " PV";
    }

    @Override
    public String toString() {
        return riepilogoStato();
    }
}
