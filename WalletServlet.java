package com.walletSevlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import com.database.DBConnection;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class WalletServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");

        List<String[]> walletList = new ArrayList<>();
        Map<String, Integer> walletActivity = new HashMap<>();
        Map<String, Integer> protocolCount = new HashMap<>();

        int buyCount = 0;
        int sellCount = 0;

        try {
            Connection conn = DBConnection.requestConnection();
            PreparedStatement ps;

            String query = "SELECT w1.wallet_address, w1.token_amount, w1.timestamp, w1.protocol, " +
                    "COALESCE((SELECT w2.token_amount FROM wallet w2 " +
                    "WHERE w2.wallet_address = w1.wallet_address AND w2.timestamp < w1.timestamp " +
                    "ORDER BY w2.timestamp DESC LIMIT 1), w1.token_amount) AS previous_amount " +
                    "FROM wallet w1 ";

            if (startDate != null && endDate != null) {
                query += "WHERE w1.timestamp BETWEEN ? AND ? ";
            }

            query += "ORDER BY w1.timestamp ASC";

            ps = conn.prepareStatement(query);
            if (startDate != null && endDate != null) {
                ps.setString(1, startDate + " 00:00:00");
                ps.setString(2, endDate + " 23:59:59");
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String walletAddress = rs.getString("wallet_address");
                int currentAmount = rs.getInt("token_amount");
                int previousAmount = rs.getInt("previous_amount");
                String timestamp = rs.getString("timestamp");
                String protocol = rs.getString("protocol");

                // Buy/Sell logic
                String action;
                if (currentAmount > previousAmount) {
                    action = "Buy";
                    buyCount++;
                } else if (currentAmount < previousAmount) {
                    action = "Sell";
                    sellCount++;
                } else {
                    action = "Hold";
                }

                // Count wallet activity
                walletActivity.put(walletAddress, walletActivity.getOrDefault(walletAddress, 0) + 1);

                // Count protocol usage
                if (protocol != null && !protocol.isEmpty()) {
                    protocolCount.put(protocol, protocolCount.getOrDefault(protocol, 0) + 1);
                }

                walletList.add(new String[]{
                    walletAddress,
                    String.valueOf(currentAmount),
                    timestamp,
                    action,
                    protocol
                });
            }

            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Filter wallets with repeated activity
        List<String> repeatedWallets = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : walletActivity.entrySet()) {
            if (entry.getValue() > 1) {
                repeatedWallets.add(entry.getKey());
            }
        }

        // Net direction
        String netDirection = buyCount > sellCount ? "Buy-heavy" :
                              sellCount > buyCount ? "Sell-heavy" : "Neutral";

        request.setAttribute("walletList", walletList);
        request.setAttribute("totalBuys", buyCount);
        request.setAttribute("totalSells", sellCount);
        request.setAttribute("netDirection", netDirection);
        request.setAttribute("activeWallets", repeatedWallets);
        request.setAttribute("protocolBreakdown", protocolCount);

        RequestDispatcher rd = request.getRequestDispatcher("wallet.jsp");
        rd.forward(request, response);
    }
}
