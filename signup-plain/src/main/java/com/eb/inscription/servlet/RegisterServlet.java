package com.eb.inscription.servlet;

import com.eb.inscription.dao.EmailVerificationCodeDAO;
import com.eb.inscription.dao.UserDAO;
import com.eb.inscription.mail.ResendMailer;
import com.eb.inscription.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Servlet d'inscription SANS FRAMEWORK Spring Boot
 * Utilise uniquement les Servlets Java standard (jakarta.servlet)
 * Enregistré manuellement via ServletConfig (pas d'annotation @WebServlet)
 */
public class RegisterServlet extends HttpServlet {
    
    private UserDAO userDAO;
    private EmailVerificationCodeDAO codeDAO;
    private ResendMailer resendMailer;
    
    @Override
    public void init() throws ServletException {
        super.init();
        // Initialisation des DAO avec JDBC pur (pas de Spring DI)
        // Utiliser getInitParameter() pour les init-param du Servlet (compatible avec ServletRegistrationBean)
        // Fallback sur getServletContext().getInitParameter() pour compatibilité avec web.xml
        String dbUrl = getInitParameter("db.url");
        if (dbUrl == null) dbUrl = getServletContext().getInitParameter("db.url");
        
        String dbUser = getInitParameter("db.username");
        if (dbUser == null) dbUser = getServletContext().getInitParameter("db.username");
        
        String dbPassword = getInitParameter("db.password");
        if (dbPassword == null) dbPassword = getServletContext().getInitParameter("db.password");
        
        this.userDAO = new UserDAO(dbUrl, dbUser, dbPassword);
        this.codeDAO = new EmailVerificationCodeDAO(dbUrl, dbUser, dbPassword);
        
        // Initialisation du service Resend (sans framework)
        String resendApiKey = getInitParameter("resend.api.key");
        if (resendApiKey == null) resendApiKey = getServletContext().getInitParameter("resend.api.key");
        // Fallback sur variable d'environnement système
        if (resendApiKey == null || resendApiKey.isEmpty()) {
            resendApiKey = System.getenv("RESEND_API_KEY");
        }
        
        String resendFromEmail = getInitParameter("resend.from.email");
        if (resendFromEmail == null) resendFromEmail = getServletContext().getInitParameter("resend.from.email");
        // Fallback sur variable d'environnement système
        if (resendFromEmail == null || resendFromEmail.isEmpty()) {
            resendFromEmail = System.getenv("RESEND_FROM_EMAIL");
        }
        if (resendFromEmail == null || resendFromEmail.isEmpty()) {
            resendFromEmail = "onboarding@resend.dev";
        }
        
        this.resendMailer = new ResendMailer(resendApiKey, resendFromEmail);
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
        String dateOfBirthStr = request.getParameter("dateOfBirth");
        String address = request.getParameter("address");
        String postalCode = request.getParameter("postalCode");
        // Gérer le cas "Autre" pour la ville
        String city = request.getParameter("city");
        if (city == null || city.isEmpty()) {
            city = request.getParameter("cityOther");
        }
        
        // Validation manuelle (pas de @Valid)
        if (email == null || email.trim().isEmpty() || 
            password == null || password.trim().isEmpty() ||
            firstName == null || firstName.trim().isEmpty() ||
            lastName == null || lastName.trim().isEmpty() ||
            phone == null || phone.trim().isEmpty() ||
            dateOfBirthStr == null || dateOfBirthStr.trim().isEmpty() ||
            address == null || address.trim().isEmpty() ||
            postalCode == null || postalCode.trim().isEmpty() ||
            city == null || city.trim().isEmpty()) {
            
            request.setAttribute("error", "Tous les champs obligatoires doivent être remplis");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            return;
        }
        
        // Validation et parsing de la date de naissance
        LocalDate dateOfBirth = null;
        try {
            dateOfBirth = LocalDate.parse(dateOfBirthStr);
            // Vérifier que la date n'est pas dans le futur
            if (dateOfBirth.isAfter(LocalDate.now())) {
                request.setAttribute("error", "La date de naissance ne peut pas être dans le futur");
                request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
                return;
            }
            // Vérifier que l'utilisateur a au moins 18 ans (optionnel, à adapter selon vos besoins)
            if (dateOfBirth.isAfter(LocalDate.now().minusYears(18))) {
                request.setAttribute("error", "Vous devez avoir au moins 18 ans pour vous inscrire");
                request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
                return;
            }
        } catch (DateTimeParseException e) {
            request.setAttribute("error", "Format de date de naissance invalide");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            return;
        }
        
        // Validation de la confirmation du mot de passe
        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Les mots de passe ne correspondent pas");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            return;
        }
        
        // Validation du mot de passe (doit correspondre à la validation côté client)
        if (password.length() < 8) {
            request.setAttribute("error", "Le mot de passe doit contenir au moins 8 caractères");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            return;
        }
        // Vérifier qu'il contient au moins une majuscule, une minuscule et un chiffre
        if (!password.matches(".*[A-Z].*")) {
            request.setAttribute("error", "Le mot de passe doit contenir au moins une majuscule");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            return;
        }
        if (!password.matches(".*[a-z].*")) {
            request.setAttribute("error", "Le mot de passe doit contenir au moins une minuscule");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            return;
        }
        if (!password.matches(".*[0-9].*")) {
            request.setAttribute("error", "Le mot de passe doit contenir au moins un chiffre");
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
            user.setPasswordHash(hashedPassword);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPhone(phone);
            user.setDateOfBirth(dateOfBirth);
            user.setAddress(address);
            user.setPostalCode(postalCode);
            user.setCity(city);
            user.setStatus("PENDING"); // Statut initial
            user.setCreatedAt(java.time.LocalDateTime.now());
            
            // Sauvegarder avec JDBC pur (pas de Spring Data JPA)
            userDAO.save(user);
            
            // Vérifier que l'ID a été généré
            if (user.getId() == null) {
                throw new IllegalStateException("L'ID utilisateur n'a pas été généré après l'insertion");
            }
            
            // Générer un code OTP à 6 chiffres
            String code = generateRandomCode();
            // Hasher le code avec BCrypt
            String codeHash = org.mindrot.jbcrypt.BCrypt.hashpw(code, org.mindrot.jbcrypt.BCrypt.gensalt(10));
            // Créer le code de vérification dans la table email_verification_codes
            codeDAO.createCode(user.getId(), codeHash, Instant.now().plus(Duration.ofMinutes(15)));
            
            // Log du code pour les tests
            System.out.println("=== CODE DE VÉRIFICATION POUR " + email + " : " + code + " ===");
            
            // Envoyer l'email avec le code via Resend
            try {
                resendMailer.sendCode(email, code);
            } catch (Exception mailException) {
                System.err.println("Erreur lors de l'envoi de l'email via Resend : " + mailException.getMessage());
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












