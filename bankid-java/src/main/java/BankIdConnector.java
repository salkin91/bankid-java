import javax.net.ssl.*;
import java.io.*;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;

public class BankIdConnector {
	private SSLContext sslContext = null;
	private final String baseUrl = "https://appapi2.test.bankid.com/rp/v5.1/";

	public BankIdConnector () throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, CertificateException, UnrecoverableKeyException {
		setUp();
		HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
	}

	private void setUp() throws KeyStoreException, IOException, NoSuchAlgorithmException, KeyManagementException, CertificateException, UnrecoverableKeyException {
		KeyStore clientStore = KeyStore.getInstance("PKCS12");
		clientStore.load(new FileInputStream("/Users/niklas/Downloads/FPTestcert3_20200618.p12"), "qwerty123".toCharArray());

		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(clientStore, "qwerty123".toCharArray());
		KeyManager[] kms = kmf.getKeyManagers();

		KeyStore trustStore = KeyStore.getInstance("JKS");
		trustStore.load(new FileInputStream("/Library/Java/JavaVirtualMachines/jdk1.8.0_73.jdk/Contents/Home/jre/lib/security/cacerts"), "changeit".toCharArray());

		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(trustStore);
		TrustManager[] tms = tmf.getTrustManagers();

		sslContext = SSLContext.getInstance("TLS");
		sslContext.init(kms, tms, new SecureRandom());
	}

	public String callBankId(String method, String json) throws IOException {
		URL url = new URL(baseUrl + method);
		HttpsURLConnection urlConn = (HttpsURLConnection) url.openConnection();
		setHeader(urlConn);

		try (OutputStream os = urlConn.getOutputStream()) {
			byte[] input = json.getBytes(StandardCharsets.UTF_8);
			os.write(input, 0, input.length);
		}
		StringBuilder response = new StringBuilder();
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(urlConn.getInputStream(), "UTF-8"))) {
			String responseLine = null;
			while ((responseLine = br.readLine()) != null) {
				response.append(responseLine.trim());
			}
		}
		return response.toString();
	}

	private void setHeader(HttpsURLConnection urlConnection) throws ProtocolException {
		urlConnection.setRequestProperty("Content-Type", "application/json");
		urlConnection.setRequestProperty("Accept", "application/json");
		urlConnection.setRequestMethod("POST");
		urlConnection.setDoOutput(true);
		urlConnection.setDoInput(true);
	}

}
