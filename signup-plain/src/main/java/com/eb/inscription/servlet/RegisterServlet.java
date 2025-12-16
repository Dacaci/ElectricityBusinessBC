package com.eb.inscription.servlet;

import com.eb.inscription.dao.EmailVerificationCodeDAO;
import com.eb.inscription.dao.UserDAO;
import com.eb.inscription.mail.Mailer;
import com.eb.inscription.model.User;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;

/**
 * Servlet d'inscription SANS FRAMEWORK Spring Boot
 * Utilise uniquement les Servlets Java standard (javax.servlet)
 */
@WebServlet("/register-servlet")
public class RegisterServlet extends HttpServlet {
    
    private UserDAO userDAO;
    private EmailVerificationCodeDAO codeDAO;
    private Mailer mailer;
    
    @Override
    public void init() throws ServletException {
        super.init();
        // Initialisation des DAO avec JDBC pur (pas de Spring DI)
        String dbUrl = getServletContext().getInitParameter("db.url");
        String dbUser = getServletContext().getInitParameter("db.username");
        String dbPassword = getServletContext().getInitParameter("db.password");
        
        this.userDAO = new UserDAO(dbUrl, dbUser, dbPassword);
        this.codeDAO = new EmailVerificationCodeDAO(dbUrl, dbUser, dbPassword);
        
        // Initialisation du service mail (sans framework)
        String mailHost = getServletContext().getInitParameter("mail.smtp.host");
        String mailPort = getServletContext().getInitParameter("mail.smtp.port");
        String mailFrom = getServletContext().getInitParameter("mail.from");
        String mailUsername = getServletContext().getInitParameter("mail.smtp.username");
        String mailPassword = getServletContext().getInitParameter("mail.smtp.password");
        
        if (mailHost == null) mailHost = "localhost";
        if (mailPort == null) mailPort = "1025";
        if (mailFrom == null) mailFrom = "no-reply@eb.local";
        
        this.mailer = new Mailer(mailHost, Integer.parseInt(mailPort), mailFrom, mailUsername, mailPassword);
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
        String confirmPassword = request.getParameter("confirmPassword");
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
        
        // Validation de la confirmation du mot de passe
        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Les mots de passe ne correspondent pas");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            return;
        }
        
        // Validation de la longueur du mot de passe
        if (password.length() < 8) {
            request.setAttribute("error", "Le mot de passe doit contenir au moins 8 caractères");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            return;
        }
        
        // Validation du format de l'email
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            request.setAttribute("error", "Format d'email invalide");
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
            // Hasher le mot de passe avec BCrypt (sans framework)
            String hashedPassword = org.mindrot.jbcrypt.BCrypt.hashpw(password, org.mindrot.jbcrypt.BCrypt.gensalt(10));
            user.setPassword(hashedPassword);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPhone(phone);
            user.setEnabled(false);
            user.setCreatedAt(java.time.LocalDateTime.now());
            
            // Sauvegarder avec JDBC pur (pas de Spring Data JPA)
            userDAO.save(user);
            
            // Générer un code OTP à 6 chiffres
            String code = generateRandomCode();
            // Hasher le code avec BCrypt
            String codeHash = org.mindrot.jbcrypt.BCrypt.hashpw(code, org.mindrot.jbcrypt.BCrypt.gensalt(10));
            // Créer le code de vérification dans la table email_verification_codes
            codeDAO.createCode(user.getId(), codeHash, Instant.now().plus(Duration.ofMinutes(15)));
            
            // Log du code pour les tests
            System.out.println("=== CODE DE VÉRIFICATION POUR " + email + " : " + code + " ===");
            
            // Envoyer l'email avec le code
            try {
                mailer.sendCode(email, code);
            } catch (Exception mailException) {
                System.err.println("Erreur lors de l'envoi de l'email : " + mailException.getMessage());
                // On continue quand même, le code est loggé dans la console pour les tests
            }
            
            // Redirection (pas de RedirectAttributes)
            response.sendRedirect(request.getContextPath() + "/verify-servlet?email=" + email);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur lors de l'inscription: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
        }
    }
    
    /**
     * Générer un code aléatoire à 6 chiffres
     */
    private String generateRandomCode() {
        SecureRandom random = new SecureRandom();
        return String.format("%06d", random.nextInt(1_000_000));
    }
}












