package com.agriwastetrade.site;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet implementation class LogoutServlet
 */
@WebServlet("/logoutservlet")
public class LogoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LogoutServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
		response.setDateHeader("Expires", 0); // Proxies.
		
		if(request.getSession(false) != null) {
			request.getSession(false).setAttribute("loginstatus", "died");
			response.sendRedirect("index.html"); // Redirect to index page// Set login status to died, navigating to previous pages is not possible(but not likely possible)
			request.getSession(false).invalidate(); // Invalidate the session
		}else {
			System.out.println("No active session found during logout.");
			return;
		}
		
		System.out.println("User logged out successfully.");
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		
//		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
//		response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
//		response.setDateHeader("Expires", 0); // Proxies.
//		
//		if(request.getSession(false) != null) {
//			request.getSession(false).setAttribute("loginstatus", "died");
//			response.sendRedirect("login.html"); // Redirect to index page// Set login status to died, navigating to previous pages is not possible(but not likely possible)
//			request.getSession(false).invalidate(); // Invalidate the session
//		}else {
//			System.out.println("No active session found during logout.");
//			return;
//		}
		
	}

}
