package controller.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SuppressWarnings("deprecation")
public class HTTPUtil {

	private final static int THREADS = Runtime.getRuntime().availableProcessors();
	public static ExecutorService executor = Executors.newFixedThreadPool(THREADS);
	
	private static Logger log = LoggerFactory.getLogger(HTTPUtil.class);
	
	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}
	
	public static Future<Object> getJsonFromURL(final String surl){
		final Future<Object> future = executor.submit(new Callable<Object>() {
			
			@Override
			public Object call() throws Exception {
				try {
					URL url = new URL(surl);
					HttpURLConnection connection = (HttpURLConnection)url.openConnection();
					connection.setRequestMethod("GET");
					connection.setRequestProperty("Accept", "application/json");
					connection.setDoOutput(true);
					connection.setDoInput(true);
					
					//send request
					InputStream inputStream = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream
							,Charset.forName("UTF-8")));
					String json = readAll(reader);
					inputStream.close();
					return json;
				} catch (IOException e) {
					log.error("Failed to deserialize data from URL: " + surl);
					return null;
					//e.printStackTrace();
				}
				//return null;
			}
		});
		return future;
	}
	
	public static Future<Object> postJsonToURL(final String surl,final String json){
		final Future<Object> future = executor.submit(new Callable<Object>() {
			
			@Override
			public Object call() throws Exception {
				String jsonResponse = "";
				try {
					URL url = new URL(surl);
					HttpURLConnection connection = (HttpURLConnection)url.openConnection();
					connection.setRequestMethod("POST");
					connection.setRequestProperty("Content-type", "application/json");
					connection.setDoInput(true);
					connection.setDoOutput(true);
						
					//send request
					OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
					writer.write(json);
					writer.flush();
					
					BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					String line;
					while ((line = reader.readLine())!=null) {
						jsonResponse = jsonResponse.concat(line);
					}
					writer.close();
					reader.close();
					return jsonResponse;
				} catch (IOException e) {
					log.error("Failed to Post data to URL {}: {}", surl, e.getMessage());
					
					//e.printStackTrace();
					return jsonResponse;
				}
				//return jsonResponse;
			}
		});
		return future;
	}
	
	public static Future<Object> deleteJsonToURL(final String surl,final String json){
		final Future<Object> future = executor.submit(new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				String jsonResponse = "";
				try {
					@SuppressWarnings("resource")
					DefaultHttpClient httpClient = new DefaultHttpClient();
					HttpDeleteUtil httpDeleteUtil = new HttpDeleteUtil(surl);
					//System.out.println(surl);
					StringEntity jsonEntity = new StringEntity(json);
					//System.out.println(json.length());
					//jsonEntity.setContentEncoding("UTF-8");
					//jsonEntity.setContentType("application/json");
					httpDeleteUtil.setEntity(jsonEntity);
					HttpResponse response = httpClient.execute(httpDeleteUtil);
					//System.out.println(jsonEntity.toString());
					//System.out.println(response.getStatusLine().getStatusCode());
					if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						HttpEntity entity = response.getEntity();
						jsonResponse = EntityUtils.getContentCharSet(entity);
					}
					
					/*URL url = new URL(surl);
					HttpURLConnection connection = (HttpURLConnection)url.openConnection();
					//connection.setRequestMethod("DELETE");
					//connection.setRequestProperty("Content-type", "application/json");
					connection.setRequestMethod("PUT");
					// We have to override the post method so we can send data
					//connection.setRequestProperty("X-HTTP-Method-Override", "DELETE");
					connection.setDoOutput(true);
					connection.setDoInput(true);
					
					System.out.println(connection.getRequestMethod());
					
					//send request
					OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
					writer.write(json);
					writer.flush();
					
					BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					String line;
					while ((line = reader.readLine())!=null) {
						jsonResponse = jsonResponse.concat(line);
					}
					writer.close();
					reader.close();
					return jsonResponse;*/
				} catch (IOException e) {
					log.error("Failed to Delete data to URL: " + surl);
					return jsonResponse;
					//e.printStackTrace();
				}
				return jsonResponse;
			}
			
		});
		return future;
	}
}
