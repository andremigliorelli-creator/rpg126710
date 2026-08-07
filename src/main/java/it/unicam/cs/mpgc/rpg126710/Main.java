package it.unicam.cs.mpgc.rpg126710;

import it.unicam.cs.mpgc.rpg126710.ui.ApplicazioneRpg;
import javafx.application.Application;

/**
 * Punto di ingresso dell'applicazione.
 *
 * <p>La classe non estende {@link Application}: e' un lanciatore separato, la
 * soluzione consigliata per avviare un'applicazione JavaFX quando le librerie
 * si trovano sul classpath. Tenere distinto l'avvio dalla classe grafica rende
 * inoltre evidente che il punto di ingresso ha una sola responsabilita': far
 * partire il programma.</p>
 */
public final class Main {

    private Main() {
        // Classe di sola utilita': non deve essere istanziata.
    }

    /**
     * Avvia l'applicazione grafica.
     *
     * @param args argomenti da riga di comando, non utilizzati
     */
    public static void main(String[] args) {
        Application.launch(ApplicazioneRpg.class, args);
    }
}
