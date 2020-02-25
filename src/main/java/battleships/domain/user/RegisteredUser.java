package battleships.domain.user;

import battleships.domain.Game.BattleshipGame;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
public class RegisteredUser extends AbstractUser {

    @Id
    private String username;

    @NotEmpty
    private String password;

    @OneToOne(mappedBy = "user")
    private Stats stats;

    @OneToMany
    private List<BattleshipGame> battleshipGames;

    @Enumerated(value = EnumType.STRING)
    private UserRole role;


    //for hibernate
    public RegisteredUser() {
        super(false);
        battleshipGames = new ArrayList<>();
        stats = new Stats();
        stats.setUser(this);
        role = UserRole.GUEST;
    }

    public RegisteredUser(String username, String password, UserRole role) {
        super(false);
        battleshipGames = new ArrayList<>();
        stats = new Stats();
        stats.setUser(this);
        this.role = role;
        this.username = username;
        this.password = password;
    }

    public void addGame(BattleshipGame game) {
        battleshipGames.add(game);
    }


    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


}
