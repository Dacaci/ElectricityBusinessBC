package com.eb.signup.auth;

import com.eb.signup.user.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet(name="DashboardServlet", urlPatterns={"/dashboard"})
public class DashboardServlet extends HttpServlet {

  @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    // Vérifier si l'utilisateur est connecté
    HttpSession session = req.getSession(false);
    if (session == null || session.getAttribute("user") == null) {
      resp.sendRedirect("login");
      return;
    }
    
    // Récupérer les informations de l'utilisateur
    User user = (User) session.getAttribute("user");
    req.setAttribute("user", user);
    req.setAttribute("userEmail", user.getEmail());
    req.setAttribute("userId", user.getId());
    
    req.getRequestDispatcher("/dashboard.jsp").forward(req, resp);
  }

  @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    // Rediriger vers GET pour éviter les soumissions de formulaire
    doGet(req, resp);
  }
}
