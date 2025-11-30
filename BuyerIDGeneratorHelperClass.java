package com.agriwastetrade.site;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BuyerIDGeneratorHelperClass {//this buyer id will be combined with the session id and timestamp to create a unique token or OTP for email verification
	public static String buyerID(String industryname, String industryid) {// instead of industry id, some alternative unique identifier should be used
		
		
		industryname = industryname.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();//normalizeString(industryname);
		String uuid = "";
		
		try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hashBytes = digest.digest(industryname.getBytes());
            
            // Convert to UUID-like format (simplified)
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xFF & b);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            
            uuid = hexString.toString().substring(0, 32); // First 32 chars
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating UUID", e);
        
		}
		
		uuid = uuid.substring(0, 8);
		uuid = uuid.toUpperCase();
		
		String buyerid = "";
		
		try (Connection conn = DatabaseConnectionPool.getConnectionPool();
             PreparedStatement pstmt = conn.prepareStatement("select buyerid from buyerdetails where buyerid = ?")) {
            pstmt.setString(1, "BUYER" + uuid + industryid);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int randomNum = (int) (Math.random() * 1000);
                    uuid = uuid + randomNum;
                    buyerid = "BUYER" + uuid + industryid;
                } else {
                    buyerid = "BUYER" + uuid + industryid;
                }
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
	   
	   return buyerid;
	}// once the email is sent, the user details will be updated in the database
	
	
}