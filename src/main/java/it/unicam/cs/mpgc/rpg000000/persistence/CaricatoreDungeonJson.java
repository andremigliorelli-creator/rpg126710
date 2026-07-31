package it.unicam.cs.mpgc.rpg000000.persistence;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import it.unicam.cs.mpgc.rpg000000.api.CatalogoOggetti;
import it.unicam.cs.mpgc.rpg000000.api.Dado;
import it.unicam.cs.mpgc.rpg000000.api.GeneratoreMappa;
import it.unicam.cs.mpgc.rpg000000.api.StrategiaCombattimento;
import it.unicam.cs.mpgc.rpg000000.model.combattimento.StrategiaAggressiva;
import it.unicam.cs.mpgc.rpg000000.model.combattimento.StrategiaCauta;
import it.unicam.cs.mpgc.rpg000000.model.mondo.Direzione;
import it.unicam.cs.mpgc.rpg000000.model.mondo.Mappa;
import it.unicam.cs.mpgc.rpg000000.model.mondo.Stanza;
import it.unicam.cs.mpgc.rpg000000.model.oggetto.Arma;
import it.unicam.cs.mpgc.rpg000000.model.oggetto.Armatura;
import it.unicam.cs.mpgc.rpg000000.model.oggetto.CategoriaOggetto;
import it.unicam.cs.mpgc.rpg000000.model.oggetto.Oggetto;
import it.unicam.cs.mpgc.rpg000000.model.oggetto.Pozione;
import it.unicam.cs.mpgc.rpg000000.model.personaggio.Nemico;
import it.unicam.cs.mpgc.rpg000000.model.personaggio.Statistiche;
import it.unicam.cs.mpgc.rpg000000.persistence.dto.DatiDungeon;
import it.unicam.cs.mpgc.rpg000000.persistence.dto.DatiNemico;
import it.unicam.cs.mpgc.rpg000000.persistence.dto.DatiOggetto;
import it.unicam.cs.mpgc.rpg000000.persistence.dto.DatiStanza;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Costruisce il dungeon leggendo il file di configurazione incluso fra le
 * risorse dell'applicazione.
 *
 * <p>I contenuti del gioco vivono in un file JSON e non nel codice: aggiungere
 * una stanza, un nemico o un oggetto significa modificare quel file, senza
 * ricompilare nulla e senza toccare il motore. Il file sta in
 * {@code src/main/resources} perche' descrive dati statici di configurazione:
 * viene impacchettato nel JAR e letto in sola lettura.</p>
 *
 * <p>La classe implementa due contratti distinti, {@link GeneratoreMappa} e
 * {@link CatalogoOggetti}, perche' chi la usa ne ha bisogno in momenti diversi:
 * il motore vuole una mappa, il caricamento di una partita vuole poter
 * ricostruire gli oggetti dai loro nomi.</p>
 */
public class CaricatoreDungeonJson implements GeneratoreMappa, CatalogoOggetti {

    private static final String RISORSA_PREDEFINITA = "dungeon.json";

    private final String nomeRisorsa;
    private final Dado dado;
    private final Map<String, Oggetto> catalogo = new LinkedHashMap<>();

    /**
     * Crea un caricatore che legge il file di configurazione predefinito.
     *
     * @param dado sorgente di casualita' assegnata ai nemici creati, non nulla
     */
    public CaricatoreDungeonJson(Dado dado) {
        this(RISORSA_PREDEFINITA, dado);
    }

    /**
     * @param nomeRisorsa nome del file di configurazione fra le risorse, non vuoto
     * @param dado        sorgente di casualita' assegnata ai nemici creati, non nulla
     * @throws IllegalArgumentException se un parametro non rispetta i vincoli
     */
    public CaricatoreDungeonJson(String nomeRisorsa, Dado dado) {
        if (nomeRisorsa == null || nomeRisorsa.isBlank()) {
            throw new IllegalArgumentException("Il nome della risorsa non puo' essere vuoto");
        }
        if (dado == null) {
            throw new IllegalArgumentException("Il dado non puo' essere nullo");
        }
        this.nomeRisorsa = nomeRisorsa;
        this.dado = dado;
    }

    @Override
    public Mappa genera() {
        DatiDungeon dati = leggiConfigurazione();
        popolaCatalogo(dati);
        Map<String, DatiNemico> modelliNemico = indicizzaModelliNemico(dati);
        Map<String, Stanza> stanze = creaStanze(dati, modelliNemico);
        collegaStanze(dati, stanze);
        return new Mappa(stanze, dati.getStanzaIniziale());
    }

    @Override
    public Optional<Oggetto> trova(String nome) {
        if (nome == null) {
            throw new IllegalArgumentException("Il nome dell'oggetto non puo' essere nullo");
        }
        if (catalogo.isEmpty()) {
            popolaCatalogo(leggiConfigurazione());
        }
        return Optional.ofNullable(catalogo.get(nome));
    }

    @Override
    public Collection<Oggetto> tutti() {
        if (catalogo.isEmpty()) {
            popolaCatalogo(leggiConfigurazione());
        }
        return Collections.unmodifiableCollection(catalogo.values());
    }

    private DatiDungeon leggiConfigurazione() {
        InputStream flusso = getClass().getClassLoader().getResourceAsStream(nomeRisorsa);
        if (flusso == null) {
            throw new IllegalStateException("File di configurazione non trovato fra le risorse: " + nomeRisorsa);
        }
        try (Reader lettore = new InputStreamReader(flusso, StandardCharsets.UTF_8)) {
            DatiDungeon dati = new Gson().fromJson(lettore, DatiDungeon.class);
            if (dati == null) {
                throw new IllegalStateException("Il file di configurazione e' vuoto: " + nomeRisorsa);
            }
            return dati;
        } catch (IOException | JsonParseException errore) {
            throw new IllegalStateException("Impossibile leggere il file di configurazione: " + nomeRisorsa, errore);
        }
    }

    private void popolaCatalogo(DatiDungeon dati) {
        catalogo.clear();
        for (DatiOggetto datiOggetto : dati.getOggetti()) {
            catalogo.put(datiOggetto.getNome(), creaOggetto(datiOggetto));
        }
    }

    private Oggetto creaOggetto(DatiOggetto dati) {
        CategoriaOggetto categoria = convertiCategoria(dati.getCategoria(), dati.getNome());
        return switch (categoria) {
            case ARMA -> new Arma(dati.getNome(), dati.getDescrizione(), dati.getValore(), dati.getPotenza());
            case ARMATURA -> new Armatura(dati.getNome(), dati.getDescrizione(), dati.getValore(), dati.getPotenza());
            case POZIONE -> new Pozione(dati.getNome(), dati.getDescrizione(), dati.getValore(), dati.getPotenza());
        };
    }

    private CategoriaOggetto convertiCategoria(String categoria, String nomeOggetto) {
        try {
            return CategoriaOggetto.valueOf(categoria);
        } catch (IllegalArgumentException | NullPointerException errore) {
            throw new IllegalStateException(
                    "Categoria sconosciuta '" + categoria + "' per l'oggetto '" + nomeOggetto + "'", errore);
        }
    }

    private Map<String, DatiNemico> indicizzaModelliNemico(DatiDungeon dati) {
        Map<String, DatiNemico> modelli = new LinkedHashMap<>();
        for (DatiNemico modello : dati.getNemici()) {
            modelli.put(modello.getId(), modello);
        }
        return modelli;
    }

    private Map<String, Stanza> creaStanze(DatiDungeon dati, Map<String, DatiNemico> modelliNemico) {
        Map<String, Stanza> stanze = new LinkedHashMap<>();
        for (DatiStanza datiStanza : dati.getStanze()) {
            Stanza stanza = new Stanza(
                    datiStanza.getId(),
                    datiStanza.getNome(),
                    datiStanza.getDescrizione(),
                    datiStanza.isUscitaDelDungeon());
            popolaNemici(stanza, datiStanza, modelliNemico);
            popolaTesori(stanza, datiStanza);
            stanze.put(stanza.getId(), stanza);
        }
        return stanze;
    }

    private void popolaNemici(Stanza stanza, DatiStanza datiStanza, Map<String, DatiNemico> modelliNemico) {
        for (String idModello : datiStanza.getNemici()) {
            DatiNemico modello = modelliNemico.get(idModello);
            if (modello == null) {
                throw new IllegalStateException("La stanza '" + stanza.getId()
                        + "' fa riferimento a un nemico inesistente: " + idModello);
            }
            stanza.aggiungiNemico(creaNemico(modello));
        }
    }

    private Nemico creaNemico(DatiNemico modello) {
        Statistiche statistiche = new Statistiche(modello.getAttacco(), modello.getDifesa(), modello.getPuntiVita());
        Oggetto bottino = modello.getBottino() == null ? null : richiediOggetto(modello.getBottino());
        return new Nemico(modello.getNome(), statistiche, dado, creaStrategia(modello), modello.getEsperienza(), bottino);
    }

    private StrategiaCombattimento creaStrategia(DatiNemico modello) {
        String nomeStrategia = modello.getStrategia() == null ? "" : modello.getStrategia().toUpperCase();
        return switch (nomeStrategia) {
            case "AGGRESSIVA" -> new StrategiaAggressiva();
            case "CAUTA" -> new StrategiaCauta(dado);
            default -> throw new IllegalStateException("Strategia sconosciuta '" + modello.getStrategia()
                    + "' per il nemico '" + modello.getNome() + "'");
        };
    }

    private void popolaTesori(Stanza stanza, DatiStanza datiStanza) {
        for (String nomeOggetto : datiStanza.getTesori()) {
            stanza.aggiungiTesoro(richiediOggetto(nomeOggetto));
        }
    }

    private Oggetto richiediOggetto(String nome) {
        Oggetto oggetto = catalogo.get(nome);
        if (oggetto == null) {
            throw new IllegalStateException("Oggetto non presente nel catalogo: " + nome);
        }
        return oggetto;
    }

    /**
     * Collega le stanze fra loro rendendo ogni passaggio percorribile nei due
     * sensi: il file dichiara il collegamento una volta sola e qui viene
     * aggiunto anche il ritorno.
     */
    private void collegaStanze(DatiDungeon dati, Map<String, Stanza> stanze) {
        for (DatiStanza datiStanza : dati.getStanze()) {
            Stanza partenza = stanze.get(datiStanza.getId());
            for (Map.Entry<String, String> uscita : datiStanza.getUscite().entrySet()) {
                Direzione direzione = convertiDirezione(uscita.getKey(), datiStanza.getId());
                Stanza arrivo = stanze.get(uscita.getValue());
                if (arrivo == null) {
                    throw new IllegalStateException("La stanza '" + datiStanza.getId()
                            + "' ha un'uscita verso una stanza inesistente: " + uscita.getValue());
                }
                partenza.collega(direzione, arrivo.getId());
                arrivo.collega(direzione.opposta(), partenza.getId());
            }
        }
    }

    private Direzione convertiDirezione(String direzione, String idStanza) {
        try {
            return Direzione.valueOf(direzione.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException errore) {
            throw new IllegalStateException(
                    "Direzione sconosciuta '" + direzione + "' nella stanza '" + idStanza + "'", errore);
        }
    }
}
