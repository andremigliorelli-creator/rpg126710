package it.unicam.cs.mpgc.rpg000000.model.personaggio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Statistiche")
class StatisticheTest {

    @Nested
    @DisplayName("Costruzione")
    class Costruzione {

        @Test
        @DisplayName("accetta valori validi")
        void accettaValoriValidi() {
            Statistiche statistiche = new Statistiche(5, 3, 20);
            assertAll(
                    () -> assertEquals(5, statistiche.getAttacco()),
                    () -> assertEquals(3, statistiche.getDifesa()),
                    () -> assertEquals(20, statistiche.getPuntiVitaMassimi()));
        }

        @Test
        @DisplayName("ammette attacco e difesa pari a zero")
        void ammetteValoriNulli() {
            assertDoesNotThrow(() -> new Statistiche(0, 0, 1));
        }

        @ParameterizedTest(name = "attacco={0} difesa={1} pvMax={2} deve essere rifiutato")
        @CsvSource({
                "-1, 0, 10",
                "0, -1, 10",
                "0, 0, 0",
                "0, 0, -5"
        })
        @DisplayName("rifiuta i valori fuori dai limiti consentiti")
        void rifiutaValoriNonValidi(int attacco, int difesa, int puntiVitaMassimi) {
            assertThrows(IllegalArgumentException.class,
                    () -> new Statistiche(attacco, difesa, puntiVitaMassimi));
        }
    }

    @Nested
    @DisplayName("Applicazione di un bonus")
    class ApplicazioneBonus {

        @Test
        @DisplayName("somma attacco e difesa lasciando invariati i punti vita massimi")
        void sommaSoloAttaccoEDifesa() {
            Statistiche risultato = new Statistiche(5, 3, 20).piu(new Bonus(4, 2));
            assertAll(
                    () -> assertEquals(9, risultato.getAttacco()),
                    () -> assertEquals(5, risultato.getDifesa()),
                    () -> assertEquals(20, risultato.getPuntiVitaMassimi()));
        }

        @Test
        @DisplayName("non modifica l'istanza di partenza")
        void nonModificaLOriginale() {
            Statistiche originali = new Statistiche(5, 3, 20);
            originali.piu(new Bonus(10, 10));
            assertEquals(new Statistiche(5, 3, 20), originali);
        }

        @Test
        @DisplayName("rifiuta un bonus nullo")
        void rifiutaBonusNullo() {
            Statistiche statistiche = new Statistiche(5, 3, 20);
            assertThrows(IllegalArgumentException.class, () -> statistiche.piu(null));
        }
    }

    @Nested
    @DisplayName("Uguaglianza")
    class Uguaglianza {

        @Test
        @DisplayName("due statistiche con gli stessi valori sono uguali e condividono l'hash")
        void stessiValoriStessoOggettoLogico() {
            Statistiche prime = new Statistiche(5, 3, 20);
            Statistiche seconde = new Statistiche(5, 3, 20);
            assertAll(
                    () -> assertEquals(prime, seconde),
                    () -> assertEquals(prime.hashCode(), seconde.hashCode()));
        }

        @Test
        @DisplayName("valori diversi producono statistiche diverse")
        void valoriDiversiNonSonoUguali() {
            assertNotEquals(new Statistiche(5, 3, 20), new Statistiche(5, 3, 21));
        }
    }
}
