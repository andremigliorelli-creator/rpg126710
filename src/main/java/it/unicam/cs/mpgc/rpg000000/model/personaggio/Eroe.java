package it.unicam.cs.mpgc.rpg000000.model.personaggio;

import it.unicam.cs.mpgc.rpg000000.api.Azione;
import it.unicam.cs.mpgc.rpg000000.api.Dado;
import it.unicam.cs.mpgc.rpg000000.model.combattimento.AzioneAttacco;
import it.unicam.cs.mpgc.rpg000000.model.combattimento.AzioneFuga;
import it.unicam.cs.mpgc.rpg000000.model.combattimento.AzioneUsaOggetto;
import it.unicam.cs.mpgc.rpg000000.model.oggetto.Arma;
import it.unicam.cs.mpgc.rpg000000.model.oggetto.Armatura;
import it.unicam.cs.mpgc.rpg000000.model.oggetto.CategoriaOggetto;
import it.unicam.cs.mpgc.rpg000000.model.oggetto.Equipaggiamento;
import it.unicam.cs.mpgc.rpg000000.model.oggetto.Inventario;
import it.unicam.cs.mpgc.rpg000000.model.oggetto.Oggetto;
import it.unicam.cs.mpgc.rpg000000.model.oggetto.Pozione;

import java.util.ArrayList;
import java.util.List;

/**
 * Personaggio controllato dall'utente.
 *
 * <p>Rispetto a un {@link Personaggio} qualsiasi possiede un inventario, un
 * equipaggiamento e una progressione per livelli. La classe resta astratta
 * perche' non esiste un "eroe generico": esistono guerrieri, maghi e ladri, e
 * ciascuno deve dichiarare la propria abilita' speciale.</p>
 */
public abstract class Eroe extends Personaggio {

    /** Esperienza richiesta per passare dal livello n al livello n+1. */
    private static final int ESPERIENZA_PER_LIVELLO = 100;

    private static final int INCREMENTO_ATTACCO_PER_LIVELLO = 2;
    private static final int INCREMENTO_DIFESA_PER_LIVELLO = 1;
    private static final int INCREMENTO_VITA_PER_LIVELLO = 5;

    private final Inventario inventario = new Inventario();
    private final Equipaggiamento equipaggiamento = new Equipaggiamento();

    private int livello = 1;
    private int esperienza;

    /**
     * @param nome            nome dell'eroe, non vuoto
     * @param statisticheBase statistiche iniziali, non nulle
     * @param dado            sorgente di casualita', non nulla
     */
    protected Eroe(String nome, Statistiche statisticheBase, Dado dado) {
        super(nome, statisticheBase, dado);
    }

    /**
     * @return il nome della classe di appartenenza, mostrato all'utente
     */
    public abstract String getNomeClasse();

    /**
     * Restituisce l'abilita' che distingue questa classe di eroe dalle altre.
     *
     * <p>E' astratta perche' ogni classe combatte in modo diverso: il guerriero
     * carica un colpo, il mago lancia un incantesimo, il ladro cerca il punto
     * scoperto. Non esiste una versione unica valida per tutti.</p>
     *
     * @return l'azione speciale della classe, mai {@code null}
     */
    public abstract Azione abilitaSpeciale();

    /**
     * Le statistiche effettive di un eroe comprendono i bonus di cio' che ha
     * equipaggiato.
     *
     * @return le statistiche di base aumentate dall'equipaggiamento
     */
    @Override
    public Statistiche getStatisticheEffettive() {
        return getStatisticheBase().piu(equipaggiamento.getBonusTotale());
    }

    public Inventario getInventario() {
        return inventario;
    }

    public Equipaggiamento getEquipaggiamento() {
        return equipaggiamento;
    }

    public int getLivello() {
        return livello;
    }

    public int getEsperienza() {
        return esperienza;
    }

    /**
     * @return l'esperienza ancora necessaria per salire di livello
     */
    public int getEsperienzaAlProssimoLivello() {
        return livello * ESPERIENZA_PER_LIVELLO - esperienza;
    }

    /**
     * Accumula esperienza facendo salire di livello l'eroe ogni volta che viene
     * raggiunta la soglia richiesta.
     *
     * @param puntiEsperienza esperienza guadagnata, non negativa
     * @return il numero di livelli guadagnati, zero se nessuno
     * @throws IllegalArgumentException se l'esperienza e' negativa
     */
    public int guadagnaEsperienza(int puntiEsperienza) {
        if (puntiEsperienza < 0) {
            throw new IllegalArgumentException("L'esperienza guadagnata non puo' essere negativa: " + puntiEsperienza);
        }
        esperienza += puntiEsperienza;
        int livelliGuadagnati = 0;
        while (esperienza >= livello * ESPERIENZA_PER_LIVELLO) {
            esperienza -= livello * ESPERIENZA_PER_LIVELLO;
            livello++;
            livelliGuadagnati++;
            applicaAvanzamentoDiLivello();
        }
        return livelliGuadagnati;
    }

    /**
     * Raccoglie un oggetto trovato nel dungeon.
     *
     * @param oggetto oggetto raccolto, non nullo
     */
    public void raccogli(Oggetto oggetto) {
        inventario.aggiungi(oggetto);
    }

    /**
     * Impugna un'arma posseduta.
     *
     * @param arma arma da equipaggiare, non nulla
     * @return {@code true} se l'arma era nell'inventario ed e' stata equipaggiata
     */
    public boolean equipaggia(Arma arma) {
        if (!inventario.contiene(arma)) {
            return false;
        }
        equipaggiamento.equipaggia(arma);
        return true;
    }

    /**
     * Indossa un'armatura posseduta.
     *
     * @param armatura armatura da equipaggiare, non nulla
     * @return {@code true} se l'armatura era nell'inventario ed e' stata indossata
     */
    public boolean equipaggia(Armatura armatura) {
        if (!inventario.contiene(armatura)) {
            return false;
        }
        equipaggiamento.equipaggia(armatura);
        return true;
    }

    /**
     * Costruisce l'elenco delle mosse che l'eroe puo' scegliere in questo
     * momento: il colpo base, la propria abilita', la fuga e una voce per ogni
     * tipo di pozione posseduta.
     *
     * @return le azioni disponibili, mai vuota
     */
    public List<Azione> azioniDisponibili() {
        List<Azione> azioni = new ArrayList<>();
        azioni.add(new AzioneAttacco());
        azioni.add(abilitaSpeciale());
        azioni.addAll(azioniPozione());
        azioni.add(new AzioneFuga(getDado()));
        return azioni;
    }

    private List<Azione> azioniPozione() {
        return inventario.filtraPerCategoria(CategoriaOggetto.POZIONE).stream()
                .filter(Pozione.class::isInstance)
                .map(Pozione.class::cast)
                .<Azione>map(pozione -> new AzioneUsaOggetto<>(pozione, inventario))
                .toList();
    }

    private void applicaAvanzamentoDiLivello() {
        Statistiche precedenti = getStatisticheBase();
        aggiornaStatisticheBase(new Statistiche(
                precedenti.getAttacco() + INCREMENTO_ATTACCO_PER_LIVELLO,
                precedenti.getDifesa() + INCREMENTO_DIFESA_PER_LIVELLO,
                precedenti.getPuntiVitaMassimi() + INCREMENTO_VITA_PER_LIVELLO));
        curati(getPuntiVitaMassimi());
    }

    /**
     * Reimposta livello ed esperienza, usato quando si ricostruisce un eroe da
     * un salvataggio.
     *
     * @param livello    livello raggiunto, maggiore di zero
     * @param esperienza esperienza accumulata nel livello corrente, non negativa
     * @throws IllegalArgumentException se i valori non rispettano i vincoli
     */
    public void ripristinaProgressione(int livello, int esperienza) {
        if (livello <= 0) {
            throw new IllegalArgumentException("Il livello deve essere positivo: " + livello);
        }
        if (esperienza < 0) {
            throw new IllegalArgumentException("L'esperienza non puo' essere negativa: " + esperienza);
        }
        this.livello = livello;
        this.esperienza = esperienza;
    }

    @Override
    public String riepilogoStato() {
        return super.riepilogoStato() + " | " + getNomeClasse() + " liv. " + livello;
    }
}
