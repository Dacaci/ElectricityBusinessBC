package com.eb.signup.auth;

import com.eb.signup.user.UserDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import javax.sql.DataSource;
import java.io.IOException;

@WebServlet(name="LoginServlet", urlPatterns={"/login"})
public class LoginServlet extends HttpServlet {
  private UserDao userDao;

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
  }

  @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    // Vérifier si l'utilisateur est déjà connecté
    HttpSession session = req.getSession(false);
    if (session != null && session.getAttribute("user") != null) {
      resp.sendRedirect("map");
      return;
    }
    
    req.getRequestDispatcher("/login.jsp").forward(req, resp);
  }

  @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    var email = req.getParameter("email");
    var password = req.getParameter("password");
    
    if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
      req.setAttribute("error", "Email et mot de passe requis");
      req.getRequestDispatcher("/login.jsp").forward(req, resp);
      return;
    }
    
    try {
      var user = userDao.findByEmail(email.trim());
      if (user == null) {
        req.setAttribute("error", "Email ou mot de passe incorrect");
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
        return;
      }
      
      // Vérifier le statut du compte (doit être ACTIVE pour se connecter)
      if (user.getStatus() == null || !"ACTIVE".equals(user.getStatus())) {
        req.setAttribute("error", "Compte non activé. Vérifiez votre email.");
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
        return;
      }
      
      // Vérifier le mot de passe
      boolean passwordValid = org.mindrot.jbcrypt.BCrypt.checkpw(password, user.getPasswordHash());
      if (!passwordValid) {
        req.setAttribute("error", "Email ou mot de passe incorrect");
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
        return;
      }
      
      // Connexion réussie - créer la session
      HttpSession session = req.getSession(true);
      session.setAttribute("user", user);
      session.setAttribute("userId", user.getId());
      session.setAttribute("userEmail", user.getEmail());
      
      // Log de débogage
      System.out.println("=== LOGIN SUCCESSFUL ===");
      System.out.println("User ID: " + user.getId());
      System.out.println("User Email: " + user.getEmail());
      System.out.println("Session ID: " + session.getId());
      System.out.println("userId attribute type: " + user.getId().getClass().getName());
      System.out.println("========================");
      
      // Rediriger vers la carte
      resp.sendRedirect("map");
      
    } catch (Exception e) {
      throw new ServletException("Erreur lors de la connexion", e);
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
    String name = envOr("DB_NAME", "eb"); // Standardisé sur "eb" comme dans docker-compose.yml
    return String.format("jdbc:postgresql://%s:%s/%s", host, port, name);
  }
}
