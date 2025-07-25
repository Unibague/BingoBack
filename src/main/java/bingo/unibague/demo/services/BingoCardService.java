package bingo.unibague.demo.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bingo.unibague.demo.models.BingoCard;
import bingo.unibague.demo.models.BingoGame;
import bingo.unibague.demo.repository.BingoCardRepository;

@Service
@Transactional
public class BingoCardService {
    
    @Autowired
    private BingoCardRepository cardRepository;
    
    public List<BingoCard> generateCardsForGame(BingoGame game, int quantity) {
        List<BingoCard> cards = new ArrayList<>();
        
        for (int i = 1; i <= quantity; i++) {
            BingoCard card = generateSingleCard();
            card.setGame(game);
            card.setNumeroCarton(i);
            cards.add(card);
        }
        
        return cardRepository.saveAll(cards);
    }
    
    public BingoCard generateSingleCard() {
        BingoCard card = new BingoCard();
        List<Integer> numbers = generateBingoNumbers();
        card.setNumbers(numbers);
        return card;
    }
    
    private List<Integer> generateBingoNumbers() {
        List<Integer> numbers = new ArrayList<>();
        Random random = new Random();
        
        // Rangos para cada columna: B(1-15), I(16-30), N(31-45), G(46-60), O(61-75)
        int[][] ranges = {{1, 15}, {16, 30}, {31, 45}, {46, 60}, {61, 75}};
        
        for (int col = 0; col < 5; col++) {
            List<Integer> availableNumbers = new ArrayList<>();
            for (int i = ranges[col][0]; i <= ranges[col][1]; i++) {
                availableNumbers.add(i);
            }
            Collections.shuffle(availableNumbers);
            
            for (int row = 0; row < 5; row++) {
                if (col == 2 && row == 2) {
                    // Centro libre
                    numbers.add(0);
                } else {
                    numbers.add(availableNumbers.get(row < 2 ? row : row - 1));
                }
            }
        }
        
        return numbers;
    }
    
    public Optional<BingoCard> getCardById(Long cardId) {
        return cardRepository.findById(cardId);
    }
    
    public List<BingoCard> getCardsByGame(Long gameId) {
        return cardRepository.findByGameIdOrderByNumeroCarton(gameId);
    }
    
    public List<BingoCard> getWinningCards(Long gameId) {
        return cardRepository.findByGameIdAndEsGanadorTrue(gameId);
    }
    
    public BingoCard markNumber(Long cardId, int number) {
        return cardRepository.findById(cardId)
                .map(card -> {
                    card.markNumber(number);
                    return cardRepository.save(card);
                })
                .orElse(null);
    }
    
    public boolean checkBingo(Long cardId) {
        return cardRepository.findById(cardId)
                .map(card -> {
                    boolean hasBingo = card.checkBingo();
                    if (hasBingo) {
                        card.setEsGanador(true);
                        cardRepository.save(card);
                    }
                    return hasBingo;
                })
                .orElse(false);
    }
    
    public long countCardsByGame(Long gameId) {
        return cardRepository.countByGameId(gameId);
    }
}
