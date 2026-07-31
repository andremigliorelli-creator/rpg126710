package it.unicam.cs.mpgc.rpg000000.service;

import it.unicam.cs.mpgc.rpg000000.api.Dado;
import it.unicam.cs.mpgc.rpg000000.model.personaggio.Eroe;
import it.unicam.cs.mpgc.rpg000000.model.personaggio.Guerriero;
import it.unicam.cs.mpgc.rpg000000.model.personaggio.Ladro;
import it.unicam.cs.mpgc.rpg000000.model.personaggio.Mago;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * Crea un eroe a partire dal nome della sua classe.
 *
 * <p>Serve sia alla schermata iniziale, dove l'utente sceglie con chi giocare,
 * sia al caricamento di una partita, dove la classe e' scritta nel salvataggio
 * come testo.</p>
 *
 * <p>Le classi disponibili sono registrate in una mappa invece che decise da
 * una catena di confronti: per rendere giocabile una nuova classe basta
 * aggiungere una riga al registro, senza modificare il metodo di creazione.</p>
 */
public class FabbricaEroi {

    private final Map<String, BiFunction<String, Dado, Eroe>> costruttori = new LinkedHashMap<>();

    /**
     * Crea la fabbrica registrando le classi giocabili previste.
     */
    public FabbricaEroi() {
        registra("Guerriero", Guerriero::new);
        registra("Mago", Mago::new);
        registra("Ladro", Ladro::new);
    }

    /**
     * Rende disponibile una nuova classe giocabile.
     *
     * @param nomeClasse  nome con cui verra' richiesta, non vuoto
     * @param costruttore funzione che crea l'eroe, non nulla
     * @throws IllegalArgumentException se un parametro non rispetta i vincoli
     */
    public final void registra(String nomeClasse, BiFunction<String, Dado, Eroe> costruttore) {
        if (nomeClasse == null || nomeClasse.isBlank()) {
            throw new IllegalArgumentException("Il nome della classe non puo' essere vuoto");
        }
        if (costruttore == null) {
            throw new IllegalArgumentException("Il costruttore della classe non puo' essere nullo");
        }
        costruttori.put(nomeClasse, costruttore);
    }

    /**
     * Crea un eroe della classe richiesta.
     *
     * @param nomeClasse classe desiderata, fra quelle registrate
     * @param nomeEroe   nome del personaggio, non vuoto
     * @param dado       sorgente di casualita', non nulla
     * @return il nuovo eroe
     * @throws IllegalArgumentException se la classe non e' fra quelle disponibili
     */
    public Eroe crea(String nomeClasse, String nomeEroe, Dado dado) {
        BiFunction<String, Dado, Eroe> costruttore = costruttori.get(nomeClasse);
        if (costruttore == null) {
            throw new IllegalArgumentException("Classe eroe non disponibile: " + nomeClasse);
        }
        return costruttore.apply(nomeEroe, dado);
    }

    /**
     * @return i nomi delle classi giocabili, in sola lettura
     */
    public Set<String> classiDisponibili() {
        return Collections.unmodifiableSet(costruttori.keySet());
    }
}
