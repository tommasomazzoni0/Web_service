package com.example.web_service;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.util.converter.IntegerStringConverter;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class schermataPrincipale extends Application {

    private BorderPane root;
    Server server= new Server();
    ArrayList<Prodotto> listaProdotti;


    Scene scene;
    @Override

    public void start(Stage primaryStage) {
        listaProdotti = server.getProdotti();

        root = new BorderPane();
        root.setStyle("-fx-background-color: #f4f6f9;");
        VBox barraLaterale = creaSidebar();
        root.setLeft(barraLaterale);

        scene = new Scene(root, 1200, 800);
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

        Image logo = new Image(getClass().getResourceAsStream("/images/logo.png"));
        ImageView logoView = new ImageView(logo);
        logoView.setFitHeight(150);
        logoView.setPreserveRatio(true);
        logoView.setSmooth(true);



        Button Inserisci = creaPulsante("Inserisci Prodotto");
        Button Aggiorna = creaPulsante("Aggiorna Prodotto");
        Button Ricerca = creaPulsante("Cerca Prodotto");
        Button Visualizza = creaPulsante("Visualizza Tutti");
        Button Elimina = creaPulsante("Elimina Prodotto");
        Button Esci = creaPulsante("Esci");

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
        barraLaterale.getChildren().addAll(logoView, Inserisci, Aggiorna, Ricerca, Visualizza, Elimina,spacer, Esci);

        return barraLaterale;
    }

    private Button creaPulsante(String testo) {
        Button button = new Button(testo);
        button.setStyle("-fx-background-color: #1abc9c; " + "-fx-text-fill: white; " + "-fx-font-size: 14px; " + "-fx-font-weight: bold; " + "-fx-background-radius: 20; " + "-fx-padding: 10 20;");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setStyle("-fx-background-radius: 20; -fx-font-size: 14px; -fx-text-fill: white;-fx-background-color: #16a085;");

        return button;
    }

    private Pane schermataInserisci() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #ecf0f1;");

        // Etichette e campi per ID e Nome sulla stessa riga
        Label idText = creaLabel("ID prodotto:");
        TextField Id = creaTextField("Inserisci id prodotto");
        Label nomeText = creaLabel("Nome prodotto:");
        TextField nome = creaTextField("Inserisci nome prodotto");

        HBox hboxIdNome = new HBox(10, idText, Id, nomeText, nome);
        hboxIdNome.setAlignment(Pos.CENTER_LEFT);
        hboxIdNome.setSpacing(10);

        idText.setPrefWidth(100);
        nomeText.setPrefWidth(120);
        Id.setPrefWidth(130);
        nome.setPrefWidth(200);

        // Descrizione prodotto
        Label descrizioneText = creaLabel("Descrizione:");
        TextArea descrizione = creaTextArea("Descrizione del prodotto");

        // Prezzo
        Label prezzoText = new Label("Prezzo:");
        prezzoText.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        TextField prezzo = new TextField();
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String newText = change.getText();
            if (newText.matches("[0-9]*[.]?[0-9]*")) {
                return change;
            }
            return null;
        };
        TextFormatter<String> formatter = new TextFormatter<>(integerFilter);
        prezzo.setTextFormatter(formatter);
        prezzo.setPrefWidth(120);

        Label euroSymbol = creaLabel("€");
        euroSymbol.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        HBox hboxPrezzo = new HBox(10);
        hboxPrezzo.getChildren().addAll(prezzoText, prezzo, euroSymbol);
        hboxPrezzo.setAlignment(Pos.CENTER_LEFT);

        Button caricaFotoButton = creaButton("Carica foto");

        Label fotoCaricataLabel = creaLabel("Foto caricata correttamente");
        fotoCaricataLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #00FF00; -fx-font-weight: bold;");

        fotoCaricataLabel.setVisible(false);

        BooleanProperty fotoCaricata = new SimpleBooleanProperty(false);

        caricaFotoButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg", "*.gif"));

            File selectedFile = fileChooser.showOpenDialog(scene.getWindow());

            if (selectedFile != null) {
                fotoCaricata.set(true);
            }
        });

        fotoCaricata.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                fotoCaricataLabel.setVisible(true);
            } else {
                fotoCaricataLabel.setVisible(false);
            }
        });

        // Taglie disponibili
        Label taglieLabel = creaLabel("Taglie disponibili:");
        ChoiceBox<String> tagliaText = new ChoiceBox<>();
        tagliaText.getItems().addAll("XS", "S", "M", "L", "XL", "XXL");
        tagliaText.setValue("Taglie disponibili");
        tagliaText.setPrefWidth(200);

        TextField campoQuantitaTaglia = new TextField();
        campoQuantitaTaglia.setPromptText("Quantità per taglia");

        UnaryOperator<TextFormatter.Change> quantityFilter = change -> {
            String newText = change.getText();
            if (newText.matches("[0-9]*")) {
                return change;
            }
            return null;
        };
        campoQuantitaTaglia.setTextFormatter(new TextFormatter<>(quantityFilter));

        Button aggiungiTaglia = creaButton("Aggiungi");
        TableView<Pair<String, Integer>> tabellaTaglie = new TableView<>();
        TableColumn<Pair<String, Integer>, String> colTaglia = new TableColumn<>("Taglia");
        TableColumn<Pair<String, Integer>, Integer> colQuantita = new TableColumn<>("Quantità");

        colTaglia.setPrefWidth(100);
        colQuantita.setPrefWidth(100);
        colTaglia.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getKey()));
        colQuantita.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getValue()).asObject());

        tabellaTaglie.getColumns().addAll(colTaglia, colQuantita);
        tabellaTaglie.setPrefWidth(200);
        tabellaTaglie.setMaxWidth(200);
        tabellaTaglie.setPrefHeight(200);
        tabellaTaglie.setMaxHeight(200);

        VBox tableContainer = new VBox(tabellaTaglie);
        tableContainer.setMaxHeight(200);
        tableContainer.setMaxWidth(200);

        // Gestione errore
        Label erroreLabel = new Label("");
        erroreLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");

        aggiungiTaglia.setOnAction(e -> {
            String taglia = tagliaText.getValue();
            String quantitaStr = campoQuantitaTaglia.getText().trim();
            erroreLabel.setText("");

            if (taglia.equals("Taglie disponibili")) {
                erroreLabel.setText("Devi selezionare una taglia.");
                return;
            }
            if (quantitaStr.isEmpty()) {
                erroreLabel.setText("Devi inserire una quantità per la taglia selezionata.");
                return;
            }

            if (quantitaStr.matches("[0-9]+")) {
                int quantitaPerTaglia = Integer.parseInt(quantitaStr);
                tabellaTaglie.getItems().add(new Pair<>(taglia, quantitaPerTaglia));
                tagliaText.getItems().remove(taglia);  // Rimuove la taglia aggiunta
                tagliaText.setValue("Taglie disponibili");
                campoQuantitaTaglia.clear();
            } else {
                erroreLabel.setText("Devi inserire una quantità valida (numero intero).");
            }
        });

        // Gestione rimozione di una taglia dalla tabella
        tabellaTaglie.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Pair<String, Integer> selectedItem = tabellaTaglie.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    tabellaTaglie.getItems().remove(selectedItem);
                    tagliaText.getItems().add(selectedItem.getKey());  // Reinserisce la taglia nell'elenco
                }
            }
        });

        HBox hboxTaglie = new HBox(10, tagliaText, campoQuantitaTaglia, aggiungiTaglia);
        hboxTaglie.setAlignment(Pos.CENTER_LEFT);
        Button inserisci = creaButton("Inserisci");

        // Gestione dell'azione del pulsante "Inserisci"
        inserisci.setOnAction(e -> {
            String nomeProdotto = nome.getText().trim();
            String descrizioneProdotto = descrizione.getText().trim();
            String prezzoProdotto = prezzo.getText().trim();
            String taglieProdotto = tabellaTaglie.getItems().stream()
                    .map(pair -> pair.getKey() + ": " + pair.getValue())
                    .collect(Collectors.joining(", "));

            // Controllo che i campi obbligatori non siano vuoti
            if (nomeProdotto.isEmpty() || descrizioneProdotto.isEmpty() || prezzoProdotto.isEmpty() || tabellaTaglie.getItems().isEmpty()) {
                erroreLabel.setText("Tutti i campi obbligatori devono essere compilati.");
                return;
            }

            // Chiamata alla funzione inserisciProdotto
            String risposta = server.inserisciProdotto(nomeProdotto, descrizioneProdotto, prezzoProdotto, taglieProdotto);
            erroreLabel.setText(risposta);  // Mostra la risposta del server
        });

        vbox.getChildren().addAll(hboxIdNome, descrizioneText, descrizione, hboxPrezzo, taglieLabel, hboxTaglie, erroreLabel, tableContainer, caricaFotoButton, fotoCaricataLabel, inserisci);

        return vbox;
    }





    private Pane schermataAggiorna() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #ecf0f1;");

        Label idText = creaLabel("ID prodotto da aggiornare:");
        TextField Id = creaTextField("Inserisci id prodotto");
        Label nomeText = creaLabel("Nuovo nome prodotto:");
        TextField nome = creaTextField("Inserisci nome prodotto");

        HBox hboxIdNome = new HBox(10, idText, Id, nomeText, nome);
        hboxIdNome.setAlignment(Pos.CENTER_LEFT);
        hboxIdNome.setSpacing(10);

        idText.setPrefWidth(200);
        nomeText.setPrefWidth(170);
        Id.setPrefWidth(150);
        nome.setPrefWidth(200);
        Label descrizioneLabel = creaLabel("Nuova descrizione:");
        TextArea descrizione = creaTextArea("Nuova descrizione del prodotto");
        Button aggiorna = creaButton("Aggiorna");

        Label prezzoText = new Label("Prezzo:");
        prezzoText.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        TextField prezzo = new TextField();
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String newText = change.getText();
            if (newText.matches("[0-9]*[.]?[0-9]*")) {
                return change;
            }
            return null;
        };

        TextFormatter<String> formatter = new TextFormatter<>(integerFilter);
        prezzo.setTextFormatter(formatter);
        prezzo.setPrefWidth(120);
        Label euroSymbol = new Label("€");
        euroSymbol.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        HBox hbox = new HBox(10);
        hbox.getChildren().addAll(prezzoText, prezzo, euroSymbol);

        Button caricaFotoButton = creaButton("Carica nuova foto");

        Label fotoCaricataLabel = creaLabel("Nuova foto caricata correttamente");
        fotoCaricataLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #00FF00; -fx-font-weight: bold;");
        Button eliminaFotoButton = creaButton("Elimina foto");

        BooleanProperty fotoCaricata = new SimpleBooleanProperty(false);

        fotoCaricataLabel.setVisible(false);
        eliminaFotoButton.setVisible(false);

        caricaFotoButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg", "*.gif"));

            File selectedFile = fileChooser.showOpenDialog(scene.getWindow());

            if (selectedFile != null) {
                fotoCaricata.set(true);
            }
        });

        eliminaFotoButton.setOnAction(e -> {
            fotoCaricata.set(false);
        });

        fotoCaricata.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                caricaFotoButton.setVisible(false);
                fotoCaricataLabel.setVisible(true);
                eliminaFotoButton.setVisible(true);
            } else {
                caricaFotoButton.setVisible(true);
                fotoCaricataLabel.setVisible(false);
                eliminaFotoButton.setVisible(false);
            }
        });
        Label taglieLabel = creaLabel("Taglie disponibili:");
        ChoiceBox<String> tagliaText = new ChoiceBox<>();
        tagliaText.getItems().addAll("XS", "S", "M", "L", "XL", "XXL");
        tagliaText.setValue("Taglie disponibili");
        tagliaText.setPrefWidth(200);

        TextField campoQuantitaTaglia = new TextField();
        campoQuantitaTaglia.setPromptText("Quantità per taglia");

        UnaryOperator<TextFormatter.Change> quantityFilter = change -> {
            String newText = change.getText();
            if (newText.matches("[0-9]*")) {
                return change;
            }
            return null;
        };
        campoQuantitaTaglia.setTextFormatter(new TextFormatter<>(quantityFilter));

        Button aggiungiTaglia = creaButton("Aggiungi");

        TableView<Pair<String, Integer>> tabellaTaglie = new TableView<>();
        TableColumn<Pair<String, Integer>, String> colTaglia = new TableColumn<>("Taglia");
        TableColumn<Pair<String, Integer>, Integer> colQuantita = new TableColumn<>("Quantità");

        colTaglia.setPrefWidth(100);
        colQuantita.setPrefWidth(100);
        colTaglia.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getKey()));
        colQuantita.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getValue()).asObject());

        tabellaTaglie.getColumns().addAll(colTaglia, colQuantita);
        tabellaTaglie.setPrefWidth(200);
        tabellaTaglie.setMaxWidth(200);
        tabellaTaglie.setPrefHeight(200);
        tabellaTaglie.setMaxHeight(200);
        tabellaTaglie.setPrefHeight(300);

        VBox container = new VBox(tabellaTaglie);
        container.setPrefWidth(200);
        container.setMaxWidth(200);
        container.setPrefHeight(300);
        Label erroreLabel = new Label("");
        erroreLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");

        aggiungiTaglia.setOnAction(e -> {
            String taglia = tagliaText.getValue();
            String quantitaStr = campoQuantitaTaglia.getText().trim();
            erroreLabel.setText("");

            if (taglia.equals("Taglie disponibili")) {
                erroreLabel.setText("Devi selezionare una taglia.");
                return;
            }
            if (quantitaStr.isEmpty()) {
                erroreLabel.setText("Devi inserire una quantità per la taglia selezionata.");
                return;
            }

            if (quantitaStr.matches("[0-9]+")) {
                int quantitaPerTaglia = Integer.parseInt(quantitaStr);
                tabellaTaglie.getItems().add(new Pair<>(taglia, quantitaPerTaglia));
                tagliaText.setValue("Taglie disponibili");
                campoQuantitaTaglia.clear();
            } else {
                erroreLabel.setText("Devi inserire una quantità valida (numero intero).");
            }
        });

        HBox hboxTaglie = new HBox(10, tagliaText, campoQuantitaTaglia, aggiungiTaglia);
        hboxTaglie.setAlignment(Pos.CENTER_LEFT);

        vbox.getChildren().addAll(hboxIdNome,descrizioneLabel,descrizione,hbox,taglieLabel, hboxTaglie, erroreLabel, tabellaTaglie,caricaFotoButton, fotoCaricataLabel, eliminaFotoButton,aggiorna);
        return vbox;
    }

    private Pane schermataRicerca() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #ecf0f1;");

        Label cercaLabel = creaLabel("Cerca prodotto per nome o ID:");
        TextField cercaText = creaTextField("Cerca prodotto");
        cercaText.setMaxWidth(200);

        Button cerca = creaButton("Cerca");

        Label risultatoLabel = new Label();
        risultatoLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        cerca.setOnAction(e -> {
            String input = cercaText.getText().trim();
            Prodotto trovato = null;

            for (Prodotto prodotto : listaProdotti) {
                if (prodotto.getId().equals(input) || prodotto.getNome().equalsIgnoreCase(input)) {
                    trovato = prodotto;
                    break;
                }
            }

            if (trovato != null) {
                risultatoLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;-fx-text-fill: #2c3e50; -fx-background-color: #ecf0f1; -fx-padding: 10px; -fx-border-radius: 5px; -fx-border-color: #3498db;-fx-border-width: 2px; -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 5, 0, 2, 2);" );

                risultatoLabel.setText("Prodotto trovato:\n\n" + "ID: " + trovato.getId() + "\n" + "Nome: " + trovato.getNome() + "\n" + "Descrizione: " + trovato.getDescrizione() + "\n" + "Prezzo: " + trovato.getPrezzo() + " €\n" + "Taglie: " + trovato.getTaglie());
            } else {
                risultatoLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: red;");
                risultatoLabel.setText("Nessun prodotto trovato con ID o nome \"" + input + "\".");
            }
        });

        cercaText.setOnAction(e -> {
            String input = cercaText.getText().trim();
            Prodotto trovato = null;

            for (Prodotto prodotto : listaProdotti) {
                if (prodotto.getId().equals(input) || prodotto.getNome().equalsIgnoreCase(input)) {
                    trovato = prodotto;
                    break;
                }
            }

            if (trovato != null) {
                risultatoLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;-fx-text-fill: #2c3e50; -fx-background-color: #ecf0f1; -fx-padding: 10px; -fx-border-radius: 5px; -fx-border-color: #3498db;-fx-border-width: 2px; -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 5, 0, 2, 2);" );

                risultatoLabel.setText("Prodotto trovato:\n\n" + "ID: " + trovato.getId() + "\n" + "Nome: " + trovato.getNome() + "\n" + "Descrizione: " + trovato.getDescrizione() + "\n" + "Prezzo: " + trovato.getPrezzo() + " €\n" + "Taglie: " + trovato.getTaglie());
            } else {
                risultatoLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: red;");
                risultatoLabel.setText("Nessun prodotto trovato con ID o nome \"" + input + "\".");
            }
        });

        vbox.getChildren().addAll(cercaLabel, cercaText, cerca, risultatoLabel);
        return vbox;
    }

    private Pane schermataVisualizza() {
        String risposta = server.mostraTuttiProdotti();

        if (risposta == null || risposta.isEmpty()) {
            Label errore = new Label("Errore nel recupero dei prodotti.");
            return new VBox(errore);
        }

        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #ecf0f1;");

        Label prodottiDisponibili = creaLabel("Prodotti disponibili:");

        TableView<Prodotto> table = new TableView<>();
        table.setPlaceholder(new Label("Nessun prodotto disponibile"));
        table.setMaxWidth(650);
        VBox.setVgrow(table, Priority.ALWAYS);

        // Colonna ID
        TableColumn<Prodotto, String> colId = new TableColumn<>("ID");
        colId.setPrefWidth(50);
        colId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));

        // Colonna Nome
        TableColumn<Prodotto, String> colNome = new TableColumn<>("Nome");
        colNome.setPrefWidth(100);
        colNome.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNome()));

        // Colonna Descrizione
        TableColumn<Prodotto, String> colDescrizione = new TableColumn<>("Descrizione");
        colDescrizione.setPrefWidth(200);
        colDescrizione.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescrizione()));
        colDescrizione.setCellFactory(tc -> {
            TableCell<Prodotto, String> cell = new TableCell<>() {
                private final Text text = new Text();
                {
                    text.wrappingWidthProperty().bind(tc.widthProperty().subtract(10));
                    setGraphic(text);
                }
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    text.setText(empty || item == null ? "" : item);
                }
            };
            return cell;
        });

        // Colonna Prezzo
        TableColumn<Prodotto, String> colPrezzo = new TableColumn<>("Prezzo");
        colPrezzo.setPrefWidth(100);
        colPrezzo.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.2f €", data.getValue().getPrezzo())));

        // Colonna Foto
        TableColumn<Prodotto, String> colFoto = new TableColumn<>("Foto");
        colFoto.setPrefWidth(50);
        colFoto.setCellValueFactory(data -> {
            String path = data.getValue().getFoto();
            return new SimpleStringProperty((path != null && !path.isBlank()) ? "Sì" : "No");
        });

        // Colonna Taglie
        TableColumn<Prodotto, String> colTaglie = new TableColumn<>("Taglie");
        colTaglie.setPrefWidth(150);
        colTaglie.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTaglie()));
        colTaglie.setCellFactory(tc -> {
            TableCell<Prodotto, String> cell = new TableCell<>() {
                private final Text text = new Text();
                {
                    text.wrappingWidthProperty().bind(tc.widthProperty().subtract(10));
                    setGraphic(text);
                }
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    text.setText(empty || item == null ? "" : item.replaceAll(", ", "\n"));
                }
            };
            return cell;
        });

        table.getColumns().addAll(colId, colNome, colDescrizione, colPrezzo, colFoto, colTaglie);

        // Elabora la risposta del PHP
        String[] prodottiArray = risposta.split(" \\| ");


        for (String prodottoStr : prodottiArray) {
            String[] datiProdotto = prodottoStr.split("_");


            if (datiProdotto.length == 5) {

                String id = datiProdotto[0];
                String nome = datiProdotto[1];
                String descrizione = datiProdotto[2];
                float prezzo = Float.parseFloat(datiProdotto[3]);
                String taglie = datiProdotto[4];

                Prodotto prodotto = new Prodotto(id, nome, descrizione, prezzo, "si", taglie);
                table.getItems().add(prodotto);
            }
        }

        vbox.getChildren().addAll(prodottiDisponibili, table);
        return vbox;
    }





    private Pane schermataElimina() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #ecf0f1;");

        Label idText = creaLabel("Inserisci ID o nome del prodotto da eliminare:");
        TextField campoId = creaTextField("ID o Nome del prodotto");
        campoId.setMaxWidth(200);
        Button cercaButton = creaButton("Cerca");
        Label risultatoLabel = new Label();
        risultatoLabel.setWrapText(true);
        risultatoLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333;");

        Button eliminaButton = creaButton("Conferma eliminazione");
        eliminaButton.setVisible(false);

        Runnable cercaEEliminaProdotto = () -> {
            String input = campoId.getText().trim();
            risultatoLabel.setText("");
            eliminaButton.setVisible(false);

            if (input.isEmpty()) {
                risultatoLabel.setStyle("-fx-text-fill: red;");
                risultatoLabel.setText("Inserisci un ID o nome valido.");
                return;
            }

            Prodotto trovato = listaProdotti.stream()
                    .filter(p -> p.getId().equalsIgnoreCase(input) || p.getNome().equalsIgnoreCase(input))
                    .findFirst().orElse(null);

            if (trovato != null) {
                risultatoLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2c3e50; -fx-background-color: #ecf0f1; -fx-padding: 10px; -fx-border-radius: 5px; -fx-border-color: #3498db; -fx-border-width: 2px; -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 5, 0, 2, 2);");
                risultatoLabel.setText("Prodotto trovato:\n\n" +
                        "ID: " + trovato.getId() + "\n" +
                        "Nome: " + trovato.getNome() + "\n" +
                        "Descrizione: " + trovato.getDescrizione() + "\n" +
                        "Prezzo: " + trovato.getPrezzo() + " €\n" +
                        "Taglie: " + trovato.getTaglie());

                eliminaButton.setVisible(true);

                eliminaButton.setOnAction(ev -> {
                    Stage stage = (Stage) eliminaButton.getScene().getWindow();

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Conferma eliminazione");
                    alert.setHeaderText("Sei sicuro di voler eliminare il prodotto \"" + trovato.getNome() + "\"?");
                    alert.setContentText("L'operazione non può essere annullata.");
                    alert.initOwner(stage);

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        try {
                            int id = Integer.parseInt(trovato.getId());
                            String rispostaServer = Server.eliminaProdotto(id); // Chiamata al PHP
                            System.out.println("Risposta dal server: " + rispostaServer);

                            if (rispostaServer.toLowerCase().contains("successo")) {
                                listaProdotti.remove(trovato);
                                risultatoLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                                risultatoLabel.setText("Prodotto eliminato con successo.");
                            } else {
                                risultatoLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                                risultatoLabel.setText("Errore durante l'eliminazione del prodotto: " + rispostaServer);
                            }

                        } catch (NumberFormatException ex) {
                            risultatoLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                            risultatoLabel.setText("ID prodotto non valido.");
                        }

                        eliminaButton.setVisible(false);
                    }
                });

            } else {
                risultatoLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: red;");
                risultatoLabel.setText("Nessun prodotto trovato con ID o nome \"" + input + "\".");
            }
        };

        cercaButton.setOnAction(e -> cercaEEliminaProdotto.run());
        campoId.setOnAction(e -> cercaEEliminaProdotto.run());

        vbox.getChildren().addAll(idText, campoId, cercaButton, risultatoLabel, eliminaButton);
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
