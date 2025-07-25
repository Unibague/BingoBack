package bingo.unibague.demo.payload.response;

public class EstadisticasResponse {
    private Integer juegosJugados;
    private Integer juegosGanados;
    private Integer puntosTotales;
    private Integer rachaActual;
    private Double porcentajeVictorias;
    
    // Getters y Setters
    public Integer getJuegosJugados() { return juegosJugados; }
    public void setJuegosJugados(Integer juegosJugados) { this.juegosJugados = juegosJugados; }
    
    public Integer getJuegosGanados() { return juegosGanados; }
    public void setJuegosGanados(Integer juegosGanados) { this.juegosGanados = juegosGanados; }
    
    public Integer getPuntosTotales() { return puntosTotales; }
    public void setPuntosTotales(Integer puntosTotales) { this.puntosTotales = puntosTotales; }
    
    public Integer getRachaActual() { return rachaActual; }
    public void setRachaActual(Integer rachaActual) { this.rachaActual = rachaActual; }
    
    public Double getPorcentajeVictorias() { return porcentajeVictorias; }
    public void setPorcentajeVictorias(Double porcentajeVictorias) { this.porcentajeVictorias = porcentajeVictorias; }
}
