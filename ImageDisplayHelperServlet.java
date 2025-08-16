package com.agriwastetrade.site;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Servlet implementation class ImageDisplayHelperServlet
 */
@WebServlet("/imagedisplayhelperservlet")
public class ImageDisplayHelperServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ImageDisplayHelperServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		try {
//			Class.forName("com.mysql.cj.jdbc.Driver");
//		}catch(ClassNotFoundException cnfe) {
//			cnfe.printStackTrace();
//		}
		System.out.println("Image display servlet");
		PreparedStatement pstmt;
		ResultSet rs;
		try(Connection conn = DatabaseConnectionPool.getConnectionPool()){//DriverManager.getConnection("jdbc:mysql://localhost:3306/agriwasteecommerceplatform","jayadithyapraneeth","0000")){
			System.out.println("entered try block in image display servlet");
			System.out.println(request.getParameter("purpose"));
			String purpose = request.getParameter("purpose");
			String cropid = request.getParameter("cropid");
			String farmerid = request.getParameter("farmerid");
			System.out.println("farmerid: "+farmerid+" cropid: "+cropid);
			
			switch(purpose) {
			case "productimage" :
				System.out.println("switch case productimage");
				pstmt = conn.prepareStatement("select productimage from farmercropjunction where farmerid = ? and cropid = ?");
				pstmt.setString(1, farmerid);
				pstmt.setString(2, cropid);
				rs = pstmt.executeQuery();
				System.out.println("result size: "+rs.getFetchSize());
				
				try {
					File f = new File ("C://Users//PRANEETH//Desktop//New folder//newfile.jpeg");
					FileOutputStream fos = new FileOutputStream(f);
					System.out.println("Entered try block again");
					if(rs.next()) {
						System.out.println("rs.next() in Image display servlet");
						byte[] imagebytes = rs.getBytes("productimage");
						response.setContentType("image/*");
						System.out.println("response set to image/*");
						fos.write(imagebytes);
						fos.close();
						if(f.exists()) {
							System.out.println("image written to newfolder successfully");
						}
						response.setContentLength(imagebytes.length);
						response.getOutputStream().write(imagebytes);
						System.out.println("response.getOutputStream().write(imagebytes)");
						response.setContentType("text/html");
					}else {
						System.out.println("rs.next() failed");
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
				

								
				break;
				
		   	case "profilepicture" :
	
		   		System.out.println("switch case profilepicture");
				pstmt = conn.prepareStatement("select profilepicture from farmerdetails where farmerid = ?");
				pstmt.setString(1, farmerid);
				rs = pstmt.executeQuery();
				System.out.println("result size: "+rs.getFetchSize());//this fetch will always remain 1 because the farmerid is unique
				
				try {
					//File f = new File ("C://Users//PRANEETH//Desktop//New folder//newfile.jpeg");
					//FileOutputStream fos = new FileOutputStream(f);
					System.out.println("Entered try block again");
					if(rs.next()) {
						System.out.println("rs.next() in Image display servlet");
						byte[] imagebytes = rs.getBytes("profilepicture");
						response.setContentType("image/*");
						System.out.println("response set to image/*");
						//fos.write(imagebytes);
						//fos.close();
						//if(f.exists()) {
						//	System.out.println("image written to newfolder successfully");
						//}
						response.setContentLength(imagebytes.length);
						response.getOutputStream().write(imagebytes);
						System.out.println("response.getOutputStream().write(imagebytes)");
						response.setContentType("application/json");
					}else {
						System.out.println("rs.next() failed");
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
		   		
		   		break;
			
			}
			
			
			
		}catch(SQLException sqle) {
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
