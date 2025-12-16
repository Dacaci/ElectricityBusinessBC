package com.eb.inscription.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet pour la page de succès après vérification
 * SANS FRAMEWORK Spring Boot
 * Enregistré manuellement via ServletConfig (pas d'annotation @WebServlet)
 */
public class VerifySuccessServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.getRequestDispatcher("/WEB-INF/views/verify-success.jsp").forward(request, response);
    }
}












