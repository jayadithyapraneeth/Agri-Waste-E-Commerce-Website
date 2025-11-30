package com.agriwastetrade.site;

import jakarta.mail.Address;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

/**
 * Servlet implementation class EmailAddressVerificationServlet
 */
@WebServlet("/emailservlet")
public class EmailServlet extends HttpServlet {
	
	private static class JSON{
		String code;
		String email;
	}
	
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EmailServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    
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
    
    private static String hashToOTP(String hash) {
        // Take the first 6 digits from the numeric part of the hash
        StringBuilder numeric = new StringBuilder();
        for (char c : hash.toCharArray()) {
            if(Character.isDigit(c)) {
                numeric.append(c);
                if(numeric.length() == 6) break;
            }
        }

        // Fallback if not enough digits found
        while (numeric.length() < 6) {
            numeric.append("0");
        }

        return numeric.toString();
    }
    
    private static String sha256(String otpinput) {
    	try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(otpinput.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
    
    private static String OTP(HttpServletRequest request, String from, Connection conn) throws Exception {
    	String otpinput = "";
    	if(from.equalsIgnoreCase("buyerregistrationpage")) {
    		PreparedStatement pstmt = conn.prepareStatement("select industryname, industryid from temporaryregistrationbuyer where regtoken = ?");
        	pstmt.setString(1, (String)request.getSession(false).getAttribute("regtoken"));
        	ResultSet rs = pstmt.executeQuery();
        	
        	System.out.println("rs:"+rs);
        	BuyerIDGeneratorHelperClass bigc = new BuyerIDGeneratorHelperClass();
        	
        	String buyerid = "";
     
        	if(rs.next()) {
    			buyerid = bigc.buyerID(rs.getString("industryname"), rs.getString("industryid"));
    		}
        	
        	if(buyerid != null) {
        		buyerid = buyerid.substring(5, 13);//extracting the unique part of the buyer id
        	}
        	
        	otpinput = request.getSession(false).getId() + System.currentTimeMillis()%10000 + buyerid;
    	}else if(from.equalsIgnoreCase("sellerregistrationpage")){
    		String sellerid = "";
    		
    		PreparedStatement pstmt = conn.prepareStatement("select farmerid from temporaryregistrationfarmer where regtoken = ?");
        	pstmt.setString(1, (String)request.getSession(false).getAttribute("regtoken"));
        	ResultSet rs = pstmt.executeQuery();
        	
        	if(rs.next()) {
        		sellerid = rs.getString("farmerid");
        	}
        	
        	otpinput = request.getSession(false).getId() + System.currentTimeMillis()%10000 + sellerid;
    	}
    	
    	
    	
    	
    	
    	//now generate the OTP based on timestamp, sessionid and buyerid
    	
    	
        //String input = sessionId + ":" + userId + ":" + timestamp;

        String hash = sha256(otpinput);
        
        String OTP = "";
        
        for (char c : hash.toCharArray()) {
            if(Character.isDigit(c)) {
                OTP += c;
                if(OTP.length() == 6)//here the OTP length is set to be 6 but it only counts the digits, it won't consider these digits as a meaningful number
                	//when this OTP converted to int or long, it may lose the leading zeros and count may be less than 6 digits
                	//so, we shoud store this OTP in the database as a string type(varchar or text) only
                	break;
            }
        }
        
        if(OTP.length() < 6) {
        	while(OTP.length() == 6) {
        		OTP += "0";
        	}
        }
        
		return OTP;
    	
    }
    
    private static boolean verifyOTP(HttpServletRequest request, String verificationcode, Connection conn) throws JsonSyntaxException, JsonIOException, IOException {

    	//Gson gson = new Gson();//here the JSON is just a POJO class the request.getReader() will fetch and assign the values to the fields in the POJO class
		//JSON json1 = gson.fromJson(request.getReader(), JSON.class);//.fromJson(request.getReader().toString(), JSONObject.class);//
		//gson.to
		//JacksonObject json = gson.fromJson(request.getReader(), JSONObject.class);
		
		
		String otp =(String) request.getParameter("code");//request.getParameter("otp");
		System.out.println("otp from user: "+otp);
		
		String email = (String) request.getParameter("email");//json1.email;//request.getParameter("email");
		System.out.println("emailid from user: "+email);
		
		String frompage = (String) request.getParameter("from");
		System.out.println("from page: "+frompage);
		
		
		try{
			String tablename = "";
			
			if(frompage.equalsIgnoreCase("buyerregistrationpage")) {
				tablename = "temporaryregistrationbuyer";
			}else if(frompage.equalsIgnoreCase("sellerregistrationpage")) {
				tablename = "temporaryregistrationfarmer";
			}else {
				System.out.println("from page = "+frompage);
			}
			
	    	PreparedStatement pstmt = conn.prepareStatement("select otp from "+tablename+" where regtoken = ?");
	    	pstmt.setString(1, (String)request.getSession(false).getAttribute("regtoken"));
	    	ResultSet rs = pstmt.executeQuery();
	    	
	    	if(rs.next()) {
	    		verificationcode = rs.getString("otp");
	    		System.out.println("verificationcode from DB: "+verificationcode);
	    	}else {
	    		System.out.println("No matching registration found for the provided regtoken.");
	    	}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		if(otp.equals(verificationcode)) {
			return true;
		}else {
			return false;
		}
		
    }
    
    
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Connection conn = null;
		
		try {
			conn = DatabaseConnectionPool.getConnectionPool();
		}catch(SQLException sqle) {
			sqle.printStackTrace();
		}
		
		
		if(request.getSession(false) == null || request.getSession(false).getAttribute("loginstatus") == "died") {
			response.sendRedirect("loginpage.html?error=Session expired. Please login again to continue.");
			return;
		}
		
		System.out.println("email servlet");
		
		String purpose = (String)request.getParameter("purpose");//tosendtheverificationcode or tosendthebuyerid or toverifyotp
		
		String emailid = (String)request.getParameter("email");//every getParameter() method responds in String format only
		
		
		String verificationcode = "";
		
		//purpose = purpose.replace("'", "");//removing the single quotes from the purpose parameter if any
		
//		if(purpose == "tosendtheverificationcode") {
//			System.out.println("yes case matched");
//		}else {
//			System.out.println(purpose.equals("tosendtheverificationcode"));
//			System.out.println(purpose.equalsIgnoreCase("tosendtheverificationcode"));
//			System.out.println(purpose == "tosendtheverificationcode");
//			System.out.println("tosendtheverificationcode" == request.getParameter("purpose"));
//			System.out.println(request.getParameter("purpose").equals("tosendtheverificationcode"));
//			System.out.println(request.getParameter("purpose").equalsIgnoreCase("tosendtheverificationcode"));
//			
//		}
		
		System.out.printf("purpose: %s, emailid: %s, verificationcode: %s\n",purpose,emailid,verificationcode);
		
		switch(purpose) {		
		
		case "tosendtheverificationcode":
			System.out.println("case 1");
			
			String frompage = (String) request.getParameter("from");
			System.out.println("from page: "+frompage);
			
			try {
				verificationcode = OTP(request,frompage,conn); // the column holding the value for this string should also be a string(varchar or text), it is because, if the column is a decimal or long data type, then the original string will undergo implicit conversion, so the database system converts the string to a number(which is having a value) not to a decimal(that where the all the symbols are considered just as digits individually)
				System.out.println("verification code(OTP): "+verificationcode);
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			
			try{
				
				String tablename = "";
				
				if(frompage.equalsIgnoreCase("buyerregistrationpage")) {
					tablename = "temporaryregistrationbuyer";
					//tablename = "update temporaryregistrationbuyer set otp = ? where regtoken = ?";
				}else if(frompage.equalsIgnoreCase("sellerregistrationpage")) {
					tablename = "temporaryregistrationfarmer";
					//tablename = "update temporaryregistrationfarmer set otp = ? where regtoken = ?";
				}
				
		    	PreparedStatement pstmt = conn.prepareStatement("update "+tablename+" set otp = ? where regtoken = ?");
		    	pstmt.setString(1, verificationcode);
		    	pstmt.setString(2, (String)request.getSession(false).getAttribute("regtoken"));
		    	
		    	System.out.println("update temporaryregistrationfarmer set otp = "+verificationcode+" where regtoken = "+(String)request.getSession(false).getAttribute("regtoken"));
		    	
		    	System.out.println("pstmt metadata : "+pstmt.getParameterMetaData().toString());
		    	
		    	int rowsupdated = pstmt.executeUpdate();
		    	if(rowsupdated > 0) {
		    		System.out.println("Email verification code updated successfully in the "+tablename+" table.");
		    	}else {
		    		System.out.println("Failed to update email verification code in the "+tablename+" table.");//to verify the frompage
		    	}
		    	
		    	if(verificationcode != null) {
		    		sendEmail(emailid, "Verification Code from Agri-Waste Trade", "Your Email verification code is: "+verificationcode);
					System.out.println("verification code is sent to email:"+emailid);
		    	}else {
		    		System.out.println("verificationcode is null : "+verificationcode);
		    	}
		    	
		    	
		    	if(frompage.equalsIgnoreCase("buyerregistrationpage")) {
		    		response.sendRedirect("EmailVerificationPage.html?email="+emailid+"&from=buyerregistrationpage");// this parameter is to display the email id on the verification page and also to get into the correct backend servlet
				}else if(frompage.equalsIgnoreCase("sellerregistrationpage")) {
					response.sendRedirect("EmailVerificationPage.html?email="+emailid+"&from=sellerregistrationpage");// this parameter is to display the email id on the verification page and also to get into the correct backend servlet
				}
		    	
				
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			
			
			break;
			
		case "tosendthebuyerid": // it will verify the OTP and then send the buyer id to the registered email id
			System.out.println("case 2");
			
			//JSONObject json1 = new JSONObject(request.getReader());//request.getReader().toString()//
			Gson gson = new Gson();//here the JSON is just a POJO class the request.getReader() will fetch and assign the values to the fields in the POJO class
			JSON json1 = gson.fromJson(request.getReader(), JSON.class);//.fromJson(request.getReader().toString(), JSONObject.class);//
			
			try{
		    	PreparedStatement pstmt = conn.prepareStatement("select otp from temporaryregistrationbuyer where regtoken = ?");
		    	pstmt.setString(1, (String)request.getSession(false).getAttribute("regtoken"));
		    	ResultSet rs = pstmt.executeQuery();
		    	
		    	if(rs.next()) {
		    		verificationcode = rs.getString("otp");
		    		System.out.println("verificationcode from DB: "+verificationcode);
		    	}else {
		    		System.out.println("No matching registration found for the provided regtoken.");
		    	}
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			if(verifyOTP(request,verificationcode,conn)) {
				System.out.println("otp.equals(verificationcode)");
				try{
			    	PreparedStatement pstmt = conn.prepareStatement("select industryname, industryid from temporaryregistrationbuyer where regtoken = ?");
			    	pstmt.setString(1, (String)request.getSession(false).getAttribute("regtoken"));
			    	
			    	ResultSet rs = pstmt.executeQuery();
			    	
			    	BuyerIDGeneratorHelperClass bigc = new BuyerIDGeneratorHelperClass();
			    	
			    	String buyerid = "";
			 
			    	if(rs.next()) {
						buyerid = bigc.buyerID(rs.getString("industryname"), rs.getString("industryid"));//we are not using timestamp to generate buyer id
						System.out.println("New Buyer ID: "+buyerid);
						sendEmail(json1.email, "Buyer ID generated", "Your Buyer ID is: "+buyerid+"\n\n You can use this Buyer ID to log in to your FarmeDregs buyer account.");
						System.out.println("Email sent to the user:"+buyerid);
						response.setContentType("application/json");
						JSONObject json = new JSONObject();
						json.put("verified", true);
						PrintWriter pw = response.getWriter();
						pw.print(json);
						
						//once the buyer id is sent to the buyer we should transfer the registration details from the temporary table to the permanent table
						
						PreparedStatement pstmt2 = conn.prepareStatement("select industryname, industryid, contactno, emailid, industryaddress, industrytype, industrypassword from temporaryregistrationbuyer where regtoken = ?");
				    	pstmt2.setString(1, (String)request.getSession(false).getAttribute("regtoken"));//only the regtoken is not retrieved from the tempoararyregistration table
				    	
				    	ResultSet rs1 = pstmt2.executeQuery();// all temporary details are fetched from the DB
				    	rs1.next();
				    	
				    	PreparedStatement pstmt1 = conn.prepareStatement("delete from temporaryregistrationbuyer where regtoken = ?");
						pstmt1.setString(1, (String)request.getSession(false).getAttribute("regtoken"));
						int deletetemporarydetails = pstmt1.executeUpdate();
						
						if(deletetemporarydetails > 0) {
							System.out.println("Temporary registration data deleted successfully.");
						}
						//removing the regtoken from the session as it is no longer needed
						request.getSession(false).removeAttribute("regtoken");
						if(request.getSession(false).getAttribute("regtoken") == null) {
							System.out.println("regtoken deleted from session");
						}
				    	
				    	PreparedStatement pstmt3 = conn.prepareStatement("insert into logincredentials(userid, password,role) values(?,?,?)");
				    	pstmt3.setString(1, buyerid);
				    	pstmt3.setString(2, rs1.getString("industrypassword"));
				    	pstmt3.setString(3, "buyer");
				    	
				    	int logincredentialsadded = pstmt3.executeUpdate();
						
						PreparedStatement pstmt4 = conn.prepareStatement("insert into buyerdetails(buyerid, industryname, industryid, contactno, emailid, industryaddress, industrytype, dateregistered) values(?,?,?,?,?,?,?,now())");
						pstmt4.setString(1, buyerid);
						pstmt4.setString(2, rs1.getString("industryname"));
						pstmt4.setString(3, rs1.getString("industryid"));
						pstmt4.setString(4, rs1.getString("contactno"));
						pstmt4.setString(5, rs1.getString("emailid"));
						pstmt4.setString(6, rs1.getString("industryaddress"));
						pstmt4.setString(7, rs1.getString("industrytype"));
						
						int buyerdetailsadded = pstmt4.executeUpdate();
						
						System.out.println("Buyer registration details added successfully to the permanent tables.");
						
						
					}else {
						System.out.println("No matching registration found for the provided regtoken.");
						System.out.println("Register again");
						PreparedStatement pstmt2 = conn.prepareStatement("delete from temporaryregistrationbuyer where regtoken = ?");
						pstmt2.setString(1, (String)request.getSession(false).getAttribute("regtoken"));
						int d = pstmt2.executeUpdate();
						if(d > 0) {
							System.out.println("Temporary registration data deleted successfully.");
						}
						request.getSession(false).removeAttribute("regtoken");
						
						if(request.getSession(false).getAttribute("regtoken") == null) {
							System.out.println("regtoken deleted from session");
						}
						
						response.setContentType("application/json");
						JSONObject json = new JSONObject();
						json.put("verified",false);
						response.getWriter().print(json);
						//response.setContentType("text/html");
						//response.getWriter().println("<h1>Registration unsuccessful. Please register again...<a href=\'NewBuyerRegistrationPage.html\'>Registration Page</a></h1> <br>or you can move to <b><HOME PAGE</b></h1>");
						//response.sendRedirect("index.html");
						
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
			}else {
				
				try {
					System.out.println("Entered wrong otp, redirected to buyer registration page");
					System.out.println("Register again");
					PreparedStatement pstmt2 = conn.prepareStatement("delete from temporaryregistrationbuyer where regtoken = ?");
					pstmt2.setString(1, (String)request.getSession(false).getAttribute("regtoken"));
					int d = pstmt2.executeUpdate();
					if(d > 0) {
						System.out.println("Temporary registration data deleted successfully.");
					}
					request.getSession(false).removeAttribute("regtoken");
					
					if(request.getSession(false).getAttribute("regtoken") == null) {
						System.out.println("regtoken deleted from session");
					}
					
					response.setContentType("application/json");
					JSONObject json = new JSONObject();
					json.put("verified",false);
					response.getWriter().print(json);
					response.sendRedirect("NewBuyerRegistrationPage.html");
				}catch(SQLException sqle) {
					sqle.printStackTrace();
				}
			}
			
			break;
			
		case "toverifyotp":
			
			if(verifyOTP(request,verificationcode, conn)){
				try{
			    	PreparedStatement pstmt = conn.prepareStatement("select fullname, farmerid from temporaryregistrationfarmer where regtoken = ?");
			    	pstmt.setString(1, (String)request.getSession(false).getAttribute("regtoken"));
			    	
			    	ResultSet rs = pstmt.executeQuery();
			 
			    	if(rs.next()) {
						JSONObject json = new JSONObject();
						json.put("verified", true);
						PrintWriter pw = response.getWriter();
						pw.print(json);
						
						PreparedStatement pstmt2 = conn.prepareStatement("select fullname, farmerid, phone, emailid, address, farmerpassword from temporaryregistrationfarmer where regtoken = ?");
				    	pstmt2.setString(1, (String)request.getSession(false).getAttribute("regtoken"));//only the regtoken is not retrieved from the tempoararyregistration table
				    	
				    	ResultSet rs1 = pstmt2.executeQuery();// all temporary details are fetched from the DB
				    	rs1.next();
				    	
				    	PreparedStatement pstmt1 = conn.prepareStatement("delete from temporaryregistrationfarmer where regtoken = ?");
						pstmt1.setString(1, (String)request.getSession(false).getAttribute("regtoken"));
						int deletetemporarydetails = pstmt1.executeUpdate();
						
						if(deletetemporarydetails > 0) {
							System.out.println("Temporary registration data deleted successfully.");
						}
						//removing the regtoken from the session as it is no longer needed
						request.getSession(false).removeAttribute("regtoken");
						if(request.getSession(false).getAttribute("regtoken") == null) {
							System.out.println("regtoken deleted from session");
						}
				    	PreparedStatement pstmt4 = conn.prepareStatement("insert into farmerdetails(farmerid, fullname, phoneno, emailid, farmeraddress, dateregistered) values(?,?,?,?,?,now())");
						pstmt4.setString(1, rs1.getString("farmerid"));
						pstmt4.setString(2, rs1.getString("fullname"));
						pstmt4.setString(3, rs1.getString("phone"));
						pstmt4.setString(4, rs1.getString("emailid"));
						pstmt4.setString(5, rs1.getString("address"));
						
						int farmerdetailsadded = pstmt4.executeUpdate();
						
				    	PreparedStatement pstmt3 = conn.prepareStatement("insert into logincredentials(userid, password,role) values(?,?,?)");
				    	pstmt3.setString(1, rs1.getString("farmerid"));
				    	pstmt3.setString(2, rs1.getString("farmerpassword"));
				    	pstmt3.setString(3, "seller");
				    	
				    	int logincredentialsadded = pstmt3.executeUpdate();
						
						
						
						System.out.println("Farmer registration details added successfully to the permanent tables.");
						
						
					}else {
						System.out.println("No matching registration found for the provided regtoken.");
						System.out.println("Register again");
						PreparedStatement pstmt2 = conn.prepareStatement("delete from temporaryregistrationfarmer where regtoken = ?");
						pstmt2.setString(1, (String)request.getSession(false).getAttribute("regtoken"));
						int d = pstmt2.executeUpdate();
						if(d > 0) {
							System.out.println("Temporary registration data deleted successfully.");
						}
						request.getSession(false).removeAttribute("regtoken");
						
						if(request.getSession(false).getAttribute("regtoken") == null) {
							System.out.println("regtoken deleted from session");
						}
						
						response.setContentType("application/json");
						JSONObject json = new JSONObject();
						json.put("verified",false);
						response.getWriter().print(json);
						//response.setContentType("text/html");
						//response.getWriter().println("<h1>Registration unsuccessful. Please register again...<a href=\'NewBuyerRegistrationPage.html\'>Registration Page</a></h1> <br>or you can move to <b><HOME PAGE</b></h1>");
						//response.sendRedirect("index.html");
						
				    }
				}catch(Exception e) {
					e.printStackTrace();
				}
			}else {
				
				try {
					System.out.println("Entered wrong otp, redirected to buyer registration page");
					System.out.println("Register again");
					PreparedStatement pstmt2 = conn.prepareStatement("delete from temporaryregistrationfarmer where regtoken = ?");
					pstmt2.setString(1, (String)request.getSession(false).getAttribute("regtoken"));
					int d = pstmt2.executeUpdate();
					if(d > 0) {
						System.out.println("Temporary registration data deleted successfully.");
					}
					request.getSession(false).removeAttribute("regtoken");
					
					if(request.getSession(false).getAttribute("regtoken") == null) {
						System.out.println("regtoken deleted from session");
					}
					
					response.setContentType("application/json");
					JSONObject json = new JSONObject();
					json.put("verified",false);
					response.getWriter().print(json);
					response.sendRedirect("NewSellerRegistrationPage.html");
				}catch(SQLException sqle) {
					sqle.printStackTrace();
				}
			}
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
