package bingo.unibague.demo.payload.response;

import java.time.LocalDateTime;
import java.util.List;

public class HistorialPartidaResponse {
    private Long id;
    private String nombre;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFinalizacion;
    private Long tiempoTotal; // en segundos
    private String estado;
    private List<HistorialCartonResponse> cartones;
    private int cartonesCompletados;
    private int numerosCantados;
    private int puntajeTotal;

    // Constructor vacío
    public HistorialPartidaResponse() {}

    // Constructor completo
    public HistorialPartidaResponse(Long id, String nombre, LocalDateTime fechaInicio, 
                                  LocalDateTime fechaFinalizacion, String estado, 
                                  List<HistorialCartonResponse> cartones, int numerosCantados, 
                                  int puntajeTotal) {
        this.id = id;
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.fechaFinalizacion = fechaFinalizacion;
        this.estado = estado;
        this.cartones = cartones;
        this.numerosCantados = numerosCantados;
        this.puntajeTotal = puntajeTotal;
        this.cartonesCompletados = (int) cartones.stream().filter(HistorialCartonResponse::isEsGanador).count();
        
        // Calcular tiempo total
        if (fechaInicio != null && fechaFinalizacion != null) {
            this.tiempoTotal = java.time.Duration.between(fechaInicio, fechaFinalizacion).getSeconds();
        } else {
            this.tiempoTotal = 0L;
        }
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDateTime getFechaFinalizacion() { return fechaFinalizacion; }
    public void setFechaFinalizacion(LocalDateTime fechaFinalizacion) { this.fechaFinalizacion = fechaFinalizacion; }

    public Long getTiempoTotal() { return tiempoTotal; }
    public void setTiempoTotal(Long tiempoTotal) { this.tiempoTotal = tiempoTotal; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public List<HistorialCartonResponse> getCartones() { return cartones; }
    public void setCartones(List<HistorialCartonResponse> cartones) { this.cartones = cartones; }

    public int getCartonesCompletados() { return cartonesCompletados; }
    public void setCartonesCompletados(int cartonesCompletados) { this.cartonesCompletados = cartonesCompletados; }

    public int getNumerosCantados() { return numerosCantados; }
    public void setNumerosCantados(int numerosCantados) { this.numerosCantados = numerosCantados; }

    public int getPuntajeTotal() { return puntajeTotal; }
    public void setPuntajeTotal(int puntajeTotal) { this.puntajeTotal = puntajeTotal; }
}