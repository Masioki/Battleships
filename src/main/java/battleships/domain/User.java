package battleships.domain;

import battleships.domain.Game.BattleshipGame;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotEmpty;
import java.util.Collection;
import java.util.List;


@Entity
@Getter
@Setter
public class User implements UserDetails {

    @Id
    private String username;

    @NotEmpty
    private String password;

    @OneToOne(mappedBy = "user")
    private Stats stats;

    @OneToMany
    private List<BattleshipGame> battleshipGames;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;//TODO
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
