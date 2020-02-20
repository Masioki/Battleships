package battleships.services;

import battleships.domain.Game.BattleshipGameImpl;
import battleships.domain.User;
import battleships.domain.UserRole;
import battleships.dto.LoginData;
import battleships.repositories.UniversalRepository;
import com.sun.istack.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UniversalRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return getUser(s);
    }

    @Nullable
    public User getUser(String username) {
        return repository.getUser(username);
    }

    public void removeGuest(String username) {
        User u = getUser(username);
        if (u != null && u.getRole() == UserRole.GUEST) {
            repository.remove(u);
        }
    }

    public List<BattleshipGameImpl> getUserGames(String s) {
        return repository.getUserGames(s);
    }

    public void registerUser(LoginData data) throws Exception {
        if (loadUserByUsername(data.getUsername()) != null) throw new Exception("user exists");
        User u = new User();
        u.setUsername(data.getUsername());
        u.setPassword(passwordEncoder.encode(data.getPassword()));
        u.setRole(UserRole.USER);
        repository.saveUser(u);
    }
}
