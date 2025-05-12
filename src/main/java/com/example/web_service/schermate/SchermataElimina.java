package com.example.web_service.schermate;

import com.example.web_service.Prodotto;
import com.example.web_service.Server;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;

public class SchermataElimina {
    Server server = new Server();
    ArrayList<Prodotto> listaProdotti;


    public Pane schermataElimina(Scene scene) {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.95); -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);");

        Label titleLabel = new Label("Elimina Prodotto");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: #2c3e50;");

        // Area per i messaggi di alert
        VBox alertBox = new VBox(10);
        alertBox.setPadding(new Insets(10));
        alertBox.setStyle("-fx-background-color: transparent;");

        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.setPadding(new Insets(10));

        TextField searchField = new TextField();
        searchField.setPromptText("Cerca per nome o ID prodotto...");
        searchField.setStyle(
                "-fx-background-color: white;" +
                        "-fx-text-fill: #2c3e50;" +
                        "-fx-prompt-text-fill: #7f8c8d;" +
                        "-fx-border-color: #00b4db;" +
                        "-fx-border-radius: 20;" +
                        "-fx-background-radius: 20;" +
                        "-fx-padding: 10 15;" +
                        "-fx-min-width: 300;"
        );

        Button searchButton = new Button("Cerca");
        searchButton.setStyle(
                "-fx-background-color: linear-gradient(to right, #00b4db, #0083b0);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 20;" +
                        "-fx-padding: 10 20;" +
                        "-fx-cursor: hand;"
        );

        searchBox.getChildren().addAll(searchField, searchButton);

        FlowPane productCardsContainer = new FlowPane();
        productCardsContainer.setHgap(20);
        productCardsContainer.setVgap(20);
        productCardsContainer.setPadding(new Insets(20));
        productCardsContainer.setAlignment(Pos.CENTER);

        ScrollPane scrollPane = new ScrollPane(productCardsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        Runnable loadProducts = () -> {
            listaProdotti = server.getProdotti();
            productCardsContainer.getChildren().clear();
            String searchText = searchField.getText().trim().toLowerCase();

            for (Prodotto prodotto : listaProdotti) {
                if (searchText.isEmpty() ||
                        prodotto.getNome().toLowerCase().contains(searchText) ||
                        prodotto.getId().toLowerCase().contains(searchText)) {
                    VBox card = createProductCard(prodotto);

                    Button deleteButton = new Button("Elimina");
                    deleteButton.setStyle(
                            "-fx-background-color: #e74c3c;" +
                                    "-fx-text-fill: white;" +
                                    "-fx-font-size: 14px;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-background-radius: 10;" +
                                    "-fx-padding: 8 15;" +
                                    "-fx-cursor: hand;"
                    );

                    deleteButton.setOnMouseEntered(e -> deleteButton.setStyle(
                            "-fx-background-color: #c0392b;" +
                                    "-fx-text-fill: white;" +
                                    "-fx-font-size: 14px;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-background-radius: 10;" +
                                    "-fx-padding: 8 15;" +
                                    "-fx-cursor: hand;"
                    ));

                    deleteButton.setOnMouseExited(e -> deleteButton.setStyle(
                            "-fx-background-color: #e74c3c;" +
                                    "-fx-text-fill: white;" +
                                    "-fx-font-size: 14px;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-background-radius: 10;" +
                                    "-fx-padding: 8 15;" +
                                    "-fx-cursor: hand;"
                    ));

                    deleteButton.setOnAction(e -> {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Conferma eliminazione");
                        alert.setHeaderText("Sei sicuro di voler eliminare il prodotto \"" + prodotto.getNome() + "\"?");
                        alert.setContentText("L'operazione non può essere annullata.");
                        alert.initOwner(scene.getWindow());

                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.isPresent() && result.get() == ButtonType.OK) {
                            try {
                                int id = Integer.parseInt(prodotto.getId());
                                String rispostaServer = Server.eliminaProdotto(id);
                                if (rispostaServer.toLowerCase().contains("successo")) {
                                    listaProdotti = server.getProdotti();
                                    productCardsContainer.getChildren().remove(card);

                                    // Aggiungi il messaggio di successo nell'alertBox
                                    Label successLabel = new Label("Prodotto eliminato con successo");
                                    successLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 14px; -fx-font-weight: bold;");
                                    alertBox.getChildren().add(successLabel);

                                    // Rimuovi il messaggio dopo 3 secondi
                                    new Thread(() -> {
                                        try {
                                            Thread.sleep(3000);
                                            Platform.runLater(() -> alertBox.getChildren().remove(successLabel));
                                        } catch (InterruptedException ex) {
                                            ex.printStackTrace();
                                        }
                                    }).start();
                                } else {
                                    // Aggiungi il messaggio di errore nell'alertBox
                                    Label errorLabel = new Label("Errore durante l'eliminazione del prodotto: " + rispostaServer);
                                    errorLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 14px; -fx-font-weight: bold;");
                                    alertBox.getChildren().add(errorLabel);

                                    // Rimuovi il messaggio dopo 3 secondi
                                    new Thread(() -> {
                                        try {
                                            Thread.sleep(3000);
                                            Platform.runLater(() -> alertBox.getChildren().remove(errorLabel));
                                        } catch (InterruptedException ex) {
                                            ex.printStackTrace();
                                        }
                                    }).start();
                                }
                            } catch (NumberFormatException ex) {
                                // Aggiungi il messaggio di errore nell'alertBox
                                Label errorLabel = new Label("ID prodotto non valido");
                                errorLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 14px; -fx-font-weight: bold;");
                                alertBox.getChildren().add(errorLabel);

                                new Thread(() -> {
                                    try {
                                        Thread.sleep(3000);
                                        Platform.runLater(() -> alertBox.getChildren().remove(errorLabel));
                                    } catch (InterruptedException ey) {
                                        ex.printStackTrace();
                                    }
                                }).start();
                            }
                        }
                    });

                    card.getChildren().add(deleteButton);
                    productCardsContainer.getChildren().add(card);
                }
            }
        };

        loadProducts.run();

        searchButton.setOnAction(e -> loadProducts.run());
        searchField.setOnAction(e -> loadProducts.run());

        vbox.getChildren().addAll(titleLabel, alertBox, searchBox, scrollPane);
        return vbox;
    }


    private VBox createProductCard(Prodotto prodotto) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 15;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);" +
                        "-fx-min-width: 300;" +
                        "-fx-max-width: 300;"
        );

        Circle idCircle = new Circle(30);
        idCircle.setStyle(
                "-fx-fill: linear-gradient(to bottom right, #00b4db, #0083b0);" +
                        "-fx-stroke: white;" +
                        "-fx-stroke-width: 2;"
        );

        String[] words = prodotto.getNome().split(" ");
        String initials = "";
        if (words.length >= 2) {
            initials = words[0].substring(0, 1) + words[1].substring(0, 1);
        } else if (words.length == 1) {
            initials = words[0].substring(0, Math.min(2, words[0].length()));
        }

        Text idText = new Text(initials.toUpperCase());
        idText.setFont(Font.font("System", FontWeight.BOLD, 16));
        idText.setFill(Color.WHITE);

        StackPane idContainer = new StackPane(idCircle, idText);
        idContainer.setAlignment(Pos.CENTER);

        Label nameLabel = new Label(prodotto.getNome());
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        nameLabel.setStyle("-fx-text-fill: #2c3e50;");

        Label idLabel = new Label("ID: " + prodotto.getId());
        idLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");

        Label descriptionLabel = new Label(prodotto.getDescrizione());
        descriptionLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14px;");
        descriptionLabel.setWrapText(true);

        Hyperlink imageLink = new Hyperlink("Link immagine");
        imageLink.setStyle("-fx-text-fill: #00b4db; -fx-font-size: 12px;");

        imageLink.setOnAction(e -> {
            String baseUrl = "https://lucacassina.altervista.org/ecommerce/sito/assets/img/";
            String nomeProdotto = prodotto.getNome().toLowerCase().replaceAll("\\s+", "");
            String[] estensioni = {".png", ".jpg", ".jpeg", ".webp"};

            new Thread(() -> {
                String imageUrlTrovato = null;

                for (String ext : estensioni) {
                    String testUrl = baseUrl + nomeProdotto + ext;
                    try {
                        HttpURLConnection conn = (HttpURLConnection) new URL(testUrl).openConnection();
                        conn.setRequestMethod("HEAD");
                        conn.setConnectTimeout(3000); // 3 secondi
                        conn.setReadTimeout(3000);
                        int responseCode = conn.getResponseCode();
                        if (responseCode == 200) {
                            imageUrlTrovato = testUrl;
                            break;
                        }
                    } catch (Exception ignored) {}
                }

                final String imageUrl = imageUrlTrovato;
                if (imageUrl != null) {
                    try {
                        String os = System.getProperty("os.name").toLowerCase();
                        Runtime runtime = Runtime.getRuntime();

                        if (os.contains("win")) {
                            runtime.exec("rundll32 url.dll,FileProtocolHandler " + imageUrl);
                        } else if (os.contains("mac")) {
                            runtime.exec("open " + imageUrl);
                        } else {
                            runtime.exec("xdg-open " + imageUrl);
                        }
                    } catch (Exception ex) {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Errore");
                            alert.setHeaderText("Impossibile aprire il browser");
                            alert.setContentText("URL: " + imageUrl + "\nErrore: " + ex.getMessage());
                            alert.showAndWait();
                        });
                    }
                } else {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Immagine non trovata");
                        alert.setHeaderText("Nessuna immagine trovata per il prodotto");
                        alert.setContentText("Sono state provate le estensioni: .png, .jpg, .jpeg, .webp");
                        alert.showAndWait();
                    });
                }
            }).start();
        });


        Label priceLabel = new Label(String.format("%.2f €", prodotto.getPrezzo()));
        priceLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        priceLabel.setStyle("-fx-text-fill: #2c3e50;");

        VBox sizesBox = new VBox(8);
        sizesBox.setStyle("-fx-background-color: transparent;");

        Label sizesTitle = new Label("Disponibilità");
        sizesTitle.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 12px;");

        GridPane sizesGrid = new GridPane();
        sizesGrid.setHgap(6);
        sizesGrid.setVgap(6);
        sizesGrid.setAlignment(Pos.CENTER_LEFT);

        String[] sizes = prodotto.getTaglie().split(",");
        int col = 0;
        int row = 0;
        int maxCols = 4;

        for (String size : sizes) {
            String[] parts = size.split(":");
            if (parts.length == 2) {
                String sizeLabel = parts[0].trim();
                String quantity = parts[1].trim();

                StackPane sizeBox = new StackPane();
                sizeBox.setStyle(
                        "-fx-background-color: #f8f9fa;" +
                                "-fx-background-radius: 4;" +
                                "-fx-padding: 6;" +
                                "-fx-border-color: #e0e0e0;" +
                                "-fx-border-radius: 4;" +
                                "-fx-min-width: 60;"
                );

                VBox sizeContent = new VBox(2);
                sizeContent.setAlignment(Pos.CENTER);

                Label sizeText = new Label(sizeLabel);
                sizeText.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px; -fx-font-weight: bold;");

                Label quantityText = new Label(quantity);
                quantityText.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 11px;");

                sizeContent.getChildren().addAll(sizeText, quantityText);
                sizeBox.getChildren().add(sizeContent);

                sizesGrid.add(sizeBox, col, row);

                col++;
                if (col >= maxCols) {
                    col = 0;
                    row++;
                }
            }
        }

        sizesBox.getChildren().addAll(sizesTitle, sizesGrid);

        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: #f8f9fa;" +
                        "-fx-background-radius: 15;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 15, 0, 0, 0);" +
                        "-fx-min-width: 300;" +
                        "-fx-max-width: 300;"
        ));

        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 15;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);" +
                        "-fx-min-width: 300;" +
                        "-fx-max-width: 300;"
        ));

        card.getChildren().addAll(idContainer, nameLabel, idLabel, descriptionLabel, imageLink, priceLabel, sizesBox);
        return card;
    }
}
