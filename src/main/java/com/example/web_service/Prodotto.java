package com.example.web_service;
public class Prodotto {
    private final String id;
    private final String nome;
    private final String descrizione;
    private final float prezzo;
    private final String foto;
    private final String taglie;

    public Prodotto(String id, String nome, String descrizione, float prezzo, String foto, String taglie) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.prezzo = prezzo;
        this.foto = foto;
        this.taglie = taglie;
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getDescrizione() { return descrizione; }
    public Float getPrezzo() { return prezzo; }
    public String getFoto() { return foto; }
    public String getTaglie() { return taglie; }
}
