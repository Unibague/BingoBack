package bingo.unibague.demo.payload.request;

public class CrearJuegoRequest {
    private String nombre;
    private String tipoJuego; // CLASICO, RAPIDO, PERSONALIZADO
    private ConfiguracionJuego configuracion;
    
    public static class ConfiguracionJuego {
        private Integer tiempoEntreNumeros = 5;
        
        // Getters y Setters
        public Integer getTiempoEntreNumeros() { return tiempoEntreNumeros; }
        public void setTiempoEntreNumeros(Integer tiempoEntreNumeros) { this.tiempoEntreNumeros = tiempoEntreNumeros; }
    }
    
    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getTipoJuego() { return tipoJuego; }
    public void setTipoJuego(String tipoJuego) { this.tipoJuego = tipoJuego; }
    
    public ConfiguracionJuego getConfiguracion() { return configuracion; }
    public void setConfiguracion(ConfiguracionJuego configuracion) { this.configuracion = configuracion; }
}
