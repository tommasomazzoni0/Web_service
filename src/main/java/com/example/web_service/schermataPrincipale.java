package com.example.web_service;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;

public class schermataPrincipale extends Application {

    private BorderPane root;

    @Override
    public void start(Stage primaryStage) {
        root = new BorderPane();
        root.setStyle("-fx-background-color: #f4f6f9;");
        VBox barraLaterale = creaSidebar();
        root.setLeft(barraLaterale);

        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setTitle("E-commerce");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.setOnCloseRequest(event -> event.consume());
        primaryStage.setFullScreen(true);


    }

    private VBox creaSidebar() {
        VBox barraLaterale = new VBox(20);
        barraLaterale.setPadding(new Insets(20));
        barraLaterale.setStyle("-fx-background-color: #2d3e50;");
        barraLaterale.setPrefWidth(250);

        Label titolo = new Label("Ecommerce");
        titolo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #ffffff; -fx-padding: 10;");

        Button Inserisci = creaPulsante("Inserisci Prodotto", "icons/add.png");
        Button Aggiorna = creaPulsante("Aggiorna Prodotto", "icons/edit.png");
        Button Ricerca = creaPulsante("Cerca Prodotto", "icons/search.png");
        Button Visualizza = creaPulsante("Visualizza Tutti", "icons/view.png");
        Button Elimina = creaPulsante("Elimina Prodotto", "icons/delete.png");
        Button Esci = creaPulsante("Esci", "icons/exit.png");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Inserisci.setOnAction(e -> root.setCenter(schermataInserisci()));
        Aggiorna.setOnAction(e -> root.setCenter(schermataAggiorna()));
        Ricerca.setOnAction(e -> root.setCenter(schermataRicerca()));
        Visualizza.setOnAction(e -> root.setCenter(schermataVisualizza()));
        Elimina.setOnAction(e -> root.setCenter(schermataElimina()));

        Esci.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Conferma uscita");
            alert.setHeaderText("Sei sicuro di voler uscire?");
            alert.setContentText("Premi OK per uscire, oppure Annulla per rimanere nell'app.");
            alert.initOwner(root.getScene().getWindow());

            ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(okButton, cancelButton);

            alert.showAndWait().ifPresent(response -> {
                if (response == okButton) {
                    Platform.exit();
                }
            });
        });
        barraLaterale.getChildren().addAll(titolo, Inserisci, Aggiorna, Ricerca, Visualizza, Elimina,spacer, Esci);

        return barraLaterale;
    }

    private Button creaPulsante(String testo, String icona) {
        Button button = new Button(testo);
        button.setStyle(
                "-fx-background-color: #1abc9c; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 20; " +
                        "-fx-padding: 10 20;"
        );
        button.setMaxWidth(Double.MAX_VALUE);
        button.setStyle("-fx-background-radius: 20; -fx-font-size: 14px; -fx-text-fill: white;-fx-background-color: #16a085;");

        return button;
    }

    private Pane schermataInserisci() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #ecf0f1;");

        Label idText = creaLabel("ID prodotto:");
        TextField Id = creaTextField("Inserisci id prodotto");

        Label nomeText = creaLabel("Nome prodotto:");
        TextField nome = creaTextField("Inserisci nome prodotto");

        Label descrizioneText = creaLabel("Descrizione:");
        TextArea descrizione = creaTextArea("Descrizione del prodotto");

        Label prezzoText = creaLabel("Prezzo:");
        TextField prezzo = creaTextField("Inserisci prezzo");

        Label taglieLabel = creaLabel("Taglie disponibili:");
        
        TextField campoTaglia = creaTextField("Taglia (es. M)");
        TextField campoQuantitaTaglia = creaTextField("Quantità per taglia");
        Button aggiungiTaglia = creaButton("Aggiungi");

        TableView<Pair<String, Integer>> tabellaTaglie = new TableView<>();
        TableColumn<Pair<String, Integer>, String> colTaglia = new TableColumn<>("Taglia");
        TableColumn<Pair<String, Integer>, Integer> colQuantita = new TableColumn<>("Quantità");

        colTaglia.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getKey()));
        colQuantita.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getValue()).asObject());

        tabellaTaglie.getColumns().addAll(colTaglia, colQuantita);
        tabellaTaglie.setPrefHeight(150);

        aggiungiTaglia.setOnAction(e -> {
            String taglia = campoTaglia.getText().trim();
            String quantitaStr = campoQuantitaTaglia.getText().trim();

            if (!taglia.isEmpty() && quantitaStr.matches("\\d+")) {
                int quantitaPerTaglia = Integer.parseInt(quantitaStr);
                tabellaTaglie.getItems().add(new Pair<>(taglia, quantitaPerTaglia));
                campoTaglia.clear();
                campoQuantitaTaglia.clear();
            }
        });

        HBox hboxTaglie = new HBox(10, campoTaglia, campoQuantitaTaglia, aggiungiTaglia);
        hboxTaglie.setAlignment(Pos.CENTER_LEFT);

        Button inserisci = creaButton("Inserisci");

        vbox.getChildren().addAll(idText, Id, nomeText, nome, descrizioneText, descrizione, prezzoText, prezzo, taglieLabel, hboxTaglie, tabellaTaglie, inserisci);

        return vbox;
    }



    private Pane schermataAggiorna() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #ecf0f1;");
        Label nomeLbael= creaLabel("ID o Nome prodotto da aggiornare:");
        TextField nome= creaTextField("ID o Nome prodotto");
        Label descrizioneLabel = creaLabel("Nuova descrizione:");
        TextArea descrizione = creaTextArea("Nuova descrizione del prodotto");
        Label prezzoLabel =creaLabel("Nuovo prezzo:");
        TextField prezzo = creaTextField("Nuovo prezzo");
        Label quantitaLabel = creaLabel("Nuova quantità:");
        TextField quantita = creaTextField("Nuova quantità");
        Button aggiorna = creaButton("Aggiorna");
        vbox.getChildren().addAll(nomeLbael,nome,descrizioneLabel,descrizione,prezzoLabel,prezzo,quantitaLabel,quantita,aggiorna);
        return vbox;
    }

    private Pane schermataRicerca() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #ecf0f1;");
        Label cercaLabel = creaLabel("Cerca prodotto per nome o ID:");
        TextField cercaText = creaTextField("Cerca prodotto");
        Button cerca = creaButton("Cerca");
        vbox.getChildren().addAll(cercaLabel,cercaText,cerca);
        return vbox;
    }

    private Pane schermataVisualizza() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #ecf0f1;");

        TableView<String> table = new TableView<>();
        table.setPlaceholder(new Label("Nessun prodotto disponibile"));
        Label prodottiDisponibili = creaLabel("Prodotti disponibili:");

        vbox.getChildren().addAll(prodottiDisponibili, table);
        return vbox;
    }

    private Pane schermataElimina() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #ecf0f1;");
        Label idText = creaLabel("Inserisci ID del prodotto da eliminare:");
        TextField id=creaTextField("ID del prodotto");
        Button elimina= creaButton("Elimina");
        vbox.getChildren().addAll(idText,id,elimina);
        return vbox;
    }

    private Label creaLabel(String testo) {
        Label label = new Label(testo);
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        return label;
    }

    private TextField creaTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.setStyle(" -fx-border-radius: 20; -fx-padding: 10;");
        return textField;
    }

    private TextArea creaTextArea(String promptText) {
        TextArea textArea = new TextArea();
        textArea.setPromptText(promptText);
        textArea.setStyle(" -fx-border-radius: 20;");
        return textArea;
    }

    private Button creaButton(String testo) {
        Button button = new Button(testo);
        button.setStyle(
                "-fx-background-color: #1abc9c; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-background-radius: 20; " +
                        "-fx-padding: 10 20;"
        );
        return button;
    }

    public static void main(String[] args) {
        launch();
    }
}
