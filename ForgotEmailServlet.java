package com.agriwastetrade.site;

import jakarta.mail.Address;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.json.JSONObject;
import com.google.gson.Gson;

/**
 * Servlet implementation class ForgotEmailServlet
 */
@WebServlet("/forgotemailservlet")
public class ForgotEmailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
	public static PrintStream out = System.out;
	
	private static class JSON{
		//@SerializedName("password")
		 String newPassword;
		//@SerializedName("role")
		 String role;
		//@SerializedName("userid")
		 String userId;
		
	}
	
    private static void sendEmail(String toaddress,String subject, String message) {
    	
    	Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");

		String sender = "machavarapujayadithyapraneeth@gmail.com";
		String password = "ygwaehbymtrftssk"; // Use an App Password if 2FA is enabled -- without this password, it won't work because gmail blocks less secure apps.
		
		Session session = Session.getInstance(props, new Authenticator() {
		  protected PasswordAuthentication getPasswordAuthentication() {
		    return new PasswordAuthentication(sender, password);
		  }
		});

		Message msg = new MimeMessage(session);
		try {
			msg.setFrom(new InternetAddress(sender));
			msg.setRecipient(Message.RecipientType.TO, new InternetAddress(toaddress));
			msg.setSubject(subject);
			msg.setText(message);
			
			msg.setContent(message, "text/html; charset=utf-8");
			//if we use MimeBodyPart then we have to use MimeMultipart to send the email
			// and if we use MimeMultipart then we might need a class called DataHandler to handle the data
			//MimeBodyPart OTP = new MimeBodyPart();
			//OTP.setText(message);
			
			Transport.send(msg);
			System.out.println("Email sent successfully!");
			System.out.println("Email sent to: ");
			for (Address address : msg.getAllRecipients()) {
				System.out.println(address.toString());
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
    public ForgotEmailServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		if(request.getSession(false) == null || request.getSession(false).getAttribute("loginstatus") == "died") {
			response.sendRedirect("loginpage.html?error=Session expired. Please login again to continue.");
			return;
		}
		
		String purpose = request.getParameter("purpose");
		String email = request.getParameter("emailid");
		out.println("email"+email);
		String role = request.getParameter("role");
		out.println("role:"+role);
		Connection conn = null;
		
		try {
			conn = DatabaseConnectionPool.getConnectionPool();//for now this servlet works on database compulsorily, if there is a case that do not need database connection then this variable has no use
		}catch(SQLException sqle) {//as currently the both the sq=witch cases need DB connection I am providing this Connection object as large scaled variable
			sqle.printStackTrace();
		}
		
		switch(purpose) {
			case "senduserid":
				out.println("case: senduserid");
				try {
					//conn = DatabaseConnectionPool.getConnectionPool();
					//String password = request.getParameter("")
					String stmt = "";
					String userid = "";
					if(role.equals("buyer")) {
						stmt = "select buyerid from buyerdetails where emailid = ?";
					}else if(role.equals("seller")) {
						stmt = "select farmerid from farmerdetails where emailid = ?";
					}else {
						stmt = null;
						out.println("statement is null: "+role);
					}
					
					PreparedStatement pstmt = conn.prepareStatement(stmt);
					pstmt.setString(1, email);
					ResultSet rs = pstmt.executeQuery();
					if(rs.next()) {
						if(role.equals("buyer")) {
							userid = rs.getString("buyerid");
						}else if(role.equals("seller")) {
							userid = rs.getString("farmerid");
						}else {
							stmt = null;
							out.println("statement is null: "+role);
						}
					}
					
					
//					PreparedStatement pstmt1 = conn.prepareStatement("select userid from logincredentials where userid = ?");
//					pstmt1.setString(1, userid);
//					ResultSet rs1 = pstmt1.executeQuery();
					
					//if(rs1.next()) {
						sendEmail(email,"Resending Forgotten userID","This is your userID "+userid);
					//}else {
						//out.println("userid not found");
					//}
					
				}catch(Exception e){
					e.printStackTrace();
				}
				break;
			case "passwordresetconfirmation":
				out.println("case: sendpassword");
				
				JSON json = new JSON();
				Gson gson = new Gson();//here the JSON is just a POJO class the request.getReader() will fetch and assign the values to the fields in the POJO class
				json = gson.fromJson(request.getReader(), JSON.class);
				out.println(json);
				out.println(json.toString());
				role = json.role;
				out.println("role: "+role);
				String userid = json.userId;//email will be fetched from the database based on the userid
				out.println("userid: "+userid);
				
				JSONObject json1 = new JSONObject();
				response.setContentType("application/json");
				
				try {
					//conn = DatabaseConnectionPool.getConnectionPool();
					PreparedStatement pstmt = conn.prepareStatement("update logincredentials set password = ? where userid = ?");
					pstmt.setString(1, json.newPassword);
					pstmt.setString(2, userid);
					int i = pstmt.executeUpdate();
					if(i > 0) {
						json1.put("status", "passwordresetsuccessful");
						out.println("Password updated successfully in logincredentials for userid: "+userid);
					}else {
						json1.put("status", "passwordresetfailed");
						out.println("Password update failed in logincredentials for userid: "+userid);
					}
					
					response.getWriter().print(json1);
					
					ResultSet rs;
					
					if(role.equals("buyer")) {
						PreparedStatement pstmt1 = conn.prepareStatement("select emailid from buyerdetails where buyerid = ?");
						pstmt1.setString(1, userid);
						rs = pstmt1.executeQuery();
						email = rs.getString("emailid");
						out.println("fetching email from buyerdetails: "+email);
						out.println("sending email to the user: "+userid);
						sendEmail(email,"Password reset successful", "Your Agriwaste buyer account password with userid: "+userid+" has been reset successfully - case: forgotten");
					}else if(role.equals("seller")) {
						PreparedStatement pstmt1 = conn.prepareStatement("select emailid from farmerdetails where farmerid = ?");
						pstmt1.setString(1, userid);
						rs = pstmt1.executeQuery();
						email = rs.getString("emailid");
						out.println("fetching email from farmerdetails: "+email);
						out.println("sending email to the user: "+userid);
						sendEmail(email,"Password reset successful", "Your Agriwaste seller account password with userid: "+userid+" has been reset successfully - case: forgotten");
					}else {
						out.println("role is null: "+role);
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
