package bingo.unibague.demo.models;

import java.time.LocalDateTime;

import bingo.unibague.demo.models.autentication.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "cartones")
public class Carton {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "numero_carton", unique = true, nullable = false)
    private String numeroCarton;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCarton estado = EstadoCarton.INACTIVO;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_activacion")
    private LocalDateTime fechaActivacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // Enum para el estado del cartón
    public enum EstadoCarton {
        ACTIVO,
        INACTIVO,
        USADO,
        BLOQUEADO
    }

    // Constructores
    public Carton() {
    }

    public Carton(User user, String numeroCarton) {
        this.user = user;
        this.numeroCarton = numeroCarton;
        this.estado = EstadoCarton.INACTIVO;
    }

    public Carton(User user, String numeroCarton, EstadoCarton estado) {
        this.user = user;
        this.numeroCarton = numeroCarton;
        this.estado = estado;
    }

    // Métodos de ciclo de vida
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
        if (estado == EstadoCarton.ACTIVO && fechaActivacion == null) {
            fechaActivacion = LocalDateTime.now();
        }
    }

    // Métodos de utilidad
    public boolean isActivo() {
        return estado == EstadoCarton.ACTIVO;
    }

    public boolean isInactivo() {
        return estado == EstadoCarton.INACTIVO;
    }

    public void activar() {
        this.estado = EstadoCarton.ACTIVO;
        if (this.fechaActivacion == null) {
            this.fechaActivacion = LocalDateTime.now();
        }
    }

    public void desactivar() {
        this.estado = EstadoCarton.INACTIVO;
    }

    public void marcarComoUsado() {
        this.estado = EstadoCarton.USADO;
    }

    public void bloquear() {
        this.estado = EstadoCarton.BLOQUEADO;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getNumeroCarton() {
        return numeroCarton;
    }

    public void setNumeroCarton(String numeroCarton) {
        this.numeroCarton = numeroCarton;
    }

    public EstadoCarton getEstado() {
        return estado;
    }

    public void setEstado(EstadoCarton estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActivacion() {
        return fechaActivacion;
    }

    public void setFechaActivacion(LocalDateTime fechaActivacion) {
        this.fechaActivacion = fechaActivacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    @Override
    public String toString() {
        return "Carton{" +
                "id=" + id +
                ", numeroCarton='" + numeroCarton + '\'' +
                ", estado=" + estado +
                ", user=" + (user != null ? user.getUsername() : "null") +
                '}';
    }
}