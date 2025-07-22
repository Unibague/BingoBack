package bingo.unibague.demo.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class BingoCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BingoGame game;

    @ElementCollection
    private List<Integer> numbers;

    @ElementCollection
    private List<Integer> markedNumbers;

    private boolean esGanador = false;
    private Integer numeroCarton;

    public BingoCard() {
        this.numbers = new ArrayList<>();
        this.markedNumbers = new ArrayList<>();
        this.fechaCreacion = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Integer> getNumbers() {
        return numbers;
    }

    public void setNumbers(List<Integer> numbers) {
        this.numbers = numbers;
    }

    public List<Integer> getMarkedNumbers() {
        return markedNumbers;
    }

    public void setMarkedNumbers(List<Integer> markedNumbers) {
        this.markedNumbers = markedNumbers;
    }

    public void markNumber(int number) {
        if (numbers.contains(number) && !markedNumbers.contains(number)) {
            markedNumbers.add(number);
        }
    }

    public boolean isComplete() {
        return markedNumbers.containsAll(numbers);
    }
}