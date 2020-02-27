package battleships.config.authentication;

import battleships.domain.user.RegisteredUser;
import battleships.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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

    /**
     * @param authentication
     * @return authentication token with AbstractUser principal
     * @throws AuthenticationException
     * @see battleships.domain.user.AbstractUser
     * @see CustomAnonymousFilter
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();
        RegisteredUser registeredUserDetails = userService.getUser(username);

        /*
        Every anonymous user should be given Anonymous Token, but with AbstractUser principal.
        Unfortunately, because of Spring Security authentication filters order, this method is never called for anonymous user.
        See CustomAnonymousFilter and SecurityConfig where this functionality is implemented.
         */
        /*
        if (registeredUserDetails == null) {
            AnonymousUser principal = AnonymousUser.newAutoGeneratedUser();
            return new AnonymousAuthenticationToken("key", principal, principal.getAuthorities());
            // throw new BadCredentialsException("Username not found");
        }
        */

        if (registeredUserDetails == null) {
            throw new BadCredentialsException("Username not found");
        }

        if (!passwordEncoder.matches(password, registeredUserDetails.getPassword())) {
            throw new BadCredentialsException("Password incorrect");
        }

        return new UsernamePasswordAuthenticationToken(registeredUserDetails, password, registeredUserDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return UsernamePasswordAuthenticationToken.class.equals(aClass);
        //return UsernamePasswordAuthenticationToken.class.isAssignableFrom(aClass);
    }
}