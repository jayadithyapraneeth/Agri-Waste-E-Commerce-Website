package com.agriwastetrade.site;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Servlet implementation class NewProductRegistrationServlet
 */
@MultipartConfig(
	//	fileSizeThreshold = 1024 * 1024 * 10, // 10 MB
		maxFileSize = 1024 * 1024 * 50 // 50 MB
	//	maxRequestSize = 1024 * 1024 * 100 // 100 MB
)
@WebServlet("/newproductregistrationdatabaseupdateservlet")
public class NewProductRegistrationDatabaseUpdateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public NewProductRegistrationDatabaseUpdateServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		
		
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		
		int result =0;
		
		HttpSession session = request.getSession(false);
		String  farmerid = "";
		if(session != null) {
			//if(request.getSession(false).getId == request.getSession(false).getParameter)
			// there is no need of checking the session id of the present session when the present session is not null
			// you just have to do your work
			farmerid = (String) session.getAttribute("userid");
		}else {
			// if the present session is null, then you have to create a new session and send its id to the next session
			session = request.getSession(true);
		}
		
		try(Connection conn = DatabaseConnectionPool.getConnectionPool()){//DriverManager.getConnection("jdbc:mysql://localhost/agriwasteecommerceplatform","jayadithyapraneeth","0000")){
			PrintWriter pw = response.getWriter();
			PreparedStatement pstmt1 = conn.prepareStatement("select cropid from farmercropjunction where farmerid = ? and cropid = ?");
			pstmt1.setString(1, farmerid);
			pstmt1.setString(2, request.getParameter("cropwastename"));
			ResultSet rs1 = pstmt1.executeQuery();
			
			if(rs1.next()) {
				// if the farmerid is already present in the farmercropjunction table, then you have to update the product
				// instead of inserting a new product
				pw.println("<h1>Product Already Registered</h1>");
				pw.println("<a href='ExistingProductsPage.html'>Please Go to Product List and Update the quantity</a>");
				return;
			}else {
				PreparedStatement pstmt = conn.prepareStatement("insert into farmercropjunction(cropid,farmerid,noofunitsavailable,priceperunit,productimage,productdescription) values(?,?,?,?,?,?)");
				pstmt.setString(1, request.getParameter("cropwastename"));
				pstmt.setString(2, farmerid);
				pstmt.setInt(3,Integer.parseInt(request.getParameter("noofunitsregistering")));
				pstmt.setInt(4,Integer.parseInt(request.getParameter("priceperunit")));
				pstmt.setBlob(5, request.getPart("productimage").getInputStream());
				pstmt.setString(6, request.getParameter("productdescription"));
				//Part filepart = request.getPart("productimage");
				//InputStream fis = filepart.getInputStream();
				//pstmt.setBlob(5, fis);
				
				result = pstmt.executeUpdate();
				
				if(result == 1) {
					pw.println("<h1>Product Registered Successfully</h1>");
					pw.println("<a href='ExistingProductPage'>Go to Product List</a>");
				}else {
					pw.println("<h1>Product Registration Failed</h1>");
					pw.println("<a href='#'>Go to Farmer Crop Junction</a>");
				}
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			
		}
	}

}
