package bingo.unibague.demo.payload.response;

public class UserCartonInfoDTO {
    private Long userId;
    private String username;
    private Integer maxCartones;
    private Integer cartonesAsignados;

    public UserCartonInfoDTO(Long userId, String username, Integer maxCartones, Integer cartonesAsignados) {
        this.userId = userId;
        this.username = username;
        this.maxCartones = maxCartones;
        this.cartonesAsignados = cartonesAsignados;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getMaxCartones() {
        return maxCartones;
    }

    public void setMaxCartones(Integer maxCartones) {
        this.maxCartones = maxCartones;
    }

    public Integer getCartonesAsignados() {
        return cartonesAsignados;
    }

    public void setCartonesAsignados(Integer cartonesAsignados) {
        this.cartonesAsignados = cartonesAsignados;
    }
}
