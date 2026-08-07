package it.unicam.cs.mpgc.rpg126710.model.oggetto;

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

@DisplayName("Inventario")
class InventarioTest {

    private Inventario inventario;
    private Arma spada;
    private Pozione pozione;

    @BeforeEach
    void preparaInventario() {
        inventario = new Inventario();
        spada = new Arma("Spada corta", "Una lama leggera.", 20, 3);
        pozione = new Pozione("Pozione minore", "Cura le ferite lievi.", 10, 10);
    }

    @Nested
    @DisplayName("Aggiunta di oggetti")
    class Aggiunta {

        @Test
        @DisplayName("registra un oggetto appena aggiunto")
        void registraOggettoAggiunto() {
            inventario.aggiungi(spada);
            assertAll(
                    () -> assertTrue(inventario.contiene(spada)),
                    () -> assertEquals(1, inventario.getQuantita(spada)),
                    () -> assertFalse(inventario.eVuoto()));
        }

        @Test
        @DisplayName("accorpa le copie dello stesso oggetto invece di duplicarle")
        void accorpaLeCopie() {
            inventario.aggiungi(pozione);
            inventario.aggiungi(new Pozione("Pozione minore", "Cura le ferite lievi.", 10, 10));
            assertAll(
                    () -> assertEquals(2, inventario.getQuantita(pozione)),
                    () -> assertEquals(1, inventario.getContenuto().size()),
                    () -> assertEquals(2, inventario.numeroTotaleOggetti()));
        }

        @Test
        @DisplayName("rifiuta quantita' non positive")
        void rifiutaQuantitaNonPositive() {
            assertThrows(IllegalArgumentException.class, () -> inventario.aggiungi(spada, 0));
        }

        @Test
        @DisplayName("rifiuta un oggetto nullo")
        void rifiutaOggettoNullo() {
            assertThrows(IllegalArgumentException.class, () -> inventario.aggiungi(null));
        }
    }

    @Nested
    @DisplayName("Rimozione di oggetti")
    class Rimozione {

        @Test
        @DisplayName("elimina la voce quando si esaurisce l'ultima copia")
        void eliminaVoceEsaurita() {
            inventario.aggiungi(spada);
            assertAll(
                    () -> assertTrue(inventario.rimuovi(spada)),
                    () -> assertFalse(inventario.contiene(spada)),
                    () -> assertTrue(inventario.eVuoto()));
        }

        @Test
        @DisplayName("decrementa la quantita' quando restano altre copie")
        void decrementaQuantita() {
            inventario.aggiungi(pozione, 3);
            inventario.rimuovi(pozione);
            assertEquals(2, inventario.getQuantita(pozione));
        }

        @Test
        @DisplayName("segnala che non c'era nulla da rimuovere")
        void segnalaAssenza() {
            assertFalse(inventario.rimuovi(spada));
        }
    }

    @Nested
    @DisplayName("Filtro per categoria")
    class Filtro {

        @Test
        @DisplayName("restituisce solo gli oggetti della categoria richiesta")
        void filtraPerCategoria() {
            inventario.aggiungi(spada);
            inventario.aggiungi(pozione);
            inventario.aggiungi(new Armatura("Scudo", "Legno robusto.", 15, 2));

            List<Oggetto> pozioni = inventario.filtraPerCategoria(CategoriaOggetto.POZIONE);
            assertAll(
                    () -> assertEquals(1, pozioni.size()),
                    () -> assertEquals(pozione, pozioni.getFirst()));
        }

        @Test
        @DisplayName("restituisce una lista vuota se la categoria non e' rappresentata")
        void categoriaAssente() {
            inventario.aggiungi(spada);
            assertTrue(inventario.filtraPerCategoria(CategoriaOggetto.POZIONE).isEmpty());
        }
    }

    @Test
    @DisplayName("espone il contenuto in sola lettura")
    void contenutoNonModificabile() {
        inventario.aggiungi(spada);
        assertThrows(UnsupportedOperationException.class, () -> inventario.getContenuto().clear());
    }
}
