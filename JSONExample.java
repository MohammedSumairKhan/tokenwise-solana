package com.walletSevlet;

import org.json.JSONArray;
import org.json.JSONObject;

public class JSONExample {
	public static void main(String[] args) {
		String jsonData = """
				{
				    "jsonrpc": "2.0",
				    "result": [
				        {
				            "account": {
				                "data": {
				                    "parsed": {
				                        "info": {
				                            "owner": "wallet_1",
				                            "tokenAmount": {
				                                "uiAmount": 123.45
				                            }
				                        }
				                    }
				                }
				            }
				        },
				        {
				            "account": {
				                "data": {
				                    "parsed": {
				                        "info": {
				                            "owner": "wallet_2",
				                            "tokenAmount": {
				                                "uiAmount": 543.21
				                            }
				                        }
				                    }
				                }
				            }
				        }
				    ]
				}
				""";

		JSONObject obj = new JSONObject(jsonData);
		JSONArray result = obj.getJSONArray("result");

		for (int i = 0; i < result.length(); i++) {
			JSONObject info = result.getJSONObject(i).getJSONObject("account").getJSONObject("data")
					.getJSONObject("parsed").getJSONObject("info");

			String wallet = info.getString("owner");
			double amount = info.getJSONObject("tokenAmount").getDouble("uiAmount");

			System.out.println("Wallet: " + wallet);
			System.out.println("Amount: " + amount);
		}
	}
}
