package mindpath.core.domain.token.confirmation;

import mindpath.core.domain.auth.user.UserEntity;
import mindpath.core.domain.token.TokenGenerator;
import mindpath.security.jwt.JwtTokenProvider;
import mindpath.security.utility.SecurityConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class ConfirmationTokenGenerator implements TokenGenerator {
    @Override
    public String generateToken(UserEntity userEntity) {
        String email = userEntity.getEmail();
        Date currentData = new Date();
        Date expireDate = new Date(System.currentTimeMillis() + SecurityConstants.CONFIRMATION_JWT_EXPIRATION);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(currentData)
                .setExpiration(expireDate)
                .signWith(JwtTokenProvider.getSignInKey(SecurityConstants.JWT_CONFIRMATION_SECRET), SignatureAlgorithm.HS256)
                .compact();
    }
}
