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
    cfg.setJdbcUrl(envOr("DB_URL", "jdbc:postgresql://172.17.0.1:5432/eb"));
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
    if (email == null || inputCode == null) { resp.sendError(400, "Paramètres manquants"); return; }

    try {
      var u = userDao.findByEmail(email);
      if (u == null) { resp.sendError(404, "Utilisateur introuvable"); return; }

      var record = codeDao.findActiveByUser(u.getId());
      if (record == null) { resp.sendError(410, "Code expiré ou déjà utilisé"); return; }

      // Anti brute-force
      if (record.getAttemptCount() != null && record.getAttemptCount() >= 5) {
        resp.sendError(429, "Trop d'essais"); return;
      }

      // Incrémente d'abord
      codeDao.incrementAttempt(record.getId());

      boolean ok = org.mindrot.jbcrypt.BCrypt.checkpw(inputCode, record.getCodeHash());
      if (!ok) { resp.sendError(401, "Code invalide"); return; }

      codeDao.markUsed(record.getId());
      userDao.activate(u.getId());
      resp.setContentType("text/plain; charset=UTF-8");
      resp.getWriter().write(" Compte activé ! Vous pouvez vous connecter (dans l'app principale).");
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }
  
  private static String envOr(String key, String defVal) {
    var v = System.getenv(key);
    return (v == null || v.isBlank()) ? defVal : v;
  }
}
