import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;

public class Main {

	public static void main(String[] args) throws IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException, KeyManagementException, InterruptedException {

		BankIdConnector bankIdConnector = new BankIdConnector();
		String json = "{\n" +
				"\"personalNumber\":\"200000000000\",\n" +
				"\"endUserIp\": \"194.168.2.25\",\n" +
				"\"userVisibleData\": \"IFRoaXMgaXMgYSBzYW1wbGUgdGV4dCB0byBiZSBzaWduZWQ=\"\n" +
				"}";

		String reply = bankIdConnector.callBankId("sign", json);
		System.out.println(reply);

		JsonObject jsonObject = new JsonParser().parse(reply).getAsJsonObject();
		String orderRef = jsonObject.get("orderRef").toString();
		while(true) {
			String collect = "{\"orderRef\":" + orderRef + "}";
			String collectReply = bankIdConnector.callBankId("collect", collect);
			JsonObject collectJson = new JsonParser().parse(collectReply).getAsJsonObject();
			String status = collectJson.get("status").getAsString();
			System.out.println(collectReply);
			if(status.equals("failed")) break;

			Thread.sleep(10000);
		}
	}



}
