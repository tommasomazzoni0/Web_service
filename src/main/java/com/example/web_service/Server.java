package com.example.web_service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Server {

    public static String mostraTuttiProdotti() {
        String urlString = "https://lucacassina.altervista.org/ecommerce/mostraTutti.php";
        StringBuilder response = new StringBuilder();

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return response.toString();
    }

    public static String inserisciProdotto(String nome, String descrizione, String prezzo, String taglie) {
        try {
            URL url = new URL("https://lucacassina.altervista.org/ecommerce/aggiungiProdotto.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Imposta il metodo di richiesta (POST)
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);  // Abilita l'output per inviare dati
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Prepara i dati da inviare (come stringa codificata URL)
            String postData = "nome=" + URLEncoder.encode(nome, StandardCharsets.UTF_8) +
                    "&descrizione=" + URLEncoder.encode(descrizione, StandardCharsets.UTF_8) +
                    "&prezzo=" + URLEncoder.encode(prezzo, StandardCharsets.UTF_8) +
                    "&taglie=" + URLEncoder.encode(taglie, StandardCharsets.UTF_8);

            // Invia i dati al server
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = postData.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Ottieni la risposta dal server
            int statusCode = conn.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                // Leggi la risposta
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    return response.toString();  // Risposta del PHP
                }
            } else {
                return "Errore nella richiesta: " + statusCode;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Errore: " + e.getMessage();
        }


    }
}