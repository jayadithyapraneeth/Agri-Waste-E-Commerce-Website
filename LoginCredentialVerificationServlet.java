package com.agriwastetrade.site;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.json.JSONObject;


@WebServlet("/logincredentialverificationservlet")
public class LoginCredentialVerificationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    public void LoginCredentialVerificationServelet() {
    	
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Handle login form submission
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            //response.sendRedirect("loginpage.html?error=Driver error");
            return;
        }

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = request.getParameter("role");

        String url = "jdbc:mysql://localhost:3306/agriwasteecommerceplatform";
        String dbUser = "jayadithyapraneeth";
        String dbPassword = "0000";
        
        try (Connection conn = DatabaseConnectionPool.getConnectionPool()){//DriverManager.getConnection(url, dbUser, dbPassword)) {
            String sql = "SELECT * FROM logincredentials WHERE userid = ? AND password = ? AND role = ?";
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                pstmt.setString(3, role);
                
                ResultSet rs = pstmt.executeQuery();
                
                    if(rs.next()) {//checking if the user exists or not
                    	
                    	switch(role) {
                    	
                    		case "seller":
                    			System.out.println("Seller logged in: " + username);
                    			
                    			PreparedStatement pstmt1 = conn.prepareStatement("SELECT fullname FROM farmerdetails WHERE farmerid = ?");
                    			pstmt1.setString(1, username);
                    			ResultSet rs1 = pstmt1.executeQuery();
	                    	
                    			// Create session and store username
//                    			if(request.getSession(false)!=null) {
//                    				request.getSession(false).invalidate();
//                    			}else {
//                    				System.out.println("No existing session found for user: " + username);
//                    			}
                    			
                    			HttpSession session = request.getSession(true);//as it is the first time the user is logging in(in this session), a new session will be created if it does not already exist
                    			//but by default there will be an existing session that is created once the user's browser sent a request to the web app or website
                    			session.setAttribute("userid", username);
                    			session.setAttribute("role", role);
                    			if(rs1.next()) {
                    				session.setAttribute("currentusername", rs1.getString("fullname"));
                    			}else {
                    				System.out.println("No fullname found for user: " + username);
                    			}
                    			session.setMaxInactiveInterval(30 * 60); // 30 minutes
                    			response.encodeRedirectURL(session.getId());//I am maintaining the same session by appending url with the session ID
	                        
                    			session.setAttribute("loginstatus", "alive");
							
                    			response.sendRedirect("SellersHomePage.html");
                    			System.out.println("redirect to sellers home page");  
                            
                    			rs1.close();
                            
                    			break;
							
                    		case "buyer":
                    			System.out.println("Buyer logged in: " + username);
                    		
                    			PreparedStatement pstmt2 = conn.prepareStatement("SELECT industryname FROM buyerdetails WHERE buyerid = ?");
                        		pstmt2.setString(1, username);
                        		ResultSet rs2 = pstmt2.executeQuery();
                        	
                        		// Create session and store username
                        		if(request.getSession(false)!=null) {
                    				request.getSession(false).invalidate();
                    			}else {
                    				System.out.println("No existing session found for user: " + username);
                    			}
                        		HttpSession session1 = request.getSession(true);//as it is the first time the user is logging in(in this session), a new session will be created if it does not already exist
                        		session1.setAttribute("userid", username);
                        		session1.setAttribute("role", role);
                        		if(rs2.next()) {
                        			session1.setAttribute("currentusername", rs2.getString("industryname"));
                        		}else {
                        			System.out.println("No fullname found for user: " + username);
                        		}
                        		session1.setMaxInactiveInterval(30 * 60); // 30 minutes
                        		response.encodeRedirectURL(session1.getId());//I am maintaining the same session by appending url with the session ID
                            
                        		session1.setAttribute("loginstatus", "alive");
                            
                        		response.sendRedirect("BuyersHomePage.html");
                        		System.out.println("redirect to buyers home page");
                            
                        		rs2.close();
                    		
                        		break;                   	
                    	}
                    } else {
                        response.sendRedirect("loginpage.html?error=Invalid credentials");
                        response.setContentType("text/html");
                        response.getWriter().write("<script>alert('Invalid credentials');window.location='loginpage.html';</script>");
                    }
                    
                    rs.close();
                    
            conn.close();//it completes the in progress transactions and releases the database resources associated with this Connection object
        	//conn.abort(null);//it will terminate the existing transactions and then releases the database resources associated with this connection object      
        } catch (SQLException e) {
            e.printStackTrace();
          //  response.sendRedirect("loginpage.html?error=Database error");
        }
    }    
}