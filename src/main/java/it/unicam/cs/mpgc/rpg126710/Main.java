package it.unicam.cs.mpgc.rpg126710;

import it.unicam.cs.mpgc.rpg126710.ui.ApplicazioneRpg;
import javafx.application.Application;

/**
 * Punto di ingresso dell'applicazione.
 *
 * <p>La classe non estende {@link Application}: e' un lanciatore separato.
 * Ora che il progetto e' un modulo JPMS la separazione non e' piu' obbligatoria,
 * ma resta preferibile perche' tiene distinta la responsabilita' di far partire
 * il programma da quella di costruirne l'interfaccia.</p>
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
