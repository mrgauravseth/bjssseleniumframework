package uk.nhs.ers.techtest.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
public final class HttpService {
	
	private BufferedReader bufferedReader;
	private StringBuilder stringBuilder;
	private String line;
	private String[] response = new String[2];
	private HttpURLConnection conn = null;
	private OutputStreamWriter outputStreamWriter = null;
	private static final Logger logger = LoggerFactory.getLogger(HttpService.class);	
	
	/**
	 * $p
	 * A simple POJO, which will contain the Java methods that are mapped to the textual steps
	 * The methods need to annotated with one of the JBehave annotations and the annotated value
	 * should contain a regex pattern that matches the textual step$/
	 * 
	 * Utility program....
	 * Receives request for rest service call and return the response code and the response messageServie 
	 *
	 */
	
		
	
	/**
	 * 
	 * Check tomcat is running
	 * @param string url of the tomcat service
	 * @return
	 */
	public final void serviceRunning(String urlStr) throws IOException {
  		if (logger.isDebugEnabled()) {
		   logger.debug("*** serviceRunning() urlStr: " + urlStr);
		} 
 
		try {		 
			URL url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
	  		if (logger.isDebugEnabled()) {
	  		   logger.debug("*** GET call: " + conn.getResponseCode());
			} 
		}
		catch (IOException e) {				// TODO Auto-generated catch block
			System.err.println(e.getMessage());
			System.err.println("*** HttpGet - SSB service unavailable, http response code: "+ conn.getResponseCode()); // + conn.getResponseCode());
			
			throw new IOException(conn.getResponseMessage());
		}
	}
	/**
	 * 
	 * Perform the Http Get call
	 * @param String Url of the service
	 * @return String array, the response code and the response message
	 */
	public final String[] httpGet(String urlStr) {
  		if (logger.isDebugEnabled()) {
		   logger.debug("*** httpGet urlStr: " + urlStr);
		} 

		response[1] = "";
		try {
			// urlStr = "http://172.22.36.144:8080/ssb-gateway/roles/044737673515%2020123%2020234%20R8000/";
			URL url = new URL(urlStr);				
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			if (logger.isDebugEnabled()) {
			   logger.debug("*** Response status code: " + conn.getResponseCode());	
			}
		
			response[0] = Integer.toString(conn.getResponseCode());
			if (response[0].equals("200")) {
				response[1] = getResponse();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("*** error in httpget ***"); 
			System.err.println(e.getMessage());
			System.err.println("*** " + conn.getErrorStream()); 			
			e.printStackTrace();
		//	System.exit(1);
		}
		if (logger.isDebugEnabled()) {
		   logger.debug("*** response: " + response[0] + " " + response[1]);	
		} 
		return response; 		  
	}
	/**
	 * Perform the Http Put call
	 * @param String Url of the service
	 * @return String array, the response code and the response message
	 */
	public final String[] httpPut(String urlStr) throws MalformedURLException {
  		if (logger.isDebugEnabled()) {
		   logger.debug("*** httpPut urlStr: " + urlStr);
		} 

		URL url = new URL(urlStr);
		response[1] = "";
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("PUT");
			outputStreamWriter = new OutputStreamWriter(conn.getOutputStream());
			outputStreamWriter.write("Resource content");
			outputStreamWriter.close();
			response[0] = Integer.toString(conn.getResponseCode());
			if (response[0].equals("200")) {
				response[1] = getResponse();
			}
		}
		catch (IOException e) {
		// TODO Auto-generated catch block
			System.err.println(e.getMessage());
			e.printStackTrace();
	  }
		return response;
	}	
	/**
	 * Perform the Http Delete call
	 * @param String Url of the service
	 * @return String array, the response code and the response message
	 */
	public final String[] httpDelete(String urlStr) throws MalformedURLException {
  		if (logger.isDebugEnabled()) {
		   logger.debug("*** httpDelete urlStr: " + urlStr);
		} 
		
		URL url = new URL(urlStr);
		response[1] = "";
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestProperty(
			    "Content-Type", "application/x-www-form-urlencoded" );
			conn.setRequestMethod("DELETE");
			conn.connect();
			response[0] = Integer.toString(conn.getResponseCode());
			if (response[0].equals("200")) {
				response[1] = getResponse();
			}
		}
	  catch (IOException e) {
		// TODO Auto-generated catch block
			System.err.println(e.getMessage());
			e.printStackTrace();	
	  }
		return response;
	}
	/**
	 * Perform the Http Post call
	 * @param String Url of the service
	 * @return String array, the response code and the response message
	 */
	public final String[] httpPost(String urlStr) throws MalformedURLException {
  		if (logger.isDebugEnabled()) {
  			logger.debug("*** httpPost urlStr: " + urlStr);
  		} 
            
		String[] paramName = {"uid","uid"};
		String[] paramVal  = {"103255370987","COX LYNNE"};

		URL url;
		url = new URL(urlStr);
		response[1] = "";
			
		try {
			  conn = (HttpURLConnection) url.openConnection();		 
			  conn.setRequestMethod("POST");		 
			  conn.setDoOutput(true);
			  conn.setDoInput(true);
			  conn.setUseCaches(false);
			  conn.setAllowUserInteraction(false);
			  conn.setRequestProperty("Content-Type",
		      "application/x-www-form-urlencoded");
 
			  OutputStream out = conn.getOutputStream();
 	 
			  Writer writer = new OutputStreamWriter(out, "UTF-8");
			  for (int i = 0; i < paramName.length; i++) {
				  writer.write(paramName[i]);
				  writer.write("=");
				  writer.write(URLEncoder.encode(paramVal[i], "UTF-8"));
				  writer.write("&");
			  }
			  writer.close();
			  		  		 
			  response[0] = Integer.toString(conn.getResponseCode());
			  if (response[0].equals("200")) {
				  response[1] = getResponse();
			  }
			  out.close();
			  conn.disconnect();
			  }
			catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace(); 		  
				}
			catch (NullPointerException npe) {
					// TODO Auto-generated catch block
					npe.printStackTrace(); 
				}
			return response;
		}
		/**
		 * Perform the Http Invalid function call
		 * @param String Url of the service
		 * @return String array, the response code and the response message
		 */
		public final String[] httpInvalid(String urlStr) throws MalformedURLException {
	  		if (logger.isDebugEnabled()) {
			   logger.debug("*** httpInvalid urlStr: " + urlStr);
			} 

			URL url = new URL(urlStr);
			response = null;
			try {
				conn = (HttpURLConnection) url.openConnection();
				conn.setDoOutput(true);
				conn.setRequestProperty(
				    "Content-Type", "application/x-www-form-urlencoded" );
				conn.setRequestMethod("INVALID");
				conn.connect();
				response[0] = Integer.toString(conn.getResponseCode());
				if (response[0].equals("200")){
				    response[1] = getResponse();
				}
			}
		    catch (ProtocolException e) {
					// TODO Auto-generated catch block
					// check for valid response codes here....
				e.printStackTrace();
		   }
		   catch (IOException e) {
			// TODO Auto-generated catch block
			// check for valid response codes here....
			  e.printStackTrace();
		    }

		  return response;
		}
		/**
		 * Retrieve the response and return as a string		 
		 * @return String response from the restservice call
		 */
		private final String getResponse() {            		      

		 // Buffer the result into a string
			try {
				bufferedReader = new BufferedReader(
				new InputStreamReader(conn.getInputStream()));
				stringBuilder = new StringBuilder();
 
				while ((line = bufferedReader.readLine()) != null) {
					stringBuilder.append(line);
				}
				bufferedReader.close();              
			}
			catch (IOException e) {
			// TODO Auto-generated catch block
				System.err.println("*** IOException - getResponse() ***");
				System.err.println(e.getMessage());
				e.printStackTrace();
				System.exit(1);
			}
			return stringBuilder.toString();
		}
	}