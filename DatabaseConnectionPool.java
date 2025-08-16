package com.agriwastetrade.site;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;

public class DatabaseConnectionPool {//
	
	
    public static Connection getConnectionPool() throws SQLException {
		BasicDataSource datasource = new BasicDataSource();
		datasource.setUrl("jdbc:mysql://localhost:3306/agriwasteecommerceplatform");
		datasource.setUsername("jayadithyapraneeth");
		datasource.setPassword("0000");
		datasource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		
		datasource.setInitialSize(5); // Initial number of connections
		datasource.setMaxTotal(10); // Maximum number of connections
		datasource.setMaxIdle(5); // Maximum number of idle connections
		datasource.setMinIdle(2); // Minimum number of idle connections
		return datasource.getConnection();
	}
	

}
