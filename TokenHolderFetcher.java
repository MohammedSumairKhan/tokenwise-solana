package com.walletSevlet;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;

import org.json.JSONArray;
import org.json.JSONObject;

import com.database.DBConnection;

public class TokenHolderFetcher {
    public static void main(String[] args) {
        try {
            String apiKey = "2fde24e6-6140-45ad-8b98-dbc443598c83";
            String rpcUrl = "https://mainnet.helius-rpc.com/?api-key=" + apiKey;

            // Step 1: Create Request Body
            String requestBody = """
            {
              "jsonrpc":"2.0","id":1,"method":"getProgramAccounts",
              "params":["TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA",
                {"encoding":"jsonParsed","filters":[
                  {"memcmp":{"offset":0,"bytes":"9BB6NFEcjBCtnNLFko2FqVQBq8HHM13kCyYcdQbgpump"}},
                  {"dataSize":165}
                ]}
              ]
            }
            """;

            // Step 2: Open Connection
            URL url = new URL(rpcUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Step 3: Send JSON Body
            OutputStream os = conn.getOutputStream();
            os.write(requestBody.getBytes());
            os.flush();
            os.close();

            // Step 4: Read Response
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
				sb.append(line);
			}
            br.close();

            // Step 5: Parse JSON
            JSONObject response = new JSONObject(sb.toString());
            JSONArray result = response.getJSONArray("result");

            Connection dbConn = DBConnection.requestConnection();
            PreparedStatement ps = dbConn.prepareStatement(
                "INSERT INTO wallet(wallet_address, token_amount, timestamp) VALUES (?, ?, NOW())");

            for (int i = 0; i < Math.min(result.length(), 60); i++) {
                JSONObject info = result.getJSONObject(i)
                    .getJSONObject("account")
                    .getJSONObject("data")
                    .getJSONObject("parsed")
                    .getJSONObject("info");

                String wallet = info.getString("owner");
                double amount = info.getJSONObject("tokenAmount").getDouble("uiAmount");

                ps.setString(1, wallet);
                ps.setDouble(2, amount);
                ps.addBatch();
            }

            ps.executeBatch();
            dbConn.close();

            System.out.println("✅ Wallet data inserted successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
