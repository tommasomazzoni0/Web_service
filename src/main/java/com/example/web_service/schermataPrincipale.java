package com.example.web_service;

import com.example.web_service.schermate.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.animation.ScaleTransition;
import javafx.animation.FadeTransition;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.effect.Glow;
import javafx.scene.effect.DropShadow;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.net.URLEncoder;


public class schermataPrincipale extends Application {

    private BorderPane root;
    Server server= new Server();
    ArrayList<Prodotto> listaProdotti;

    Schermatainserisci schermatainserisci = new Schermatainserisci();
    SchermataAggiorna schermataAggiorna= new SchermataAggiorna();
    SchermataRicerca schermataRicerca = new SchermataRicerca();
    SchermataVisualizza schermataVisualizza = new SchermataVisualizza();
    SchermataElimina schermataElimina = new SchermataElimina();
    SchermataUtenti schermataUtenti= new SchermataUtenti();
    Scene scene;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showLoginScreen();
    }

    private void showLoginScreen() {
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        // Blocca i tasti di chiusura
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Conferma uscita");
            alert.setHeaderText("Sei sicuro di voler uscire?");
            alert.setContentText("Premi OK per uscire, oppure Annulla per rimanere nell'app.");
            alert.initOwner(primaryStage);

            ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(okButton, cancelButton);

            alert.showAndWait().ifPresent(response -> {
                if (response == okButton) {
                    Platform.exit();
                }
            });
        });

        // Blocca Alt+F4 e altri tasti di sistema
        primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.F4 && event.isAltDown()) {
                event.consume();
            }
        });

        VBox loginBox = new VBox(20);
        loginBox.setAlignment(Pos.CENTER);
        loginBox.setStyle("-fx-background-color: linear-gradient(to bottom right, #0f2027, #203a43, #2c5364);");
        loginBox.setPadding(new Insets(50));

        Button closeButton = new Button("×");
        closeButton.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.15);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 24px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;" +
                        "-fx-background-radius: 50;" +
                        "-fx-min-width: 40px;" +
                        "-fx-min-height: 40px;" +
                        "-fx-padding: 0;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 0);"
        );
        closeButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Conferma uscita");
            alert.setHeaderText("Sei sicuro di voler uscire?");
            alert.setContentText("Premi OK per uscire, oppure Annulla per rimanere nell'app.");
            alert.initOwner(primaryStage);

            ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(okButton, cancelButton);

            alert.showAndWait().ifPresent(response -> {
                if (response == okButton) {
                    Platform.exit();
                }
            });
        });
        closeButton.setOnMouseEntered(e -> closeButton.setStyle(
                "-fx-background-color: #ff4444;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 24px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;" +
                        "-fx-background-radius: 50;" +
                        "-fx-min-width: 40px;" +
                        "-fx-min-height: 40px;" +
                        "-fx-padding: 0;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 8, 0, 0, 0);"
        ));
        closeButton.setOnMouseExited(e -> closeButton.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.15);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 24px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;" +
                        "-fx-background-radius: 50;" +
                        "-fx-min-width: 40px;" +
                        "-fx-min-height: 40px;" +
                        "-fx-padding: 0;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 0);"
        ));

        HBox topBar = new HBox();
        topBar.setAlignment(Pos.TOP_RIGHT);
        topBar.setPadding(new Insets(15));
        topBar.getChildren().add(closeButton);

        VBox contentBox = new VBox(30);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setMaxWidth(500);
        contentBox.setTranslateY(-100);

        Image logo = new Image(getClass().getResourceAsStream("/images/logo.png"));
        ImageView logoView = new ImageView(logo);
        logoView.setFitHeight(120);
        logoView.setPreserveRatio(true);
        logoView.setEffect(new Glow(0.8));

        ScaleTransition st = new ScaleTransition(Duration.seconds(2), logoView);
        st.setFromX(0.8);
        st.setFromY(0.8);
        st.setToX(1.0);
        st.setToY(1.0);
        st.setCycleCount(ScaleTransition.INDEFINITE);
        st.setAutoReverse(true);
        st.play();

        Label titleLabel = new Label("E-commerce Dashboard");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 32));
        titleLabel.setStyle("-fx-text-fill: white;");
        titleLabel.setEffect(new DropShadow(20, Color.BLACK));

        VBox formBox = new VBox(20);
        formBox.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.95);" +
                        "-fx-background-radius: 20;" +
                        "-fx-padding: 40;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 20, 0, 0, 0);"
        );
        formBox.setMaxWidth(400);

        Label welcomeLabel = new Label("Benvenuto");
        welcomeLabel.setFont(Font.font("System", FontWeight.BOLD, 28));
        welcomeLabel.setStyle("-fx-text-fill: #2c3e50;");

        Label usernameLabel = new Label("Username");
        usernameLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        usernameLabel.setStyle("-fx-text-fill: #2c3e50;");
        TextField usernameField = new TextField();
        usernameField.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #00b4db;" +
                        "-fx-border-radius: 10;" +
                        "-fx-padding: 12;" +
                        "-fx-font-size: 14px;"
        );
        usernameField.setPromptText("Inserisci il tuo username");

        Label passwordLabel = new Label("Password");
        passwordLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        passwordLabel.setStyle("-fx-text-fill: #2c3e50;");
        PasswordField passwordField = new PasswordField();
        passwordField.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #00b4db;" +
                        "-fx-border-radius: 10;" +
                        "-fx-padding: 12;" +
                        "-fx-font-size: 14px;"
        );
        passwordField.setPromptText("Inserisci la tua password");

        CheckBox showPassword = new CheckBox("Mostra password");
        showPassword.setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 14px;");
        TextField passwordVisible = new TextField();
        passwordVisible.setStyle(passwordField.getStyle());
        passwordVisible.setVisible(false);
        passwordVisible.setManaged(false);

        showPassword.setOnAction(e -> {
            if (showPassword.isSelected()) {
                passwordVisible.setText(passwordField.getText());
                passwordVisible.setVisible(true);
                passwordVisible.setManaged(true);
                passwordField.setVisible(false);
                passwordField.setManaged(false);
            } else {
                passwordField.setText(passwordVisible.getText());
                passwordField.setVisible(true);
                passwordField.setManaged(true);
                passwordVisible.setVisible(false);
                passwordVisible.setManaged(false);
            }
        });

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 14px;");
        errorLabel.setWrapText(true);

        Button loginButton = new Button("Accedi");
        loginButton.setStyle(
                "-fx-background-color: linear-gradient(to right, #00b4db, #0083b0);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 15 30;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);"
        );

        loginButton.setOnMouseEntered(e -> loginButton.setStyle(
                "-fx-background-color: linear-gradient(to right, #0083b0, #00b4db);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 15 30;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 8, 0, 0, 3);"
        ));

        loginButton.setOnMouseExited(e -> loginButton.setStyle(
                "-fx-background-color: linear-gradient(to right, #00b4db, #0083b0);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 15 30;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);"
        ));

        ProgressIndicator loadingIndicator = new ProgressIndicator();
        loadingIndicator.setVisible(false);
        loadingIndicator.setMaxSize(20, 20);

        Runnable loginAction = () -> {
            String username = usernameField.getText();
            String password = showPassword.isSelected() ? passwordVisible.getText() : passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Inserisci username e password");
                return;
            }

            loginButton.setDisable(true);
            loadingIndicator.setVisible(true);
            errorLabel.setText("");

            try {
                String response = authenticateUser(username, password);

                if (response.contains("Login effettuato con successo")) {
                    FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), loginBox);
                    fadeOut.setFromValue(1.0);
                    fadeOut.setToValue(0.0);
                    fadeOut.setOnFinished(e -> {
                        listaProdotti = server.getProdotti();
                        showMainScreen();
                    });
                    fadeOut.play();
                } else {
                    errorLabel.setText("Credenziali non valide");
                    loginButton.setDisable(false);
                    loadingIndicator.setVisible(false);
                }
            } catch (Exception ex) {
                errorLabel.setText("Errore di connessione al server");
                loginButton.setDisable(false);
                loadingIndicator.setVisible(false);
            }
        };

        loginButton.setOnAction(e -> loginAction.run());
        passwordField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                loginAction.run();
            }
        });
        passwordVisible.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                loginAction.run();
            }
        });

        HBox buttonBox = new HBox(10, loginButton, loadingIndicator);
        buttonBox.setAlignment(Pos.CENTER);

        formBox.getChildren().addAll(
                welcomeLabel,
                usernameLabel,
                usernameField,
                passwordLabel,
                passwordField,
                passwordVisible,
                showPassword,
                errorLabel,
                buttonBox
        );

        contentBox.getChildren().addAll(logoView, titleLabel, formBox);
        loginBox.getChildren().addAll(topBar, contentBox);

        Scene loginScene = new Scene(loginBox);
        loginScene.setFill(Color.TRANSPARENT);
        primaryStage.setTitle("Login - E-commerce Dashboard");
        primaryStage.setScene(loginScene);

        primaryStage.setMaximized(true);
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.show();
    }

    private String authenticateUser(String username, String password) {
        try {
            URL url = new URL("https://lucacassina.altervista.org/ecommerce/login.php");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            String postData = "username=" + URLEncoder.encode(username, "UTF-8") +
                    "&password=" + URLEncoder.encode(password, "UTF-8");

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = postData.getBytes("UTF-8");
                os.write(input, 0, input.length);
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                return response.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Errore di connessione";
        }
    }

    private void showMainScreen() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #0f2027, #203a43, #2c5364);");

        HBox topMenu = createTopMenu();
        root.setTop(topMenu);

        VBox dashboard = createDashboard();
        root.setCenter(dashboard);

        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setTitle("E-commerce Dashboard");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.setOnCloseRequest(event -> event.consume());
        primaryStage.setFullScreen(true);
    }

    private HBox createTopMenu() {
        HBox menuBar = new HBox(20);
        menuBar.setPadding(new Insets(15));
        menuBar.setStyle("-fx-background-color: rgba(0, 0, 0, 0.2); -fx-background-radius: 0 0 20 20;");
        menuBar.setAlignment(Pos.CENTER);

        HBox logoContainer = new HBox(10);
        logoContainer.setAlignment(Pos.CENTER_LEFT);
        logoContainer.setStyle("-fx-cursor: hand;");

        Image logo = new Image(getClass().getResourceAsStream("/images/logo.png"));
        ImageView logoView = new ImageView(logo);
        logoView.setFitHeight(40);
        logoView.setPreserveRatio(true);
        logoView.setSmooth(true);
        logoView.setEffect(new javafx.scene.effect.Glow(0.5));

        Label titleLabel = new Label("E-commerce Dashboard");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 0);");

        logoContainer.getChildren().addAll(logoView, titleLabel);

        logoContainer.setOnMouseClicked(e -> root.setCenter(createDashboard()));
        logoView.setOnMouseClicked(e -> root.setCenter(createDashboard()));
        titleLabel.setOnMouseClicked(e -> root.setCenter(createDashboard()));

        Button Inserisci = createMenuButton("Inserisci Prodotto");
        Button Aggiorna = createMenuButton("Aggiorna Prodotto");
        Button Ricerca = createMenuButton("Cerca Prodotto");
        Button Visualizza = createMenuButton("Visualizza Tutti");
        Button Elimina = createMenuButton("Elimina Prodotto");
        Button Utenti = createMenuButton("Visualizza Utenti");
        Button Esci = createMenuButton("Esci");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Inserisci.setOnAction(e -> {
            root.setCenter(schermatainserisci.schermataInserisci(this.scene));
        });

        Aggiorna.setOnAction(e -> {
            root.setCenter(schermataAggiorna.schermataAggiorna());
        });
        Ricerca.setOnAction(e -> {
            root.setCenter(schermataRicerca.schermataRicerca());
        });
        Visualizza.setOnAction(e-> {
            root.setCenter(schermataVisualizza.schermataVisualizza());
        });
        Elimina.setOnAction(e -> {
            root.setCenter(schermataElimina.schermataElimina(this.scene));
        });
        Utenti.setOnAction(e -> {
            root.setCenter(schermataUtenti.schermataUtenti());
        });

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

        menuBar.getChildren().addAll(logoContainer, Inserisci, Aggiorna, Ricerca, Visualizza, Elimina, Utenti, spacer, Esci);
        return menuBar;
    }

    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 8 15;" +
                        "-fx-cursor: hand;"
        );

        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.1);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 8 15;" +
                        "-fx-cursor: hand;" +
                        "-fx-background-radius: 5;"
        ));

        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 8 15;" +
                        "-fx-cursor: hand;"
        ));

        return button;
    }

    private VBox createDashboard() {
        VBox dashboard = new VBox(20);
        dashboard.setPadding(new Insets(30));
        dashboard.setAlignment(Pos.CENTER);

        Label welcomeLabel = new Label("Benvenuto nel Sistema di Gestione E-commerce");
        welcomeLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);");

        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(20);
        statsGrid.setVgap(20);
        statsGrid.setAlignment(Pos.CENTER);

        statsGrid.add(createStatCard("Prodotti Totali", String.valueOf(listaProdotti.size()), "#00b4db"), 0, 0);
        statsGrid.add(createStatCard("Funzionalità Attive", "6", "#0083b0"), 1, 0);
        statsGrid.add(createStatCard("Taglie Disponibili", "5", "#00b4db"), 2, 0);

        VBox quickActions = new VBox(15);
        quickActions.setAlignment(Pos.CENTER);
        Label quickActionsLabel = new Label("Azioni Rapide");
        quickActionsLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        HBox actionButtons = new HBox(20);
        actionButtons.setAlignment(Pos.CENTER);

        Button quickInsert = createDashboardButton("Inserisci Nuovo Prodotto");
        Button quickView = createDashboardButton("Visualizza Catalogo");
        Button quickUsers = createDashboardButton("Visualizza Utenti");


        quickInsert.setOnAction(e -> root.setCenter(schermatainserisci.schermataInserisci(this.scene)));
        quickView.setOnAction(e -> root.setCenter(schermataVisualizza.schermataVisualizza()));
        quickUsers.setOnAction(e -> root.setCenter(schermataUtenti.schermataUtenti()));

        actionButtons.getChildren().addAll(quickInsert, quickView, quickUsers);
        quickActions.getChildren().addAll(quickActionsLabel, actionButtons);

        dashboard.getChildren().addAll(welcomeLabel, statsGrid, quickActions);
        return dashboard;
    }

    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-background-radius: 15;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);"
        );
        card.setPrefWidth(200);
        card.setPrefHeight(150);
        card.setAlignment(Pos.CENTER);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: white;");

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    private Button createDashboardButton(String text) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.1);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 15 30;" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;"
        );

        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.2);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 15 30;" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;"
        ));

        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.1);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 15 30;" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;"
        ));

        return button;
    }

    public static void main(String[] args) {
        launch();
    }
}

