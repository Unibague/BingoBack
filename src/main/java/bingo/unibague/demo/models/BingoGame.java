package bingo.unibague.demo.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "bingo_games")
public class BingoGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "user_email")
    private String userEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_juego")
    private TipoJuego tipoJuego = TipoJuego.CLASICO;

    @Enumerated(EnumType.STRING)
    private EstadoJuego estado = EstadoJuego.ESPERANDO;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @Column(name = "tiempo_entre_numeros")
    private Integer tiempoEntreNumeros = 5;

    @ElementCollection
    @Column(name = "numeros_cantados")
    private List<Integer> numerosCantados = new ArrayList<>();

    @Column(name = "numero_actual")
    private Integer numeroActual;

    @Column(name = "puntaje_total")
    private Integer puntajeTotal = 0;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Player> players = new ArrayList<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BingoCard> cartones = new ArrayList<>();

    // Enums
    public enum TipoJuego {
        CLASICO, RAPIDO, PERSONALIZADO
    }

    public enum EstadoJuego {
        ESPERANDO, ACTIVO, PAUSADO, FINALIZADO
    }

    // Constructor
    public BingoGame() {
        this.fechaCreacion = LocalDateTime.now();
        this.estado = EstadoJuego.ESPERANDO;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public TipoJuego getTipoJuego() { return tipoJuego; }
    public void setTipoJuego(TipoJuego tipoJuego) { this.tipoJuego = tipoJuego; }

    public EstadoJuego getEstado() { return estado; }
    public void setEstado(EstadoJuego estado) { this.estado = estado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDateTime fechaFin) { this.fechaFin = fechaFin; }

    public Integer getTiempoEntreNumeros() { return tiempoEntreNumeros; }
    public void setTiempoEntreNumeros(Integer tiempoEntreNumeros) { this.tiempoEntreNumeros = tiempoEntreNumeros; }

    public List<Integer> getNumerosCantados() { return numerosCantados; }
    public void setNumerosCantados(List<Integer> numerosCantados) { this.numerosCantados = numerosCantados; }

    public Integer getNumeroActual() { return numeroActual; }
    public void setNumeroActual(Integer numeroActual) { this.numeroActual = numeroActual; }

    public Integer getPuntajeTotal() { return puntajeTotal; }
    public void setPuntajeTotal(Integer puntajeTotal) { this.puntajeTotal = puntajeTotal; }

    public List<Player> getPlayers() { return players; }
    public void setPlayers(List<Player> players) { this.players = players; }

    public List<BingoCard> getCartones() { return cartones; }
    public void setCartones(List<BingoCard> cartones) { this.cartones = cartones; }

    // Métodos de negocio
    public void addCalledNumber(int number) {
        if (!numerosCantados.contains(number)) {
            numerosCantados.add(number);
            numeroActual = number;
        }
    }

    public void addPlayer(Player player) {
        players.add(player);
        player.setGame(this);
    }

    public void addCarton(BingoCard carton) {
        cartones.add(carton);
        carton.setGame(this);
    }

    public boolean isActive() {
        return EstadoJuego.ACTIVO.equals(estado);
    }

    public boolean isFinished() {
        return EstadoJuego.FINALIZADO.equals(estado);
    }

    // Métodos de compatibilidad con tu código existente
    public String getStatus() {
        return estado.name();
    }

    public void setStatus(String status) {
        try {
            this.estado = EstadoJuego.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            this.estado = EstadoJuego.ESPERANDO;
        }
    }

    public List<Integer> getCalledNumbers() {
        return numerosCantados;
    }

    public void setCalledNumbers(List<Integer> calledNumbers) {
        this.numerosCantados = calledNumbers;
    }
}
