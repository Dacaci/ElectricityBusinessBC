package com.eb.signup.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet(name="LogoutServlet", urlPatterns={"/logout"})
public class LogoutServlet extends HttpServlet {

  @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    // Invalider la session
    HttpSession session = req.getSession(false);
    if (session != null) {
      session.invalidate();
    }
    
    // Rediriger vers la carte (accessible sans authentification)
    resp.sendRedirect("map");
  }

  @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    // Rediriger vers GET pour éviter les soumissions de formulaire
    doGet(req, resp);
  }
}
