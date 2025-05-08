package com.example.web_service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

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
    public static String inserisciProdotto(String nome, String descrizione, String prezzo, String taglie, String nomeImmagine) {
        try {
            URL url = new URL("https://lucacassina.altervista.org/ecommerce/aggiungiProdotto.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String postData = "nome=" + URLEncoder.encode(nome, StandardCharsets.UTF_8) +
                    "&descrizione=" + URLEncoder.encode(descrizione, StandardCharsets.UTF_8) +
                    "&prezzo=" + URLEncoder.encode(prezzo, StandardCharsets.UTF_8) +
                    "&taglie=" + URLEncoder.encode(taglie, StandardCharsets.UTF_8) +
                    "&immagine=" + URLEncoder.encode(nomeImmagine, StandardCharsets.UTF_8);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = postData.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int statusCode = conn.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    return response.toString();
                }
            } else {
                return "Errore nella richiesta: " + statusCode;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Errore: " + e.getMessage();
        }
    }


    public static String eliminaProdotto(int id) {
        String urlString = "https://lucacassina.altervista.org/ecommerce/eliminaProdotto.php";
        StringBuilder response = new StringBuilder();

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            String urlParameters = "id=" + id;

            OutputStream os = conn.getOutputStream();
            os.write(urlParameters.getBytes());
            os.flush();
            os.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return "Errore di connessione.";
        }

        return response.toString();
    }

    public static boolean aggiornaProdotto(String id, String nome, String descrizione, float prezzo, String taglie) {
        String urlString = "https://lucacassina.altervista.org/ecommerce/aggiornaProdotto.php";
        String charset = "UTF-8";

        try {
            String dati = String.format("id_prodotto=%s&nome=%s&descrizione=%s&prezzo=%s&taglie=%s",
                    URLEncoder.encode(id, charset),
                    URLEncoder.encode(nome, charset),
                    URLEncoder.encode(descrizione, charset),
                    URLEncoder.encode(String.valueOf(prezzo), charset),
                    URLEncoder.encode(taglie, charset)
            );

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=" + charset);
            conn.setRequestProperty("Content-Length", String.valueOf(dati.length()));

            try (OutputStream os = conn.getOutputStream()) {
                os.write(dati.getBytes(charset));
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));
            StringBuilder risposta = new StringBuilder();
            String riga;
            while ((riga = reader.readLine()) != null) {
                risposta.append(riga);
            }
            reader.close();


            return risposta.toString().contains("successo");

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static ArrayList<Prodotto> getProdotti() {
        ArrayList<Prodotto> prodotti = new ArrayList<>();
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

        String risposta = response.toString();

        String[] prodottiArray = risposta.split("\\|");

        for (String prodottoStr : prodottiArray) {
            prodottoStr = prodottoStr.trim();

            String[] parti = prodottoStr.split("_", 5);

            if (parti.length == 5) {
                try {
                    String id = parti[0].trim();
                    String nome = parti[1].trim();
                    String descrizione = parti[2].trim();
                    float prezzo = Float.parseFloat(parti[3].trim());
                    String taglie = parti[4].trim();

                    Prodotto prodotto = new Prodotto(id, nome, descrizione, prezzo, "sì", taglie);
                    prodotti.add(prodotto);

                } catch (NumberFormatException e) {
                    System.out.println("Errore nel parsing del prodotto: " + prodottoStr);
                }
            } else {
                System.out.println("Formato non valido per il prodotto: " + prodottoStr);
            }
        }

        return prodotti;
    }


}