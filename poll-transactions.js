const mysql = require('mysql2');
const fetch = require('node-fetch');

// wallets to track
const walletsToTrack = [
  '3ZCZDvyFQswEezauPTHeR82yq5ghipa65s8rUVTPSM8v',
  '8bkjspFVbxq7MFriBfaaz8ckyp2fGiV7x1nTFdQCmeE5'
];

// === MySQL DB Setup ===
const db = mysql.createConnection({
  host: 'localhost',
  user: 'root',
  password: 'Sumair@1',
  database: 'wallet'
});

const HELIUS_API_KEY = '2fde24e6-6140-45ad-8b98-dbc443598c83';

async function pollTransactions() {
  try {
    for (const wallet of walletsToTrack) {
      const response = await fetch(
        `https://api.helius.xyz/v0/addresses/${wallet}/transactions?api-key=${HELIUS_API_KEY}&limit=1`
      );
      const data = await response.json();

      if (!Array.isArray(data)) {
        console.log(`No data for wallet ${wallet}`);
        continue;
      }

      for (const tx of data) {
        const timestamp = new Date(tx.timestamp * 1000).toISOString().slice(0, 19).replace('T', ' ');
        const signature = tx.signature;
        const protocol = tx.description?.toLowerCase().includes('jupiter') ? 'Jupiter' : 'Unknown';
        const tokenAmount = 0; 

        const query = `
          INSERT IGNORE INTO wallet 
            (wallet_address, token_amount, timestamp, protocol, signature)
          VALUES (?, ?, ?, ?, ?)`;

        db.query(query, [wallet, tokenAmount, timestamp, protocol, signature], (err) => {
          if (err) console.error(`❌ MySQL insert error for ${wallet}:`, err);
          else console.log(`✅ Inserted tx for wallet: ${wallet}`);
        });
      }
    }
  } catch (err) {
    console.error('❌ Polling error:', err.message);
  }
}

// Poll every 10 seconds
setInterval(pollTransactions, 10000);
