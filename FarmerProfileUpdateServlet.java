package com.agriwastetrade.site;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.JSONObject;

/**
 * Servlet implementation class FarmerProfileUpdateServlet
 */
@WebServlet("/farmerprofileupdateservlet")

@MultipartConfig(
		//	fileSizeThreshold = 1024 * 1024 * 10, // 10 MB
			maxFileSize = 1024 * 1024 * 50 // 50 MB
		//	maxRequestSize = 1024 * 1024 * 100 // 100 MB
	)
public class FarmerProfileUpdateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FarmerProfileUpdateServlet() {
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
		
		//String userid = (String) request.getSession(false).getAttribute("userid");
		HttpSession session = request.getSession(false);
		
		System.out.println("Session ID: " + (session != null ? session.getId() : "null"));
		System.out.println("farmer profile update servlet");
		
		if(session == null) {
			
			System.out.println("Session is null, redirecting to login page.");
			response.sendRedirect("loginpage.html?error=Session expired. Please log in again.");
			
		}else {
			
			Collection<Part> parts = request.getParts();
			//request.getPart("");
			ArrayList<String> names = new ArrayList<>();
			
			for(Part p : parts) {
				System.out.println("p");
				System.out.println(p.getName());
				names.add(p.getName());
				//System.out.println(p.getInputStream());
			}
			
			//String stmt = createInsertStatement(names);
			String stmt = "update farmerdetails set ";
	    	for(String s : names) {
	    		stmt = stmt.concat(","+s+" = ?");
	    	}
	    	System.out.println(stmt);
			stmt = stmt.replaceFirst(",","");
			stmt = stmt.concat(" where farmerid = ?");
			System.out.println(stmt);
			
			System.out.println(stmt);
			
			String farmerid = (String) session.getAttribute("userid");

			
			System.out.println("Entered else in database update of farmer profileicture");
			
			try(Connection conn = DatabaseConnectionPool.getConnectionPool()){
				
				
				System.out.println("Entering try database update for farmer profile.");

				
				PreparedStatement pstmt = conn.prepareStatement(stmt);
				int i = 1;
				for(String s : names) {
					if(s != "profilepicture") {
						pstmt.setBlob(i, request.getPart(s).getInputStream());
					}else if(s != "phone"){
						pstmt.setLong(i,Long.parseLong(request.getParameter(s)));
					}else {
						pstmt.setString(i, request.getParameter(s));
					}
					i++;
				}
				pstmt.setString(i, farmerid);
				
				int effectedrows = pstmt.executeUpdate();
				
				JSONObject json = new JSONObject();
				if(effectedrows == 1) {
					json.put("status", "success");
					System.out.println("Profile updated successfully for farmer ID: " + farmerid);
				}else if(effectedrows == 0) {
					json.put("status", "failure");
					System.out.println("No changes made to the profile for farmer ID: " + farmerid);
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
