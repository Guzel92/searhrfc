package org.example.filter;

import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.attribute.RequestAttributes;
import org.example.security.Authentication;
import org.example.security.AuthenticationException;
import org.example.security.TokenAuthentication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Optional;

public class BearerAuthenticationFilter extends HttpFilter {
    private RSASSAVerifier verifier;

    @Override
    public void init() throws ServletException {
        try {
            final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            final byte[] encodedKeyBytes = Files.readAllBytes(Paths.get(
                    Optional.ofNullable(System.getenv("VERIFY_KEY"))
                            .orElse("C:\\Users\\79178\\Desktop\\tomcat-embed (2)\\verify.key")
            ));
            final byte[] keyBytes = Base64.getMimeDecoder().decode(encodedKeyBytes);
            final PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(keyBytes));
            this.verifier = new RSASSAVerifier((RSAPublicKey) publicKey);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        final Authentication existing = (Authentication) req.getAttribute(RequestAttributes.AUTH_ATTR);
        // early exit: если уже есть аутентификация и она не анонимная
        // значит уже кто-то до нас аутентифицировал пользователя
        if (existing != null && !existing.isAnonymous()) {
            chain.doFilter(req, res);
            return;
        }

        // проверяем, есть ли заголовок Authorization
        final String header = req.getHeader("Authorization");
        if (header == null) {
            chain.doFilter(req, res);
            return;
        }
        if (!header.startsWith("Bearer")) {
            chain.doFilter(req, res);
            return;
        }
        // Значит это точно Authorization: Bearer token
        try {
            final String token = header.substring("Bearer".length() + 1);

            final SignedJWT jwt = SignedJWT.parse(token);
            final boolean verification = jwt.verify(verifier);
            if (verification == false) {
                throw new AuthenticationException("wrong signature");
            }
            // TODO: date check
            final JWTClaimsSet claimsSet = jwt.getJWTClaimsSet();
            final long id = claimsSet.getLongClaim("id");
            final String login = claimsSet.getSubject();
            final String[] roles = claimsSet.getStringArrayClaim("roles");
            final TokenAuthentication authentication = new TokenAuthentication(id, login, roles);

            req.setAttribute(
                    RequestAttributes.AUTH_ATTR,
                    authentication
            );
        } catch (Exception e) {
            res.sendError(401);
            return;
        }

        chain.doFilter(req, res);
    }
}
