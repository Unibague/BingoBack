package bingo.unibague.demo.payload.response;

import java.time.LocalDateTime;
import java.util.List;

public class HistorialCartonResponse {
    private Long cartonId;
    private Integer numeroCarton;
    private boolean esGanador;
    private LocalDateTime fechaCreacion;
    private List<Integer> numeros;
    private List<Integer> numerosMarcados;
    private int numerosMarcadosCount;

    // Constructor vacío
    public HistorialCartonResponse() {}

    // Constructor completo
    public HistorialCartonResponse(Long cartonId, Integer numeroCarton, boolean esGanador, 
                                 LocalDateTime fechaCreacion, List<Integer> numeros, 
                                 List<Integer> numerosMarcados) {
        this.cartonId = cartonId;
        this.numeroCarton = numeroCarton;
        this.esGanador = esGanador;
        this.fechaCreacion = fechaCreacion;
        this.numeros = numeros;
        this.numerosMarcados = numerosMarcados;
        this.numerosMarcadosCount = numerosMarcados != null ? numerosMarcados.size() : 0;
    }

    // Getters y Setters
    public Long getCartonId() { return cartonId; }
    public void setCartonId(Long cartonId) { this.cartonId = cartonId; }

    public Integer getNumeroCarton() { return numeroCarton; }
    public void setNumeroCarton(Integer numeroCarton) { this.numeroCarton = numeroCarton; }

    public boolean isEsGanador() { return esGanador; }
    public void setEsGanador(boolean esGanador) { this.esGanador = esGanador; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public List<Integer> getNumeros() { return numeros; }
    public void setNumeros(List<Integer> numeros) { this.numeros = numeros; }

    public List<Integer> getNumerosMarcados() { return numerosMarcados; }
    public void setNumerosMarcados(List<Integer> numerosMarcados) { 
        this.numerosMarcados = numerosMarcados;
        this.numerosMarcadosCount = numerosMarcados != null ? numerosMarcados.size() : 0;
    }

    public int getNumerosMarcadosCount() { return numerosMarcadosCount; }
    public void setNumerosMarcadosCount(int numerosMarcadosCount) { this.numerosMarcadosCount = numerosMarcadosCount; }
}