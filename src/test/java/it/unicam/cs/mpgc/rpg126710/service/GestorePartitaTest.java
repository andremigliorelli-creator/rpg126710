package it.unicam.cs.mpgc.rpg126710.service;

import it.unicam.cs.mpgc.rpg126710.api.Dado;
import it.unicam.cs.mpgc.rpg126710.model.mondo.Direzione;
import it.unicam.cs.mpgc.rpg126710.persistence.CaricatoreDungeonJson;
import it.unicam.cs.mpgc.rpg126710.persistence.RepositoryPartitaJson;
import it.unicam.cs.mpgc.rpg126710.util.DadoFisso;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica che avvio, salvataggio e ripristino di una partita funzionino
 * insieme, usando i componenti reali ma scrivendo in una cartella temporanea.
 */
@DisplayName("Gestore della partita")
class GestorePartitaTest {

    @TempDir
    Path cartellaTemporanea;

    private GestorePartita gestore;

    @BeforeEach
    void preparaGestore() {
        Dado dado = new DadoFisso(3);
        CaricatoreDungeonJson caricatore = new CaricatoreDungeonJson(dado);
        gestore = new GestorePartita(
                caricatore,
                caricatore,
                new RepositoryPartitaJson(cartellaTemporanea.resolve("partita.json")),
                new FabbricaEroi(),
                dado);
    }

    @Test
    @DisplayName("crea una partita nuova con l'eroe richiesto")
    void nuovaPartita() {
        MotoreGioco motore = gestore.nuovaPartita("Aldo", "Mago");
        assertAll(
                () -> assertEquals("Aldo", motore.getEroe().getNome()),
                () -> assertEquals("Mago", motore.getEroe().getNomeClasse()),
                () -> assertEquals("ingresso", motore.getStanzaCorrente().getId()));
    }

    @Test
    @DisplayName("rifiuta una classe eroe non disponibile")
    void classeSconosciuta() {
        assertThrows(IllegalArgumentException.class, () -> gestore.nuovaPartita("Aldo", "Bardo"));
    }

    @Test
    @DisplayName("non trova salvataggi prima del primo salvataggio")
    void nessunSalvataggioIniziale() {
        assertAll(
                () -> assertFalse(gestore.esisteSalvataggio()),
                () -> assertTrue(gestore.carica().isEmpty()));
    }

    @Test
    @DisplayName("ripristina eroe, posizione e progressi dopo un salvataggio")
    void salvataggioERipristino() {
        MotoreGioco partita = gestore.nuovaPartita("Aldo", "Guerriero");
        partita.iniziaPartita();
        partita.getEroe().guadagnaEsperienza(150);
        gestore.salva(partita);

        MotoreGioco ripresa = gestore.carica().orElseThrow();

        assertAll(
                () -> assertTrue(gestore.esisteSalvataggio()),
                () -> assertEquals("Aldo", ripresa.getEroe().getNome()),
                () -> assertEquals("Guerriero", ripresa.getEroe().getNomeClasse()),
                () -> assertEquals(2, ripresa.getEroe().getLivello()),
                () -> assertEquals(50, ripresa.getEroe().getEsperienza()),
                () -> assertEquals("ingresso", ripresa.getStanzaCorrente().getId()));
    }

    @Test
    @DisplayName("conserva inventario ed equipaggiamento raccolti nel dungeon")
    void ripristinaInventarioEdEquipaggiamento() {
        MotoreGioco partita = gestore.nuovaPartita("Aldo", "Guerriero");
        partita.iniziaPartita();
        gestore.salva(partita);

        MotoreGioco ripresa = gestore.carica().orElseThrow();

        assertAll(
                () -> assertFalse(ripresa.getEroe().getInventario().eVuoto()),
                () -> assertEquals("Spada corta",
                        ripresa.getEroe().getEquipaggiamento().getArma().orElseThrow().getNome()));
    }

    @Test
    @DisplayName("non ripropone i nemici gia' sconfitti nelle stanze completate")
    void stanzeCompletateRestanoVuote() {
        MotoreGioco partita = gestore.nuovaPartita("Aldo", "Guerriero");
        partita.iniziaPartita();
        gestore.salva(partita);

        MotoreGioco ripresa = gestore.carica().orElseThrow();

        assertAll(
                () -> assertTrue(ripresa.getStanzaCorrente().eVisitata()),
                () -> assertTrue(ripresa.getStanzaCorrente().getTesori().isEmpty()));
    }

    @Test
    @DisplayName("riprende la partita dalla stanza in cui era stata lasciata")
    void riprendeDallaStanzaCorretta() {
        MotoreGioco partita = gestore.nuovaPartita("Aldo", "Guerriero");
        partita.iniziaPartita();
        partita.muovi(Direzione.NORD);
        gestore.salva(partita);

        assertEquals("corridoio", gestore.carica().orElseThrow().getStanzaCorrente().getId());
    }

    @Test
    @DisplayName("rifiuta di salvare una partita nulla")
    void rifiutaPartitaNulla() {
        assertThrows(IllegalArgumentException.class, () -> gestore.salva(null));
    }
}
