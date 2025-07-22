package bingo.unibague.demo.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bingo.unibague.demo.models.Player;
import bingo.unibague.demo.repository.PlayerRepository;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Player registerPlayer(Player player) {
        return playerRepository.save(player);
    }

    public void deletePlayer(Long playerId) {
        playerRepository.deleteById(playerId);
    }

    public List<Player> getPlayersByGameId(Long gameId) {
        return playerRepository.findByGameId(gameId);
    }

}