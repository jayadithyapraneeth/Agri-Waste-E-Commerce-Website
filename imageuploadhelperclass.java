package com.agriwastetrade.site;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class imageuploadhelperclass {
	
	public static void main(String[] args) {
		
		File f = new File("C:/Users/PRANEETH/Pictures/inventoryimages");
		
		if(f.isDirectory()) {
			System.out.println("directory found");
		}
		
		File[] imagelist = f.listFiles();
		
		try {
		String sql = "update inventorydetails set productimage = ? where cropid = ?";
		Connection conn = DatabaseConnectionPool.getConnectionPool();
		PreparedStatement pstmt = conn.prepareStatement(sql);
		
		FileInputStream imagedata;
		int effectedrows;
		for(File imageiteration : imagelist) {
			
			imagedata = new FileInputStream(imageiteration);
			pstmt.setBlob(1, imagedata);
			pstmt.setString(2, imageiteration.getName().substring(0, imageiteration.getName().indexOf(".")));
			effectedrows = pstmt.executeUpdate();
			if(effectedrows == 1) {
				System.out.println("file name: "+ imageiteration.getName().substring(0, imageiteration.getName().indexOf(".")));
			}else {
				System.out.println("file not inserted");
			}
			
	    }
		
		}catch(SQLException sqle) {
			sqle.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
		
		
	}

}
