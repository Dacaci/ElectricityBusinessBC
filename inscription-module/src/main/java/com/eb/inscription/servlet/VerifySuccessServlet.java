package com.eb.inscription.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class VerifySuccessServlet extends HttpServlet {

    private static final String VIEW_VERIFY_SUCCESS = "/WEB-INF/views/verify-success.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher(VIEW_VERIFY_SUCCESS).forward(request, response);
    }
}












