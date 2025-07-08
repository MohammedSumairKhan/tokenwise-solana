<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.*" %>
<html>
<head>
    <title>Wallet List</title>
      <style>
    <%@ include file="/WEB-INF/styles.css" %>
</style>
	
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>

<body>
    <h2>📊 Wallet Insights Dashboard</h2>

    <!-- Filter by Date -->
    <form method="get" action="wallets">
        Start Date: <input type="date" name="startDate" required />
        End Date: <input type="date" name="endDate" required />
        <input type="submit" value="Filter" />
    </form>

    <!-- CSV Export Button -->
    <form action="export" method="get">
        <button type="submit">📥 Download CSV</button>
    </form>

    <hr>

    <!-- 🔍 Insights Summary -->
    <h3>📈 Summary</h3>
    <p><strong>Total Buys:</strong> ${totalBuys}</p>
    <p><strong>Total Sells:</strong> ${totalSells}</p>
    <p><strong>Net Direction:</strong> ${netDirection}</p>

    <!-- 🥧 Buy vs Sell Pie Chart -->
    <h3>🟢 Buy vs 🔴 Sell Chart</h3>
    <canvas id="buySellChart" width="400" height="300"></canvas>

    <h3>🔁 Wallets with Repeated Activity</h3>
    <ul>
    <%
        List<String> activeWallets = (List<String>) request.getAttribute("activeWallets");
        if (activeWallets != null) {
            for (String wallet : activeWallets) {
    %>
        <li><%= wallet %></li>
    <%
            }
        }
    %>
    </ul>

    <h3>🔍 Protocol Usage Breakdown</h3>
    <table border="1">
        <tr><th>Protocol</th><th>Count</th></tr>
    <%
        Map<String, Integer> protocolMap = (Map<String, Integer>) request.getAttribute("protocolBreakdown");
        if (protocolMap != null) {
            for (Map.Entry<String, Integer> entry : protocolMap.entrySet()) {
    %>
        <tr><td><%= entry.getKey() %></td><td><%= entry.getValue() %></td></tr>
    <%
            }
        }
    %>
    </table>

    <!-- 🥧 Protocol Usage Pie Chart -->
    <h3>📈 Protocol Usage Chart</h3>
    <canvas id="protocolChart" width="400" height="300"></canvas>

    <hr>

    <!-- Chart -->
    <h3>📊 Top 10 Wallets - Token Amount</h3>
    <canvas id="walletChart" width="600" height="300"></canvas>
    <br><br>

    <!-- Wallet Table -->
    <h3>📋 Wallet Table</h3>
    <table border="1" id="walletTable">
        <tr>
            <th>Wallet Address</th>
            <th>Token Amount</th>
            <th>Timestamp</th>
            <th>Action</th>
            <th>Protocol</th>
        </tr>
        <tbody id="walletTableBody">
        <%
            List<String[]> walletList = (List<String[]>) request.getAttribute("walletList");
            if (walletList != null && !walletList.isEmpty()) {
                for (String[] row : walletList) {
        %>
        <tr>
            <td><%= row[0] %></td>
            <td><%= row[1] %></td>
            <td><%= row[2] %></td>
            <td><%= row[3] %></td>
            <td><%= row[4] %></td>
        </tr>
        <%
                }
            } else {
        %>
        <tr>
            <td colspan="5">No wallet data found.</td>
        </tr>
        <%
            }
        %>
        </tbody>
    </table>

    <!-- Chart Script -->
    <script>
        // === Top 10 Wallets Bar Chart ===
        const labels = [];
        const data = [];

        <% 
            if (walletList != null && !walletList.isEmpty()) {
                int count = 0;
                for (String[] row : walletList) {
                    if (count >= 10) break;
        %>
        labels.push("<%= row[0].substring(0, 6) %>...");
        data.push(<%= row[1] %>);
        <% 
                    count++;
                }
            }
        %>

        const ctx = document.getElementById('walletChart').getContext('2d');
        new Chart(ctx, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Token Amount',
                    data: data,
                    backgroundColor: 'rgba(75, 192, 192, 0.6)',
                    borderColor: 'rgba(75, 192, 192, 1)',
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                scales: {
                    y: { beginAtZero: true }
                }
            }
        });

        // === Protocol Pie Chart ===
        const protocolLabels = [];
        const protocolData = [];

        <% 
            if (protocolMap != null) {
                for (Map.Entry<String, Integer> entry : protocolMap.entrySet()) {
        %>
        protocolLabels.push("<%= entry.getKey() %>");
        protocolData.push(<%= entry.getValue() %>);
        <% 
                }
            }
        %>

        const ctxProtocol = document.getElementById('protocolChart').getContext('2d');
        new Chart(ctxProtocol, {
            type: 'pie',
            data: {
                labels: protocolLabels,
                datasets: [{
                    data: protocolData,
                    backgroundColor: [
                        'rgba(255, 99, 132, 0.6)',
                        'rgba(54, 162, 235, 0.6)',
                        'rgba(255, 206, 86, 0.6)',
                        'rgba(75, 192, 192, 0.6)',
                        'rgba(153, 102, 255, 0.6)',
                        'rgba(255, 159, 64, 0.6)'
                    ],
                    borderColor: 'rgba(255, 255, 255, 1)',
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: { position: 'bottom' },
                    title: { display: true, text: 'Protocol Usage Breakdown' }
                }
            }
        });

        // === Buy vs Sell Pie Chart ===
        const ctxBuySell = document.getElementById('buySellChart').getContext('2d');
        new Chart(ctxBuySell, {
            type: 'pie',
            data: {
                labels: ['Buys', 'Sells'],
                datasets: [{
                    data: [<%= request.getAttribute("totalBuys") %>, <%= request.getAttribute("totalSells") %>],
                    backgroundColor: ['rgba(0, 200, 83, 0.6)', 'rgba(244, 67, 54, 0.6)'],
                    borderColor: 'rgba(255, 255, 255, 1)',
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: { position: 'bottom' },
                    title: { display: true, text: 'Buy vs Sell Distribution' }
                }
            }
        });

        // 🔄 Auto-refresh wallet table every 10s
        setInterval(() => {
            fetch('wallets')
                .then(res => res.text())
                .then(html => {
                    const parser = new DOMParser();
                    const doc = parser.parseFromString(html, 'text/html');
                    const newBody = doc.querySelector('#walletTableBody');
                    if (newBody) {
                        document.querySelector('#walletTableBody').innerHTML = newBody.innerHTML;
                    }
                });
        }, 10000);
    </script>
</body>
</html>
