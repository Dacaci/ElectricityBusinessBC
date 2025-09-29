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
    cfg.setJdbcUrl(envOr("DB_URL", "jdbc:postgresql://172.17.0.1:5432/eb"));
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
    var email = req.getParameter("email");
    var password = req.getParameter("password");
    if (email == null || password == null || password.length() < 8) {
      resp.sendError(400, "Paramètres invalides"); return;
    }
    try {
      var existing = userDao.findByEmail(email);
      if (existing != null) { resp.sendError(409, "Email déjà utilisé"); return; }

      var hash = org.mindrot.jbcrypt.BCrypt.hashpw(password, org.mindrot.jbcrypt.BCrypt.gensalt(10));
      long userId = userDao.create(email, hash);

    String code = random6();
    String codeHash = org.mindrot.jbcrypt.BCrypt.hashpw(code, org.mindrot.jbcrypt.BCrypt.gensalt(10));
    codeDao.createCode(userId, codeHash, Instant.now().plus(Duration.ofMinutes(15)));

    // Log du code pour les tests
    System.out.println("=== CODE DE VÉRIFICATION POUR " + email + " : " + code + " ===");
    
    mailer.sendCode(email, code);
      // Rediriger vers la page de vérification
      resp.sendRedirect("verify");
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  private static String random6() {
    var r = new SecureRandom(); return String.format("%06d", r.nextInt(1_000_000));
  }
  
  private static String envOr(String key, String defVal) {
    var v = System.getenv(key);
    return (v == null || v.isBlank()) ? defVal : v;
  }
}
