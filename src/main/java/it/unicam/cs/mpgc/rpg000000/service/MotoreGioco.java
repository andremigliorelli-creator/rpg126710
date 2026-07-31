package it.unicam.cs.mpgc.rpg000000.service;

import it.unicam.cs.mpgc.rpg000000.api.Azione;
import it.unicam.cs.mpgc.rpg000000.api.EsitoAzione;
import it.unicam.cs.mpgc.rpg000000.api.OsservatoreGioco;
import it.unicam.cs.mpgc.rpg000000.model.combattimento.Combattimento;
import it.unicam.cs.mpgc.rpg000000.model.combattimento.EsitoCombattimento;
import it.unicam.cs.mpgc.rpg000000.model.mondo.Direzione;
import it.unicam.cs.mpgc.rpg000000.model.mondo.Mappa;
import it.unicam.cs.mpgc.rpg000000.model.mondo.Stanza;
import it.unicam.cs.mpgc.rpg000000.model.oggetto.Arma;
import it.unicam.cs.mpgc.rpg000000.model.oggetto.Armatura;
import it.unicam.cs.mpgc.rpg000000.model.oggetto.Oggetto;
import it.unicam.cs.mpgc.rpg000000.model.personaggio.Eroe;
import it.unicam.cs.mpgc.rpg000000.model.personaggio.Nemico;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Regia della partita: tiene insieme eroe, mappa e scontri, e annuncia a chi
 * osserva tutto cio' che accade.
 *
 * <p>Non disegna nulla e non salva nulla: si limita a far rispettare le regole
 * del gioco. Disegnare e' compito dell'interfaccia grafica, salvare e' compito
 * del livello di persistenza. Il motore comunica con entrambi soltanto
 * attraverso astrazioni, quindi puo' essere collaudato senza aprire una
 * finestra e senza scrivere su disco.</p>
 */
public class MotoreGioco {

    private final Eroe eroe;
    private final Mappa mappa;
    private final List<OsservatoreGioco> osservatori = new ArrayList<>();

    private Stanza stanzaCorrente;
    private Stanza stanzaPrecedente;
    private Combattimento combattimentoInCorso;
    private StatoGioco stato = StatoGioco.ESPLORAZIONE;

    /**
     * @param eroe  personaggio controllato dall'utente, non nullo
     * @param mappa dungeon da esplorare, non nullo
     * @throws IllegalArgumentException se un parametro e' nullo
     */
    public MotoreGioco(Eroe eroe, Mappa mappa) {
        if (eroe == null) {
            throw new IllegalArgumentException("L'eroe della partita non puo' essere nullo");
        }
        if (mappa == null) {
            throw new IllegalArgumentException("La mappa della partita non puo' essere nulla");
        }
        this.eroe = eroe;
        this.mappa = mappa;
        this.stanzaCorrente = mappa.getStanzaIniziale();
        this.stanzaPrecedente = stanzaCorrente;
    }

    /**
     * Registra un componente interessato agli eventi della partita.
     *
     * @param osservatore osservatore da registrare, non nullo
     */
    public void registraOsservatore(OsservatoreGioco osservatore) {
        if (osservatore == null) {
            throw new IllegalArgumentException("L'osservatore non puo' essere nullo");
        }
        osservatori.add(osservatore);
    }

    /**
     * Avvia la partita entrando nella stanza corrente e annunciando l'inizio.
     */
    public void iniziaPartita() {
        notifica(TipoEvento.PARTITA_INIZIATA,
                eroe.getNome() + " il " + eroe.getNomeClasse() + " entra nel dungeon.");
        entraNellaStanza(stanzaCorrente);
    }

    /**
     * Riprende una partita ricostruita da un salvataggio, rientrando nella
     * stanza in cui l'eroe era stato lasciato.
     */
    public void riprendiPartita() {
        notifica(TipoEvento.PARTITA_CARICATA,
                "Partita ripresa: " + eroe.riepilogoStato() + ".");
        entraNellaStanza(stanzaCorrente);
    }

    /**
     * Sposta l'eroe nella direzione indicata, se il passaggio esiste e se non
     * si e' impegnati in uno scontro.
     *
     * @param direzione direzione da percorrere, non nulla
     * @throws IllegalArgumentException se la direzione e' nulla
     */
    public void muovi(Direzione direzione) {
        if (direzione == null) {
            throw new IllegalArgumentException("La direzione non puo' essere nulla");
        }
        if (stato != StatoGioco.ESPLORAZIONE) {
            notifica(TipoEvento.MOSSA_NON_VALIDA, messaggioMossaNonConsentita());
            return;
        }
        Optional<Stanza> destinazione = mappa.stanzaAdiacente(stanzaCorrente, direzione);
        if (destinazione.isEmpty()) {
            notifica(TipoEvento.MOSSA_NON_VALIDA,
                    "Non c'e' alcun passaggio verso " + direzione.getEtichetta() + ".");
            return;
        }
        stanzaPrecedente = stanzaCorrente;
        entraNellaStanza(destinazione.get());
    }

    /**
     * Fa eseguire all'eroe la mossa scelta nel combattimento in corso.
     *
     * @param azione mossa scelta, non nulla
     * @throws IllegalArgumentException se l'azione e' nulla
     * @throws IllegalStateException    se non c'e' alcun combattimento in corso
     */
    public void eseguiAzioneCombattimento(Azione azione) {
        if (azione == null) {
            throw new IllegalArgumentException("L'azione non puo' essere nulla");
        }
        if (stato != StatoGioco.COMBATTIMENTO || combattimentoInCorso == null) {
            throw new IllegalStateException("Non c'e' alcun combattimento in corso");
        }
        for (EsitoAzione esito : combattimentoInCorso.eseguiTurno(azione)) {
            notifica(TipoEvento.AZIONE_ESEGUITA, esito.getDescrizione());
        }
        if (combattimentoInCorso.eConcluso()) {
            concludiCombattimento();
        }
    }

    /**
     * @return le mosse che l'eroe puo' scegliere adesso, vuota fuori dal combattimento
     */
    public List<Azione> azioniDisponibili() {
        if (stato != StatoGioco.COMBATTIMENTO) {
            return List.of();
        }
        return eroe.azioniDisponibili();
    }

    /**
     * @return le direzioni percorribili dalla stanza corrente
     */
    public List<Direzione> direzioniDisponibili() {
        return List.copyOf(stanzaCorrente.getUscite().keySet());
    }

    public Eroe getEroe() {
        return eroe;
    }

    public Mappa getMappa() {
        return mappa;
    }

    public Stanza getStanzaCorrente() {
        return stanzaCorrente;
    }

    public StatoGioco getStato() {
        return stato;
    }

    /**
     * @return il nemico attualmente affrontato, se si sta combattendo
     */
    public Optional<Nemico> getNemicoInCombattimento() {
        if (combattimentoInCorso == null) {
            return Optional.empty();
        }
        return Optional.of((Nemico) combattimentoInCorso.getNemico());
    }

    /**
     * Riporta l'eroe in una stanza specifica, usato quando si riprende una
     * partita salvata.
     *
     * @param idStanza identificatore della stanza, presente nella mappa
     * @throws IllegalArgumentException se la stanza non esiste
     */
    public void posizionaIn(String idStanza) {
        Stanza stanza = mappa.getStanza(idStanza)
                .orElseThrow(() -> new IllegalArgumentException("Stanza inesistente: " + idStanza));
        this.stanzaCorrente = stanza;
        this.stanzaPrecedente = stanza;
    }

    private void entraNellaStanza(Stanza stanza) {
        stanzaCorrente = stanza;
        stanzaCorrente.segnaComeVisitata();
        notifica(TipoEvento.STANZA_CAMBIATA, stanza.getNome() + ". " + stanza.getDescrizione());
        if (!avviaCombattimentoSePresenteNemico()) {
            risolviStanzaLibera();
        }
    }

    private boolean avviaCombattimentoSePresenteNemico() {
        Optional<Nemico> nemico = stanzaCorrente.getPrimoNemicoVivo();
        if (nemico.isEmpty()) {
            return false;
        }
        Nemico avversario = nemico.get();
        combattimentoInCorso = new Combattimento(eroe, avversario, avversario.getStrategia());
        stato = StatoGioco.COMBATTIMENTO;
        notifica(TipoEvento.COMBATTIMENTO_INIZIATO,
                avversario.getNome() + " ti sbarra la strada! (" + avversario.riepilogoStato() + ")");
        return true;
    }

    private void concludiCombattimento() {
        EsitoCombattimento esito = combattimentoInCorso.getStato();
        Nemico avversario = (Nemico) combattimentoInCorso.getNemico();
        notifica(TipoEvento.COMBATTIMENTO_TERMINATO, esito.getMessaggio() + ".");
        combattimentoInCorso = null;

        switch (esito) {
            case SCONFITTA -> dichiaraSconfitta();
            case FUGA_EROE -> tornaAllaStanzaPrecedente();
            case VITTORIA -> {
                assegnaRicompense(avversario);
                stanzaCorrente.rimuoviNemico(avversario);
                riprendiEsplorazione();
            }
            case FUGA_NEMICO -> {
                stanzaCorrente.rimuoviNemico(avversario);
                riprendiEsplorazione();
            }
            default -> throw new IllegalStateException("Esito di combattimento inatteso: " + esito);
        }
    }

    private void riprendiEsplorazione() {
        stato = StatoGioco.ESPLORAZIONE;
        if (!avviaCombattimentoSePresenteNemico()) {
            risolviStanzaLibera();
        }
    }

    private void risolviStanzaLibera() {
        stato = StatoGioco.ESPLORAZIONE;
        raccogliTesori();
        verificaUscitaDungeon();
    }

    private void assegnaRicompense(Nemico avversario) {
        int livelliGuadagnati = eroe.guadagnaEsperienza(avversario.getEsperienzaConcessa());
        notifica(TipoEvento.AZIONE_ESEGUITA,
                "Guadagni " + avversario.getEsperienzaConcessa() + " punti esperienza.");
        if (livelliGuadagnati > 0) {
            notifica(TipoEvento.LIVELLO_AUMENTATO,
                    eroe.getNome() + " sale al livello " + eroe.getLivello() + "!");
        }
        avversario.getBottino().ifPresent(bottino -> {
            stanzaCorrente.aggiungiTesoro(bottino);
            notifica(TipoEvento.AZIONE_ESEGUITA, avversario.getNome() + " lascia cadere " + bottino.getNome() + ".");
        });
    }

    private void raccogliTesori() {
        for (Oggetto tesoro : stanzaCorrente.raccogliTesori()) {
            eroe.raccogli(tesoro);
            notifica(TipoEvento.TESORO_RACCOLTO, "Raccogli " + tesoro.riepilogo() + ".");
            equipaggiaSeMigliore(tesoro);
        }
    }

    /**
     * Indossa automaticamente un oggetto appena raccolto se e' migliore di
     * quello attualmente equipaggiato.
     *
     * <p>E' una regola di gioco, non una scelta di interfaccia: per questo vive
     * nel motore e non nella finestra. Evita all'utente di dover ricordare di
     * equipaggiare ogni ritrovamento.</p>
     */
    private void equipaggiaSeMigliore(Oggetto oggetto) {
        if (oggetto instanceof Arma arma && armaMigliore(arma) && eroe.equipaggia(arma)) {
            notifica(TipoEvento.AZIONE_ESEGUITA, "Impugni " + arma.riepilogo() + ".");
        } else if (oggetto instanceof Armatura armatura && armaturaMigliore(armatura)
                && eroe.equipaggia(armatura)) {
            notifica(TipoEvento.AZIONE_ESEGUITA, "Indossi " + armatura.riepilogo() + ".");
        }
    }

    private boolean armaMigliore(Arma candidata) {
        return eroe.getEquipaggiamento().getArma()
                .map(attuale -> candidata.getBonusAttacco() > attuale.getBonusAttacco())
                .orElse(true);
    }

    private boolean armaturaMigliore(Armatura candidata) {
        return eroe.getEquipaggiamento().getArmatura()
                .map(attuale -> candidata.getBonusDifesa() > attuale.getBonusDifesa())
                .orElse(true);
    }

    private void verificaUscitaDungeon() {
        if (stanzaCorrente.eUscitaDelDungeon()) {
            stato = StatoGioco.VITTORIA;
            notifica(TipoEvento.PARTITA_VINTA,
                    eroe.getNome() + " raggiunge l'uscita del dungeon. Vittoria!");
        }
    }

    private void dichiaraSconfitta() {
        stato = StatoGioco.SCONFITTA;
        notifica(TipoEvento.PARTITA_PERSA, eroe.getNome() + " cade nel dungeon. Partita terminata.");
    }

    private void tornaAllaStanzaPrecedente() {
        stato = StatoGioco.ESPLORAZIONE;
        entraNellaStanza(stanzaPrecedente);
    }

    private String messaggioMossaNonConsentita() {
        if (stato == StatoGioco.COMBATTIMENTO) {
            return "Non puoi allontanarti mentre sei impegnato in uno scontro.";
        }
        return "La partita e' terminata: nessun movimento consentito.";
    }

    /**
     * Annuncia un evento a tutti gli osservatori registrati.
     *
     * @param tipo      categoria dell'evento
     * @param messaggio testo leggibile
     */
    private void notifica(TipoEvento tipo, String messaggio) {
        EventoGioco evento = new EventoGioco(tipo, messaggio);
        for (OsservatoreGioco osservatore : osservatori) {
            osservatore.suEvento(evento);
        }
    }
}
