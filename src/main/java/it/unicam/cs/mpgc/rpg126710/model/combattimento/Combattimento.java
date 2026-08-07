package it.unicam.cs.mpgc.rpg126710.model.combattimento;

import it.unicam.cs.mpgc.rpg126710.api.Azione;
import it.unicam.cs.mpgc.rpg126710.api.Combattente;
import it.unicam.cs.mpgc.rpg126710.api.EsitoAzione;
import it.unicam.cs.mpgc.rpg126710.api.StrategiaCombattimento;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Scontro a turni fra l'eroe e un singolo nemico.
 *
 * <p>La classe ha un'unica responsabilita': far rispettare le regole del turno
 * e stabilire quando lo scontro finisce. Non sa nulla di esperienza, bottino o
 * interfaccia grafica, che restano compiti di chi la usa.</p>
 *
 * <p>Lavora su {@link Combattente} e non su classi concrete: qualunque cosa
 * rispetti quel contratto puo' combattere, e per introdurre un nuovo tipo di
 * nemico non serve modificare una riga di questa classe.</p>
 */
public class Combattimento {

    private final Combattente eroe;
    private final Combattente nemico;
    private final StrategiaCombattimento strategiaNemico;
    private final List<String> diario = new ArrayList<>();

    private EsitoCombattimento stato = EsitoCombattimento.IN_CORSO;
    private int numeroTurno;

    /**
     * @param eroe            combattente controllato dall'utente, non nullo
     * @param nemico          avversario, non nullo
     * @param strategiaNemico criterio con cui l'avversario sceglie le mosse, non nullo
     * @throws IllegalArgumentException se un parametro e' nullo
     */
    public Combattimento(Combattente eroe, Combattente nemico, StrategiaCombattimento strategiaNemico) {
        if (eroe == null) {
            throw new IllegalArgumentException("L'eroe del combattimento non puo' essere nullo");
        }
        if (nemico == null) {
            throw new IllegalArgumentException("Il nemico del combattimento non puo' essere nullo");
        }
        if (strategiaNemico == null) {
            throw new IllegalArgumentException("La strategia del nemico non puo' essere nulla");
        }
        this.eroe = eroe;
        this.nemico = nemico;
        this.strategiaNemico = strategiaNemico;
    }

    /**
     * Esegue un turno completo: prima agisce l'eroe, poi, se lo scontro e'
     * ancora aperto, risponde il nemico secondo la propria strategia.
     *
     * @param azioneEroe mossa scelta dall'utente, non nulla
     * @return gli esiti prodotti nel turno, in ordine cronologico
     * @throws IllegalArgumentException se l'azione e' nulla
     * @throws IllegalStateException    se lo scontro e' gia' concluso
     */
    public List<EsitoAzione> eseguiTurno(Azione azioneEroe) {
        if (azioneEroe == null) {
            throw new IllegalArgumentException("L'azione dell'eroe non puo' essere nulla");
        }
        if (stato.eConcluso()) {
            throw new IllegalStateException("Il combattimento e' gia' terminato: " + stato.getMessaggio());
        }

        numeroTurno++;
        List<EsitoAzione> esitiDelTurno = new ArrayList<>();

        esitiDelTurno.add(registra(azioneEroe.esegui(eroe, nemico)));
        if (aggiornaStatoDopoMossaEroe(esitiDelTurno.getLast())) {
            return Collections.unmodifiableList(esitiDelTurno);
        }

        Azione azioneNemico = strategiaNemico.scegliAzione(nemico, eroe);
        esitiDelTurno.add(registra(azioneNemico.esegui(nemico, eroe)));
        aggiornaStatoDopoMossaNemico(esitiDelTurno.getLast());

        return Collections.unmodifiableList(esitiDelTurno);
    }

    /**
     * @return lo stato corrente dello scontro
     */
    public EsitoCombattimento getStato() {
        return stato;
    }

    /**
     * @return {@code true} se lo scontro e' terminato
     */
    public boolean eConcluso() {
        return stato.eConcluso();
    }

    /**
     * @return il numero di turni giocati finora
     */
    public int getNumeroTurno() {
        return numeroTurno;
    }

    public Combattente getEroe() {
        return eroe;
    }

    public Combattente getNemico() {
        return nemico;
    }

    /**
     * @return il resoconto testuale dello scontro, in sola lettura
     */
    public List<String> getDiario() {
        return Collections.unmodifiableList(diario);
    }

    private EsitoAzione registra(EsitoAzione esito) {
        diario.add(esito.getDescrizione());
        return esito;
    }

    private boolean aggiornaStatoDopoMossaEroe(EsitoAzione esito) {
        if (esito.terminaCombattimento()) {
            stato = EsitoCombattimento.FUGA_EROE;
            return true;
        }
        if (!nemico.eVivo()) {
            stato = EsitoCombattimento.VITTORIA;
            return true;
        }
        return false;
    }

    private void aggiornaStatoDopoMossaNemico(EsitoAzione esito) {
        if (esito.terminaCombattimento()) {
            stato = EsitoCombattimento.FUGA_NEMICO;
            return;
        }
        if (!eroe.eVivo()) {
            stato = EsitoCombattimento.SCONFITTA;
        }
    }
}
