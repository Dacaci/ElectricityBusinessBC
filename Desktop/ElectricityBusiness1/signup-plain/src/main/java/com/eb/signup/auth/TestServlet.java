package com.eb.signup.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet(name="TestServlet", urlPatterns={"/test"})
public class TestServlet extends HttpServlet {
  
  @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    resp.setContentType("text/html; charset=UTF-8");
    resp.getWriter().write("<h1>Test Servlet</h1><p>L'application fonctionne !</p>");
  }
}
