package com.eb.inscription.servlet;

import com.eb.inscription.dao.EmailVerificationCodeDAO;
import com.eb.inscription.dao.UserDAO;
import com.eb.inscription.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.time.Instant;

public class VerifyServlet extends HttpServlet {

    private static final String VIEW_VERIFY = "/WEB-INF/views/verify.jsp";

    private UserDAO userDAO;
    private EmailVerificationCodeDAO codeDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        String dbUrl = getInitParameter("db.url");
        String dbUser = getInitParameter("db.username");
        String dbPassword = getInitParameter("db.password");
        this.userDAO = new UserDAO(dbUrl, dbUser, dbPassword);
        this.codeDAO = new EmailVerificationCodeDAO(dbUrl, dbUser, dbPassword);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        request.setAttribute("email", email);
        request.getRequestDispatcher(VIEW_VERIFY).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String code = request.getParameter("verificationCode");

        if (isEmpty(email) || isEmpty(code)) {
            forwardWithError(request, response, email, "Email et code de vérification requis");
            return;
        }

        try {
            User user = userDAO.findByEmail(email);
            if (user == null) {
                forwardWithError(request, response, email, "Utilisateur non trouvé");
                return;
            }

            if (user.isActive()) {
                forwardWithError(request, response, email, "Ce compte est déjà vérifié");
                return;
            }

            EmailVerificationCodeDAO.EmailVerification verification = codeDAO.findActiveByUser(user.getId());
            if (verification == null) {
                forwardWithError(request, response, email,
                    "Aucun code de vérification valide trouvé. Veuillez en demander un nouveau.");
                return;
            }

            if (verification.getExpiresAt().isBefore(Instant.now())) {
                forwardWithError(request, response, email,
                    "Le code de vérification a expiré. Veuillez en demander un nouveau.");
                return;
            }

            if (!BCrypt.checkpw(code, verification.getCodeHash())) {
                codeDAO.incrementAttempt(verification.getId());
                forwardWithError(request, response, email, "Code de vérification incorrect");
                return;
            }

            codeDAO.markUsed(verification.getId());
            userDAO.enableUser(email);
            response.sendRedirect(request.getContextPath() + "/verify-success-servlet");

        } catch (Exception e) {
            e.printStackTrace();
            forwardWithError(request, response, email, "Erreur lors de la vérification: " + e.getMessage());
        }
    }

    private void forwardWithError(HttpServletRequest request, HttpServletResponse response,
                                  String email, String error) throws ServletException, IOException {
        request.setAttribute("error", error);
        request.setAttribute("email", email);
        request.getRequestDispatcher(VIEW_VERIFY).forward(request, response);
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}












