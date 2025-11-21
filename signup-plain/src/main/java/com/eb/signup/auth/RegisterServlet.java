package com.eb.signup.auth;

import com.eb.signup.mail.Mailer;
import com.eb.signup.user.UserDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import javax.sql.DataSource;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;

@WebServlet(name="RegisterServlet", urlPatterns={"/register"})
public class RegisterServlet extends HttpServlet {
  private UserDao userDao;
  private EmailVerificationDao codeDao;
  private Mailer mailer;

  @Override public void init() {
    // Créer la DataSource directement
    var cfg = new com.zaxxer.hikari.HikariConfig();
    cfg.setJdbcUrl(getDbUrl());
    cfg.setUsername(envOr("DB_USER", "eb"));
    cfg.setPassword(envOr("DB_PASS", "eb"));
    cfg.setDriverClassName("org.postgresql.Driver");
    cfg.setMaximumPoolSize(5);
    DataSource ds = new com.zaxxer.hikari.HikariDataSource(cfg);
    
    userDao = new UserDao(ds);
    codeDao = new EmailVerificationDao(ds);
    String host = envOr("MAIL_SMTP_HOST", "localhost");
    int port = Integer.parseInt(envOr("MAIL_SMTP_PORT", "1025"));
    String from = envOr("MAIL_FROM", "no-reply@eb.local");
    mailer = new Mailer(host, port, from);
  }

  @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    req.getRequestDispatcher("/register.jsp").forward(req, resp);
  }

  @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    // Récupération des paramètres
    var firstName = req.getParameter("firstName");
    var lastName = req.getParameter("lastName");
    var email = req.getParameter("email");
    var phone = req.getParameter("phone");
    var dateOfBirthStr = req.getParameter("dateOfBirth");
    var address = req.getParameter("address");
    var postalCode = req.getParameter("postalCode");
    var city = req.getParameter("city");
    var password = req.getParameter("password");
    var confirmPassword = req.getParameter("confirmPassword");
    
    // Validation des champs obligatoires
    if (firstName == null || firstName.trim().isEmpty() ||
        lastName == null || lastName.trim().isEmpty() ||
        email == null || email.trim().isEmpty() ||
        password == null || password.length() < 8) {
      req.setAttribute("error", "Les champs marqués d'un * sont obligatoires et le mot de passe doit contenir au moins 8 caractères");
      req.getRequestDispatcher("/register.jsp").forward(req, resp);
      return;
    }
    
    // Validation de la confirmation du mot de passe
    if (!password.equals(confirmPassword)) {
      req.setAttribute("error", "Les mots de passe ne correspondent pas");
      req.getRequestDispatcher("/register.jsp").forward(req, resp);
      return;
    }
    
    // Validation de l'email
    if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
      req.setAttribute("error", "Format d'email invalide");
      req.getRequestDispatcher("/register.jsp").forward(req, resp);
      return;
    }
    
    try {
      var existing = userDao.findByEmail(email);
      if (existing != null) { 
        req.setAttribute("error", "Cet email est déjà utilisé");
        req.getRequestDispatcher("/register.jsp").forward(req, resp);
        return; 
      }

      // Conversion de la date de naissance
      java.time.LocalDate dateOfBirth = null;
      if (dateOfBirthStr != null && !dateOfBirthStr.trim().isEmpty()) {
        try {
          dateOfBirth = java.time.LocalDate.parse(dateOfBirthStr);
        } catch (Exception e) {
          req.setAttribute("error", "Format de date invalide");
          req.getRequestDispatcher("/register.jsp").forward(req, resp);
          return;
        }
      }

      var hash = org.mindrot.jbcrypt.BCrypt.hashpw(password, org.mindrot.jbcrypt.BCrypt.gensalt(10));
      long userId = userDao.create(firstName.trim(), lastName.trim(), email.trim(), 
                                  phone != null ? phone.trim() : null, dateOfBirth,
                                  address != null ? address.trim() : null,
                                  postalCode != null ? postalCode.trim() : null,
                                  city != null ? city.trim() : null, hash);

      String code = random6();
      String codeHash = org.mindrot.jbcrypt.BCrypt.hashpw(code, org.mindrot.jbcrypt.BCrypt.gensalt(10));
      codeDao.createCode(userId, codeHash, Instant.now().plus(Duration.ofMinutes(15)));

      // Log du code pour les tests
      System.out.println("=== CODE DE VÉRIFICATION POUR " + email + " : " + code + " ===");
      
      mailer.sendCode(email, code);
      
      // Rediriger vers la page de vérification
      resp.sendRedirect("verify");
    } catch (Exception e) {
      req.setAttribute("error", "Erreur lors de la création du compte : " + e.getMessage());
      req.getRequestDispatcher("/register.jsp").forward(req, resp);
    }
  }

  private static String random6() {
    var r = new SecureRandom(); return String.format("%06d", r.nextInt(1_000_000));
  }
  
  private static String envOr(String key, String defVal) {
    var v = System.getenv(key);
    return (v == null || v.isBlank()) ? defVal : v;
  }
  
  private static String getDbUrl() {
    // Utiliser DB_URL directement s'il existe
    String dbUrl = System.getenv("DB_URL");
    if (dbUrl != null && !dbUrl.isBlank()) {
      // S'assurer que l'URL commence par jdbc: si nécessaire
      if (!dbUrl.startsWith("jdbc:")) {
        return "jdbc:" + dbUrl;
      }
      return dbUrl;
    }
    // Sinon construire depuis les variables séparées
    String host = envOr("DB_HOST", "172.17.0.1");
    String port = envOr("DB_PORT", "5432");
    String name = envOr("DB_NAME", "eb");
    return String.format("jdbc:postgresql://%s:%s/%s", host, port, name);
  }
}
