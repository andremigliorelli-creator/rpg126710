package it.unicam.cs.mpgc.rpg126710.service;

import it.unicam.cs.mpgc.rpg126710.api.CatalogoOggetti;
import it.unicam.cs.mpgc.rpg126710.api.Dado;
import it.unicam.cs.mpgc.rpg126710.api.GeneratoreMappa;
import it.unicam.cs.mpgc.rpg126710.api.RepositoryPartita;
import it.unicam.cs.mpgc.rpg126710.model.mondo.Mappa;
import it.unicam.cs.mpgc.rpg126710.model.mondo.Stanza;
import it.unicam.cs.mpgc.rpg126710.model.oggetto.Arma;
import it.unicam.cs.mpgc.rpg126710.model.oggetto.Armatura;
import it.unicam.cs.mpgc.rpg126710.model.oggetto.Oggetto;
import it.unicam.cs.mpgc.rpg126710.model.personaggio.Eroe;
import it.unicam.cs.mpgc.rpg126710.model.personaggio.Nemico;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Coordina l'avvio, il salvataggio e il recupero di una partita.
 *
 * <p>E' il punto in cui si incontrano i pezzi che il motore di gioco non deve
 * conoscere: da dove arriva la mappa, dove finiscono i salvataggi, come si
 * costruisce un eroe. Riceve tutti questi collaboratori come astrazioni, quindi
 * puo' essere collaudato con una mappa costruita a mano e un archivio
 * temporaneo.</p>
 */
public class GestorePartita {

    private final GeneratoreMappa generatoreMappa;
    private final CatalogoOggetti catalogoOggetti;
    private final RepositoryPartita repository;
    private final FabbricaEroi fabbricaEroi;
    private final Dado dado;

    /**
     * @param generatoreMappa sorgente del dungeon, non nullo
     * @param catalogoOggetti catalogo per ricostruire gli oggetti salvati, non nullo
     * @param repository      archivio delle partite, non nullo
     * @param fabbricaEroi    creatore di eroi, non nullo
     * @param dado            sorgente di casualita', non nulla
     * @throws IllegalArgumentException se un parametro e' nullo
     */
    public GestorePartita(GeneratoreMappa generatoreMappa,
                          CatalogoOggetti catalogoOggetti,
                          RepositoryPartita repository,
                          FabbricaEroi fabbricaEroi,
                          Dado dado) {
        if (generatoreMappa == null || catalogoOggetti == null
                || repository == null || fabbricaEroi == null || dado == null) {
            throw new IllegalArgumentException("I collaboratori del gestore partita non possono essere nulli");
        }
        this.generatoreMappa = generatoreMappa;
        this.catalogoOggetti = catalogoOggetti;
        this.repository = repository;
        this.fabbricaEroi = fabbricaEroi;
        this.dado = dado;
    }

    /**
     * Prepara una partita nuova con l'eroe richiesto.
     *
     * @param nomeEroe   nome del personaggio, non vuoto
     * @param nomeClasse classe giocabile, fra quelle disponibili
     * @return il motore pronto a essere avviato
     */
    public MotoreGioco nuovaPartita(String nomeEroe, String nomeClasse) {
        Eroe eroe = fabbricaEroi.crea(nomeClasse, nomeEroe, dado);
        return new MotoreGioco(eroe, generatoreMappa.genera());
    }

    /**
     * @return {@code true} se esiste una partita da riprendere
     */
    public boolean esisteSalvataggio() {
        return repository.esisteSalvataggio();
    }

    /**
     * Salva lo stato attuale della partita.
     *
     * @param motore partita in corso, non nulla
     * @throws IllegalArgumentException se il motore e' nullo
     */
    public void salva(MotoreGioco motore) {
        if (motore == null) {
            throw new IllegalArgumentException("Il motore di gioco non puo' essere nullo");
        }
        repository.salva(creaFotografia(motore));
    }

    /**
     * Ricostruisce la partita conservata nell'archivio.
     *
     * @return il motore ripristinato, vuoto se non esiste alcun salvataggio
     */
    public Optional<MotoreGioco> carica() {
        return repository.carica().map(this::ricostruisci);
    }

    private StatoPartita creaFotografia(MotoreGioco motore) {
        Eroe eroe = motore.getEroe();
        StatoPartita stato = new StatoPartita(
                eroe.getNome(),
                eroe.getNomeClasse(),
                eroe.getLivello(),
                eroe.getEsperienza(),
                eroe.getPuntiVita(),
                motore.getStanzaCorrente().getId());

        for (Map.Entry<Oggetto, Integer> voce : eroe.getInventario().getContenuto().entrySet()) {
            stato.aggiungiVoceInventario(voce.getKey().getNome(), voce.getValue());
        }
        eroe.getEquipaggiamento().getArma()
                .ifPresent(arma -> stato.setArmaEquipaggiata(arma.getNome()));
        eroe.getEquipaggiamento().getArmatura()
                .ifPresent(armatura -> stato.setArmaturaEquipaggiata(armatura.getNome()));

        registraProgressoStanze(motore.getMappa(), stato);
        return stato;
    }

    private void registraProgressoStanze(Mappa mappa, StatoPartita stato) {
        for (Stanza stanza : mappa.getStanze().values()) {
            if (!stanza.eVisitata()) {
                continue;
            }
            stato.segnaStanzaVisitata(stanza.getId());
            if (stanza.eLibera() && stanza.getTesori().isEmpty()) {
                stato.segnaStanzaCompletata(stanza.getId());
            }
        }
    }

    private MotoreGioco ricostruisci(StatoPartita stato) {
        stato.verificaCoerenza();
        Mappa mappa = generatoreMappa.genera();
        Eroe eroe = fabbricaEroi.crea(stato.getClasseEroe(), stato.getNomeEroe(), dado);

        eroe.ripristinaProgressione(stato.getLivello(), stato.getEsperienza());
        ripristinaInventario(eroe, stato);
        ripristinaEquipaggiamento(eroe, stato);
        eroe.impostaPuntiVita(Math.min(stato.getPuntiVita(), eroe.getPuntiVitaMassimi()));

        ripristinaProgressoStanze(mappa, stato);

        MotoreGioco motore = new MotoreGioco(eroe, mappa);
        motore.posizionaIn(stato.getIdStanzaCorrente());
        return motore;
    }

    private void ripristinaInventario(Eroe eroe, StatoPartita stato) {
        for (Map.Entry<String, Integer> voce : stato.getInventario().entrySet()) {
            catalogoOggetti.trova(voce.getKey())
                    .ifPresent(oggetto -> eroe.getInventario().aggiungi(oggetto, voce.getValue()));
        }
    }

    private void ripristinaEquipaggiamento(Eroe eroe, StatoPartita stato) {
        stato.getArmaEquipaggiata()
                .flatMap(catalogoOggetti::trova)
                .filter(Arma.class::isInstance)
                .map(Arma.class::cast)
                .ifPresent(eroe::equipaggia);
        stato.getArmaturaEquipaggiata()
                .flatMap(catalogoOggetti::trova)
                .filter(Armatura.class::isInstance)
                .map(Armatura.class::cast)
                .ifPresent(eroe::equipaggia);
    }

    /**
     * Riporta il dungeon nella condizione in cui era stato lasciato: le stanze
     * gia' viste risultano visitate e quelle completate restano prive di nemici
     * e di tesori.
     */
    private void ripristinaProgressoStanze(Mappa mappa, StatoPartita stato) {
        for (String idStanza : stato.getIdStanzeVisitate()) {
            mappa.getStanza(idStanza).ifPresent(Stanza::segnaComeVisitata);
        }
        for (String idStanza : stato.getIdStanzeCompletate()) {
            mappa.getStanza(idStanza).ifPresent(this::svuota);
        }
    }

    private void svuota(Stanza stanza) {
        List<Nemico> presenti = List.copyOf(stanza.getNemici());
        presenti.forEach(stanza::rimuoviNemico);
        stanza.raccogliTesori();
    }
}
