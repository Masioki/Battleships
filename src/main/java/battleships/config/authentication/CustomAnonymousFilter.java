package battleships.config.authentication;

import battleships.domain.user.AbstractUser;
import battleships.domain.user.AnonymousUser;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * Custom anonymous authentication filter.
 * Every anonymous user is given unique username and AbstractUser object as principal.
 * @see AbstractUser
 */
public class CustomAnonymousFilter extends GenericFilterBean {


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            AbstractUser user = AnonymousUser.newAutoGeneratedUser();
            SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken("key", user, user.getAuthorities()));
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
