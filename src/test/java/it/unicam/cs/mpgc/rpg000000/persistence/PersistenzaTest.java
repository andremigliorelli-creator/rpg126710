package it.unicam.cs.mpgc.rpg000000.persistence;

import it.unicam.cs.mpgc.rpg000000.model.mondo.Direzione;
import it.unicam.cs.mpgc.rpg000000.model.mondo.Mappa;
import it.unicam.cs.mpgc.rpg000000.model.mondo.Stanza;
import it.unicam.cs.mpgc.rpg000000.service.StatoPartita;
import it.unicam.cs.mpgc.rpg000000.util.DadoFisso;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Persistenza")
class PersistenzaTest {

    @Nested
    @DisplayName("Caricamento del dungeon")
    class CaricamentoDungeon {

        private final CaricatoreDungeonJson caricatore = new CaricatoreDungeonJson(new DadoFisso(3));

        @Test
        @DisplayName("costruisce una mappa coerente a partire dalla configurazione")
        void mappaCoerente() {
            Mappa mappa = caricatore.genera();
            assertAll(
                    () -> assertEquals(8, mappa.numeroStanze()),
                    () -> assertEquals("ingresso", mappa.getStanzaIniziale().getId()),
                    () -> assertTrue(mappa.getStanza("uscita").orElseThrow().eUscitaDelDungeon()));
        }

        @Test
        @DisplayName("rende i passaggi percorribili nei due sensi")
        void passaggiBidirezionali() {
            Mappa mappa = caricatore.genera();
            Stanza ingresso = mappa.getStanzaIniziale();
            Stanza corridoio = mappa.stanzaAdiacente(ingresso, Direzione.NORD).orElseThrow();
            Stanza ritorno = mappa.stanzaAdiacente(corridoio, Direzione.SUD).orElseThrow();
            assertEquals(ingresso, ritorno);
        }

        @Test
        @DisplayName("popola le stanze con nemici e tesori dichiarati")
        void contenutoStanze() {
            Mappa mappa = caricatore.genera();
            assertAll(
                    () -> assertFalse(mappa.getStanzaIniziale().getTesori().isEmpty()),
                    () -> assertTrue(mappa.getStanza("corridoio").orElseThrow()
                            .getPrimoNemicoVivo().isPresent()));
        }

        @Test
        @DisplayName("crea istanze distinte per lo stesso modello di nemico")
        void nemiciIndipendenti() {
            Mappa mappa = caricatore.genera();
            var primoScheletro = mappa.getStanza("cripta").orElseThrow().getPrimoNemicoVivo().orElseThrow();
            var secondoScheletro = mappa.getStanza("armeria").orElseThrow().getPrimoNemicoVivo().orElseThrow();

            primoScheletro.subisciDanno(5);

            assertAll(
                    () -> assertEquals(13, primoScheletro.getPuntiVita()),
                    () -> assertEquals(18, secondoScheletro.getPuntiVita()));
        }

        @Test
        @DisplayName("espone il catalogo degli oggetti per nome")
        void catalogoConsultabile() {
            assertAll(
                    () -> assertTrue(caricatore.trova("Spada corta").isPresent()),
                    () -> assertTrue(caricatore.trova("Oggetto inventato").isEmpty()),
                    () -> assertEquals(7, caricatore.tutti().size()));
        }

        @Test
        @DisplayName("segnala una configurazione mancante invece di fallire in silenzio")
        void configurazioneMancante() {
            CaricatoreDungeonJson inesistente =
                    new CaricatoreDungeonJson("dungeon-che-non-esiste.json", new DadoFisso(3));
            assertThrows(IllegalStateException.class, inesistente::genera);
        }
    }

    @Nested
    @DisplayName("Archivio delle partite")
    class ArchivioPartite {

        @TempDir
        Path cartellaTemporanea;

        private StatoPartita statoDiEsempio() {
            StatoPartita stato = new StatoPartita("Aldo", "Guerriero", 2, 50, 24, "corridoio");
            stato.aggiungiVoceInventario("Pozione minore", 2);
            stato.setArmaEquipaggiata("Spada corta");
            stato.segnaStanzaVisitata("ingresso");
            stato.segnaStanzaCompletata("ingresso");
            return stato;
        }

        @Test
        @DisplayName("non trova nulla se non e' mai stato salvato niente")
        void archivioVuoto() {
            RepositoryPartitaJson repository =
                    new RepositoryPartitaJson(cartellaTemporanea.resolve("partita.json"));
            assertAll(
                    () -> assertFalse(repository.esisteSalvataggio()),
                    () -> assertTrue(repository.carica().isEmpty()));
        }

        @Test
        @DisplayName("rilegge senza perdite quanto e' stato scritto")
        void andataERitorno() {
            RepositoryPartitaJson repository =
                    new RepositoryPartitaJson(cartellaTemporanea.resolve("partita.json"));
            repository.salva(statoDiEsempio());

            StatoPartita riletto = repository.carica().orElseThrow();

            assertAll(
                    () -> assertTrue(repository.esisteSalvataggio()),
                    () -> assertEquals("Aldo", riletto.getNomeEroe()),
                    () -> assertEquals("Guerriero", riletto.getClasseEroe()),
                    () -> assertEquals(2, riletto.getLivello()),
                    () -> assertEquals(50, riletto.getEsperienza()),
                    () -> assertEquals(24, riletto.getPuntiVita()),
                    () -> assertEquals("corridoio", riletto.getIdStanzaCorrente()),
                    () -> assertEquals(2, riletto.getInventario().get("Pozione minore")),
                    () -> assertEquals(Optional.of("Spada corta"), riletto.getArmaEquipaggiata()),
                    () -> assertEquals(1, riletto.getIdStanzeCompletate().size()));
        }

        @Test
        @DisplayName("un nuovo salvataggio sostituisce il precedente")
        void sovrascritturaSalvataggio() {
            RepositoryPartitaJson repository =
                    new RepositoryPartitaJson(cartellaTemporanea.resolve("partita.json"));
            repository.salva(statoDiEsempio());
            repository.salva(new StatoPartita("Bea", "Mago", 1, 0, 22, "ingresso"));

            assertEquals("Bea", repository.carica().orElseThrow().getNomeEroe());
        }

        @Test
        @DisplayName("rifiuta di salvare uno stato nullo")
        void rifiutaStatoNullo() {
            RepositoryPartitaJson repository =
                    new RepositoryPartitaJson(cartellaTemporanea.resolve("partita.json"));
            assertThrows(IllegalArgumentException.class, () -> repository.salva(null));
        }
    }
}
