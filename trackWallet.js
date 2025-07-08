const { getAccount } = require('@solana/spl-token');

async function fetchAndSaveTopWallets() {
    try {
        const largestAccounts = await connection.getTokenLargestAccounts(TOKEN_MINT);
        const accountInfos = largestAccounts.value;

        for (const account of accountInfos) {
            const tokenAccountPubkey = account.address;
            const amount = parseInt(account.amount);

            const tokenAccountInfo = await connection.getParsedAccountInfo(tokenAccountPubkey);
            const wallet = tokenAccountInfo.value.data.parsed.info.owner;

            const protocol = 'Jupiter'; // Placeholder
            const now = new Date();
            const timestamp = now.toISOString().slice(0, 19).replace('T', ' ');

            const query = `INSERT INTO wallet (wallet_address, token_amount, timestamp, protocol) 
                           VALUES (?, ?, ?, ?)`;

            db.query(query, [wallet, amount, timestamp, protocol], (err) => {
                if (err) console.error(`❌ Error inserting wallet ${wallet}:`, err);
                else console.log(`✅ Inserted: ${wallet}`);
            });
        }

    } catch (err) {
        console.error('❌ Error:', err);
    }
}
