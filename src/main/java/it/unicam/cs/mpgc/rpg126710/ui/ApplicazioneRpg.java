package it.unicam.cs.mpgc.rpg126710.ui;

import it.unicam.cs.mpgc.rpg126710.api.Dado;
import it.unicam.cs.mpgc.rpg126710.persistence.CaricatoreDungeonJson;
import it.unicam.cs.mpgc.rpg126710.persistence.RepositoryPartitaJson;
import it.unicam.cs.mpgc.rpg126710.service.FabbricaEroi;
import it.unicam.cs.mpgc.rpg126710.service.GestorePartita;
import it.unicam.cs.mpgc.rpg126710.service.MotoreGioco;
import it.unicam.cs.mpgc.rpg126710.util.DadoCasuale;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Applicazione grafica: costruisce i collaboratori concreti e alterna le
 * schermate.
 *
 * <p>E' l'unico punto in cui si decide quale implementazione usare per ciascuna
 * astrazione: il dungeon arriva da un file JSON, i salvataggi da un file su
 * disco, la casualita' da un generatore pseudocasuale. Tutto il resto del
 * programma conosce soltanto i contratti, quindi sostituire una di queste
 * scelte significa modificare questa sola classe.</p>
 */
public class ApplicazioneRpg extends Application {

    private static final int LARGHEZZA_FINESTRA = 980;
    private static final int ALTEZZA_FINESTRA = 640;

    private Stage finestra;
    private GestorePartita gestorePartita;

    @Override
    public void start(Stage finestraPrincipale) {
        this.finestra = finestraPrincipale;
        this.gestorePartita = creaGestorePartita();

        finestra.setTitle("Le Cripte di Camerino - Progetto RPG");
        finestra.setMinWidth(LARGHEZZA_FINESTRA);
        finestra.setMinHeight(ALTEZZA_FINESTRA);
        mostraSchermataIniziale();
        finestra.show();
    }

    private GestorePartita creaGestorePartita() {
        Dado dado = new DadoCasuale();
        CaricatoreDungeonJson caricatore = new CaricatoreDungeonJson(dado);
        return new GestorePartita(
                caricatore,
                caricatore,
                new RepositoryPartitaJson(),
                new FabbricaEroi(),
                dado);
    }

    /**
     * Mostra la schermata di scelta del personaggio.
     */
    public void mostraSchermataIniziale() {
        SchermataIniziale schermata = new SchermataIniziale(gestorePartita, this::avviaPartita);
        cambiaScena(schermata.getRadice());
    }

    private void avviaPartita(MotoreGioco motore, boolean partitaRipresa) {
        SchermataGioco schermata = new SchermataGioco(motore, gestorePartita, this::mostraSchermataIniziale);
        cambiaScena(schermata.getRadice());
        schermata.avvia(partitaRipresa);
    }

    private void cambiaScena(Parent radice) {
        Scene scena = new Scene(radice, LARGHEZZA_FINESTRA, ALTEZZA_FINESTRA);
        scena.getStylesheets().add(getClass().getResource("/stile.css").toExternalForm());
        finestra.setScene(scena);
    }
}
