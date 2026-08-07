package it.unicam.cs.mpgc.rpg126710.ui;

import it.unicam.cs.mpgc.rpg126710.api.Azione;
import it.unicam.cs.mpgc.rpg126710.api.OsservatoreGioco;
import it.unicam.cs.mpgc.rpg126710.model.mondo.Direzione;
import it.unicam.cs.mpgc.rpg126710.model.oggetto.Oggetto;
import it.unicam.cs.mpgc.rpg126710.model.personaggio.Eroe;
import it.unicam.cs.mpgc.rpg126710.model.personaggio.Nemico;
import it.unicam.cs.mpgc.rpg126710.service.EventoGioco;
import it.unicam.cs.mpgc.rpg126710.service.GestorePartita;
import it.unicam.cs.mpgc.rpg126710.service.MotoreGioco;
import it.unicam.cs.mpgc.rpg126710.service.StatoGioco;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.Map;

/**
 * Schermata principale della partita: mostra la stanza, lo stato dell'eroe, il
 * diario degli eventi e i comandi disponibili.
 *
 * <p>La classe realizza la parte "vista" dello schema Modello-Vista-Controllo.
 * Si registra come {@link OsservatoreGioco} presso il motore e si limita a
 * riflettere cio' che accade: non contiene alcuna regola di gioco. Il motore,
 * dal canto suo, non conosce questa classe, quindi si potrebbe affiancargli una
 * seconda interfaccia (testuale, web o mobile) senza modificarne una riga.</p>
 */
public class SchermataGioco implements OsservatoreGioco {

    private final MotoreGioco motore;
    private final GestorePartita gestorePartita;
    private final Runnable ritornoAlMenu;

    private final BorderPane radice = new BorderPane();
    private final Label etichettaStanza = new Label();
    private final Label etichettaDescrizione = new Label();
    private final VBox pannelloEroe = new VBox(6);
    private final VBox pannelloNemico = new VBox(6);
    private final FlowPane pannelloComandi = new FlowPane(8, 8);
    private final ObservableList<EventoGioco> diario = FXCollections.observableArrayList();
    private final ListView<EventoGioco> vistaDiario = new ListView<>(diario);
    private final Button bottoneSalva = new Button("Salva partita");

    /**
     * @param motore         partita da mostrare, non nulla
     * @param gestorePartita servizio di salvataggio, non nullo
     * @param ritornoAlMenu  azione da eseguire per tornare alla schermata iniziale, non nulla
     * @throws IllegalArgumentException se un parametro e' nullo
     */
    public SchermataGioco(MotoreGioco motore, GestorePartita gestorePartita, Runnable ritornoAlMenu) {
        if (motore == null || gestorePartita == null || ritornoAlMenu == null) {
            throw new IllegalArgumentException("I collaboratori della schermata non possono essere nulli");
        }
        this.motore = motore;
        this.gestorePartita = gestorePartita;
        this.ritornoAlMenu = ritornoAlMenu;
        costruisciInterfaccia();
    }

    /**
     * @return il nodo radice da inserire nella scena
     */
    public Parent getRadice() {
        return radice;
    }

    /**
     * Collega la schermata al motore e mette in moto la partita.
     *
     * @param partitaRipresa {@code true} se la partita proviene da un salvataggio
     */
    public void avvia(boolean partitaRipresa) {
        motore.registraOsservatore(this);
        if (partitaRipresa) {
            motore.riprendiPartita();
        } else {
            motore.iniziaPartita();
        }
        aggiornaInterfaccia();
    }

    @Override
    public void suEvento(EventoGioco evento) {
        diario.add(evento);
        vistaDiario.scrollTo(diario.size() - 1);
    }

    private void costruisciInterfaccia() {
        radice.getStyleClass().add("schermata-gioco");
        radice.setTop(creaIntestazione());
        radice.setLeft(creaColonna(pannelloEroe, "pannello-eroe"));
        radice.setRight(creaColonna(pannelloNemico, "pannello-nemico"));
        radice.setCenter(creaDiario());
        radice.setBottom(creaBarraComandi());
    }

    private Region creaIntestazione() {
        etichettaStanza.getStyleClass().add("titolo-stanza");
        etichettaDescrizione.getStyleClass().add("descrizione-stanza");
        etichettaDescrizione.setWrapText(true);

        bottoneSalva.setOnAction(evento -> salvaPartita());

        Button bottoneMenu = new Button("Torna al menu");
        bottoneMenu.setOnAction(evento -> ritornoAlMenu.run());

        HBox comandiPartita = new HBox(8, bottoneSalva, bottoneMenu);
        comandiPartita.setAlignment(Pos.CENTER_RIGHT);

        VBox testi = new VBox(4, etichettaStanza, etichettaDescrizione);
        HBox.setHgrow(testi, javafx.scene.layout.Priority.ALWAYS);

        HBox intestazione = new HBox(12, testi, comandiPartita);
        intestazione.getStyleClass().add("intestazione");
        intestazione.setPadding(new Insets(14));
        return intestazione;
    }

    private Region creaColonna(VBox contenuto, String classeStile) {
        contenuto.getStyleClass().add(classeStile);
        contenuto.setPadding(new Insets(14));
        contenuto.setPrefWidth(240);
        return contenuto;
    }

    private Region creaDiario() {
        vistaDiario.getStyleClass().add("diario");
        vistaDiario.setCellFactory(lista -> new CellaEvento());
        VBox contenitore = new VBox(6, new Label("Diario della spedizione"), vistaDiario);
        contenitore.setPadding(new Insets(14));
        VBox.setVgrow(vistaDiario, javafx.scene.layout.Priority.ALWAYS);
        return contenitore;
    }

    private Region creaBarraComandi() {
        pannelloComandi.getStyleClass().add("comandi");
        pannelloComandi.setPadding(new Insets(14));
        pannelloComandi.setAlignment(Pos.CENTER_LEFT);
        return pannelloComandi;
    }

    private void aggiornaInterfaccia() {
        aggiornaStanza();
        aggiornaPannelloEroe();
        aggiornaPannelloNemico();
        aggiornaComandi();
        bottoneSalva.setDisable(motore.getStato() != StatoGioco.ESPLORAZIONE);
        bottoneSalva.setTooltip(new Tooltip(bottoneSalva.isDisabled()
                ? "Puoi salvare solo mentre esplori, non durante uno scontro."
                : "Conserva la partita per riprenderla piu' tardi."));
    }

    private void aggiornaStanza() {
        etichettaStanza.setText(motore.getStanzaCorrente().getNome());
        etichettaDescrizione.setText(motore.getStanzaCorrente().getDescrizione());
    }

    private void aggiornaPannelloEroe() {
        Eroe eroe = motore.getEroe();
        pannelloEroe.getChildren().setAll(
                titoloPannello(eroe.getNome()),
                new Label(eroe.getNomeClasse() + " - livello " + eroe.getLivello()),
                barraVita(eroe.getPuntiVita(), eroe.getPuntiVitaMassimi()),
                new Label(eroe.getPuntiVita() + " / " + eroe.getPuntiVitaMassimi() + " PV"),
                new Label(eroe.getStatisticheEffettive().toString()),
                new Label("Prossimo livello: " + eroe.getEsperienzaAlProssimoLivello() + " PE"),
                separatore("Equipaggiamento"),
                etichettaACapo(eroe.getEquipaggiamento().riepilogo()),
                separatore("Zaino"),
                etichettaACapo(descriviInventario(eroe)));
    }

    private void aggiornaPannelloNemico() {
        pannelloNemico.getChildren().clear();
        motore.getNemicoInCombattimento().ifPresentOrElse(
                this::mostraNemico,
                () -> pannelloNemico.getChildren().addAll(
                        titoloPannello("Nessuna minaccia"),
                        etichettaACapo("La stanza e' tranquilla. Scegli dove andare.")));
    }

    private void mostraNemico(Nemico nemico) {
        pannelloNemico.getChildren().addAll(
                titoloPannello(nemico.getNome()),
                barraVita(nemico.getPuntiVita(), nemico.getPuntiVitaMassimi()),
                new Label(nemico.getPuntiVita() + " / " + nemico.getPuntiVitaMassimi() + " PV"),
                new Label(nemico.getStatisticheEffettive().toString()));
    }

    private void aggiornaComandi() {
        pannelloComandi.getChildren().clear();
        switch (motore.getStato()) {
            case COMBATTIMENTO -> mostraComandiCombattimento();
            case ESPLORAZIONE -> mostraComandiEsplorazione();
            case VITTORIA, SCONFITTA -> mostraComandiFinePartita();
        }
    }

    private void mostraComandiCombattimento() {
        for (Azione azione : motore.azioniDisponibili()) {
            Button bottone = new Button(azione.getNome());
            bottone.setTooltip(new Tooltip(azione.getDescrizione()));
            bottone.setOnAction(evento -> eseguiAzione(azione));
            pannelloComandi.getChildren().add(bottone);
        }
    }

    private void mostraComandiEsplorazione() {
        for (Direzione direzione : motore.direzioniDisponibili()) {
            Button bottone = new Button("Vai a " + direzione.getEtichetta());
            bottone.setOnAction(evento -> muovi(direzione));
            pannelloComandi.getChildren().add(bottone);
        }
        if (pannelloComandi.getChildren().isEmpty()) {
            pannelloComandi.getChildren().add(new Label("Non ci sono uscite da questa stanza."));
        }
    }

    private void mostraComandiFinePartita() {
        Label esito = new Label(motore.getStato() == StatoGioco.VITTORIA
                ? "Hai raggiunto l'uscita: spedizione conclusa."
                : "Il dungeon ha avuto la meglio.");
        esito.getStyleClass().add("esito-partita");

        Button bottoneMenu = new Button("Torna al menu");
        bottoneMenu.getStyleClass().add("bottone-principale");
        bottoneMenu.setOnAction(evento -> ritornoAlMenu.run());

        pannelloComandi.getChildren().addAll(esito, bottoneMenu);
    }

    private void eseguiAzione(Azione azione) {
        motore.eseguiAzioneCombattimento(azione);
        aggiornaInterfaccia();
    }

    private void muovi(Direzione direzione) {
        motore.muovi(direzione);
        aggiornaInterfaccia();
    }

    private void salvaPartita() {
        try {
            gestorePartita.salva(motore);
            suEvento(new EventoGioco(it.unicam.cs.mpgc.rpg126710.service.TipoEvento.PARTITA_SALVATA,
                    "Partita salvata."));
        } catch (RuntimeException errore) {
            Alert avviso = new Alert(Alert.AlertType.ERROR, "Salvataggio non riuscito: " + errore.getMessage());
            avviso.setHeaderText(null);
            avviso.showAndWait();
        }
    }

    private String descriviInventario(Eroe eroe) {
        if (eroe.getInventario().eVuoto()) {
            return "Lo zaino e' vuoto.";
        }
        StringBuilder descrizione = new StringBuilder();
        for (Map.Entry<Oggetto, Integer> voce : eroe.getInventario().getContenuto().entrySet()) {
            descrizione.append("- ").append(voce.getKey().riepilogo());
            if (voce.getValue() > 1) {
                descrizione.append(" x").append(voce.getValue());
            }
            descrizione.append(System.lineSeparator());
        }
        return descrizione.toString().trim();
    }

    private Label titoloPannello(String testo) {
        Label etichetta = new Label(testo);
        etichetta.getStyleClass().add("titolo-pannello");
        return etichetta;
    }

    private Label separatore(String testo) {
        Label etichetta = new Label(testo);
        etichetta.getStyleClass().add("separatore");
        return etichetta;
    }

    private Label etichettaACapo(String testo) {
        Label etichetta = new Label(testo);
        etichetta.setWrapText(true);
        return etichetta;
    }

    private ProgressBar barraVita(int puntiVita, int puntiVitaMassimi) {
        ProgressBar barra = new ProgressBar((double) puntiVita / puntiVitaMassimi);
        barra.setMaxWidth(Double.MAX_VALUE);
        barra.getStyleClass().add("barra-vita");
        return barra;
    }

    /**
     * Cella del diario che assegna a ogni riga uno stile diverso a seconda del
     * tipo di evento, cosi' che vittorie, sconfitte e ritrovamenti si
     * distinguano a colpo d'occhio.
     */
    private static class CellaEvento extends ListCell<EventoGioco> {

        @Override
        protected void updateItem(EventoGioco evento, boolean vuota) {
            super.updateItem(evento, vuota);
            getStyleClass().removeIf(classe -> classe.startsWith("evento-"));
            if (vuota || evento == null) {
                setText(null);
                return;
            }
            setText(evento.getMessaggio());
            setWrapText(true);
            getStyleClass().add("evento-" + evento.getTipo().name().toLowerCase());
        }
    }
}
