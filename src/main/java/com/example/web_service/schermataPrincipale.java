package com.example.web_service;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.util.Pair;
import javafx.util.converter.IntegerStringConverter;
import javafx.animation.ScaleTransition;
import javafx.animation.FadeTransition;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.shape.Circle;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.effect.Glow;
import javafx.scene.effect.DropShadow;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Hyperlink;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.net.URLEncoder;


public class schermataPrincipale extends Application {

    private BorderPane root;
    Server server= new Server();
    ArrayList<Prodotto> listaProdotti;


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

        Inserisci.setOnAction(e -> root.setCenter(schermataInserisci()));
        Aggiorna.setOnAction(e -> root.setCenter(schermataAggiorna()));
        Ricerca.setOnAction(e -> root.setCenter(schermataRicerca()));
        Visualizza.setOnAction(e -> root.setCenter(schermataVisualizza()));
        Elimina.setOnAction(e -> root.setCenter(schermataElimina()));
        Utenti.setOnAction(e -> root.setCenter(schermataUtenti()));

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
        statsGrid.add(createStatCard("Funzionalità Attive", "8", "#0083b0"), 1, 0);
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

        quickInsert.setOnAction(e -> root.setCenter(schermataInserisci()));
        quickView.setOnAction(e -> root.setCenter(schermataVisualizza()));
        quickUsers.setOnAction(e -> root.setCenter(schermataUtenti()));

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

    private Pane schermataInserisci() {
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


    private Pane schermataAggiorna() {
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
                taglieDisponibili.setAll("XS", "S", "M", "L", "XL", "XXL");
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

    private Pane schermataRicerca() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.95); -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);");

        Label titleLabel = new Label("Ricerca Prodotti");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: #2c3e50;");

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
            // Aggiorna la lista dei prodotti dal server
            listaProdotti = server.getProdotti();
            
            productCardsContainer.getChildren().clear();
            String searchText = searchField.getText().trim().toLowerCase();

            for (Prodotto prodotto : listaProdotti) {
                if (searchText.isEmpty() ||
                        prodotto.getNome().toLowerCase().contains(searchText) ||
                        prodotto.getId().toLowerCase().contains(searchText)) {
                    productCardsContainer.getChildren().add(createProductCard(prodotto));
                }
            }
        };

        // Carica i prodotti all'avvio
        loadProducts.run();

        searchButton.setOnAction(e -> loadProducts.run());
        searchField.setOnAction(e -> loadProducts.run());

        vbox.getChildren().addAll(titleLabel, searchBox, scrollPane);
        return vbox;
    }

    private Pane schermataVisualizza() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.95); -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);");

        Label titleLabel = new Label("Catalogo Prodotti");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: #2c3e50;");

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

        // Aggiorna la lista dei prodotti dal server
        listaProdotti = server.getProdotti();
        
        for (Prodotto prodotto : listaProdotti) {
            productCardsContainer.getChildren().add(createProductCard(prodotto));
        }

        vbox.getChildren().addAll(titleLabel, scrollPane);
        return vbox;
    }

    private Pane schermataElimina() {
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

    private Pane schermataUtenti() {
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
            String imageUrl = "https://lucacassina.altervista.org/ecommerce/sito/assets/img/" +
                    prodotto.getNome().toLowerCase().replaceAll("\\s+", "") + ".png";

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
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Errore");
                alert.setHeaderText("Impossibile aprire il browser");
                alert.setContentText("URL: " + imageUrl + "\nErrore: " + ex.getMessage());
                alert.showAndWait();
            }
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

    public static void main(String[] args) {
        launch();
    }
}

