package bingo.unibague.demo.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "puntajes")
public class Puntaje {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_email", nullable = false)
    private String userEmail;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "juego_id")
    private BingoGame juego;
    
    @Column(name = "puntaje")
    private Integer puntaje;
    
    @Column(name = "es_ganador")
    private Boolean esGanador = false;
    
    @Column(name = "tiempo_juego") // en segundos
    private Integer tiempoJuego;
    
    @Column(name = "numeros_cantados")
    private Integer numerosCantados;
    
    @Column(name = "fecha_juego")
    private LocalDateTime fechaJuego;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_victoria")
    private TipoVictoria tipoVictoria;
    
    public enum TipoVictoria {
        LINEA, BINGO_COMPLETO, CUATRO_ESQUINAS, DIAGONAL
    }
    
    // Constructores
    public Puntaje() {
        this.fechaJuego = LocalDateTime.now();
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    
    public BingoGame getJuego() { return juego; }
    public void setJuego(BingoGame juego) { this.juego = juego; }
    
    public Integer getPuntaje() { return puntaje; }
    public void setPuntaje(Integer puntaje) { this.puntaje = puntaje; }
    
    public Boolean getEsGanador() { return esGanador; }
    public void setEsGanador(Boolean esGanador) { this.esGanador = esGanador; }
    
    public Integer getTiempoJuego() { return tiempoJuego; }
    public void setTiempoJuego(Integer tiempoJuego) { this.tiempoJuego = tiempoJuego; }
    
    public Integer getNumerosCantados() { return numerosCantados; }
    public void setNumerosCantados(Integer numerosCantados) { this.numerosCantados = numerosCantados; }
    
    public LocalDateTime getFechaJuego() { return fechaJuego; }
    public void setFechaJuego(LocalDateTime fechaJuego) { this.fechaJuego = fechaJuego; }
    
    public TipoVictoria getTipoVictoria() { return tipoVictoria; }
    public void setTipoVictoria(TipoVictoria tipoVictoria) { this.tipoVictoria = tipoVictoria; }
}
