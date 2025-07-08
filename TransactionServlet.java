package com.walletSevlet;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import com.database.DBConnection;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

public class TransactionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<String[]> walletList = new ArrayList<>();
        Map<String, Integer> protocolBreakdown = new HashMap<>();
        Map<String, Integer> walletActivity = new HashMap<>();
        int totalBuys = 0, totalSells = 0;

        try {
            Connection conn = DBConnection.requestConnection();
            String query = "SELECT * FROM wallet ORDER BY timestamp ASC";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            Map<String, Integer> lastAmountMap = new HashMap<>();

            while (rs.next()) {
                String wallet = rs.getString("wallet_address");
                int amount = rs.getInt("token_amount");
                String timestamp = rs.getString("timestamp");
                String protocol = rs.getString("protocol");

                // Buy/Sell/Hold Logic
                int last = lastAmountMap.getOrDefault(wallet, amount);
                String action = amount > last ? "Buy" : amount < last ? "Sell" : "Hold";
                lastAmountMap.put(wallet, amount);

                if (action.equals("Buy")) totalBuys++;
                if (action.equals("Sell")) totalSells++;

                // Protocol Count
                protocolBreakdown.put(protocol, protocolBreakdown.getOrDefault(protocol, 0) + 1);

                // Active Wallets
                walletActivity.put(wallet, walletActivity.getOrDefault(wallet, 0) + 1);

                walletList.add(new String[]{wallet, String.valueOf(amount), timestamp, action, protocol});
            }

            rs.close();
            ps.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Repeated wallets (activity ≥ 2)
        List<String> activeWallets = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : walletActivity.entrySet()) {
            if (entry.getValue() >= 2) {
                activeWallets.add(entry.getKey());
            }
        }

        String netDirection = totalBuys > totalSells ? "Buying" : totalSells > totalBuys ? "Selling" : "Neutral";

        request.setAttribute("walletList", walletList);
        request.setAttribute("totalBuys", totalBuys);
        request.setAttribute("totalSells", totalSells);
        request.setAttribute("netDirection", netDirection);
        request.setAttribute("protocolBreakdown", protocolBreakdown);
        request.setAttribute("activeWallets", activeWallets);

        RequestDispatcher rd = request.getRequestDispatcher("wallet.jsp");
        rd.forward(request, response);
    }
}
