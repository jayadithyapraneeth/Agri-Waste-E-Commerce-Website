package com.agriwastetrade.site;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import org.json.JSONObject;

import com.google.gson.Gson;

/**
 * Servlet implementation class BuyerProfileUpdateServlet
 */
@WebServlet("/buyerprofileupdateservlet")
public class BuyerProfileUpdateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
	
	private static class JSON{
		public String industryname;
		public String industrytype;
		public String email;
		public long phone;
		public String alternatephone;//temporarily this field is not being used as buyerdetails table doesn't have this column currently
		public String gstin;
		public String address; 
	}
	
    public BuyerProfileUpdateServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		if(request.getSession(false) == null || request.getSession(false).getAttribute("loginstatus") == "died") {
			response.sendRedirect("loginpage.html?error=Session expired. Please login again to continue.");
			return;
		}
		
		//String userid = (String) request.getSession(false).getAttribute("userid");
		HttpSession session = request.getSession(false);// Get existing session from the url as the last servlet encoded the session id in the url - that is login or registration servlet
		
		System.out.println("Session ID: " + (session != null ? session.getId() : "null"));
		System.out.println("buyer profile update servlet");
		
		if(session == null) {//redirecting to the login page we can't retrieve the required details without the previous session
			
			System.out.println("Session is null, redirecting to login page.");
			response.sendRedirect("loginpage.html?error=Session expired. Please log in again.");
			
		}else {
			
//			Collection<Part> parts = request.getParts();
//			//request.getPart("");
//			ArrayList<String> names = new ArrayList<>();
//			
//			for(Part p : parts) {
//				System.out.println("p");
//				System.out.println(p.getName());//fetching names or keys of all parts
//				names.add(p.getName());
//				//System.out.println(p.getInputStream());
//			}
			
			
			Gson gson = new Gson();
			JSON jsonpojo = gson.fromJson(request.getReader(), JSON.class);//here the JSON is just a POJO class the request.getReader() will fetch and assign the values from jsonstring to the fields in the POJO class
			
			//String stmt = createInsertStatement(names);
			String stmt = "update buyerdetails set industryname = ?, industrytype = ?, emailid = ?, contactno = ?, industryaddress = ? where buyerid = ?";
			//as the alternatephone, gstin and profilepicture are not present in buyerdetailsl structures currently, these fields are exempted from insertion
			
			
		    System.out.println(stmt);
			
			
			String buyerid = (String) session.getAttribute("userid");

			
			System.out.println("Entered else in database update of buyer profile");
			
			try (Connection conn = DatabaseConnectionPool.getConnectionPool();
			     PreparedStatement pstmt = conn.prepareStatement(stmt)) {
				pstmt.setString(1, jsonpojo.industryname);
				pstmt.setString(2, jsonpojo.industrytype);
				pstmt.setString(3, jsonpojo.email);
				pstmt.setLong(4, jsonpojo.phone);
				pstmt.setString(5, jsonpojo.address);
				pstmt.setString(6, buyerid);
				
				int effectedrows = pstmt.executeUpdate();
				
				JSONObject json = new JSONObject();
				if(effectedrows == 1) {
					json.put("status", "success");
					System.out.println("Profile updated successfully for buyer ID: " + buyerid);
				}else if(effectedrows == 0) {
					json.put("status", "failure");
					System.out.println("No changes made to the profile for buyer ID: " + buyerid);
				}else {
					System.out.println("Unexpected number of rows affected: " + effectedrows);
				}
				
				response.getWriter().print(json);
				
			}catch(SQLException sqle) {
				sqle.printStackTrace();
				response.getWriter().write("{\"status\":\"error\",\"message\":\"Database connection error.\"}");
				return;
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