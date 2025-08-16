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
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Servlet implementation class OrderTrackingServletFarmer
 */
@WebServlet("/ordertrackingservletfarmer")
public class OrderTrackingServletFarmer extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public OrderTrackingServletFarmer() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("response/json");
		
		String farmerid = "";
		HttpSession session = request.getSession(false);
		if(session != null) {
			farmerid = (String)session.getAttribute("userid");
		} else {
			session = request.getSession(true);
		}
		
		
		
		try(Connection conn = DatabaseConnectionPool.getConnectionPool()){//DriverManager.getConnection("jdbc:mysql://localhost:3306/agriwasteecommerceplatform", "jayadithyapraneeth", "0000")) {
			
			PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM orderhistory WHERE sellerid = ? order by Date ASC");
			pstmt.setString(1, farmerid);
			ResultSet rs = pstmt.executeQuery();
			ResultSet rs1 = conn.createStatement().executeQuery("select count(*) from orderhistory where sellerid = '"+farmerid+"'");
			rs1.next();
			
			
			PrintWriter pw = response.getWriter();
			
			System.out.println("Order Tracking Page afterresultset");
			
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
			response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
			response.setDateHeader("Expires", 0); // Proxies.
			
			JSONArray jsonarray = new JSONArray();
					if (rs.next()) {
					  
					  JSONObject[] json = new JSONObject[rs1.getInt("count(*)")];
						int i = 0;
					  System.out.println("Order Tracking Page for Farmer: " + farmerid);
						do {
					          //JSONObject json = new JSONObject();
                              json[i]=new JSONObject();
                              
					          json[i].put("orderid", rs.getString("orderid"));
                              json[i].put("cropid", rs.getString("cropid"));
                              json[i].put("noofunitsordered", rs.getInt("noofunits"));
                              json[i].put("toaddress", rs.getString("toaddress"));
                              json[i].put("orderworth", rs.getInt("orderworth"));
                              json[i].put("orderdate", rs.getDate("Date"));
                              json[i].put("orderstatus", rs.getString("status"));
                              json[i].put("paymentstatus", rs.getString("paymentstatus"));
                              json[i].put("delivereddate", rs.getDate("delivereddate"));
                              json[i].put("paymentdate", rs.getDate("paymentdate"));
                              
                              jsonarray.put(json[i]);
      						i+=1;
					    }while(rs.next());
						
						response.getWriter().print(jsonarray);
					}else {
						response.getWriter().print(jsonarray);
					}
					
						
		}catch (SQLException sqle) {
			sqle.printStackTrace();	
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