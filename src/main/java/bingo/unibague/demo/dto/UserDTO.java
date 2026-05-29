package bingo.unibague.demo.dto;

import java.util.List;

public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String existEmail;
    private List<String> roles;
    private Integer maxCartones;
    private Integer cartonesActivos;

    // Constructores
    public UserDTO() {
    }

    public UserDTO(Long id, String username, String email, String existEmail, List<String> roles, Integer maxCartones) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.existEmail = existEmail;
        this.roles = roles;
        this.maxCartones = maxCartones;
        this.cartonesActivos = 0; // Por defecto 0
    }

    public UserDTO(Long id, String username, String email, String existEmail, List<String> roles, Integer maxCartones, Integer cartonesActivos) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.existEmail = existEmail;
        this.roles = roles;
        this.maxCartones = maxCartones;
        this.cartonesActivos = cartonesActivos;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getExistEmail() {
        return existEmail;
    }

    public void setExistEmail(String existEmail) {
        this.existEmail = existEmail;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public Integer getMaxCartones() {
        return maxCartones;
    }

    public void setMaxCartones(Integer maxCartones) {
        this.maxCartones = maxCartones;
    }

    public Integer getCartonesActivos() {
        return cartonesActivos;
    }

    public void setCartonesActivos(Integer cartonesActivos) {
        this.cartonesActivos = cartonesActivos;
    }
}