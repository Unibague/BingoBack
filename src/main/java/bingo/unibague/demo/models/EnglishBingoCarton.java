package bingo.unibague.demo.models;

import java.util.List;

public class EnglishBingoCarton {

    private String id;
    private List<List<String>> palabras;
    private List<List<Boolean>> marcadas;
    private boolean esBingo;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public List<List<String>> getPalabras() { return palabras; }
    public void setPalabras(List<List<String>> palabras) { this.palabras = palabras; }

    public List<List<Boolean>> getMarcadas() { return marcadas; }
    public void setMarcadas(List<List<Boolean>> marcadas) { this.marcadas = marcadas; }

    public boolean isEsBingo() { return esBingo; }
    public void setEsBingo(boolean esBingo) { this.esBingo = esBingo; }
}
