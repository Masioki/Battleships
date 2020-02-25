package battleships.config;

import battleships.domain.user.AnonymousUser;
import battleships.domain.user.RegisteredUser;
import battleships.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AuthProvider implements AuthenticationProvider {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();
        RegisteredUser registeredUserDetails = userService.getUser(username);
        if (registeredUserDetails == null) {
            AnonymousUser principal = AnonymousUser.newAutoGeneratedUser();
            return new AnonymousAuthenticationToken("key", principal, principal.getAuthorities());
           // throw new BadCredentialsException("Username not found");
        }


        if (!passwordEncoder.matches(password, registeredUserDetails.getPassword())) {
            throw new BadCredentialsException("Password incorrect");
        }

        return new UsernamePasswordAuthenticationToken(registeredUserDetails, password, registeredUserDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
