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
		
		
//		if(request.getSession(false) == null || request.getSession(false).getAttribute("loginstatus") == "died") {
//			response.sendRedirect("loginpage.html?error=Session expired. Please login again to continue.");
//			return;
//		}
		
		HttpSession s = request.getSession(false);// Get existing session from the url as the last servlet encoded the session id in the url - that is login or registration servlet
		if(s != null) {
			if(((String) s.getAttribute("userstatus")) == "registered") {
				response.sendRedirect("loginpage.html?error=You are already registered. Please login to continue.");
				return;
			}
		}else if(s == null) {
			System.out.println("session is null in newsellerregistrationserlet");
		}
		
		
		try(Connection conn = DatabaseConnectionPool.getConnectionPool()) {
			
//			if(request.getSession(false)!=null) {
//				request.getSession(false).invalidate();
//			}else {
//				System.out.println("No existing session found for new seller registration.");
//			}
			
			HttpSession session = request.getSession(true);//as the user is registering for the first time, create a new session if it does not exist
			
			String userid = request.getParameter("username");
			String password = request.getParameter("password");
			String email = request.getParameter("email");
			String fullname = request.getParameter("name");
			String phone = request.getParameter("phoneno");
			String address = request.getParameter("location");
			
			PreparedStatement checkUserExists = conn.prepareStatement("SELECT * FROM logincredentials WHERE userid = ? and role = 'seller'");
			checkUserExists.setString(1, userid);
			
			if(checkUserExists.executeQuery().next()) {
				response.sendRedirect("loginpage.html?error=Username already exists. Please choose a different username --><a href='NewSellerRegistrationPage.html'>Register here..</a><br>"
						+ " or just login with same user name --> <a href= 'loginpage.html'>login here....</a>");
				System.out.println("userexisted");
				return;
			}else {
				
				System.out.println("User does not exist, proceeding with registration.");
				
				PreparedStatement pstmt = conn.prepareStatement("insert into temporaryregistrationfarmer(regtoken,fullname, farmerid, phone, emailid, address , farmerpassword) values(?,?,?,?,?,?,?)");
				
				String regtoken = session.getId() + System.currentTimeMillis();
				
				pstmt.setString(1,regtoken);
				pstmt.setString(2, fullname);
				pstmt.setString(3, userid);
				pstmt.setString(4, phone);
				pstmt.setString(5, email);
				pstmt.setString(6, address);
				pstmt.setString(7, password);
				
				int effectedrows = pstmt.executeUpdate();
				
				if(effectedrows > 0) {
					session.setAttribute("regtoken", regtoken);
					response.encodeRedirectURL(session.getId());
					response.sendRedirect("emailservlet?purpose=tosendtheverificationcode&email="+email+"&from=sellerregistrationpage");//remember to put the parameters without upperquotes and spaces between the parameters
				}else {
					
				}
			}
			conn.close();//it completes the in progress transactions and releases the database resources associated with this Connection object
			//conn.abort(null);//it will terminate the existing transactions and then releases the database resources associated with this connection object
		}catch (Exception e) {
			e.printStackTrace();
			response.sendRedirect("NewSellerRegistrationPage.html?error=Registration failed. Please try again.");
			return;
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
