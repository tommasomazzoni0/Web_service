package com.example.web_service.schermate;

import com.example.web_service.Server;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SchermataUtenti {
    public Pane schermataUtenti() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.95); -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);");

        Label titleLabel = new Label("Visualizzazione Utenti");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: #2c3e50;");

        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.setPadding(new Insets(10));

        TextField searchField = new TextField();
        searchField.setPromptText("Cerca per nome, cognome o username...");
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

        FlowPane userCardsContainer = new FlowPane();
        userCardsContainer.setHgap(20);
        userCardsContainer.setVgap(20);
        userCardsContainer.setPadding(new Insets(20));
        userCardsContainer.setAlignment(Pos.CENTER);

        ScrollPane scrollPane = new ScrollPane(userCardsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        Runnable loadUsers = () -> {
            userCardsContainer.getChildren().clear();
            try {
                URL url = new URL("https://lucacassina.altervista.org/ecommerce/tornaUtenti.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder responseBuilder = new StringBuilder();
                String line;

                while ((line = in.readLine()) != null) {
                    responseBuilder.append(line);
                }
                in.close();
                connection.disconnect();

                String response = responseBuilder.toString();
                String[] lines = response.split("<br>");

                Map<String, String> currentUser = null;
                String searchText = searchField.getText().trim().toLowerCase();

                for (String line1 : lines) {
                    line1 = line1.trim();
                    if (line1.isEmpty()) continue;

                    if (line1.startsWith("ID Account:")) {
                        if (currentUser != null && currentUser.containsKey("id")) {
                            if (searchText.isEmpty() ||
                                    currentUser.get("nome").toLowerCase().contains(searchText) ||
                                    currentUser.get("cognome").toLowerCase().contains(searchText) ||
                                    currentUser.get("username").toLowerCase().contains(searchText)) {
                                userCardsContainer.getChildren().add(createUserCard(currentUser));
                            }
                        }
                        currentUser = new HashMap<>();
                        currentUser.put("id", line1.substring("ID Account:".length()).trim());
                    }
                    else if (currentUser != null) {
                        if (line1.startsWith("Username:")) {
                            currentUser.put("username", line1.substring("Username:".length()).trim());
                        } else if (line1.startsWith("Email:")) {
                            currentUser.put("email", line1.substring("Email:".length()).trim());
                        } else if (line1.startsWith("Nome:")) {
                            currentUser.put("nome", line1.substring("Nome:".length()).trim());
                        } else if (line1.startsWith("Cognome:")) {
                            currentUser.put("cognome", line1.substring("Cognome:".length()).trim());
                        }
                    }
                }

                if (currentUser != null && currentUser.containsKey("id")) {
                    if (searchText.isEmpty() ||
                            currentUser.get("nome").toLowerCase().contains(searchText) ||
                            currentUser.get("cognome").toLowerCase().contains(searchText) ||
                            currentUser.get("username").toLowerCase().contains(searchText)) {
                        userCardsContainer.getChildren().add(createUserCard(currentUser));
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                Label errorLabel = new Label("Errore nel caricamento degli utenti: " + ex.getMessage());
                errorLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 14px;");
                userCardsContainer.getChildren().add(errorLabel);
            }
        };

        loadUsers.run();

        searchButton.setOnAction(e -> loadUsers.run());
        searchField.setOnAction(e -> loadUsers.run());

        vbox.getChildren().addAll(titleLabel, searchBox, scrollPane);
        return vbox;
    }

    private VBox createUserCard(Map<String, String> user) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 15;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);" +
                        "-fx-min-width: 300;" +
                        "-fx-max-width: 300;"
        );

        String initials = "";
        if (user.containsKey("nome") && user.containsKey("cognome")) {
            String nome = user.get("nome");
            String cognome = user.get("cognome");
            if (!nome.isEmpty() && !cognome.isEmpty()) {
                initials = nome.substring(0, 1) + cognome.substring(0, 1);
            }
        }

        Circle avatar = new Circle(40);
        avatar.setStyle(
                "-fx-fill: linear-gradient(to bottom right, #00b4db, #0083b0);" +
                        "-fx-stroke: white;" +
                        "-fx-stroke-width: 2;"
        );

        Text avatarText = new Text(initials.toUpperCase());
        avatarText.setFont(Font.font("System", FontWeight.BOLD, 24));
        avatarText.setFill(Color.WHITE);

        StackPane avatarContainer = new StackPane(avatar, avatarText);
        avatarContainer.setAlignment(Pos.CENTER);

        Label nameLabel = new Label(user.get("nome") + " " + user.get("cognome"));
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        nameLabel.setStyle("-fx-text-fill: #2c3e50;");

        Label usernameLabel = new Label("@" + user.get("username"));
        usernameLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14px;");

        Label emailLabel = new Label(user.get("email"));
        emailLabel.setStyle("-fx-text-fill: #34495e; -fx-font-size: 14px;");

        Label idLabel = new Label("ID: " + user.get("id"));
        idLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");

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

        card.getChildren().addAll(avatarContainer, nameLabel, usernameLabel, emailLabel, idLabel);
        return card;
    }
}
