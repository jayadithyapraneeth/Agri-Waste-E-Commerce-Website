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
 * Servlet implementation class NewBuyerRegistraionServlet
 */
@WebServlet("/newbuyerregistrationservlet")
public class NewBuyerRegistraionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public NewBuyerRegistraionServlet() {
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
		
		String industryname = request.getParameter("industryname");
		String industryid = request.getParameter("industryid");
		String industrytype = request.getParameter("industrytype");
		String industryaddress = request.getParameter("industrylocation");
		String industryemail = request.getParameter("emailid");
		String industryphone = request.getParameter("phoneno");
		String Password = request.getParameter("password");
		
		HttpSession session = request.getSession(false);
		if(session == null){
			//as the user is registering for the first time, create a new session if it does not exist
			session = request.getSession(true);
			response.encodeRedirectURL(session.getId());//encoding session id as session is new
		}
		
		try(Connection conn = DatabaseConnectionPool.getConnectionPool()){
			PreparedStatement pstmt = conn.prepareStatement("insert into temporaryregistration(regtoken,industryname, industryid, contactno, emailid, industryaddress , industrytype, industrypassword) values(?,?,?,?,?,?,?,?)");
			
			String regtoken = session.getId() + System.currentTimeMillis();
			Long timestamp = System.currentTimeMillis();
			
			
			pstmt.setString(1, regtoken);
			pstmt.setString(2, industryname);
			pstmt.setString(3, industryid);
			pstmt.setString(4, industryphone);
			pstmt.setString(5, industryemail);
			pstmt.setString(6, industryaddress);
			pstmt.setString(7, industrytype);
			pstmt.setString(8, Password);
			
			int rowsAffected = pstmt.executeUpdate();
			if(rowsAffected > 0) {
				System.out.println("Buyer registration details inserted successfully into temporaryregistration table.");
				session.setAttribute("regtoken", regtoken);
				response.sendRedirect("emailservlet?purpose=tosendtheverificationcode&email="+industryemail);
			}
	    }catch(Exception e) {
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
