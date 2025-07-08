const web3 = require('@solana/web3.js');
const fs = require('fs');
const os = require('os');

(async () => {
  // Connect to Solana Devnet
  const connection = new web3.Connection(web3.clusterApiUrl('devnet'), 'confirmed');
  console.log("Connected to Devnet ✅");

  // Read and parse the keypair file
  const secretKeyString = fs.readFileSync(os.homedir() + '/.config/solana/id.json', 'utf8');
  const secretKey = Uint8Array.from(JSON.parse(secretKeyString));
  const keypair = web3.Keypair.fromSecretKey(secretKey);

  console.log("Wallet public key:", keypair.publicKey.toBase58());

  // Get balance
  const balance = await connection.getBalance(keypair.publicKey);
  console.log(`Wallet balance: ${balance / web3.LAMPORTS_PER_SOL} SOL`);
})();
