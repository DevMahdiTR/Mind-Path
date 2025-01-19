package mindpath.security.jwt.handler;

import mindpath.security.jwt.JwtTokenProvider;
import mindpath.security.utility.CustomUserDetailsService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class UserValidationHandler extends FilterChainHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    public UserValidationHandler(JwtTokenProvider jwtTokenProvider, CustomUserDetailsService customUserDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    public boolean handle(String jwtToken) {
        String userEmail = jwtTokenProvider.extractEmailFromJwt(jwtToken);

        if (userEmail == null || SecurityContextHolder.getContext().getAuthentication() != null) {
            return false;
        }
        UserDetails userEntity = this.customUserDetailsService.loadUserByUsername(userEmail);

        if (!userEntity.isEnabled()) {
            return false;
        }
        return handleNext(jwtToken);
    }
}
