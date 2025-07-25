package bingo.unibague.demo.payload.response;

import java.time.LocalDateTime;
import java.util.List;

public class JuegoResponse {
    private Long id;
    private String nombre;
    private String tipoJuego;
    private String estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Integer tiempoEntreNumeros;
    private Integer numeroActual;
    private Integer puntajeTotal;
    private List<Integer> numerosCantados;
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getTipoJuego() { return tipoJuego; }
    public void setTipoJuego(String tipoJuego) { this.tipoJuego = tipoJuego; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }
    
    public LocalDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDateTime fechaFin) { this.fechaFin = fechaFin; }
    
    public Integer getTiempoEntreNumeros() { return tiempoEntreNumeros; }
    public void setTiempoEntreNumeros(Integer tiempoEntreNumeros) { this.tiempoEntreNumeros = tiempoEntreNumeros; }
    
    public Integer getNumeroActual() { return numeroActual; }
    public void setNumeroActual(Integer numeroActual) { this.numeroActual = numeroActual; }
    
    public Integer getPuntajeTotal() { return puntajeTotal; }
    public void setPuntajeTotal(Integer puntajeTotal) { this.puntajeTotal = puntajeTotal; }
    
    public List<Integer> getNumerosCantados() { return numerosCantados; }
    public void setNumerosCantados(List<Integer> numerosCantados) { this.numerosCantados = numerosCantados; }
}
