package mindpath.core.service.auth;

import mindpath.core.domain.token.access.AccessToken;
import mindpath.core.service.token.TokenService;
import mindpath.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
public class LogoutService implements LogoutHandler {

    private final TokenService<AccessToken> tokenService;
    private final JwtTokenProvider jwtTokenProvider;

    public LogoutService(TokenService<AccessToken> tokenService, JwtTokenProvider jwtTokenProvider) {
        this.tokenService = tokenService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String jwt = jwtTokenProvider.resolveToken(request);
        AccessToken storedToken = tokenService.findByToken(jwt);
        if (storedToken != null) {
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenService.save(storedToken);
        }

    }
}

