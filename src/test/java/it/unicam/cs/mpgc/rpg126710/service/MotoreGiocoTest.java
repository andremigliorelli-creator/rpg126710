package it.unicam.cs.mpgc.rpg126710.service;

import it.unicam.cs.mpgc.rpg126710.api.Dado;
import it.unicam.cs.mpgc.rpg126710.model.combattimento.AzioneAttacco;
import it.unicam.cs.mpgc.rpg126710.model.combattimento.StrategiaAggressiva;
import it.unicam.cs.mpgc.rpg126710.model.mondo.Direzione;
import it.unicam.cs.mpgc.rpg126710.model.mondo.Mappa;
import it.unicam.cs.mpgc.rpg126710.model.mondo.Stanza;
import it.unicam.cs.mpgc.rpg126710.model.oggetto.Arma;
import it.unicam.cs.mpgc.rpg126710.model.personaggio.Guerriero;
import it.unicam.cs.mpgc.rpg126710.model.personaggio.Nemico;
import it.unicam.cs.mpgc.rpg126710.model.personaggio.Statistiche;
import it.unicam.cs.mpgc.rpg126710.util.DadoFisso;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Motore di gioco")
class MotoreGiocoTest {

    private Dado dado;
    private Guerriero eroe;
    private List<EventoGioco> eventiRicevuti;

    @BeforeEach
    void preparaPartita() {
        dado = new DadoFisso(3);
        eroe = new Guerriero("Aldo", dado);
        eventiRicevuti = new ArrayList<>();
    }

    /**
     * Costruisce un dungeon minimo di tre stanze in fila: ingresso, sala e
     * uscita. Costruirlo qui, invece di leggerlo dal file dei contenuti, rende
     * il test indipendente da eventuali modifiche al dungeon del gioco.
     */
    private Mappa mappaDiProva(Nemico guardiaDellaSala) {
        Stanza ingresso = new Stanza("ingresso", "Ingresso", "Una scala scende nel buio.", false);
        Stanza sala = new Stanza("sala", "Sala", "Un ampio salone.", false);
        Stanza uscita = new Stanza("uscita", "Uscita", "La luce del giorno.", true);

        ingresso.collega(Direzione.NORD, "sala");
        sala.collega(Direzione.SUD, "ingresso");
        sala.collega(Direzione.NORD, "uscita");
        uscita.collega(Direzione.SUD, "sala");

        if (guardiaDellaSala != null) {
            sala.aggiungiNemico(guardiaDellaSala);
        }

        Map<String, Stanza> stanze = new LinkedHashMap<>();
        stanze.put(ingresso.getId(), ingresso);
        stanze.put(sala.getId(), sala);
        stanze.put(uscita.getId(), uscita);
        return new Mappa(stanze, "ingresso");
    }

    private Nemico nemicoFragile() {
        return new Nemico("Goblin", new Statistiche(2, 0, 1), dado, new StrategiaAggressiva(), 120, null);
    }

    private MotoreGioco avvia(Mappa mappa) {
        MotoreGioco motore = new MotoreGioco(eroe, mappa);
        motore.registraOsservatore(eventiRicevuti::add);
        motore.iniziaPartita();
        return motore;
    }

    private boolean haRicevuto(TipoEvento tipo) {
        return eventiRicevuti.stream().anyMatch(evento -> evento.getTipo() == tipo);
    }

    @Nested
    @DisplayName("Avvio")
    class Avvio {

        @Test
        @DisplayName("colloca l'eroe nella stanza iniziale e la segna come visitata")
        void avvioNellaStanzaIniziale() {
            MotoreGioco motore = avvia(mappaDiProva(null));
            assertAll(
                    () -> assertEquals("ingresso", motore.getStanzaCorrente().getId()),
                    () -> assertTrue(motore.getStanzaCorrente().eVisitata()),
                    () -> assertEquals(StatoGioco.ESPLORAZIONE, motore.getStato()),
                    () -> assertTrue(haRicevuto(TipoEvento.PARTITA_INIZIATA)));
        }

        @Test
        @DisplayName("rifiuta di partire senza eroe o senza mappa")
        void rifiutaCollaboratoriNulli() {
            Mappa mappa = mappaDiProva(null);
            assertAll(
                    () -> assertThrows(IllegalArgumentException.class, () -> new MotoreGioco(null, mappa)),
                    () -> assertThrows(IllegalArgumentException.class, () -> new MotoreGioco(eroe, null)));
        }
    }

    @Nested
    @DisplayName("Movimento")
    class Movimento {

        @Test
        @DisplayName("sposta l'eroe dove esiste un passaggio")
        void spostamentoValido() {
            MotoreGioco motore = avvia(mappaDiProva(null));
            motore.muovi(Direzione.NORD);
            assertEquals("sala", motore.getStanzaCorrente().getId());
        }

        @Test
        @DisplayName("segnala una direzione senza passaggio senza spostare l'eroe")
        void spostamentoNonValido() {
            MotoreGioco motore = avvia(mappaDiProva(null));
            motore.muovi(Direzione.EST);
            assertAll(
                    () -> assertEquals("ingresso", motore.getStanzaCorrente().getId()),
                    () -> assertTrue(haRicevuto(TipoEvento.MOSSA_NON_VALIDA)));
        }

        @Test
        @DisplayName("impedisce di allontanarsi durante uno scontro")
        void nessunMovimentoInCombattimento() {
            MotoreGioco motore = avvia(mappaDiProva(nemicoFragile()));
            motore.muovi(Direzione.NORD);
            eventiRicevuti.clear();

            motore.muovi(Direzione.SUD);

            assertAll(
                    () -> assertEquals(StatoGioco.COMBATTIMENTO, motore.getStato()),
                    () -> assertEquals("sala", motore.getStanzaCorrente().getId()),
                    () -> assertTrue(haRicevuto(TipoEvento.MOSSA_NON_VALIDA)));
        }
    }

    @Nested
    @DisplayName("Combattimento")
    class Combattimento {

        @Test
        @DisplayName("inizia da solo entrando in una stanza presidiata")
        void combattimentoAutomatico() {
            MotoreGioco motore = avvia(mappaDiProva(nemicoFragile()));
            motore.muovi(Direzione.NORD);
            assertAll(
                    () -> assertEquals(StatoGioco.COMBATTIMENTO, motore.getStato()),
                    () -> assertTrue(haRicevuto(TipoEvento.COMBATTIMENTO_INIZIATO)),
                    () -> assertTrue(motore.getNemicoInCombattimento().isPresent()));
        }

        @Test
        @DisplayName("assegna esperienza e libera la stanza dopo la vittoria")
        void vittoriaAssegnaRicompense() {
            MotoreGioco motore = avvia(mappaDiProva(nemicoFragile()));
            motore.muovi(Direzione.NORD);

            motore.eseguiAzioneCombattimento(new AzioneAttacco());

            assertAll(
                    () -> assertEquals(StatoGioco.ESPLORAZIONE, motore.getStato()),
                    () -> assertTrue(motore.getStanzaCorrente().eLibera()),
                    () -> assertEquals(2, eroe.getLivello()),
                    () -> assertTrue(haRicevuto(TipoEvento.LIVELLO_AUMENTATO)));
        }

        @Test
        @DisplayName("dichiara la sconfitta quando l'eroe cade")
        void sconfittaTerminaLaPartita() {
            Nemico letale = new Nemico("Titano", new Statistiche(1000, 500, 500),
                    dado, new StrategiaAggressiva(), 10, null);
            MotoreGioco motore = avvia(mappaDiProva(letale));
            motore.muovi(Direzione.NORD);

            motore.eseguiAzioneCombattimento(new AzioneAttacco());

            assertAll(
                    () -> assertEquals(StatoGioco.SCONFITTA, motore.getStato()),
                    () -> assertTrue(motore.getStato().partitaTerminata()),
                    () -> assertTrue(haRicevuto(TipoEvento.PARTITA_PERSA)));
        }

        @Test
        @DisplayName("non accetta mosse di combattimento durante l'esplorazione")
        void nessunaMossaFuoriDalCombattimento() {
            MotoreGioco motore = avvia(mappaDiProva(null));
            AzioneAttacco attacco = new AzioneAttacco();
            assertThrows(IllegalStateException.class, () -> motore.eseguiAzioneCombattimento(attacco));
        }
    }

    @Nested
    @DisplayName("Tesori e conclusione")
    class TesoriEConclusione {

        @Test
        @DisplayName("raccoglie i tesori della stanza ed equipaggia il ritrovamento migliore")
        void raccoltaEdEquipaggiamento() {
            Mappa mappa = mappaDiProva(null);
            mappa.getStanzaIniziale().aggiungiTesoro(new Arma("Spada lunga", "Acciaio temprato.", 60, 5));

            MotoreGioco motore = avvia(mappa);

            assertAll(
                    () -> assertTrue(haRicevuto(TipoEvento.TESORO_RACCOLTO)),
                    () -> assertTrue(motore.getStanzaCorrente().getTesori().isEmpty()),
                    () -> assertEquals("Spada lunga",
                            eroe.getEquipaggiamento().getArma().orElseThrow().getNome()));
        }

        @Test
        @DisplayName("dichiara la vittoria al raggiungimento dell'uscita")
        void vittoriaAllUscita() {
            MotoreGioco motore = avvia(mappaDiProva(null));
            motore.muovi(Direzione.NORD);
            motore.muovi(Direzione.NORD);

            assertAll(
                    () -> assertEquals(StatoGioco.VITTORIA, motore.getStato()),
                    () -> assertTrue(haRicevuto(TipoEvento.PARTITA_VINTA)));
        }
    }
}
