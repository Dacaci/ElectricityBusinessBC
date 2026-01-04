package com.eb.inscription.servlet;

import com.eb.inscription.dao.EmailVerificationCodeDAO;
import com.eb.inscription.dao.UserDAO;
import com.eb.inscription.mail.Mailer;
import com.eb.inscription.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class RegisterServlet extends HttpServlet {
    
    private static final String VIEW_REGISTER = "/WEB-INF/views/register.jsp";
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MIN_AGE = 18;
    private static final int OTP_EXPIRATION_MINUTES = 15;
    
    private UserDAO userDAO;
    private EmailVerificationCodeDAO codeDAO;
    private Mailer mailer;
    
    @Override
    public void init() throws ServletException {
        super.init();
        
        String dbUrl = getInitParameter("db.url");
        String dbUser = getInitParameter("db.username");
        String dbPassword = getInitParameter("db.password");
        
        this.userDAO = new UserDAO(dbUrl, dbUser, dbPassword);
        this.codeDAO = new EmailVerificationCodeDAO(dbUrl, dbUser, dbPassword);
        
        // Configuration SMTP (Brevo ou autre service)
        String smtpHost = System.getenv("MAIL_SMTP_HOST");
        String smtpPortStr = System.getenv("MAIL_SMTP_PORT");
        String smtpUser = System.getenv("MAIL_SMTP_USER");
        String smtpPassword = System.getenv("MAIL_SMTP_PASSWORD");
        String mailFrom = System.getenv("MAIL_FROM");
        
        int smtpPort = 587; // Port par défaut
        if (smtpPortStr != null && !smtpPortStr.isEmpty()) {
            try {
                smtpPort = Integer.parseInt(smtpPortStr);
            } catch (NumberFormatException e) {
                smtpPort = 587;
            }
        }
        
        if (mailFrom == null || mailFrom.isEmpty()) {
            mailFrom = "noreply@localhost";
        }
        
        // Créer le Mailer SMTP
        if (smtpHost != null && !smtpHost.isEmpty()) {
            this.mailer = new Mailer(smtpHost, smtpPort, mailFrom, smtpUser, smtpPassword);
        } else {
            // Fallback : Mailer sans authentification (pour développement local)
            this.mailer = new Mailer("localhost", 1025, mailFrom);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher(VIEW_REGISTER).forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String phone = request.getParameter("phone");
        String dateOfBirthStr = request.getParameter("dateOfBirth");
        String address = request.getParameter("address");
        String postalCode = request.getParameter("postalCode");
        String city = request.getParameter("city");
        if (city == null || city.isEmpty()) {
            city = request.getParameter("cityOther");
        }
        
        String validationError = validateForm(email, password, confirmPassword, firstName, lastName, 
                                              phone, dateOfBirthStr, address, postalCode, city);
        if (validationError != null) {
            forwardWithError(request, response, validationError);
            return;
        }
        
        LocalDate dateOfBirth = parseDateOfBirth(dateOfBirthStr);
        if (dateOfBirth == null) {
            forwardWithError(request, response, "Format de date de naissance invalide");
            return;
        }
        
        if (!isValidAge(dateOfBirth)) {
            forwardWithError(request, response, "Vous devez avoir au moins " + MIN_AGE + " ans pour vous inscrire");
            return;
        }
        
        try {
            if (userDAO.existsByEmail(email)) {
                forwardWithError(request, response, "Cet email est déjà utilisé");
                return;
            }
            
            User user = createUser(email, password, firstName, lastName, phone, dateOfBirth, address, postalCode, city);
            userDAO.save(user);
            
            if (user.getId() == null) {
                throw new IllegalStateException("L'ID utilisateur n'a pas été généré après l'insertion");
            }
            
            String code = generateRandomCode();
            String codeHash = BCrypt.hashpw(code, BCrypt.gensalt(10));
            codeDAO.createCode(user.getId(), codeHash, Instant.now().plus(Duration.ofMinutes(OTP_EXPIRATION_MINUTES)));
            
            System.out.println("=== CODE DE VÉRIFICATION POUR " + email + " : " + code + " ===");
            
            try {
                mailer.sendCode(email, code);
            } catch (Exception e) {
                System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
            }
            
            response.sendRedirect(request.getContextPath() + "/verify-servlet?email=" + email);
            
        } catch (Exception e) {
            e.printStackTrace();
            forwardWithError(request, response, "Erreur lors de l'inscription: " + e.getMessage());
        }
    }
    
    private String validateForm(String email, String password, String confirmPassword, 
                                String firstName, String lastName, String phone, 
                                String dateOfBirthStr, String address, String postalCode, String city) {
        if (isEmpty(email) || isEmpty(password) || isEmpty(confirmPassword) || 
            isEmpty(firstName) || isEmpty(lastName) || isEmpty(phone) || 
            isEmpty(dateOfBirthStr) || isEmpty(address) || isEmpty(postalCode) || isEmpty(city)) {
            return "Tous les champs obligatoires doivent être remplis";
        }
        
        if (!password.equals(confirmPassword)) {
            return "Les mots de passe ne correspondent pas";
        }
        
        String passwordError = validatePassword(password);
        if (passwordError != null) {
            return passwordError;
        }
        
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return "Format d'email invalide";
        }
        
        return null;
    }
    
    private String validatePassword(String password) {
        if (password.length() < MIN_PASSWORD_LENGTH) {
            return "Le mot de passe doit contenir au moins " + MIN_PASSWORD_LENGTH + " caractères";
        }
        if (!password.matches(".*[A-Z].*")) {
            return "Le mot de passe doit contenir au moins une majuscule";
        }
        if (!password.matches(".*[a-z].*")) {
            return "Le mot de passe doit contenir au moins une minuscule";
        }
        if (!password.matches(".*[0-9].*")) {
            return "Le mot de passe doit contenir au moins un chiffre";
        }
        return null;
    }
    
    private LocalDate parseDateOfBirth(String dateStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr);
            if (date.isAfter(LocalDate.now())) {
                return null;
            }
            return date;
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    
    private boolean isValidAge(LocalDate dateOfBirth) {
        return !dateOfBirth.isAfter(LocalDate.now().minusYears(MIN_AGE));
    }
    
    private User createUser(String email, String password, String firstName, String lastName, 
                           String phone, LocalDate dateOfBirth, String address, 
                           String postalCode, String city) {
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt(10)));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhone(phone);
        user.setDateOfBirth(dateOfBirth);
        user.setAddress(address);
        user.setPostalCode(postalCode);
        user.setCity(city);
        user.setStatus("PENDING");
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }

    private void forwardWithError(HttpServletRequest request, HttpServletResponse response, String error)
            throws ServletException, IOException {
        request.setAttribute("error", error);
        request.getRequestDispatcher(VIEW_REGISTER).forward(request, response);
    }

    private String generateRandomCode() {
        SecureRandom random = new SecureRandom();
        return String.format("%06d", random.nextInt(1_000_000));
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}












