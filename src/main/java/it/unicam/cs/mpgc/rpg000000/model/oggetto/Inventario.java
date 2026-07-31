package it.unicam.cs.mpgc.rpg000000.model.oggetto;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Insieme degli oggetti posseduti da un eroe, con la relativa quantita'.
 *
 * <p>La struttura scelta e' una mappa oggetto/quantita' invece di una lista di
 * duplicati: cercare o rimuovere un oggetto costa in media tempo costante
 * anziche' richiedere la scansione dell'intera collezione. Questo funziona
 * perche' {@link Oggetto} ridefinisce {@code equals} e {@code hashCode}, quindi
 * due copie della stessa spada finiscono nella stessa posizione.</p>
 *
 * <p>Si usa {@link LinkedHashMap} per conservare l'ordine di raccolta e
 * mostrare all'utente un inventario stabile fra un'apertura e l'altra.</p>
 */
public class Inventario {

    private final Map<Oggetto, Integer> contenuto = new LinkedHashMap<>();

    /**
     * Aggiunge una singola copia dell'oggetto indicato.
     *
     * @param oggetto oggetto da aggiungere, non nullo
     * @throws IllegalArgumentException se l'oggetto e' nullo
     */
    public void aggiungi(Oggetto oggetto) {
        aggiungi(oggetto, 1);
    }

    /**
     * Aggiunge piu' copie dell'oggetto indicato.
     *
     * @param oggetto  oggetto da aggiungere, non nullo
     * @param quantita numero di copie, maggiore di zero
     * @throws IllegalArgumentException se i parametri non rispettano i vincoli
     */
    public void aggiungi(Oggetto oggetto, int quantita) {
        richiediOggettoValido(oggetto);
        if (quantita <= 0) {
            throw new IllegalArgumentException("La quantita' da aggiungere deve essere positiva: " + quantita);
        }
        contenuto.merge(oggetto, quantita, Integer::sum);
    }

    /**
     * Rimuove una copia dell'oggetto indicato, eliminando la voce quando la
     * quantita' scende a zero.
     *
     * @param oggetto oggetto da rimuovere, non nullo
     * @return {@code true} se l'oggetto era presente ed e' stato rimosso
     * @throws IllegalArgumentException se l'oggetto e' nullo
     */
    public boolean rimuovi(Oggetto oggetto) {
        richiediOggettoValido(oggetto);
        Integer quantitaAttuale = contenuto.get(oggetto);
        if (quantitaAttuale == null) {
            return false;
        }
        if (quantitaAttuale == 1) {
            contenuto.remove(oggetto);
        } else {
            contenuto.put(oggetto, quantitaAttuale - 1);
        }
        return true;
    }

    /**
     * @param oggetto oggetto da cercare, non nullo
     * @return {@code true} se l'inventario contiene almeno una copia
     */
    public boolean contiene(Oggetto oggetto) {
        richiediOggettoValido(oggetto);
        return contenuto.containsKey(oggetto);
    }

    /**
     * @param oggetto oggetto da cercare, non nullo
     * @return il numero di copie possedute, zero se assente
     */
    public int getQuantita(Oggetto oggetto) {
        richiediOggettoValido(oggetto);
        return contenuto.getOrDefault(oggetto, 0);
    }

    /**
     * @return la mappa oggetto/quantita' in sola lettura
     */
    public Map<Oggetto, Integer> getContenuto() {
        return Collections.unmodifiableMap(contenuto);
    }

    /**
     * Restituisce gli oggetti posseduti appartenenti a una categoria.
     *
     * @param categoria categoria da filtrare, non nulla
     * @return la lista degli oggetti corrispondenti, eventualmente vuota
     */
    public List<Oggetto> filtraPerCategoria(CategoriaOggetto categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("La categoria da filtrare non puo' essere nulla");
        }
        return contenuto.keySet().stream()
                .filter(oggetto -> oggetto.getCategoria() == categoria)
                .collect(Collectors.toList());
    }

    /**
     * @return {@code true} se non e' presente alcun oggetto
     */
    public boolean eVuoto() {
        return contenuto.isEmpty();
    }

    /**
     * @return il numero complessivo di copie possedute
     */
    public int numeroTotaleOggetti() {
        return contenuto.values().stream().mapToInt(Integer::intValue).sum();
    }

    private void richiediOggettoValido(Oggetto oggetto) {
        if (oggetto == null) {
            throw new IllegalArgumentException("L'oggetto non puo' essere nullo");
        }
    }
}
