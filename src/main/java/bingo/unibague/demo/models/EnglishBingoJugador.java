package bingo.unibague.demo.models;

import java.util.ArrayList;
import java.util.List;

public class EnglishBingoJugador {

    private String id;
    private String username;
    private List<EnglishBingoCarton> cartones;
    private boolean listo;

    public EnglishBingoJugador() {
        this.cartones = new ArrayList<>();
        this.listo = true;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public List<EnglishBingoCarton> getCartones() { return cartones; }
    public void setCartones(List<EnglishBingoCarton> cartones) { this.cartones = cartones; }

    public boolean isListo() { return listo; }
    public void setListo(boolean listo) { this.listo = listo; }
}
