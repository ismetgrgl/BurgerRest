package org.burgerapp.utility;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;


import org.burgerapp.exception.ErrorType;
import org.burgerapp.exception.UserServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.util.Date;
import java.util.Optional;


@Component
public class JwtTokenManager {
    @Value("${jwt.secret.secret-key}")
    String secretKey;
    @Value("${jwt.secret.issuer}")
    String issuer;
    Long expireTime = 1000L * 60 * 15; // 15dakikalık bir zaman


    //1.Token üretmeli:

    /**
     * Dikkat! Claim objeleri içindeki değerler herkes tarafından görülebilir. O yüzden claimler ile e-mail,password
     * gibi herkesin görmesini istemediğimiz bilgileri payload kısmında tutmayız.
     */
    public Optional<String> createToken(Long id) {
        String token = "";
        try {
            token = JWT.create().withAudience()
                    .withClaim("id",
                            id)
                    .withClaim("whichpage",
                            "AuthMicroService")
                    .withClaim("ders",
                            "Java JWT")
                    .withClaim("grup",
                            "Java14")
                    .withIssuer(issuer)
                    .withIssuedAt(new Date()) //Tokenın yaratıldığı an.
                    .withExpiresAt(new Date(System.currentTimeMillis() + expireTime)) //Data,Instant
                    .sign(Algorithm.HMAC512(secretKey));
            return Optional.of(token);
        } catch (IllegalArgumentException e) {
            throw new UserServiceException(ErrorType.TOKEN_CREATION_FAILED);
        } catch (JWTCreationException e) {
            throw new UserServiceException(ErrorType.TOKEN_CREATION_FAILED);
        }
    }


    //2. Token Doğrulanmalı:
//	public Boolean verifyToken(String token) {
//
//	}


    //3. Tokendan bilgi çıkarımı yapmalı:
    public Optional<Long> getIdFromToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC512(secretKey);
            JWTVerifier verifier = JWT.require(algorithm).withIssuer(issuer).build();
            DecodedJWT decodedJWT = verifier.verify(token);

            if (decodedJWT == null)
                return Optional.empty();

            Long id = decodedJWT.getClaim("id").asLong();
            return Optional.of(id);
        } catch (IllegalArgumentException e) {
            throw new UserServiceException(ErrorType.TOKEN_ARGUMENT_NOTVALID);
        } catch (JWTVerificationException e) {
            throw new UserServiceException(ErrorType.TOKEN_VERIFY_FAILED);
        }

    }

    public Optional<String> getRoleFromToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC512(secretKey);
            JWTVerifier verifier = JWT.require(algorithm).withIssuer(issuer).build();
            DecodedJWT decodedJWT = verifier.verify(token);

            if (decodedJWT == null)
                return Optional.empty();

            String role = decodedJWT.getClaim("role").asString();
            return Optional.of(role);
        } catch (IllegalArgumentException e) {
            throw new UserServiceException(ErrorType.TOKEN_ARGUMENT_NOTVALID);
        } catch (JWTVerificationException e) {
            throw new UserServiceException(ErrorType.TOKEN_VERIFY_FAILED);
        }

    }
}