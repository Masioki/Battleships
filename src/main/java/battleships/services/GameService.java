package battleships.services;

import battleships.domain.Game.BattleshipGame;
import battleships.domain.Game.BattleshipGameFacade;
import battleships.domain.Game.Game;
import battleships.repositories.UniversalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//TODO: ADD ASYNC

@Service
public class GameService {

    @Autowired
    private UniversalRepository repository;

    @Autowired
    private UserService userService;

    public Game getGame(int gameID) {
        BattleshipGame g = repository.getBattleshipGame(gameID);
        if (g == null) return null;
        return new BattleshipGameFacade(g);
    }
}
