package mihael.macuka.chatapp.security;

import static mihael.macuka.chatapp.security.SecurityConstants.AUTHORIZATION_KEY;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthorizationFilter extends BasicAuthenticationFilter {
    private final static String AUTHORIZATION_HEADER = "Authorization";

    public AuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        final String header = request.getHeader(AUTHORIZATION_HEADER);
        if (header == null) {
            chain.doFilter(request, response);
            return;
        }

        final UsernamePasswordAuthenticationToken authenticationToken = authenticate(request);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken authenticate(final HttpServletRequest request) {
        final String token = request.getHeader(AUTHORIZATION_HEADER);

        if (token != null) {
            final Claims user = Jwts.parser()
                .setSigningKey(Keys.hmacShaKeyFor(AUTHORIZATION_KEY.getBytes()))
                .parseClaimsJws(token)
                .getBody();
            if (user != null) {
                return new UsernamePasswordAuthenticationToken(
                    user, null, new ArrayList<>()
                );
            }
        }
        return null;
    }
}
