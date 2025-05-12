package com.example.web_service.schermate;

import com.example.web_service.Prodotto;
import com.example.web_service.Server;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SchermataAggiorna {
    Server server= new Server();
    public Pane schermataAggiorna() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.95); -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);");

        Label idLabel = creaLabel("ID prodotto da aggiornare:");
        ChoiceBox<String> idChoiceBox = new ChoiceBox<>();
        idChoiceBox.setPrefWidth(150);
        idChoiceBox.setValue("Seleziona ID...");

        Label nomeText = creaLabel("Nuovo nome prodotto:");
        TextField nome = creaTextField("Inserisci nome prodotto");

        HBox hboxIdNome = new HBox(10, idLabel, idChoiceBox, nomeText, nome);
        hboxIdNome.setAlignment(Pos.CENTER_LEFT);

        Label descrizioneLabel = creaLabel("Nuova descrizione:");
        TextArea descrizione = creaTextArea("Nuova descrizione del prodotto");

        Label prezzoText = creaLabel("Prezzo:");
        TextField prezzo = new TextField();
        prezzo.setPrefWidth(120);
        prezzo.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getText();
            return newText.matches("[0-9]*[.]?[0-9]*") ? change : null;
        }));
        Label euroSymbol = creaLabel("€");
        HBox hboxPrezzo = new HBox(10, prezzoText, prezzo, euroSymbol);

        Label taglieLabel = creaLabel("Taglie disponibili:");
        ChoiceBox<String> tagliaText = new ChoiceBox<>();
        ObservableList<String> taglieDisponibili = FXCollections.observableArrayList("XS", "S", "M", "L", "XL");
        tagliaText.setItems(taglieDisponibili);
        tagliaText.setValue("Taglie disponibili");

        TextField campoQuantitaTaglia = new TextField();
        campoQuantitaTaglia.setPromptText("Quantità per taglia");
        campoQuantitaTaglia.setTextFormatter(new TextFormatter<>(change -> change.getText().matches("[0-9]*") ? change : null));
        Button aggiungiTaglia = creaButton("Aggiungi");

        TableView<Pair<String, Integer>> tabellaTaglie = new TableView<>();
        TableColumn<Pair<String, Integer>, String> colTaglia = new TableColumn<>("Taglia");
        TableColumn<Pair<String, Integer>, Integer> colQuantita = new TableColumn<>("Quantità");

        colTaglia.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getKey()));
        colQuantita.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getValue()).asObject());
        colTaglia.setPrefWidth(100);
        colQuantita.setPrefWidth(100);
        colTaglia.setResizable(false);
        colQuantita.setResizable(false);
        colTaglia.setReorderable(false);
        colQuantita.setReorderable(false);
        tabellaTaglie.getColumns().addAll(colTaglia, colQuantita);
        tabellaTaglie.setPrefWidth(200);
        tabellaTaglie.setMaxWidth(200);
        tabellaTaglie.setPrefHeight(200);
        tabellaTaglie.setMaxHeight(200);

        VBox tableContainer = new VBox(tabellaTaglie);
        tableContainer.setMaxHeight(200);
        tableContainer.setMaxWidth(200);

        Label erroreLabel = creaLabel("");
        erroreLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");

        Label successoLabel = creaLabel("");
        successoLabel.setStyle("-fx-text-fill: green; -fx-font-size: 14px;");
        successoLabel.setVisible(false);

        aggiungiTaglia.setOnAction(e -> {
            String taglia = tagliaText.getValue();
            String quantitaStr = campoQuantitaTaglia.getText().trim();
            erroreLabel.setText("");
            if (taglia.equals("Taglie disponibili") || quantitaStr.isEmpty()) {
                erroreLabel.setText("Inserisci una taglia e una quantità valide.");
                return;
            }
            int quantita = Integer.parseInt(quantitaStr);

            tabellaTaglie.getItems().removeIf(item -> item.getKey().equals(taglia));

            tabellaTaglie.getItems().add(new Pair<>(taglia, quantita));

            taglieDisponibili.remove(taglia);
            tagliaText.setValue("Taglie disponibili");
            campoQuantitaTaglia.clear();
        });

        tabellaTaglie.setRowFactory(tv -> {
            TableRow<Pair<String, Integer>> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 2) {
                    Pair<String, Integer> taglia = row.getItem();
                    tabellaTaglie.getItems().remove(taglia);
                    if (!taglieDisponibili.contains(taglia.getKey())) {
                        taglieDisponibili.add(taglia.getKey());
                        FXCollections.sort(taglieDisponibili);
                    }
                }
            });
            return row;
        });

        Button aggiorna = creaButton("Aggiorna");
        aggiorna.setOnAction(e -> {
            String id = idChoiceBox.getValue();
            String nomeVal = nome.getText();
            String descrizioneVal = descrizione.getText();
            String prezzoVal = prezzo.getText();

            StringBuilder taglieString = new StringBuilder();
            for (Pair<String, Integer> item : tabellaTaglie.getItems()) {
                taglieString.append(item.getKey()).append(":").append(item.getValue()).append(",");
            }
            if (taglieString.length() > 0)
                taglieString.setLength(taglieString.length() - 1);

            server.aggiornaProdotto(id, nomeVal, descrizioneVal, Float.parseFloat(prezzoVal), taglieString.toString());

            successoLabel.setText("Prodotto aggiornato con successo!");
            successoLabel.setVisible(true);
        });

        ArrayList<Prodotto> prodotti = server.getProdotti();
        Map<String, Prodotto> mappaProdotti = new HashMap<>();
        for (Prodotto p : prodotti) {
            idChoiceBox.getItems().add(p.getId());
            mappaProdotti.put(p.getId(), p);
        }

        idChoiceBox.setOnAction(e -> {
            String selectedId = idChoiceBox.getValue();
            if (selectedId != null && mappaProdotti.containsKey(selectedId)) {
                Prodotto p = mappaProdotti.get(selectedId);
                nome.setText(p.getNome());
                descrizione.setText(p.getDescrizione());
                prezzo.setText(String.valueOf(p.getPrezzo()));
                tabellaTaglie.getItems().clear();
                taglieDisponibili.setAll("XS", "S", "M", "L", "XL");
                for (String taglia : p.getTaglie().split(",")) {
                    String[] parts = taglia.split(":");
                    if (parts.length == 2) {
                        String tg = parts[0].trim();
                        int qt = Integer.parseInt(parts[1].trim());
                        tabellaTaglie.getItems().add(new Pair<>(tg, qt));
                        taglieDisponibili.remove(tg);
                    }
                }
            }
        });

        HBox hboxTaglie = new HBox(10, tagliaText, campoQuantitaTaglia, aggiungiTaglia);

        vbox.getChildren().addAll(hboxIdNome, descrizioneLabel, descrizione, hboxPrezzo, taglieLabel, hboxTaglie,
                erroreLabel, tabellaTaglie, successoLabel, aggiorna);

        return vbox;
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
