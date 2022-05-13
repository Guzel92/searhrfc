package org.example.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.attribute.RequestAttributes;
import org.example.security.AnonymousAuthentication;

import java.io.IOException;

public class AnonymousAuthenticationFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        if (req.getAttribute(RequestAttributes.AUTH_ATTR) == null) {
            req.setAttribute(RequestAttributes.AUTH_ATTR, new AnonymousAuthentication());
        }
        chain.doFilter(req, res);
    }
}
