package com.walletSevlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.database.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ExportCSVServlet extends HttpServlet {
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"wallet_data.csv\"");

        try (PrintWriter out = response.getWriter();
             Connection conn = DBConnection.requestConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT wallet_address, token_amount, timestamp FROM wallet")) {

            out.println("Wallet Address,Token Amount,Timestamp");

            while (rs.next()) {
                String address = rs.getString("wallet_address");
                String amount = rs.getString("token_amount");
                String timestamp = rs.getString("timestamp");
                out.println(address + "," + amount + "," + timestamp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
