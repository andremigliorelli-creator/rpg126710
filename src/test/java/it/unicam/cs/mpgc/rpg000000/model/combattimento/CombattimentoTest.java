package it.unicam.cs.mpgc.rpg000000.model.combattimento;

import it.unicam.cs.mpgc.rpg000000.api.Azione;
import it.unicam.cs.mpgc.rpg000000.api.Dado;
import it.unicam.cs.mpgc.rpg000000.api.EsitoAzione;
import it.unicam.cs.mpgc.rpg000000.model.personaggio.Guerriero;
import it.unicam.cs.mpgc.rpg000000.model.personaggio.Nemico;
import it.unicam.cs.mpgc.rpg000000.model.personaggio.Statistiche;
import it.unicam.cs.mpgc.rpg000000.util.DadoFisso;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Combattimento a turni")
class CombattimentoTest {

    private Dado dado;
    private Guerriero eroe;

    @BeforeEach
    void preparaScontro() {
        dado = new DadoFisso(3);
        eroe = new Guerriero("Aldo", dado);
    }

    private Nemico creaNemico(int attacco, int difesa, int puntiVita) {
        return new Nemico("Goblin", new Statistiche(attacco, difesa, puntiVita),
                dado, new StrategiaAggressiva(), 40, null);
    }

    @Nested
    @DisplayName("Attacco base")
    class AttaccoBase {

        @Test
        @DisplayName("infligge il danno dell'attaccante ridotto dalla difesa del bersaglio")
        void dannoRidottoDallaDifesa() {
            Nemico nemico = creaNemico(3, 2, 40);
            // Attacco effettivo dell'eroe 6, piu' il dado fisso 3, meno la difesa 2.
            new AzioneAttacco().esegui(eroe, nemico);
            assertEquals(40 - 7, nemico.getPuntiVita());
        }

        @Test
        @DisplayName("garantisce sempre almeno un punto di danno")
        void dannoMinimoGarantito() {
            Nemico corazzato = creaNemico(3, 500, 40);
            new AzioneAttacco().esegui(eroe, corazzato);
            assertEquals(39, corazzato.getPuntiVita());
        }

        @Test
        @DisplayName("rifiuta un bersaglio nullo")
        void rifiutaBersaglioNullo() {
            assertThrows(IllegalArgumentException.class, () -> new AzioneAttacco().esegui(eroe, null));
        }
    }

    @Nested
    @DisplayName("Abilita' speciali")
    class AbilitaSpeciali {

        @Test
        @DisplayName("il colpo possente raddoppia il danno quando la prova riesce")
        void colpoPossenteRaddoppia() {
            Nemico nemico = creaNemico(3, 2, 60);
            // Il dado dell'abilita' decide solo se il colpo va a segno; la potenza
            // del colpo dipende dal dado dell'eroe. Quindi: attacco 6 piu' dado 3,
            // meno difesa 2, il tutto raddoppiato.
            new ColpoPossente(new DadoFisso(6)).esegui(eroe, nemico);
            assertEquals(60 - 14, nemico.getPuntiVita());
        }

        @Test
        @DisplayName("il colpo possente fallito infligge solo il danno minimo")
        void colpoPossenteFallito() {
            Guerriero sfortunato = new Guerriero("Aldo", new DadoFisso(1));
            Nemico nemico = creaNemico(3, 2, 60);
            new ColpoPossente(new DadoFisso(1)).esegui(sfortunato, nemico);
            assertEquals(59, nemico.getPuntiVita());
        }

        @Test
        @DisplayName("la palla di fuoco ignora l'armatura del bersaglio")
        void pallaDiFuocoIgnoraArmatura() {
            Nemico corazzato = creaNemico(3, 500, 60);
            new PallaDiFuoco(new DadoFisso(4)).esegui(eroe, corazzato);
            assertEquals(60 - 10, corazzato.getPuntiVita());
        }
    }

    @Nested
    @DisplayName("Svolgimento del turno")
    class SvolgimentoTurno {

        @Test
        @DisplayName("dopo la mossa dell'eroe risponde il nemico")
        void entrambiAgisconoNelTurno() {
            Nemico nemico = creaNemico(4, 1, 40);
            Combattimento scontro = new Combattimento(eroe, nemico, nemico.getStrategia());

            List<EsitoAzione> esiti = scontro.eseguiTurno(new AzioneAttacco());

            assertAll(
                    () -> assertEquals(2, esiti.size()),
                    () -> assertEquals(1, scontro.getNumeroTurno()),
                    () -> assertTrue(nemico.getPuntiVita() < 40),
                    () -> assertTrue(eroe.getPuntiVita() < eroe.getPuntiVitaMassimi()));
        }

        @Test
        @DisplayName("il nemico non replica se viene abbattuto dalla mossa dell'eroe")
        void nemicoAbbattutoNonReplica() {
            Nemico fragile = creaNemico(4, 0, 1);
            Combattimento scontro = new Combattimento(eroe, fragile, fragile.getStrategia());

            List<EsitoAzione> esiti = scontro.eseguiTurno(new AzioneAttacco());

            assertAll(
                    () -> assertEquals(1, esiti.size()),
                    () -> assertEquals(EsitoCombattimento.VITTORIA, scontro.getStato()),
                    () -> assertEquals(eroe.getPuntiVitaMassimi(), eroe.getPuntiVita()));
        }

        @Test
        @DisplayName("dichiara la sconfitta quando l'eroe cade")
        void sconfittaDellEroe() {
            Nemico letale = creaNemico(1000, 0, 500);
            Combattimento scontro = new Combattimento(eroe, letale, letale.getStrategia());

            scontro.eseguiTurno(new AzioneAttacco());

            assertAll(
                    () -> assertEquals(EsitoCombattimento.SCONFITTA, scontro.getStato()),
                    () -> assertFalse(eroe.eVivo()),
                    () -> assertTrue(scontro.eConcluso()));
        }

        @Test
        @DisplayName("una fuga riuscita chiude lo scontro")
        void fugaRiuscita() {
            Nemico nemico = creaNemico(4, 1, 40);
            Combattimento scontro = new Combattimento(eroe, nemico, nemico.getStrategia());

            scontro.eseguiTurno(new AzioneFuga(new DadoFisso(6)));

            assertEquals(EsitoCombattimento.FUGA_EROE, scontro.getStato());
        }

        @Test
        @DisplayName("non consente altre mosse dopo la conclusione")
        void nessunaMossaDopoLaFine() {
            Nemico fragile = creaNemico(4, 0, 1);
            Combattimento scontro = new Combattimento(eroe, fragile, fragile.getStrategia());
            scontro.eseguiTurno(new AzioneAttacco());

            Azione attacco = new AzioneAttacco();
            assertThrows(IllegalStateException.class, () -> scontro.eseguiTurno(attacco));
        }

        @Test
        @DisplayName("registra nel diario ogni azione svolta")
        void diarioPopolato() {
            Nemico nemico = creaNemico(4, 1, 40);
            Combattimento scontro = new Combattimento(eroe, nemico, nemico.getStrategia());
            scontro.eseguiTurno(new AzioneAttacco());
            assertEquals(2, scontro.getDiario().size());
        }
    }

    @Nested
    @DisplayName("Strategia cauta")
    class StrategiaCautaTest {

        @Test
        @DisplayName("attacca finche' il nemico e' in forze")
        void attaccaConVitaAlta() {
            Nemico nemico = creaNemico(4, 1, 40);
            Azione scelta = new StrategiaCauta(dado).scegliAzione(nemico, eroe);
            assertEquals("Attacca", scelta.getNome());
        }

        @Test
        @DisplayName("tenta la fuga quando la vita scende sotto la soglia")
        void fuggeConVitaBassa() {
            Nemico nemico = creaNemico(4, 1, 40);
            nemico.subisciDanno(35);
            Azione scelta = new StrategiaCauta(dado).scegliAzione(nemico, eroe);
            assertEquals("Fuggi", scelta.getNome());
        }
    }
}
