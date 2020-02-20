package battleships.services;

import battleships.domain.Game.BattleshipGame;
import battleships.domain.Game.BattleshipGameImpl;
import battleships.domain.Game.GameStatus;
import battleships.domain.Game.Move;
import battleships.domain.User;
import battleships.dto.MoveDTO;
import battleships.exceptions.GameFinishedException;
import battleships.exceptions.ShipDestroyedException;
import battleships.exceptions.WrongMoveException;
import battleships.repositories.UniversalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

//TODO: ADD ASYNC

@Service
public class GameService {

    @Autowired
    private UniversalRepository repository;

    @Autowired
    private UserService userService;

    @Autowired
    private SimpMessagingTemplate messaging;


    public BattleshipGame getGame(int gameID) {
        return repository.getBattleshipGame(gameID);
    }

    public BattleshipGame createGame(String ownerUsername) {
        return new BattleshipGameImpl(ownerUsername);
    }

    public Move move(MoveDTO move, int gameID) throws WrongMoveException, ShipDestroyedException, GameFinishedException {
        BattleshipGame game = getGame(gameID);
        Move result = null;
        if (game != null && game.getGameStatus() == GameStatus.IN_PROGRESS) {
            result = game.attack(move);
            if (game.getGameStatus() == GameStatus.FINISHED) throw new GameFinishedException(game.getWinnerUsername());
        }
        return result;
    }

    public void guestSurrenderAndSend(String username, String destination) {
        if (destination != null) {
            if (destination.startsWith("/game/")) {
                int gameID = Integer.parseInt(destination.substring(6));
                surrender(username, gameID);
                messaging.convertAndSend(destination, new MoveDTO(MoveDTO.MoveType.SURRENDER));
            }
            messaging.convertAndSend(destination, new MoveDTO(MoveDTO.MoveType.OPPONENT_DISCONNECT));
        }
    }

    public void surrender(String username, int gameID) {
        BattleshipGame game = getGame(gameID);
        if (game != null) {
            game.surrender(username);
            User winner = userService.getUser(game.getWinnerUsername());
            User loser = userService.getUser(username);
            if (winner != null && loser != null) {
                winner.getStats().updatePoints(true, loser.getStats().getPoints());
                loser.getStats().updatePoints(false, winner.getStats().getPoints());
            }
            repository.remove(game);
        }
    }

    public void removeGame(int gameID) {
        BattleshipGame game = getGame(gameID);
        if (game != null) repository.remove(game);
    }
}
