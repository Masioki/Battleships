package battleships.services;

import battleships.domain.Game.BattleshipGame;
import battleships.domain.user.RegisteredUser;
import battleships.domain.user.UserRole;
import battleships.dto.LoginData;
import battleships.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserService implements UserDetailsService {

    private static final int GUEST_POINTS = 10;

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return getUser(s);
    }

    @Nullable
    public RegisteredUser getUser(String username) {
        return repository.getUser(username);
    }

    public void removeGuest(String username) {
        RegisteredUser u = getUser(username);
        if (u != null && u.getRole() == UserRole.GUEST) {
            repository.remove(u);
        }
    }

    public List<BattleshipGame> getUserGames(String s) {
        return repository.getUserGames(s);
    }

    public void registerUser(LoginData data, UserRole role) throws Exception {
        String username = data.getUsername();
        String password = data.getPassword();
        if (username == null || username.length() == 0 || password == null || password.length() == 0)
            throw new Exception("Wrong data");
        if (userExists(username)) throw new Exception("User already exists");
        RegisteredUser u = new RegisteredUser();
        u.setUsername(data.getUsername());
        u.setPassword(passwordEncoder.encode(data.getPassword()));
        u.setRole(role);
        repository.saveUser(u);
    }

    public boolean userExists(String username) {
        return repository.userExists(username);
    }

    public void updatePoints(String winnerUsername, String loserUsername) {
        boolean winnerIsGuest = !userExists(winnerUsername);
        boolean loserIsGuest = !userExists(loserUsername);
        //every User.getStats() != null is checked above
        if (winnerIsGuest) {
            if (!loserIsGuest) {
                RegisteredUser loser = getUser(loserUsername);
                loser.getStats().updatePoints(false, GUEST_POINTS);
                repository.saveUser(loser);
            }
        } else {
            RegisteredUser winner = getUser(winnerUsername);
            long points;
            if (loserIsGuest) {
                points = GUEST_POINTS;
            } else {
                RegisteredUser loser = getUser(loserUsername);
                loser.getStats().updatePoints(false, winner.getStats().getPoints());
                points = loser.getStats().getPoints();
                repository.saveUser(loser);

            }
            winner.getStats().updatePoints(true, points);
            repository.saveUser(winner);
        }
    }
}
