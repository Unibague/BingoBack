package bingo.unibague.demo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bingo.unibague.demo.models.BingoGame;
import bingo.unibague.demo.repository.BingoRepository;

@Service
public class BingoService {

    private final BingoRepository bingoRepository;

    @Autowired
    public BingoService(BingoRepository bingoRepository) {
        this.bingoRepository = bingoRepository;
    }

    public BingoGame createGame(BingoGame game) {
        return bingoRepository.save(game);
    }

    public Optional<BingoGame> startGame(Long gameId) {
        Optional<BingoGame> game = bingoRepository.findById(gameId);
        game.ifPresent(bingoGame -> {
            bingoGame.setStatus("ACTIVE");
            bingoRepository.save(bingoGame);
        });
        return game;
    }

    public Optional<BingoGame> endGame(Long gameId) {
        Optional<BingoGame> game = bingoRepository.findById(gameId);
        game.ifPresent(bingoGame -> {
            bingoGame.setStatus("INACTIVE");
            bingoRepository.save(bingoGame);
        });
        return game;
    }

    public List<BingoGame> getAllGames() {
        return bingoRepository.findAll();
    }

    public Optional<BingoGame> getGameById(Long gameId) {
        return bingoRepository.findById(gameId);
    }

    public void drawNumber(Long gameId, int number) {
        Optional<BingoGame> game = bingoRepository.findById(gameId);
        game.ifPresent(bingoGame -> {
            bingoGame.getCalledNumbers().add(number);
            bingoRepository.save(bingoGame);
        });
    }

    public boolean checkGameStatus(Long gameId) {
        return bingoRepository.findById(gameId)
                .map(BingoGame::isActive)
                .orElse(false);
    }
}