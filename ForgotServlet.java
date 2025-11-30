package com.agriwastetrade.site;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.System;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONObject;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
/**
 * Servlet implementation class ForgotServlet
 */
@WebServlet(description = "only the dynamic conetent will be delivered to the page for password or userid", urlPatterns = { "/forgotservlet" })
public class ForgotServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	PrintStream out  = System.out;
	
	private static class JSON{
		//@SerializedName("password")
		 String password;
		//@SerializedName("emailid")
		 String emailid;
		//@SerializedName("userid")
		 String userid;
	}
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ForgotServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		HttpSession session = request.getSession(false);
		
		if(session == null || session.getAttribute("loginstatus") == "died") {
			response.sendRedirect("loginpage.html?error=Session expired. Please login again to continue.");
			return;
		}
		
		String from = request.getParameter("from");// ensuring the request is coming from a valid source like login page
		String role = request.getParameter("role");
		String purpose = request.getParameter("purpose");// either forgotuserid or forgotpassword
		
		out.println("role:"+role);
		
		if(from == null || !from.equals("forgotpassword") && !from.equals("forgotuserid")) {
			System.out.println("Invalid access to forgot servlet -- from parameter missing or incorrect"+from);
			response.sendRedirect("index.html");
			return;
		}else if(purpose == null || (!purpose.equals("forgotuserid") && !purpose.equals("forgotpassword"))) {
			System.out.println("Invalid access to forgot servlet -- purpose parameter missing or incorrect"+purpose);
			response.sendRedirect("index.html");
			return;
		}else {
			System.out.println("Valid access to forgot servlet");
			out.println("Purpose: " + purpose);
			String email = "";
			JSONObject json = new JSONObject();
			
			Connection conn = null;
			try {
				conn = DatabaseConnectionPool.getConnectionPool();//for now this servlet works on database compulsorily, if there is a case that do not need database connection then this variable has no use
			}catch(SQLException sqle) {//as currently the both the sq=witch cases need DB connection I am providing this Connection object as large scaled variable
				sqle.printStackTrace();
			}
			
			switch(purpose) {
			
			case "forgotuserid":
				out.println("case: forgotuserid");
				//request.getRequestDispatcher("forgotuserid.jsp").forward(request, response);
				Gson gson1 = new Gson();//here the JSON is just a POJO class the request.getReader() will fetch and assign the values from jsonstring to the fields in the POJO class
				JSON json1 = gson1.fromJson(request.getReader(), JSON.class);
				out.println(json1.emailid);
				email = json1.emailid;
				String password = json1.password;
				out.println(json1.password);
				
				try{
					//conn = DatabaseConnectionPool.getConnectionPool();
					String stmt = "";
					String count = "";
					
					//ResultSet rs1 = conn.prepareStatement("select farmerid from farmerdetails where emailid='raamu@gmail.com'").executeQuery();
					//rs1.next();
					//ResultSet rs2 = conn.prepareStatement("select password from logincredentials where userid=\'"+rs1.getString("farmerid")+"\'").executeQuery();
					
					if(role.equalsIgnoreCase("buyer")) {
						//stmt = "select count(buyerid) from buyerdetails where emailid = ? and password = ?";
						stmt = "select count(buyerid) from logincredentials as a, (select buyerid,emailid from buyerdetails where emailid = ?) as b where a.userid = b.buyerid and a.password=?";
						count = "count(buyerid)";
					}else if(role.equalsIgnoreCase("seller")) {
						//stmt = "select count(farmerid) from farmerdetails where emailid = ?";
						stmt = "select count(farmerid) from logincredentials as a, (select farmerid,emailid from farmerdetails where emailid = ?) as b where a.userid = b.farmerid and a.password=?"; 
						count = "count(farmerid)";
					}else {
						stmt = null;
						out.println("statement is null: "+role);
					}
					//ofcourse the emailid is not a primary key here, so that, it can be a restriction to use the unique email id for each account 
					PreparedStatement pstmt = conn.prepareStatement(stmt);
					pstmt.setString(1, email);
					pstmt.setString(2, password);
					ResultSet rs = pstmt.executeQuery();//should change the query to get only the unique rows and also to ensure count = 1
					//an OTP will be sent to the email id if the email id and password match
					
					if(rs.next()) { // if email id and password match and the industryid is found
						if(rs.getInt(count) == 1) {
							json.put("status", "useridfound");
							response.setContentType("application/json");
							response.getWriter().print(json);
							
							out.println("status sent to the page: userid found");
							
							response.encodeRedirectURL(session.getId());
							response.sendRedirect("forgotemailservlet?purpose=senduserid&emailid="+email+"&role="+role);
							
							out.println("focus redirected to the forgotemailservlet");
							
						}else {//if there are multiple accounts with the same email id and password, then we cannot send the userid to the email id
							json.put("status", "useridnotfound");
							response.setContentType("application/json");
							response.getWriter().print(json);
							
							out.println("status sent to the page: userid not found");
						}
						
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
				
				break;
			case "forgotpassword":
				out.println("case: forgotpassword");
				
				Gson gson2 = new Gson();//here the JSON is just a POJO class the request.getReader() will fetch and assign the values to the fields in the POJO class
				BufferedReader br = request.getReader();
				out.println(br.lines());
				JSON json2 = gson2.fromJson(br, JSON.class);
				out.println(json2);
				out.println(json2.toString());
				email = json2.emailid;
				String userid = json2.userid;
				
				out.println("email:"+email);
				out.println("userid:"+userid);
				out.println("password:"+json2.password);
				
				try {
					//conn = DatabaseConnectionPool.getConnectionPool();
					
					String stmt = "";
					String count = "";
					if(role.equalsIgnoreCase("buyer")) {
						stmt = "select count(buyerid) from buyerdetails where buyerid=? and emailid=?";
						count = "count(buyerid)";
					}else if(role.equalsIgnoreCase("seller")) {
						stmt = "select count(farmerid) from farmerdetails where farmerid=? and emailid=?";
						count = "count(farmerid)";
					}else {
						stmt = null;
						out.println("statement is null: "+role);
					}
					
					PreparedStatement pstmt = conn.prepareStatement(stmt);
					pstmt.setString(1,userid);
					pstmt.setString(2, email);
					
					ResultSet rs = pstmt.executeQuery();//here the buyerid is unique so the count is just make sure the email is correct and ensure no data leakage
					
					
					if(rs.next()) {
						if(rs.getInt(count) == 1) {
							json.put("status", "emailmatched");
							response.setContentType("application/json");
							response.getWriter().print(json);
							
							out.println("status sent to the ForgotPasswordPage: email matched");
						}else {
							out.println("count:"+rs.getInt(count));
						}
					}else {
						json.put("status", "emailnotmatched");
						response.setContentType("application/json");
						response.getWriter().print(json);
						
						out.println("status sent to the ForgotPasswordPage: email not matched");	
					}
					
				}catch(Exception e) {
					e.printStackTrace();
				}
				
				
				
				break;
			default:
				System.out.println("Invalid purpose parameter: " + purpose);
				response.sendRedirect("index.html");
				break;
				
			}
			
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
