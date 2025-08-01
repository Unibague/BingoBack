package bingo.unibague.demo.payload.request;

public class CreateUserRequest {
    private String username;
    private String email;
    private String existEmail;
    private String password;
    private Integer maxCartones;
    private String role;

    // Getters y setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public Integer getMaxCartones() { return maxCartones; }
    public void setMaxCartones(Integer maxCartones) { this.maxCartones = maxCartones; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getExistEmail() { return existEmail; }
    public void setExistEmail(String existEmail) { this.existEmail = existEmail; }
}
