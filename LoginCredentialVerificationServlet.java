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


@WebServlet("/logincredentialverificationpage")
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
                
                    if (rs.next()) {
                    	PreparedStatement pstmt1 = conn.prepareStatement("SELECT fullname FROM farmerdetails WHERE farmerid = ?");
                    	pstmt1.setString(1, username);
                    	ResultSet rs1 = pstmt1.executeQuery();
                    	
                        // Create session and store username
                        HttpSession session = request.getSession();//as it is the first time the user is logging in, a new session will be created if it does not already exist
                        session.setAttribute("userid", username);
                        session.setAttribute("role", role);
                        if(rs1.next()) {
							session.setAttribute("currentusername", rs1.getString("fullname"));
                        }else {
                        	System.out.println("No fullname found for user: " + username);
                        }
                        session.setMaxInactiveInterval(30 * 60); // 30 minutes
                        response.encodeRedirectURL(session.getId());//I am maintaining the same session by appending url with the session ID
                        
                        
                        // Redirect based on role
                        if ("seller".equals(role)) {
                            response.sendRedirect("SellersHomePage.html");
                            System.out.println("redirect to sellers home page");   
                        } else if("buyer".equals(role) ) {
                            response.sendRedirect("BuyersHomePage.html");
                        }else {
                        	return;
                        }
                        
                        
                    } else {
                        response.sendRedirect("loginpage.html?error=Invalid credentials");
                    }
                    
                    rs.close();
                    
                    
        } catch (SQLException e) {
            e.printStackTrace();
          //  response.sendRedirect("loginpage.html?error=Database error");
        }
    }    
}