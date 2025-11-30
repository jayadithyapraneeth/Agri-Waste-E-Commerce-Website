package com.agriwastetrade.site;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONObject;

/**
 * Servlet implementation class CurrentUserInfoServlet
 */
@WebServlet("/currentuserinfoservlet")
public class CurrentUserInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CurrentUserInfoServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */

	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
         
        // Handle session data requests
        //response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        System.out.println("current user info");
        //response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        //response.setHeader("Pragma", "no-cache");
        //response.setHeader("Expires", "0");
        
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        
        
        HttpSession session = request.getSession(false); // Get existing session from the url as the previous servlet encoded the session id in the url - that is login or registration servlet
        
        if(request.getSession(false) == null || request.getSession(false).getAttribute("loginstatus") == "died") {
			response.sendRedirect("loginpage.html?error=Session expired. Please login again to continue.");
			return;
		}
        
        Connection conn = null;
        try {
        	conn = DatabaseConnectionPool.getConnectionPool();
        }catch(SQLException sqle) {
        	sqle.printStackTrace();
        }
        
        String frompage = request.getParameter("frompage");
        
        if(session!=null) {
        	
        	JSONObject json = new JSONObject();
        	System.out.println("Session exists, Jsonobject created.");
        	
        	switch(frompage) {
        	
        	case "sellershomepage":
        		System.out.println("CurrentUserInfoServlet sellers home page");
        		
        		String username = (String) session.getAttribute("currentusername");//I am not setting the session attribute "currentusername" here because we have set that at the beginning of the session either in the loginpage or in the registrationpage
                System.out.println("Current username: " + username);
                if(username != null) {
                    json.put("currentusername", username);
                    response.setContentType("application/json");
                    response.getWriter().print(json);//.print(json);//write(json.toString());
                }else {
            	// Not logged in
                	System.out.println("User not logged in, session exists but no username found.");
                json.put("error", username);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                PrintWriter out = response.getWriter();
                out.print(json);
                //response.getWriter().write(json.toString());
                }
        		
        		break;
        		
        	case "farmerprofilepage":
        		System.out.println("CurrentUserInfoServlet farmer profile page");
        		
        		try {
        			
        			System.out.println("CurrentUserInfoServlet farmers home page");
        			
        			PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM farmerdetails WHERE farmerid = ?");
            		pstmt.setString(1, (String)session.getAttribute("userid"));
                    //System.out.println("Current username: " + username);
        			ResultSet rs = pstmt.executeQuery();
        			
        			if(rs.next()) {
        				json.put("username", rs.getString("farmerid"));
        				json.put("fullname",rs.getString("fullname"));//session.getAttribute("currentusername")//I have to set the session attribute "currentusername" at the time of login or registration
        				json.put("location", rs.getString("farmeraddress"));
        				json.put("phone", rs.getLong("phoneno"));
        				json.put("email", rs.getString("emailid"));
        				json.put("dateregistered", rs.getDate("dateregistered").toString());
        				System.out.println("dateregistered: "+rs.getDate("dateregistered").toString());
        				System.out.println("dateregistered: "+rs.getDate("dateregistered"));
        				json.put("profilepicture", "imagedisplayhelperservlet?purpose=profilepicture&farmerid="+(String)session.getAttribute("userid"));//response.getOutputStream().write(imagebytes));
                        response.setContentType("application/json");
                        response.getWriter().print(json);//.print(json);//write(json.toString());
        			}else {
        				System.out.println("User not logged in, session exists but no username found.");
                        json.put("error", (String)session.getAttribute("userid"));
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        PrintWriter out = response.getWriter();
                        out.print(json);
        			}
        			
        		}catch(SQLException e) {
					e.printStackTrace();
					//response.sendRedirect("errorpage.html?error=Database connection error");
					return;
				}
        		
        		
        		break;
        		
        	case "buyershomepage":
        		System.out.println("CurrentUserInfoServlet buyers home page");
        		
        		String username1 = (String) session.getAttribute("userid");//I am not setting the session attribute "currentusername" here because we have set that at the beginning of the session either in the loginpage or in the registrationpage
				System.out.println("Current username: " + username1);
        		if(username1 != null) {
        			try{
        				PreparedStatement pstmt = conn.prepareStatement("SELECT industrytype FROM buyerdetails WHERE buyerid = ?");
        				pstmt.setString(1, username1);
        				ResultSet rs = pstmt.executeQuery();
        				if(rs.next()) {
        					json.put("industryname", (String)session.getAttribute("currentusername"));
        					json.put("industrytype", rs.getString("industrytype"));
        					System.out.println("industry name: "+(String)session.getAttribute("currentusername"));
        					System.out.println("industry type: "+rs.getString("industrytype"));
						}
        				
        				PreparedStatement pstmt1 = conn.prepareStatement("SELECT count(orderid) FROM orderhistory WHERE buyerid = ?");//total orders
        				PreparedStatement pstmt2 = conn.prepareStatement("SELECT count(orderid) FROM orderhistory WHERE buyerid = ? and status = 'accepted'");//active orders
        				PreparedStatement pstmt3 = conn.prepareStatement("SELECT count(orderid) FROM orderhistory WHERE buyerid = ? and status = 'placed'");//pending orders
        				
        				pstmt1.setString(1, username1);
        				pstmt2.setString(1, username1);
        				pstmt3.setString(1, username1);
        				ResultSet rs1, rs2, rs3;
        				
        				rs2 = pstmt2.executeQuery();
        				rs3 = pstmt3.executeQuery();
        				
        				if(rs2.next() && rs3.next()) {
        					json.put("activeorders", rs2.getInt("count(orderid)"));
        					json.put("pendingorders", rs3.getInt("count(orderid)"));
        					System.out.println("active orders: "+rs2.getInt("count(orderid)"));
        					System.out.println("pending orders: "+rs3.getInt("count(orderid)"));
        				}
        				
        			   response.setContentType("application/json");
        			   response.getWriter().print(json);
        				
        			}catch(SQLException sqle) {
        				sqle.printStackTrace();
        			}

        		}else {
        			System.out.println("User not logged in, session exists but no username found.");
                    json.put("error", username1);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    PrintWriter out = response.getWriter();
                    out.print(json);
        		}
        		
        		break;
        		
        	case "buyerprofilepage":
        		System.out.println("CurrentUserInfoServlet buyer profile page");
        		
        		try{
        			
        			PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM buyerdetails WHERE buyerid = ?");
            		pstmt.setString(1, (String)session.getAttribute("userid"));
                    //System.out.println("Current username: " + username);
        			ResultSet rs = pstmt.executeQuery();
        			
        			if(rs.next()) {
        				//json.put("buyerid", rs.getString("buyerid"));
        				json.put("industryname",rs.getString("industryname"));
        				json.put("address", rs.getString("industryaddress"));
        				json.put("phone", rs.getLong("contactno"));
        				json.put("email", rs.getString("emailid"));
        				//json.put("dateregistered", rs.getDate("dateregistered").toString());
        				json.put("industrytype", rs.getString("industrytype"));
        				
        				PreparedStatement pstmt1 = conn.prepareStatement("SELECT count(orderid) FROM orderhistory WHERE buyerid = ?");//total orders
        				PreparedStatement pstmt2 = conn.prepareStatement("SELECT count(orderid) FROM orderhistory WHERE buyerid = ? and status = 'accepted'");//active orders
        				PreparedStatement pstmt3 = conn.prepareStatement("SELECT count(orderid) FROM orderhistory WHERE buyerid = ? and status = 'placed'");//pending orders
        				
        				pstmt1.setString(1, rs.getString("buyerid"));
        				pstmt2.setString(1, rs.getString("buyerid"));
        				pstmt3.setString(1, rs.getString("buyerid"));
        				ResultSet rs1, rs2, rs3;
        				
        				rs1 = pstmt1.executeQuery();
        				rs2 = pstmt2.executeQuery();
        				rs3 = pstmt3.executeQuery();
        				
        				if(rs1.next() && rs2.next() && rs3.next()) {
        					json.put("totalorders", rs1.getInt("count(orderid)"));
        					json.put("activeorders", rs2.getInt("count(orderid)"));
        					json.put("pendingdeliveries", rs3.getInt("count(orderid)"));
        					System.out.println("active orders: "+rs2.getInt("count(orderid)"));
        					System.out.println("pending deliveries: "+rs3.getInt("count(orderid)"));
        				}
        				
        				
        				System.out.println("dateregistered: "+rs.getDate("dateregistered").toString());
        				System.out.println("dateregistered: "+rs.getDate("dateregistered"));
        				
                        response.setContentType("application/json");
                        response.getWriter().print(json);//.print(json);//write(json.toString());
        			}else {
        				System.out.println("User not logged in, session exists but no username found.");
                        json.put("error", (String)session.getAttribute("userid"));
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        PrintWriter out = response.getWriter();
                        out.print(json);
        			}
        			
        			rs.close();
        			
        		}catch(SQLException e) {
					e.printStackTrace();
					//response.sendRedirect("errorpage.html?error=Database connection error");
					return;
				}
        		
        		
        		break;
        		
        	
        	}
        	
        	
        	
            //System.out.println("doGet visited back");
            
            //System.out.println("CurrentUserInfoServlet called. Session: " + (session != null ? session.getId() : "null"));
            
                
        	
        }else if(session == null) {
        	System.out.println("Session is null, user not logged in.");
        	response.sendRedirect("loginpage.html");
        }
        
        
        
        
    }

}
