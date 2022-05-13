package org.example.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.attribute.ContextAttributes;
import org.example.attribute.RequestAttributes;
import org.example.security.Authentication;
import org.example.security.AuthenticationException;
import org.example.security.X509Authentication;
import org.example.service.UserService;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class X509AuthenticationFilter extends HttpFilter {
    private final Pattern pattern = Pattern.compile("CN=(.*?)(?:,|$)"); // extract Common Name from certificate
    private UserService userService;

    @Override
    public void init() throws ServletException {
        ApplicationContext context = (ApplicationContext) getServletContext().getAttribute(ContextAttributes.CONTEXT_ATTR);
        userService = context.getBean(UserService.class);
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

        // attributes - аналог Map<String, Object>
        final Object attribute = req.getAttribute(RequestAttributes.X509_ATTR);
        // early exit - если сертификата нет, то тоже выходим, кто-то другой аутентифицирует
        if (attribute == null) {
            chain.doFilter(req, res);
            return;
        }

        try {
            final X509Certificate[] x509Certificates = (X509Certificate[]) attribute;
            final X509Certificate certificate = x509Certificates[0];
            final String name = certificate.getSubjectX500Principal().getName();
            final Matcher matcher = pattern.matcher(name);
            if (!matcher.find()) {
                throw new AuthenticationException("Can't find common name: " + name);
            }
            final String commonName = matcher.group(1);
            final X509Authentication authentication = userService.authenticateByCommonName(commonName);
            req.setAttribute(
                    RequestAttributes.AUTH_ATTR,
                    authentication
            );
        } catch (AuthenticationException e) {
            e.printStackTrace();
            res.sendError(401);
            return;
        }

        // передача запроса дальше по цепочке
        chain.doFilter(req, res);
    }
}
