package ch.unil.doplab.cinerate.ui;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {"*.xhtml"})
public class AuthenticationFilter implements Filter {

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String requestURI = req.getRequestURI();

        // Les images, le CSS et les scripts passent toujours
        if (requestURI.contains("resource")) {
            chain.doFilter(request, response);
            return;
        }

        // Est-ce que l'utilisateur est connecté ?
        HttpSession session = req.getSession(false);
        boolean loggedIn = session != null
                && session.getAttribute("uuid") != null
                && session.getAttribute("username") != null;

        // Les pages Login et Register sont ouvertes à tout le monde
        String loginURI = req.getContextPath() + "/Login.xhtml";
        String registerURI = req.getContextPath() + "/Register.xhtml";
        boolean loginRequest = requestURI.equals(loginURI);
        boolean registerRequest = requestURI.equals(registerURI);

        if (loggedIn || loginRequest || registerRequest) {
            chain.doFilter(request, response);   // on laisse passer
        } else {
            res.sendRedirect(loginURI);          // sinon, retour à la connexion
        }
    }
}