package com.eb.inscription.servlet;

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
    
    @Override
    public void init() throws ServletException {
        super.init();
        // Initialisation du DAO avec JDBC pur
        String dbUrl = getServletContext().getInitParameter("db.url");
        String dbUser = getServletContext().getInitParameter("db.username");
        String dbPassword = getServletContext().getInitParameter("db.password");
        
        this.userDAO = new UserDAO(dbUrl, dbUser, dbPassword);
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
            
            // Vérifier le code
            if (!code.equals(user.getVerificationCode())) {
                request.setAttribute("error", "Code de vérification incorrect");
                request.setAttribute("email", email);
                request.getRequestDispatcher("/WEB-INF/views/verify.jsp").forward(request, response);
                return;
            }
            
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

