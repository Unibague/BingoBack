package bingo.unibague.demo.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class BingoCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private BingoGame game;

    @ElementCollection
    private List<Integer> numbers;

    @ElementCollection
    private List<Integer> markedNumbers;

    private boolean esGanador = false;
    private Integer numeroCarton;
    private LocalDateTime fechaCreacion;

    public BingoCard() {
        this.numbers = new ArrayList<>();
        this.markedNumbers = new ArrayList<>();
        this.fechaCreacion = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BingoGame getGame() { return game; }
    public void setGame(BingoGame game) { this.game = game; }

    public List<Integer> getNumbers() { return numbers; }
    public void setNumbers(List<Integer> numbers) { this.numbers = numbers; }

    public List<Integer> getMarkedNumbers() { return markedNumbers; }
    public void setMarkedNumbers(List<Integer> markedNumbers) { this.markedNumbers = markedNumbers; }

    public boolean isEsGanador() { return esGanador; }
    public void setEsGanador(boolean esGanador) { this.esGanador = esGanador; }

    public Integer getNumeroCarton() { return numeroCarton; }
    public void setNumeroCarton(Integer numeroCarton) { this.numeroCarton = numeroCarton; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    // Métodos de negocio
    public void markNumber(int number) {
        if (numbers.contains(number) && !markedNumbers.contains(number)) {
            markedNumbers.add(number);
        }
    }

    public boolean isComplete() {
        return markedNumbers.containsAll(numbers);
    }

    public boolean checkBingo() {
        // Verificar si tiene bingo (línea, columna, diagonal, etc.)
        // Implementar lógica de verificación según las reglas
        return markedNumbers.size() >= 24; // Ejemplo simple
    }
}
