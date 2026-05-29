package bingo.unibague.demo.models;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class EnglishBingoSala {

    public enum Estado { ESPERANDO, EN_JUEGO, FINALIZADA }

    private String id;
    private String nombre;
    private String adminId;
    private String adminUsername;
    private Estado estado;
    private List<EnglishBingoJugador> jugadores;
    private String palabraActual;
    private List<String> palabrasLlamadas;
    private int moduloSeleccionado;
    private int cartonesPerJugador;
    private String ganador;
    private String createdAt;

    public EnglishBingoSala() {
        this.jugadores = new ArrayList<>();
        this.palabrasLlamadas = new ArrayList<>();
        this.estado = Estado.ESPERANDO;
        this.createdAt = Instant.now().toString();
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getAdminId() { return adminId; }
    public void setAdminId(String adminId) { this.adminId = adminId; }

    public String getAdminUsername() { return adminUsername; }
    public void setAdminUsername(String adminUsername) { this.adminUsername = adminUsername; }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }

    public List<EnglishBingoJugador> getJugadores() { return jugadores; }
    public void setJugadores(List<EnglishBingoJugador> jugadores) { this.jugadores = jugadores; }

    public String getPalabraActual() { return palabraActual; }
    public void setPalabraActual(String palabraActual) { this.palabraActual = palabraActual; }

    public List<String> getPalabrasLlamadas() { return palabrasLlamadas; }
    public void setPalabrasLlamadas(List<String> palabrasLlamadas) { this.palabrasLlamadas = palabrasLlamadas; }

    public int getModuloSeleccionado() { return moduloSeleccionado; }
    public void setModuloSeleccionado(int moduloSeleccionado) { this.moduloSeleccionado = moduloSeleccionado; }

    public int getCartonesPerJugador() { return cartonesPerJugador; }
    public void setCartonesPerJugador(int cartonesPerJugador) { this.cartonesPerJugador = cartonesPerJugador; }

    public String getGanador() { return ganador; }
    public void setGanador(String ganador) { this.ganador = ganador; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
