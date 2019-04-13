package com.microservices.core.http.microservices;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import com.jfinal.log.Log;
import com.microservices.core.http.MicroservicesHttpBase;
import com.microservices.core.http.MicroservicesHttpRequest;
import com.microservices.core.http.MicroservicesHttpResponse;
import com.microservices.exception.MicroservicesException;
import com.microservices.utils.ArrayUtils;
import com.microservices.utils.StringUtils;

public class MicroservicesHttpImpl extends MicroservicesHttpBase {

	private static final Log LOG = Log.getLog(MicroservicesHttpImpl.class);

	@Override
	public MicroservicesHttpResponse handle(MicroservicesHttpRequest request) {

		MicroservicesHttpResponse response = request.getDownloadFile() == null ? new MicroservicesHttpResponse() : new MicroservicesHttpResponse(request.getDownloadFile());
		doProcess(request, response);
		return response;
	}

	private void doProcess(MicroservicesHttpRequest request, MicroservicesHttpResponse response) {
		HttpURLConnection connection = null;
		InputStream stream = null;
		try {

			connection = getConnection(request);
			configConnection(connection, request);

			if (request.isGetRquest()) {

				connection.setInstanceFollowRedirects(true);
				connection.connect();
			}
			/**
			 * 处理 post请求
			 */
			else if (request.isPostRquest()) {

				connection.setRequestMethod("POST");
				connection.setDoOutput(true);

				/**
				 * 处理 非文件上传的 post 请求
				 */
				if (!request.isMultipartFormData()) {

					String postContent = buildParams(request);
					if (StringUtils.isNotEmpty(postContent)) {
						DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
						dos.write(postContent.getBytes(request.getCharset()));
						dos.flush();
						dos.close();
					}

				}

				/**
				 * 处理文件上传的post请求
				 */
				else {

					if (ArrayUtils.isNotEmpty(request.getParams())) {
						uploadData(request, connection);
					}

				}
			}

			stream = getInutStream(connection);

			response.setContentType(connection.getContentType());
			response.setResponseCode(connection.getResponseCode());
			response.setHeaders(connection.getHeaderFields());

			response.pipe(stream);
			response.finish();

		} catch (Throwable ex) {
			LOG.warn(ex.toString(), ex);
			response.setError(ex);
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private InputStream getInutStream(HttpURLConnection connection) throws IOException {
		return connection.getResponseCode() >= 400 ? connection.getErrorStream() : connection.getInputStream();
	}

	private void uploadData(MicroservicesHttpRequest request, HttpURLConnection connection) throws IOException {
		String endFlag = "\r\n";
		String boundary = "---------" + StringUtils.uuid();
		connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
		DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
		for (Map.Entry entry : request.getParams().entrySet()) {
			if (entry.getValue() instanceof File) {
				File file = (File) entry.getValue();
				checkFileNormal(file);
				dos.writeBytes(boundary + endFlag);
				dos.writeBytes(String.format("Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"", entry.getKey(), file.getName()) + endFlag);
				dos.writeBytes(endFlag);
				FileInputStream fStream = new FileInputStream(file);
				byte[] buffer = new byte[2028];
				for (int len = 0; (len = fStream.read(buffer)) > 0;) {
					dos.write(buffer, 0, len);
				}

				dos.writeBytes(endFlag);
			} else {
				dos.writeBytes("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"");
				dos.writeBytes(endFlag);
				dos.writeBytes(endFlag);
				dos.writeBytes(String.valueOf(entry.getValue()));
				dos.writeBytes(endFlag);
			}
		}

		dos.writeBytes("--" + boundary + "--" + endFlag);
	}

	private static void checkFileNormal(File file) {
		if (!file.exists()) {
			throw new MicroservicesException("file not exists!!!!" + file);
		}
		if (file.isDirectory()) {
			throw new MicroservicesException("cannot upload directory!!!!" + file);
		}
		if (!file.canRead()) {
			throw new MicroservicesException("cannnot read file!!!" + file);
		}
	}

	private static void configConnection(HttpURLConnection connection, MicroservicesHttpRequest request) throws ProtocolException {
		if (connection == null)
			return;
		connection.setReadTimeout(request.getReadTimeOut());
		connection.setConnectTimeout(request.getConnectTimeOut());
		connection.setRequestMethod(request.getMethod());

		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");
		if (request.getHeaders() != null && request.getHeaders().size() > 0) {
			for (Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
				connection.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}
	}

	private static HttpURLConnection getConnection(MicroservicesHttpRequest request) {
		try {
			if (request.isGetRquest()) {
				buildGetUrlWithParams(request);
			}
			if (request.getRequestUrl().toLowerCase().startsWith("https")) {
				return getHttpsConnection(request);
			} else {
				return getHttpConnection(request.getRequestUrl());
			}
		} catch (Throwable ex) {
			throw new MicroservicesException(ex);
		}
	}

	private static HttpURLConnection getHttpConnection(String urlStr) throws Exception {
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		return conn;
	}

	private static HttpsURLConnection getHttpsConnection(MicroservicesHttpRequest request) throws Exception {
		URL url = new URL(request.getRequestUrl());
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

		if (request.getCertPath() != null && request.getCertPass() != null) {

			KeyStore clientStore = KeyStore.getInstance("PKCS12");
			clientStore.load(new FileInputStream(request.getCertPath()), request.getCertPass().toCharArray());

			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(clientStore, request.getCertPass().toCharArray());
			KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();

			TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init(clientStore);

			SSLContext sslContext = SSLContext.getInstance("TLSv1");
			sslContext.init(keyManagers, trustManagerFactory.getTrustManagers(), new SecureRandom());

			conn.setSSLSocketFactory(sslContext.getSocketFactory());

		} else {
			conn.setHostnameVerifier(hnv);
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			if (sslContext != null) {
				TrustManager[] tm = { trustAnyTrustManager };
				sslContext.init(null, tm, null);
				SSLSocketFactory ssf = sslContext.getSocketFactory();
				conn.setSSLSocketFactory(ssf);
			}
		}
		return conn;
	}

	private static X509TrustManager trustAnyTrustManager = new X509TrustManager() {
		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	};

	private static HostnameVerifier hnv = new HostnameVerifier() {
		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};

}
