package bingo.unibague.demo.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "bingo_games")
public class BingoGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "status")
    private String status;

    @ElementCollection
    @Column(name = "called_numbers")
    private List<Integer> calledNumbers = new ArrayList<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Player> players = new ArrayList<>();

    public BingoGame() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Integer> getCalledNumbers() {
        return calledNumbers;
    }

    public void setCalledNumbers(List<Integer> calledNumbers) {
        this.calledNumbers = calledNumbers;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void addCalledNumber(int number) {
        this.calledNumbers.add(number);
    }

    public void addPlayer(Player player) {
        this.players.add(player);
        player.setGame(this);
    }

    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(status);
    }
}