package bingo.unibague.demo.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class SendCredentialsRequest {

    @NotBlank(message = "El campo 'to' es obligatorio")
    @Email(message = "El campo 'to' debe ser un correo electrónico válido")
    private String to;

    @NotBlank(message = "El campo 'username' es obligatorio")
    private String username;

    @NotBlank(message = "El campo 'password' es obligatorio")
    private String password;

    public SendCredentialsRequest() {
    }

    public SendCredentialsRequest(String to, String username, String password) {
        this.to = to;
        this.username = username;
        this.password = password;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
