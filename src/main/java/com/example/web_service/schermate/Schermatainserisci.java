package com.example.web_service.schermate;

import com.example.web_service.Server;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Pair;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Collectors;

public class Schermatainserisci {

    Server server= new Server();
    public Pane schermataInserisci(Scene scene) {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.95); -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);");

        Label nomeText = creaLabel("Nome prodotto:");
        TextField nome = creaTextField("Inserisci nome prodotto");

        File[] immagineSelezionata = new File[1];

        HBox hboxIdNome = new HBox(10, nomeText, nome);
        hboxIdNome.setAlignment(Pos.CENTER_LEFT);
        nomeText.setPrefWidth(120);
        nome.setPrefWidth(200);

        Label descrizioneText = creaLabel("Descrizione:");
        TextArea descrizione = creaTextArea("Descrizione del prodotto");

        Label prezzoText = new Label("Prezzo:");
        prezzoText.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        TextField prezzo = new TextField();
        prezzo.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getText().matches("[0-9]*[.]?[0-9]*")) return change;
            return null;
        }));
        prezzo.setPrefWidth(120);

        Label euroSymbol = creaLabel("€");
        euroSymbol.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        HBox hboxPrezzo = new HBox(10, prezzoText, prezzo, euroSymbol);
        hboxPrezzo.setAlignment(Pos.CENTER_LEFT);

        Button caricaFotoButton = creaButton("Carica foto");
        Label fotoCaricataLabel = creaLabel("Foto caricata correttamente");
        fotoCaricataLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #00FF00; -fx-font-weight: bold;");
        fotoCaricataLabel.setVisible(false);

        BooleanProperty fotoCaricata = new SimpleBooleanProperty(false);

        Label erroreLabel = new Label("");
        erroreLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");

        Label successoLabel = new Label("");
        successoLabel.setStyle("-fx-font-size: 14px; -fx-text-fill:green;");
        successoLabel.setVisible(false);

        caricaFotoButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg", "*.gif"));
            File selectedFile = fileChooser.showOpenDialog(scene.getWindow());
            if (selectedFile != null) {
                try {
                    uploadImageToServer(selectedFile, nome.getText().trim());
                    immagineSelezionata[0] = selectedFile;
                    fotoCaricata.set(true);
                } catch (IOException ex) {
                    erroreLabel.setText("Errore durante l'upload dell'immagine.");
                    ex.printStackTrace();
                }
            }
        });

        fotoCaricata.addListener((obs, oldVal, newVal) -> fotoCaricataLabel.setVisible(newVal));

        Label taglieLabel = creaLabel("Taglie disponibili:");
        ChoiceBox<String> tagliaText = new ChoiceBox<>();
        tagliaText.getItems().addAll("XS", "S", "M", "L", "XL");
        tagliaText.setValue("Taglie disponibili");
        tagliaText.setPrefWidth(200);

        TextField campoQuantitaTaglia = new TextField();
        campoQuantitaTaglia.setPromptText("Quantità per taglia");
        campoQuantitaTaglia.setTextFormatter(new TextFormatter<>(change -> change.getText().matches("[0-9]*") ? change : null));

        Button aggiungiTaglia = creaButton("Aggiungi");
        TableView<Pair<String, Integer>> tabellaTaglie = new TableView<>();
        TableColumn<Pair<String, Integer>, String> colTaglia = new TableColumn<>("Taglia");
        TableColumn<Pair<String, Integer>, Integer> colQuantita = new TableColumn<>("Quantità");
        colTaglia.setPrefWidth(100);
        colQuantita.setPrefWidth(100);
        colTaglia.setResizable(false);
        colQuantita.setResizable(false);
        colTaglia.setReorderable(false);
        colQuantita.setReorderable(false);
        colTaglia.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getKey()));
        colQuantita.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getValue()).asObject());
        tabellaTaglie.getColumns().addAll(colTaglia, colQuantita);
        tabellaTaglie.setPrefWidth(200);
        tabellaTaglie.setMaxWidth(200);
        tabellaTaglie.setPrefHeight(200);
        tabellaTaglie.setMaxHeight(200);

        aggiungiTaglia.setOnAction(e -> {
            String taglia = tagliaText.getValue();
            String quantitaStr = campoQuantitaTaglia.getText().trim();
            erroreLabel.setText("");

            if (taglia.equals("Taglie disponibili") || quantitaStr.isEmpty()) {
                erroreLabel.setText("Compila correttamente taglia e quantità.");
                return;
            }

            if (quantitaStr.matches("[0-9]+")) {
                int quantita = Integer.parseInt(quantitaStr);
                tabellaTaglie.getItems().add(new Pair<>(taglia, quantita));
                tagliaText.getItems().remove(taglia);
                tagliaText.setValue("Taglie disponibili");
                campoQuantitaTaglia.clear();
            } else {
                erroreLabel.setText("Quantità non valida.");
            }
        });

        tabellaTaglie.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Pair<String, Integer> selected = tabellaTaglie.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    tabellaTaglie.getItems().remove(selected);
                    tagliaText.getItems().add(selected.getKey());
                }
            }
        });

        HBox hboxTaglie = new HBox(10, tagliaText, campoQuantitaTaglia, aggiungiTaglia);
        hboxTaglie.setAlignment(Pos.CENTER_LEFT);
        Button inserisci = creaButton("Inserisci");

        inserisci.setOnAction(e -> {
            String nomeProdotto = nome.getText().trim();
            String descrizioneProdotto = descrizione.getText().trim();
            String prezzoProdotto = prezzo.getText().trim();
            String taglieProdotto = tabellaTaglie.getItems().stream()
                    .map(pair -> pair.getKey() + ": " + pair.getValue())
                    .collect(Collectors.joining(", "));

            if (nomeProdotto.isEmpty() || descrizioneProdotto.isEmpty() || prezzoProdotto.isEmpty()
                    || tabellaTaglie.getItems().isEmpty() || immagineSelezionata[0] == null) {
                erroreLabel.setText("Tutti i campi e l'immagine sono obbligatori.");
                return;
            }

            String risposta = server.inserisciProdotto(nomeProdotto, descrizioneProdotto, prezzoProdotto, taglieProdotto, immagineSelezionata[0].getName());

            if (risposta.equals("Prodotto inserito con successo.")) {
                successoLabel.setText("Prodotto inserito con successo!");
                successoLabel.setVisible(true);
            } else {
                erroreLabel.setText(risposta);
            }
        });

        vbox.getChildren().addAll(hboxIdNome, descrizioneText, descrizione, hboxPrezzo, taglieLabel,
                hboxTaglie, erroreLabel, tabellaTaglie, caricaFotoButton, fotoCaricataLabel, successoLabel, inserisci);

        return vbox;
    }

    private void uploadImageToServer(File imageFile, String nomeProdotto) throws IOException {
        String url = "https://lucacassina.altervista.org/ecommerce/sito/salvaImmagine.php";
        String boundary = Long.toHexString(System.currentTimeMillis());
        String CRLF = "\r\n";

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        try (
                OutputStream output = connection.getOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8), true)
        ) {
            // Campo nome prodotto
            writer.append("--").append(boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"nome\"").append(CRLF);
            writer.append(CRLF).append(nomeProdotto).append(CRLF).flush();

            // Campo file immagine
            writer.append("--").append(boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"immagine\"; filename=\"")
                    .append(imageFile.getName()).append("\"").append(CRLF);
            writer.append("Content-Type: ").append(Files.probeContentType(imageFile.toPath())).append(CRLF);
            writer.append(CRLF).flush();
            Files.copy(imageFile.toPath(), output);
            output.flush();
            writer.append(CRLF).flush();

            writer.append("--").append(boundary).append("--").append(CRLF).flush();
        }

        if (connection.getResponseCode() != 200) {
            throw new IOException("Errore caricamento immagine: " + connection.getResponseMessage());
        }
    }

    private Label creaLabel(String testo) {
        Label label = new Label(testo);
        label.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #2c3e50;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 2, 0, 0, 1);"
        );
        return label;
    }

    private TextField creaTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.setStyle(
                "-fx-background-color: white;" +
                        "-fx-text-fill: #2c3e50;" +
                        "-fx-prompt-text-fill: #7f8c8d;" +
                        "-fx-border-color: #00b4db;" +
                        "-fx-border-radius: 20;" +
                        "-fx-background-radius: 20;" +
                        "-fx-padding: 10 15;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 3, 0, 0, 1);"
        );
        return textField;
    }

    private TextArea creaTextArea(String promptText) {
        TextArea textArea = new TextArea();
        textArea.setPromptText(promptText);
        textArea.setStyle(
                "-fx-background-color: white;" +
                        "-fx-text-fill: #2c3e50;" +
                        "-fx-prompt-text-fill: #7f8c8d;" +
                        "-fx-border-color: #00b4db;" +
                        "-fx-border-radius: 20;" +
                        "-fx-background-radius: 20;" +
                        "-fx-padding: 10 15;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 3, 0, 0, 1);"
        );
        return textArea;
    }

    private Button creaButton(String testo) {
        Button button = new Button(testo);
        button.setStyle(
                "-fx-background-color: linear-gradient(to right, #00b4db, #0083b0);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 20;" +
                        "-fx-padding: 12 25;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);"
        );

        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: linear-gradient(to right, #0083b0, #00b4db);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 20;" +
                        "-fx-padding: 12 25;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 8, 0, 0, 3);"
        ));

        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: linear-gradient(to right, #00b4db, #0083b0);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 20;" +
                        "-fx-padding: 12 25;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);"
        ));

        return button;
    }

}
