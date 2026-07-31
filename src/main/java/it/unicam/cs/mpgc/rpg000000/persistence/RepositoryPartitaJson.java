package it.unicam.cs.mpgc.rpg000000.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import it.unicam.cs.mpgc.rpg000000.api.RepositoryPartita;
import it.unicam.cs.mpgc.rpg000000.service.StatoPartita;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Conserva la partita in un file JSON sul disco dell'utente.
 *
 * <p>A differenza del file dei contenuti, che sta fra le risorse ed e' di sola
 * lettura, il salvataggio e' un dato dinamico che cambia a ogni partita: per
 * questo viene scritto in una cartella esterna all'applicazione e non dentro il
 * pacchetto eseguibile, che a esecuzione avviata non e' modificabile.</p>
 *
 * <p>La scrittura passa da un file temporaneo che sostituisce il precedente
 * solo a operazione completata: se il programma venisse interrotto a meta'
 * salvataggio, la partita precedente resterebbe comunque integra.</p>
 */
public class RepositoryPartitaJson implements RepositoryPartita {

    private static final String CARTELLA_PREDEFINITA = "salvataggi";
    private static final String NOME_FILE = "partita.json";

    private final Path percorsoFile;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Crea un archivio che scrive nella cartella predefinita dei salvataggi.
     */
    public RepositoryPartitaJson() {
        this(Path.of(CARTELLA_PREDEFINITA, NOME_FILE));
    }

    /**
     * @param percorsoFile percorso del file di salvataggio, non nullo
     * @throws IllegalArgumentException se il percorso e' nullo
     */
    public RepositoryPartitaJson(Path percorsoFile) {
        if (percorsoFile == null) {
            throw new IllegalArgumentException("Il percorso del salvataggio non puo' essere nullo");
        }
        this.percorsoFile = percorsoFile;
    }

    @Override
    public void salva(StatoPartita stato) {
        if (stato == null) {
            throw new IllegalArgumentException("Lo stato da salvare non puo' essere nullo");
        }
        Path fileTemporaneo = percorsoFile.resolveSibling(NOME_FILE + ".tmp");
        try {
            creaCartellaSeMancante();
            try (Writer scrittore = Files.newBufferedWriter(fileTemporaneo, StandardCharsets.UTF_8)) {
                gson.toJson(stato, scrittore);
            }
            Files.move(fileTemporaneo, percorsoFile,
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException errore) {
            throw new IllegalStateException("Impossibile salvare la partita in " + percorsoFile, errore);
        }
    }

    @Override
    public Optional<StatoPartita> carica() {
        if (!esisteSalvataggio()) {
            return Optional.empty();
        }
        try (Reader lettore = Files.newBufferedReader(percorsoFile, StandardCharsets.UTF_8)) {
            StatoPartita stato = gson.fromJson(lettore, StatoPartita.class);
            if (stato == null) {
                throw new IllegalStateException("Il file di salvataggio e' vuoto: " + percorsoFile);
            }
            stato.verificaCoerenza();
            return Optional.of(stato);
        } catch (IOException | JsonParseException errore) {
            throw new IllegalStateException("Impossibile leggere la partita da " + percorsoFile, errore);
        }
    }

    @Override
    public boolean esisteSalvataggio() {
        return Files.isRegularFile(percorsoFile);
    }

    private void creaCartellaSeMancante() throws IOException {
        Path cartella = percorsoFile.getParent();
        if (cartella != null) {
            Files.createDirectories(cartella);
        }
    }
}
