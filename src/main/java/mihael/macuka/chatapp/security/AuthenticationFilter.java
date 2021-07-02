package mihael.macuka.chatapp.security;

import static mihael.macuka.chatapp.security.SecurityConstants.AUTHORIZATION_HEADER;
import static mihael.macuka.chatapp.security.SecurityConstants.AUTHORIZATION_KEY;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import mihael.macuka.chatapp.dtos.UserDto;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final static ObjectMapper MAPPER = new ObjectMapper();
    private static final Long EXPIRATION_TIME = 60 * 60 * 1000L;
    private final AuthenticationManager authenticationManager;

    public AuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            final UserDto userDto = MAPPER.readValue(request.getInputStream(), UserDto.class);
            return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    userDto.getUsername(),
                    userDto.getPassword(),
                    new ArrayList<>()
                )
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        final Date expirationDate = new Date(System.currentTimeMillis() + EXPIRATION_TIME);
        final Key key = Keys.hmacShaKeyFor(AUTHORIZATION_KEY.getBytes(StandardCharsets.UTF_8));
        final Claims claims = Jwts.claims().setSubject(((User) authResult.getPrincipal()).getUsername());
        final String token = Jwts.builder().setClaims(claims).signWith(key, SignatureAlgorithm.HS512).setExpiration(expirationDate).compact();
        response.addHeader(AUTHORIZATION_HEADER, token);
    }
}
