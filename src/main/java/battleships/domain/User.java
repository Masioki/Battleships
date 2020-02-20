package battleships.domain;

import battleships.domain.Game.BattleshipGameImpl;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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
    private List<BattleshipGameImpl> battleshipGameImpls;

    @Enumerated(value = EnumType.STRING)
    private UserRole role;

    private boolean autoGenerated;


    public User() {
        battleshipGameImpls = new ArrayList<>();
        stats = new Stats();
        role = UserRole.GUEST;
        autoGenerated = false;
    }


    public void setAutoGenerated() {
        autoGenerated = true;
        stats.setPoints(10);
    }

    public void addGame(BattleshipGameImpl game) {
        battleshipGameImpls.add(game);
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        List<String> roles = Stream.of(UserRole.values())
                .filter(r -> r.getValue() <= role.getValue())
                .map(UserRole::name)
                .collect(Collectors.toList());
        for (String s : roles) authorities.add(new SimpleGrantedAuthority(s));
        return authorities;
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
