package battleships.services;

import battleships.domain.Game.BattleshipGame;
import battleships.domain.user.AbstractUser;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ActiveGamesAndUsersRegistry {
    private final List<BattleshipGame> games;
    private final List<AbstractUser> users;

    public ActiveGamesAndUsersRegistry() {
        games = new ArrayList<>();
        users = new ArrayList<>();
    }


    public boolean containsGame(int gameID) {
        return games.stream().anyMatch(game -> game.getGameID() == gameID);
    }

    public boolean containsUser(String username) {
        return users.stream().anyMatch(principal -> principal.getName().equals(username));
    }


    public void removeGame(int gameID) {
        games.removeIf(game -> game.getGameID() == gameID);
    }

    public void removeUser(String username) {
        users.removeIf(principal -> principal.getName().equals(username));
    }

    public List<BattleshipGame> getGamesWithPlayer(String username) {
        return games.stream().filter(g -> g.containsUser(username)).collect(Collectors.toList());
    }

    @Nullable
    public BattleshipGame getGame(int gameID) {
        for (BattleshipGame g : games) {
            if (g.getGameID() == gameID) return g;
        }
        return null;
    }

    @Nullable
    public AbstractUser getUser(String username) {
        for (AbstractUser cp : users) {
            if (cp.getName().equals(username)) return cp;
        }
        return null;
    }


    public void addGame(BattleshipGame game) {
        if (!containsGame(game.getGameID())) games.add(game);
    }

    public void addUser(AbstractUser principal) {
        if (!containsUser(principal.getName())) users.add(principal);
    }

}
