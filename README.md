# 🧠 TokenWise — Real-Time Wallet Intelligence on Solana

## 🔍 Overview

**TokenWise** is a real-time wallet intelligence system built on the **Solana blockchain**. It tracks the top 60 token holders, monitors their transactions in real-time, and provides a powerful dashboard for insights into token behavior, trends, and protocol usage.

---

## 🚀 Features

* ✅ **Fetch Top 60 Wallet Holders** of a Solana SPL token
* ♻️ **Real-time Monitoring** of wallet transactions (via polling)
* ⚖️ **Identify Protocols Used** (Jupiter, Raydium, Orca, etc.)
* 📊 **Insights Dashboard** with JSP + Chart.js
* 📅 **Date Range Filters** and 📄 CSV Export
* 🚀 Built with **Node.js**, **Java Servlets**, and **MySQL**

---

## 🔗 Target Token

* **Token Mint Address**: `9BB6NFEcjBCtnNLFko2FqVQBq8HHM13kCyYcdQbgpump`
* **Network**: Solana Mainnet

---

## 💻 Tech Stack

| Layer                | Technology                           |
| -------------------- | ------------------------------------ |
| Frontend             | JSP, HTML, CSS, JavaScript, Chart.js |
| Backend              | Node.js, Java Servlets               |
| DB                   | MySQL                                |
| Blockchain Interface | `@solana/web3.js` + Helius API       |

---

## 📆 Setup Instructions

### 1. Clone the Repository

```bash
git clone <your-repo-url>
cd solana-connect
```

### 2. Install Node.js Dependencies

```bash
npm install
```

### 3. MySQL Setup

Create database and table:

```sql
CREATE DATABASE wallet;
USE wallet;

CREATE TABLE wallet (
    wallet_address VARCHAR(100),
    token_amount DECIMAL(20,6),
    timestamp DATETIME,
    action VARCHAR(20),
    protocol VARCHAR(50),
    PRIMARY KEY (wallet_address, timestamp)
);
```

### 4. Configure API Key

Use your [Helius API](https://www.helius.xyz/) key:

```env
HELIUS_API_KEY=2fde24e6-6140-45ad-8b98-dbc443598c83
```

Or directly use in `trackwallet.js` and `polling.js`.

### 5. Run Scripts

* **Track Top Wallets**

```bash
node trackwallet.js
```

* **Start Real-Time Polling**

```bash
node polling.js
```

* **Start JSP Dashboard** Deploy the Java Web App using Apache Tomcat or Eclipse IDE.

---

## 📊 Dashboard Features

* Bar Chart for top 10 wallets (Chart.js)
* Pie Chart for protocol usage
* Auto-refresh wallet table (every 10s)
* Filter by date (Start/End)
* Repeated wallet activity list
* CSV download button for export

---

## 📂 Folder Structure

tokenwise-solana/
├── trackwallet.js         # Fetch top 60 token holders
├── polling.js             # Monitor transactions via polling
├── wallet.jsp             # Dashboard frontend
├── styles.css             # Dashboard styling
├── TokenWiseDashboard/
│   ├── WalletServlet.java     # Servlet logic for data fetching
│   ├── ExportCSVServlet.java  # CSV export functionality
├── index.js
├── DBConnection.java
├── package.json
├── package-lock.json
├── web.xml
├── README.md
```

---

## 📅 Output Example (CSV)

```
wallet_address,token_amount,timestamp,action,protocol
3ZCZ...TPSM8v,1200.00,2025-07-05 13:30:00,buy,Jupiter
8bkj...QCmeE5,500.00,2025-07-05 13:31:00,sell,Unknown
```

---

## ✨ Future Improvements

* Replace polling with WebSocket (Helius Stream)
* Add frontend filters for protocol
* Host dashboard publicly (Render/Netlify)
* Support multiple token tracking
* Add Telegram alerts

---

## 👨‍💼 Developed By

**Mohammed Sumair Khan**
Java Developer | Web3 Learner | Bangalore, India
📧 [sumairk0777@gmail.com](mailto:sumairk0777@gmail.com)
📞 +91-9916358557
