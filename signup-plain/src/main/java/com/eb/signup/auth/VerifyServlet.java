package com.eb.signup.auth;

import com.eb.signup.user.UserDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import javax.sql.DataSource;
import java.io.IOException;

@WebServlet(name="VerifyServlet", urlPatterns={"/verify"})
public class VerifyServlet extends HttpServlet {
  private UserDao userDao;
  private EmailVerificationDao codeDao;

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
  }

  @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    req.getRequestDispatcher("/verify.jsp").forward(req, resp);
  }

  @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    var email = req.getParameter("email");
    var inputCode = req.getParameter("code");
    
    resp.setContentType("text/html; charset=UTF-8");
    
    if (email == null || inputCode == null) { 
      resp.setStatus(400);
      resp.getWriter().write("Paramètres manquants");
      return; 
    }

    try {
      var u = userDao.findByEmail(email);
      if (u == null) { 
        resp.setStatus(404);
        resp.getWriter().write("Utilisateur introuvable"); 
        return; 
      }

      var record = codeDao.findActiveByUser(u.getId());
      if (record == null) { 
        resp.setStatus(410);
        resp.getWriter().write("Code expiré ou déjà utilisé"); 
        return; 
      }

      // Anti brute-force
      if (record.getAttemptCount() != null && record.getAttemptCount() >= 5) {
        resp.setStatus(429);
        resp.getWriter().write("Trop d'essais"); 
        return;
      }

      // Incrémente d'abord
      codeDao.incrementAttempt(record.getId());

      boolean ok = org.mindrot.jbcrypt.BCrypt.checkpw(inputCode, record.getCodeHash());
      if (!ok) { 
        resp.setStatus(401);
        resp.getWriter().write("Code invalide"); 
        return; 
      }

      codeDao.markUsed(record.getId());
      userDao.activate(u.getId());
      
      // Rediriger vers la page de succès
      resp.sendRedirect("verify-success.jsp");
    } catch (Exception e) {
      e.printStackTrace();
      resp.setStatus(500);
      resp.getWriter().write("Erreur serveur: " + e.getMessage());
    }
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
    String name = envOr("DB_NAME", "electricity_business_avmm");
    return String.format("jdbc:postgresql://%s:%s/%s", host, port, name);
  }
}
