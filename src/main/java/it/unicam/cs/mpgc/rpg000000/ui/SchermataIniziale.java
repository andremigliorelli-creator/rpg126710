package it.unicam.cs.mpgc.rpg000000.ui;

import it.unicam.cs.mpgc.rpg000000.service.GestorePartita;
import it.unicam.cs.mpgc.rpg000000.service.MotoreGioco;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Optional;

/**
 * Schermata di apertura: consente di creare un nuovo personaggio o di
 * riprendere la partita salvata.
 *
 * <p>La classe si occupa solo di raccogliere le scelte dell'utente e di
 * chiedere al {@link GestorePartita} di preparare la partita. Non conosce ne'
 * come viene costruito il dungeon ne' dove vengono conservati i salvataggi.</p>
 */
public class SchermataIniziale {

    private final GestorePartita gestorePartita;
    private final AvvioPartita avvioPartita;
    private final VBox radice = new VBox();

    private final TextField campoNome = new TextField();
    private final ComboBox<String> selettoreClasse = new ComboBox<>();

    /**
     * @param gestorePartita servizio che prepara le partite, non nullo
     * @param avvioPartita   richiamo da invocare quando la partita e' pronta, non nullo
     * @throws IllegalArgumentException se un parametro e' nullo
     */
    public SchermataIniziale(GestorePartita gestorePartita, AvvioPartita avvioPartita) {
        if (gestorePartita == null || avvioPartita == null) {
            throw new IllegalArgumentException("I collaboratori della schermata non possono essere nulli");
        }
        this.gestorePartita = gestorePartita;
        this.avvioPartita = avvioPartita;
        costruisciInterfaccia();
    }

    /**
     * @return il nodo radice da inserire nella scena
     */
    public Parent getRadice() {
        return radice;
    }

    private void costruisciInterfaccia() {
        radice.getStyleClass().add("schermata-iniziale");
        radice.setAlignment(Pos.CENTER);
        radice.setSpacing(14);

        Label titolo = new Label("Le Cripte di Camerino");
        titolo.getStyleClass().add("titolo");

        Label sottotitolo = new Label("Esplora il dungeon, sopravvivi agli scontri, raggiungi l'uscita.");
        sottotitolo.getStyleClass().add("sottotitolo");

        campoNome.setPromptText("Nome del personaggio");
        campoNome.setMaxWidth(260);

        selettoreClasse.getItems().addAll(new it.unicam.cs.mpgc.rpg000000.service.FabbricaEroi().classiDisponibili());
        selettoreClasse.getSelectionModel().selectFirst();
        selettoreClasse.setMaxWidth(260);

        Button bottoneNuova = new Button("Nuova partita");
        bottoneNuova.getStyleClass().add("bottone-principale");
        bottoneNuova.setOnAction(evento -> avviaNuovaPartita());

        Button bottoneRiprendi = new Button("Riprendi partita");
        bottoneRiprendi.setDisable(!gestorePartita.esisteSalvataggio());
        bottoneRiprendi.setOnAction(evento -> riprendiPartita());

        HBox comandi = new HBox(12, bottoneNuova, bottoneRiprendi);
        comandi.setAlignment(Pos.CENTER);

        radice.getChildren().addAll(titolo, sottotitolo,
                new Label("Nome"), campoNome,
                new Label("Classe"), selettoreClasse,
                comandi);
    }

    private void avviaNuovaPartita() {
        String nome = campoNome.getText() == null ? "" : campoNome.getText().trim();
        if (nome.isBlank()) {
            mostraAvviso("Scegli un nome per il tuo personaggio prima di iniziare.");
            return;
        }
        try {
            MotoreGioco motore = gestorePartita.nuovaPartita(nome, selettoreClasse.getValue());
            avvioPartita.avvia(motore, false);
        } catch (RuntimeException errore) {
            mostraAvviso("Impossibile iniziare la partita: " + errore.getMessage());
        }
    }

    private void riprendiPartita() {
        try {
            Optional<MotoreGioco> motore = gestorePartita.carica();
            if (motore.isEmpty()) {
                mostraAvviso("Non e' stata trovata alcuna partita salvata.");
                return;
            }
            avvioPartita.avvia(motore.get(), true);
        } catch (RuntimeException errore) {
            mostraAvviso("Impossibile riprendere la partita: " + errore.getMessage());
        }
    }

    private void mostraAvviso(String messaggio) {
        Alert avviso = new Alert(Alert.AlertType.WARNING, messaggio);
        avviso.setHeaderText(null);
        avviso.showAndWait();
    }
}
