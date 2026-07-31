package it.unicam.cs.mpgc.rpg000000.model.mondo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Mappa e stanze")
class MappaTest {

    private Map<String, Stanza> dueStanzeCollegate() {
        Stanza ingresso = new Stanza("ingresso", "Ingresso", "La porta si chiude alle tue spalle.", false);
        Stanza sala = new Stanza("sala", "Sala", "Un ampio salone vuoto.", true);
        ingresso.collega(Direzione.NORD, "sala");
        sala.collega(Direzione.SUD, "ingresso");

        Map<String, Stanza> stanze = new LinkedHashMap<>();
        stanze.put(ingresso.getId(), ingresso);
        stanze.put(sala.getId(), sala);
        return stanze;
    }

    @Nested
    @DisplayName("Costruzione")
    class Costruzione {

        @Test
        @DisplayName("accetta una mappa coerente e ne indica la stanza iniziale")
        void mappaCoerente() {
            Mappa mappa = new Mappa(dueStanzeCollegate(), "ingresso");
            assertAll(
                    () -> assertEquals("ingresso", mappa.getStanzaIniziale().getId()),
                    () -> assertEquals(2, mappa.numeroStanze()));
        }

        @Test
        @DisplayName("rifiuta una mappa priva di stanze")
        void rifiutaMappaVuota() {
            assertThrows(IllegalArgumentException.class, () -> new Mappa(Map.of(), "ingresso"));
        }

        @Test
        @DisplayName("rifiuta una stanza iniziale che non appartiene alla mappa")
        void rifiutaStanzaInizialeEstranea() {
            Map<String, Stanza> stanze = dueStanzeCollegate();
            assertThrows(IllegalArgumentException.class, () -> new Mappa(stanze, "cantina"));
        }

        @Test
        @DisplayName("rifiuta un'uscita che punta a una stanza inesistente")
        void rifiutaUscitaVersoIlNulla() {
            Stanza isolata = new Stanza("isolata", "Isolata", "Una porta che non porta da nessuna parte.", false);
            isolata.collega(Direzione.EST, "inesistente");
            Map<String, Stanza> stanze = Map.of("isolata", isolata);
            assertThrows(IllegalArgumentException.class, () -> new Mappa(stanze, "isolata"));
        }
    }

    @Nested
    @DisplayName("Navigazione")
    class Navigazione {

        @Test
        @DisplayName("raggiunge la stanza adiacente nella direzione indicata")
        void raggiungeStanzaAdiacente() {
            Mappa mappa = new Mappa(dueStanzeCollegate(), "ingresso");
            Stanza destinazione = mappa.stanzaAdiacente(mappa.getStanzaIniziale(), Direzione.NORD).orElseThrow();
            assertEquals("sala", destinazione.getId());
        }

        @Test
        @DisplayName("non trova nulla dove non c'e' passaggio")
        void nessunPassaggio() {
            Mappa mappa = new Mappa(dueStanzeCollegate(), "ingresso");
            assertTrue(mappa.stanzaAdiacente(mappa.getStanzaIniziale(), Direzione.EST).isEmpty());
        }

        @Test
        @DisplayName("conta le stanze visitate")
        void conteggioVisitate() {
            Mappa mappa = new Mappa(dueStanzeCollegate(), "ingresso");
            assertEquals(0, mappa.numeroStanzeVisitate());
            mappa.getStanzaIniziale().segnaComeVisitata();
            assertEquals(1, mappa.numeroStanzeVisitate());
        }
    }

    @Nested
    @DisplayName("Direzioni")
    class Direzioni {

        @ParameterizedTest(name = "opposta di {0} = {1}")
        @CsvSource({"NORD, SUD", "SUD, NORD", "EST, OVEST", "OVEST, EST"})
        @DisplayName("ogni direzione ha la propria opposta")
        void direzioneOpposta(Direzione partenza, Direzione attesa) {
            assertEquals(attesa, partenza.opposta());
        }
    }

    @Nested
    @DisplayName("Identita' delle stanze")
    class IdentitaStanze {

        @Test
        @DisplayName("due stanze con lo stesso identificatore sono la stessa stanza")
        void stessoIdStessaStanza() {
            Stanza prima = new Stanza("cripta", "Cripta", "Loculi aperti.", false);
            Stanza seconda = new Stanza("cripta", "Altro nome", "Altra descrizione.", true);
            assertAll(
                    () -> assertEquals(prima, seconda),
                    () -> assertEquals(prima.hashCode(), seconda.hashCode()));
        }

        @Test
        @DisplayName("identificatori diversi indicano stanze diverse")
        void idDiversiStanzeDiverse() {
            assertNotEquals(
                    new Stanza("a", "A", "Prima stanza.", false),
                    new Stanza("b", "B", "Seconda stanza.", false));
        }
    }

    @Nested
    @DisplayName("Contenuto di una stanza")
    class ContenutoStanza {

        @Test
        @DisplayName("la raccolta dei tesori svuota la stanza")
        void raccoltaSvuotaLaStanza() {
            Stanza stanza = new Stanza("deposito", "Deposito", "Casse impilate.", false);
            stanza.aggiungiTesoro(new it.unicam.cs.mpgc.rpg000000.model.oggetto.Pozione(
                    "Pozione minore", "Cura poco.", 10, 10));

            assertAll(
                    () -> assertEquals(1, stanza.raccogliTesori().size()),
                    () -> assertTrue(stanza.getTesori().isEmpty()));
        }

        @Test
        @DisplayName("una stanza senza nemici risulta libera")
        void stanzaSenzaNemiciELibera() {
            Stanza stanza = new Stanza("corridoio", "Corridoio", "Silenzio assoluto.", false);
            assertAll(
                    () -> assertTrue(stanza.eLibera()),
                    () -> assertFalse(stanza.eVisitata()));
        }
    }
}
