package bingo.unibague.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bingo.unibague.demo.models.BingoGame;

@Repository
public interface BingoRepository extends JpaRepository<BingoGame, Long> {
    // Custom query methods can be defined here if needed
}