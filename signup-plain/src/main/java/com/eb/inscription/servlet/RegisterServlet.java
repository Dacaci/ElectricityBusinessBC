package com.eb.inscription.servlet;

import com.eb.inscription.dao.UserDAO;
import com.eb.inscription.model.User;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * Servlet d'inscription SANS FRAMEWORK Spring Boot
 * Utilise uniquement les Servlets Java standard (javax.servlet)
 */
@WebServlet("/register-servlet")
public class RegisterServlet extends HttpServlet {
    
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        // Initialisation du DAO avec JDBC pur (pas de Spring DI)
        String dbUrl = getServletContext().getInitParameter("db.url");
        String dbUser = getServletContext().getInitParameter("db.username");
        String dbPassword = getServletContext().getInitParameter("db.password");
        
        this.userDAO = new UserDAO(dbUrl, dbUser, dbPassword);
    }
    
    /**
     * GET /register-servlet - Affiche le formulaire d'inscription
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Pas de Thymeleaf, on forward vers une JSP
        request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
    }
    
    /**
     * POST /register-servlet - Traite l'inscription
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Récupération des paramètres (pas de @RequestBody, tout manuel)
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String phone = request.getParameter("phone");
        
        // Validation manuelle (pas de @Valid)
        if (email == null || email.trim().isEmpty() || 
            password == null || password.trim().isEmpty() ||
            firstName == null || firstName.trim().isEmpty() ||
            lastName == null || lastName.trim().isEmpty()) {
            
            request.setAttribute("error", "Tous les champs obligatoires doivent être remplis");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            return;
        }
        
        try {
            // Vérifier si l'email existe déjà (JDBC pur)
            if (userDAO.existsByEmail(email)) {
                request.setAttribute("error", "Cet email est déjà utilisé");
                request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
                return;
            }
            
            // Créer l'utilisateur (pas d'entité JPA, juste un POJO)
            User user = new User();
            user.setEmail(email);
            user.setPassword(password); // TODO: Hasher avec BCrypt manuel
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPhone(phone);
            user.setEnabled(false);
            user.setVerificationCode(UUID.randomUUID().toString().substring(0, 6).toUpperCase());
            
            // Sauvegarder avec JDBC pur (pas de Spring Data JPA)
            userDAO.save(user);
            
            // Redirection (pas de RedirectAttributes)
            response.sendRedirect(request.getContextPath() + "/verify-servlet?email=" + email);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur lors de l'inscription: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
        }
    }
}







