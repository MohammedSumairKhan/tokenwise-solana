package com.database;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
	static Connection con = null;
	static String url="jdbc:mysql://localhost:3306/wallet";
	static String un = "root";
	static String pwd = "Sumair@1";
		public static Connection requestConnection() {
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				con=DriverManager.getConnection(url,un,pwd);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return con;
		}
}
