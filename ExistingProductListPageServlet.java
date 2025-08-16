package com.agriwastetrade.site;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Servlet implementation class ExistingProductListPageServlet
 */
@WebServlet("/existingproductlistpageservlet")
public class ExistingProductListPageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ExistingProductListPageServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String userid = (String) request.getSession(false).getAttribute("userid");
		if (userid != null){
			try(Connection conn = DatabaseConnectionPool.getConnectionPool()){
				PreparedStatement pstmt = conn.prepareStatement("select farmercropjunction.cropid,categoryid,noofunitsavailable,priceperunit,totalworth from farmercropjunction join inventorydetails where farmercropjunction.cropid = inventorydetails.cropid and farmercropjunction.farmerid = ?");
				pstmt.setString(1, userid);
				ResultSet rs = pstmt.executeQuery();
				ResultSet rs1 = conn.createStatement().executeQuery("select count(*) from farmercropjunction where farmerid = '"+userid+"'");
				rs1.next();
				//int noofrows = rs.getFetchSize();
				
				if(rs.next()) {
					JSONArray jsonarray = new JSONArray();
					JSONObject[] jsonobject = new JSONObject[rs1.getInt("count(*)")];
					int i = 0;
					do {
						jsonobject[i] = new JSONObject();
						jsonobject[i].put("cropid", rs.getString("cropid"));
						//imagebytes = rs.getBytes("productimage");
						//response.setContentType("images/*");
						//response.setContentLength(imagebytes.length);
						jsonobject[i].put("productimage", "imagedisplayhelperservlet?purpose=productimage&farmerid="+userid+"&cropid="+rs.getString("cropid"));//response.getOutputStream().write(imagebytes));
						
						jsonobject[i].put("category", rs.getString("categoryid"));
						jsonobject[i].put("noofunitsavailable", rs.getInt("noofunitsavailable"));
						jsonobject[i].put("priceperunit", rs.getInt("priceperunit"));
						jsonobject[i].put("totalworth", rs.getInt("totalworth"));
						
						jsonarray.put(jsonobject[i]);
						i+=1;
						//jsonobject.append(userid, jsonobject);
						
					}while(rs.next());
					
					response.setContentType("application/json");
					response.getWriter().print(jsonarray);
				}else {
					JSONArray jsonarray = new JSONArray();
					response.setContentType("application/json");
					response.getWriter().print(jsonarray);
				}
				
				
			}catch(SQLException e) {
				e.printStackTrace();
				//response.sendRedirect("errorpage.html?error=Database connection error");
				return;
			}
			
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
