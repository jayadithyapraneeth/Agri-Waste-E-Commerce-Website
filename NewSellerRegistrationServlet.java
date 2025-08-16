package com.agriwastetrade.site;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Servlet implementation class NewSellerRegistrationServlet
 */
@WebServlet("/newsellerregistrationservlet")
public class NewSellerRegistrationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public NewSellerRegistrationServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
		response.setDateHeader("Expires", 0); // Proxies.
		
		try(Connection conn = DatabaseConnectionPool.getConnectionPool()) {
			
			HttpSession session = request.getSession(false);
			
			String userid = request.getParameter("username");
			String password = request.getParameter("password");
			
			PreparedStatement checkUserExists = conn.prepareStatement("SELECT * FROM logincredentials WHERE userid = ? and role = 'seller'");
			checkUserExists.setString(1, userid);
			
			if(checkUserExists.executeQuery().next()) {
				response.sendRedirect("loginpage.html?error=Username already exists. Please choose a different username --><a href='NewSellerRegistrationPage.html'>Register here..</a><br>"
						+ " or just login with same user name --> <a href= 'loginpage.html'>login here....</a>");
				return;
			}else {
				System.out.println("User does not exist, proceeding with registration.");
				
				PreparedStatement pstmt = conn.prepareStatement("INSERT INTO logincredentials (userid, password) VALUES (?, ?)");
				pstmt.setString(1, userid);
				pstmt.setString(2, password);
				
				
					
					PreparedStatement pstmt2 = conn.prepareStatement("INSERT INTO farmerdetails(farmerid,fullname,farmeraddress,phoneno,emailid,dateregistered) VALUES (?,?,?,?,?,now())");
					pstmt2.setString(1, userid);
					pstmt2.setString(2, request.getParameter("name"));
					pstmt2.setString(3, request.getParameter("location"));
					pstmt2.setLong(4, Long.parseLong(request.getParameter("phoneno")));
	                pstmt2.setString(5, request.getParameter("email"));
	                
	                int result2 = pstmt2.executeUpdate();
	                int result1 = pstmt.executeUpdate();
	                if(result2 > 0 && result1 > 0) {
	                	
	                	if(session != null) {
	                		session.setAttribute("userid", userid);
	                    	session.setAttribute("currentusername", request.getParameter("name"));
	                    	response.encodeRedirectURL(session.getId());
	                	}else if(session == null) {//create a new session if it does not exist because the user is logging in for the first time
							session = request.getSession(true);
							session.setAttribute("userid", userid);
	                    	session.setAttribute("currentusername", request.getParameter("name"));
	                    	response.encodeRedirectURL(session.getId());
	                	}
	                	
	                	response.sendRedirect("SellersHomePage.html?message=Registration successful");
	                	
	               }else {
						response.sendRedirect("NewSellerRegistrationPage.html?error=Registration failed. Please try again.");
						return;
			       }
		       		
			}//Registration successful
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
