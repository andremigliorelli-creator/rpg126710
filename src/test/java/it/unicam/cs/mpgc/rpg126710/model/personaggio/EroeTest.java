package it.unicam.cs.mpgc.rpg126710.model.personaggio;

import it.unicam.cs.mpgc.rpg126710.api.Azione;
import it.unicam.cs.mpgc.rpg126710.model.oggetto.Arma;
import it.unicam.cs.mpgc.rpg126710.model.oggetto.Armatura;
import it.unicam.cs.mpgc.rpg126710.model.oggetto.Pozione;
import it.unicam.cs.mpgc.rpg126710.util.DadoFisso;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Eroe")
class EroeTest {

    private Guerriero guerriero;

    @BeforeEach
    void preparaEroe() {
        guerriero = new Guerriero("Aldo", new DadoFisso(3));
    }

    @Nested
    @DisplayName("Stato iniziale")
    class StatoIniziale {

        @Test
        @DisplayName("nasce al primo livello con i punti vita al massimo")
        void statoDiPartenza() {
            assertAll(
                    () -> assertEquals(1, guerriero.getLivello()),
                    () -> assertEquals(0, guerriero.getEsperienza()),
                    () -> assertEquals(guerriero.getPuntiVitaMassimi(), guerriero.getPuntiVita()),
                    () -> assertTrue(guerriero.eVivo()),
                    () -> assertTrue(guerriero.getInventario().eVuoto()));
        }

        @Test
        @DisplayName("rifiuta un nome vuoto")
        void rifiutaNomeVuoto() {
            assertThrows(IllegalArgumentException.class, () -> new Guerriero("  ", new DadoFisso(3)));
        }
    }

    @Nested
    @DisplayName("Equipaggiamento")
    class Equipaggiamento {

        @Test
        @DisplayName("aumenta le statistiche effettive senza toccare quelle di base")
        void bonusApplicatoSoloAlleEffettive() {
            Arma spada = new Arma("Spada lunga", "Acciaio ben temprato.", 60, 5);
            guerriero.raccogli(spada);
            guerriero.equipaggia(spada);

            assertAll(
                    () -> assertEquals(guerriero.getStatisticheBase().getAttacco() + 5,
                            guerriero.getStatisticheEffettive().getAttacco()),
                    () -> assertEquals(6, guerriero.getStatisticheBase().getAttacco()));
        }

        @Test
        @DisplayName("non equipaggia un oggetto che non si possiede")
        void nonEquipaggiaOggettoNonPosseduto() {
            Armatura armatura = new Armatura("Cotta di maglia", "Anelli di ferro.", 50, 3);
            assertAll(
                    () -> assertFalse(guerriero.equipaggia(armatura)),
                    () -> assertTrue(guerriero.getEquipaggiamento().getArmatura().isEmpty()));
        }

        @Test
        @DisplayName("somma i bonus di arma e armatura")
        void sommaBonusMultipli() {
            Arma spada = new Arma("Spada corta", "Una lama leggera.", 20, 3);
            Armatura scudo = new Armatura("Scudo di legno", "Ammaccato ma utile.", 15, 2);
            guerriero.raccogli(spada);
            guerriero.raccogli(scudo);
            guerriero.equipaggia(spada);
            guerriero.equipaggia(scudo);

            Statistiche effettive = guerriero.getStatisticheEffettive();
            assertAll(
                    () -> assertEquals(9, effettive.getAttacco()),
                    () -> assertEquals(6, effettive.getDifesa()));
        }
    }

    @Nested
    @DisplayName("Progressione")
    class Progressione {

        @Test
        @DisplayName("non sale di livello sotto la soglia richiesta")
        void nessunAvanzamentoSottoSoglia() {
            int livelliGuadagnati = guerriero.guadagnaEsperienza(99);
            assertAll(
                    () -> assertEquals(0, livelliGuadagnati),
                    () -> assertEquals(1, guerriero.getLivello()),
                    () -> assertEquals(99, guerriero.getEsperienza()));
        }

        @Test
        @DisplayName("sale di livello e migliora le statistiche al raggiungimento della soglia")
        void avanzamentoAllaSoglia() {
            Statistiche prima = guerriero.getStatisticheBase();
            int livelliGuadagnati = guerriero.guadagnaEsperienza(100);
            Statistiche dopo = guerriero.getStatisticheBase();

            assertAll(
                    () -> assertEquals(1, livelliGuadagnati),
                    () -> assertEquals(2, guerriero.getLivello()),
                    () -> assertEquals(prima.getAttacco() + 2, dopo.getAttacco()),
                    () -> assertEquals(prima.getDifesa() + 1, dopo.getDifesa()),
                    () -> assertEquals(prima.getPuntiVitaMassimi() + 5, dopo.getPuntiVitaMassimi()));
        }

        @Test
        @DisplayName("puo' guadagnare piu' livelli con una sola ricompensa")
        void avanzamentiMultipli() {
            assertEquals(2, guerriero.guadagnaEsperienza(300));
        }

        @Test
        @DisplayName("rifiuta esperienza negativa")
        void rifiutaEsperienzaNegativa() {
            assertThrows(IllegalArgumentException.class, () -> guerriero.guadagnaEsperienza(-1));
        }
    }

    @Nested
    @DisplayName("Azioni disponibili")
    class AzioniDisponibili {

        @Test
        @DisplayName("comprendono sempre attacco, abilita' speciale e fuga")
        void azioniDiBase() {
            assertEquals(3, guerriero.azioniDisponibili().size());
        }

        @Test
        @DisplayName("si arricchiscono di una voce per ogni tipo di pozione posseduta")
        void unaVocePerPozione() {
            guerriero.raccogli(new Pozione("Pozione minore", "Cura poco.", 10, 10));
            guerriero.raccogli(new Pozione("Pozione maggiore", "Cura molto.", 30, 25));
            assertEquals(5, guerriero.azioniDisponibili().size());
        }

        @Test
        @DisplayName("l'abilita' speciale non e' mai nulla")
        void abilitaSpecialePresente() {
            Azione abilita = guerriero.abilitaSpeciale();
            assertAll(
                    () -> assertNotNull(abilita),
                    () -> assertEquals("Colpo possente", abilita.getNome()));
        }
    }

    @Nested
    @DisplayName("Punti vita")
    class PuntiVita {

        @Test
        @DisplayName("il danno non porta mai i punti vita sotto zero")
        void dannoLimitatoAZero() {
            int subito = guerriero.subisciDanno(1000);
            assertAll(
                    () -> assertEquals(30, subito),
                    () -> assertEquals(0, guerriero.getPuntiVita()),
                    () -> assertFalse(guerriero.eVivo()));
        }

        @Test
        @DisplayName("la cura non supera mai il massimo")
        void curaLimitataAlMassimo() {
            guerriero.subisciDanno(10);
            int recuperato = guerriero.curati(1000);
            assertAll(
                    () -> assertEquals(10, recuperato),
                    () -> assertEquals(guerriero.getPuntiVitaMassimi(), guerriero.getPuntiVita()));
        }

        @Test
        @DisplayName("rifiuta un danno negativo")
        void rifiutaDannoNegativo() {
            assertThrows(IllegalArgumentException.class, () -> guerriero.subisciDanno(-5));
        }
    }
}
