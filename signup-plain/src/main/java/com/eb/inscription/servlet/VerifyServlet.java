package com.eb.inscription.servlet;

import com.eb.inscription.dao.EmailVerificationCodeDAO;
import com.eb.inscription.dao.UserDAO;
import com.eb.inscription.model.User;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet de vérification SANS FRAMEWORK Spring Boot
 * Utilise uniquement les Servlets Java standard
 */
@WebServlet("/verify-servlet")
public class VerifyServlet extends HttpServlet {
    
    private UserDAO userDAO;
    private EmailVerificationCodeDAO codeDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        // Initialisation des DAO avec JDBC pur
        String dbUrl = getServletContext().getInitParameter("db.url");
        String dbUser = getServletContext().getInitParameter("db.username");
        String dbPassword = getServletContext().getInitParameter("db.password");
        
        this.userDAO = new UserDAO(dbUrl, dbUser, dbPassword);
        this.codeDAO = new EmailVerificationCodeDAO(dbUrl, dbUser, dbPassword);
    }
    
    /**
     * GET /verify-servlet - Affiche le formulaire de vérification
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        request.setAttribute("email", email);
        
        // Forward vers JSP (pas de Thymeleaf)
        request.getRequestDispatcher("/WEB-INF/views/verify.jsp").forward(request, response);
    }
    
    /**
     * POST /verify-servlet - Traite la vérification du code
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String code = request.getParameter("verificationCode");
        
        // Validation manuelle
        if (email == null || email.trim().isEmpty() || 
            code == null || code.trim().isEmpty()) {
            
            request.setAttribute("error", "Email et code de vérification requis");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/views/verify.jsp").forward(request, response);
            return;
        }
        
        try {
            // Récupérer l'utilisateur avec JDBC pur
            User user = userDAO.findByEmail(email);
            
            if (user == null) {
                request.setAttribute("error", "Utilisateur non trouvé");
                request.setAttribute("email", email);
                request.getRequestDispatcher("/WEB-INF/views/verify.jsp").forward(request, response);
                return;
            }
            
            if (user.isEnabled()) {
                request.setAttribute("error", "Ce compte est déjà vérifié");
                request.setAttribute("email", email);
                request.getRequestDispatcher("/WEB-INF/views/verify.jsp").forward(request, response);
                return;
            }
            
            // Récupérer le code de vérification actif depuis email_verification_codes
            EmailVerificationCodeDAO.EmailVerification verification = codeDAO.findActiveByUser(user.getId());
            
            if (verification == null) {
                request.setAttribute("error", "Aucun code de vérification valide trouvé. Veuillez en demander un nouveau.");
                request.setAttribute("email", email);
                request.getRequestDispatcher("/WEB-INF/views/verify.jsp").forward(request, response);
                return;
            }
            
            // Vérifier si le code n'est pas expiré
            if (verification.getExpiresAt().isBefore(java.time.Instant.now())) {
                request.setAttribute("error", "Le code de vérification a expiré. Veuillez en demander un nouveau.");
                request.setAttribute("email", email);
                request.getRequestDispatcher("/WEB-INF/views/verify.jsp").forward(request, response);
                return;
            }
            
            // Vérifier le code avec BCrypt
            if (!org.mindrot.jbcrypt.BCrypt.checkpw(code, verification.getCodeHash())) {
                // Incrémenter le compteur de tentatives
                codeDAO.incrementAttempt(verification.getId());
                request.setAttribute("error", "Code de vérification incorrect");
                request.setAttribute("email", email);
                request.getRequestDispatcher("/WEB-INF/views/verify.jsp").forward(request, response);
                return;
            }
            
            // Marquer le code comme utilisé
            codeDAO.markUsed(verification.getId());
            
            // Activer l'utilisateur avec JDBC pur
            userDAO.enableUser(email);
            
            // Redirection vers la page de succès
            response.sendRedirect(request.getContextPath() + "/verify-success-servlet");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur lors de la vérification: " + e.getMessage());
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/views/verify.jsp").forward(request, response);
        }
    }
}












