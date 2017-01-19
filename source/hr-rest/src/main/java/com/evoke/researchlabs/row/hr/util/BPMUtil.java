package com.evoke.researchlabs.row.hr.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.conn.SchemeRegistryFactory;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jboss.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.evoke.researchlabs.row.hr.constants.MethodType;
import com.evoke.researchlabs.row.hr.domain.BPMConfiguration;

/**
 * Class provides utility methods for working with Bonita Platform.
 * 
 * @author Zama
 *
 */
public class BPMUtil {
	private static final Logger logger = Logger.getLogger(BPMUtil.class);
	private HttpClient httpClient;
	private HttpContext httpContext;
	private String bonitaURI;
	
	/**
	 * Verify Bonita environment by login.
	 * 
	 * @param bpmConfiguration
	 * @return
	 */
	public static Boolean checkConnection(BPMConfiguration bpmConfiguration){
		Boolean status = true;
		try {
			PoolingClientConnectionManager conMan = getConnectionManager();
			BPMUtil util = new BPMUtil(new DefaultHttpClient(conMan), bpmConfiguration.getUrl());
			util.loginAs(bpmConfiguration.getTechnicalUser(), bpmConfiguration.getTechnicalPwd());
			logger.info("Successfully logged in as Technical User!!!");
		} catch (Exception e) {
			logger.error("Error while contacting Bonita Engine.", e);
			status = false;
		}
		return status;
	}
	
	public static Boolean importOrganization(BPMConfiguration bpmConfig) {
		Boolean status = false;
		PoolingClientConnectionManager conMan = getConnectionManager();
		BPMUtil util = new BPMUtil(new DefaultHttpClient(conMan), bpmConfig.getUrl());
		
		util.loginAs(bpmConfig.getTechnicalUser(), bpmConfig.getTechnicalPwd());
		
		if(util.importOrganizationFromFile(new File(bpmConfig.getOrganizationFile())) != -1){
			logger.info("Organization imported successfully!!!");
			status = true;			
		}  
		util.logout();
		
		return status;
	}
	
	public static Boolean updateProfile(BPMConfiguration bpmConfig) {
		Boolean status = false;
		PoolingClientConnectionManager conMan = getConnectionManager();
		BPMUtil util = new BPMUtil(new DefaultHttpClient(conMan), bpmConfig.getUrl());
		util.loginAs(bpmConfig.getTechnicalUser(), bpmConfig.getTechnicalPwd());
		try {
			int userId = util.extractIdFrom(util.executeGetRequest("/API/identity/user?p=0&c=10&o=lastname%20ASC&s=walter&f=enabled%3dtrue"));
			logger.info("User Id: "+userId);

			//Update profile
			String payLoad = "{\"profile_id\":\"1\",\"member_type\":\"ADMINISTRATOR\",\"user_id\":\""+userId+"\"}";
			HttpResponse response4 = util.executePostRequest("/API/portal/profileMember", payLoad);
			logger.info("Response: "+EntityUtils.toString(response4.getEntity()));

			payLoad = "{\"profile_id\":\"2\",\"member_type\":\"USER\",\"user_id\":\""+userId+"\"}";
			HttpResponse response5 = util.executePostRequest("/API/portal/profileMember", payLoad);
			logger.info("Response: "+EntityUtils.toString(response5.getEntity()));
			status = true;
		} catch (ParseException | IOException e) {
			logger.error("Error while updating user profile.", e);
		}
		util.logout();
		
		return status;
	}
	
	public static Boolean installBDM(BPMConfiguration bpmConfig) {
		Boolean status = true;
		PoolingClientConnectionManager conMan = getConnectionManager();
		BPMUtil util = new BPMUtil(new DefaultHttpClient(conMan), bpmConfig.getUrl());
		util.loginAs(bpmConfig.getTechnicalUser(), bpmConfig.getTechnicalPwd());
		//Pause the Tenant (Services)
		try {
			String payLoad = "{ \"paused\":\"true\" }";
			HttpResponse response1 = util.executePutRequest("/API/system/tenant/unusedid", payLoad);
			logger.info("Response: "+EntityUtils.toString(response1.getEntity()));

			//Import BDM
			String tempFileName = util.uploadFile(bpmConfig.getBdmFile());
			payLoad = "{\"fileUpload\":\""+tempFileName+"\"}";
			logger.info("/API/tenant/bdm"+ "\t" + payLoad);
			HttpResponse response2 = util.executePostRequest("/API/tenant/bdm", payLoad);
			logger.info("Response: "+EntityUtils.toString(response2.getEntity()));

			//Start the Tenant (Services)
			payLoad = "{ \"paused\":\"false\" }";
			HttpResponse response3 = util.executePutRequest("/API/system/tenant/unusedid", payLoad);
			logger.info("Response: "+EntityUtils.toString(response3.getEntity()));
		} catch (ParseException | IOException e) {
			logger.error("Error while importing BDM file.", e);
			status = false;
		}
		util.logout();
		
		return status;
	}
	
	public static Boolean installProcess(BPMConfiguration bpmConfig){
		Boolean status = true;
		PoolingClientConnectionManager conMan = getConnectionManager();
		BPMUtil util = new BPMUtil(new DefaultHttpClient(conMan), bpmConfig.getUrl());
		util.loginAs(bpmConfig.getUserName(), bpmConfig.getPwd());
		try {
			logger.info("Upload Bar file to Docker container: "+bpmConfig.getBarFile());
			String tempLocation = util.uploadGeneratedBar(bpmConfig.getBarFile());

			//Deploy bar from Docker Container's temp location
			logger.info("Installing Bar from Docker container's temp directory: /opt//bonita/BonitaBPMCommunity-7.3.3-Tomcat-7.0.67/temp/bonita_portal_1\\@51b1120f385e/tenants/1/"+tempLocation);
			long processId = util.installProcessFromUploadedBar(tempLocation);
			logger.info("Process deployed with id: " + processId);

			//Enable the process
			logger.info("Enabling process '" + bpmConfig.getProcessName() + "' (ID:" + processId + ")...");
			util.enableProcess(processId);
			logger.info("Process Enabled!");
		} catch (IOException e) {
			logger.error("Error while importing Process.", e);
			status = false;
		}
		util.logout();
		
		return status;
	}
	
	/**
	 * Setup Bonita environment: Import Organization, Setup User Profile, Import BDM and Process, Enable the Process.
	 * 
	 * @param bpmConfiguration
	 * @return
	 */
	public static Boolean setupBonitaEnvironment(BPMConfiguration bpmConfiguration){
		Boolean status = true;
		try {
			PoolingClientConnectionManager conMan = getConnectionManager();
			BPMUtil util = new BPMUtil(new DefaultHttpClient(conMan), bpmConfiguration.getUrl());
			
			//Step 1: Import Organization
			logger.info("---Step 1: Import Organization---");
			importOrganization(bpmConfiguration);

			//Step 2: Update User Profiles
			logger.info("\n---Step 2: Update User Profiles---");
			updateProfile(bpmConfiguration);

			//Step 3: Import Business Data Models 
			logger.info("\n---Step 3: Import Business Data Models---");
			installBDM(bpmConfiguration);

			//Step 4: Import Process and Activate it
			logger.info("\n---Step 4: Import Process---");
			installProcess(bpmConfiguration);
		} catch (Exception e) {
			logger.error("Error while setting up Bonita Environment.", e);
		}
		return status;
	}

	/**
	 * Perform BPM related operations using this method.
	 * 
	 * @param uri
	 * @param userName
	 * @param pwd - password
	 * @param method - GET/POST
	 * @return
	 */
	public static HttpResponse callBPMEngine(String bonitaURI, String apiURI, String userName, String pwd, String method, String payloadAsString){
		HttpResponse httpResponse = null;
		PoolingClientConnectionManager conMan = getConnectionManager();
		BPMUtil util = new BPMUtil(new DefaultHttpClient(conMan), bonitaURI);
		util.loginAs(userName, pwd);

		if(MethodType.GET.toString().equalsIgnoreCase(method)){
			httpResponse = util.executeGetRequest(apiURI);
		}else if(MethodType.POST.toString().equalsIgnoreCase(method)){
			httpResponse = util.executePostRequest(apiURI, payloadAsString);
		}else if(MethodType.PUT.toString().equalsIgnoreCase(method)){
			httpResponse = util.executePutRequest(apiURI, payloadAsString);
		}

		util.logout();
		return httpResponse;
	}

	public void loginAs(String username, String password) {
		try {
			CookieStore cookieStore = new BasicCookieStore();
			httpContext = new BasicHttpContext();
			httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			urlParameters.add(new BasicNameValuePair("username", username));
			urlParameters.add(new BasicNameValuePair("password", password));
			urlParameters.add(new BasicNameValuePair("redirect", "false"));

			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(urlParameters, "utf-8");
			executePostRequest("/loginservice", entity);
		} catch (Exception e) {
			logger.error("Error while perfoming login operation.", e);
		}
	}

	private void logout() {
		consumeResponse(executeGetRequest("/logoutservice"),true);
	}

	private HttpResponse executeGetRequest(String apiURI) {
		HttpResponse response = null;
		try {
			logger.info(bonitaURI + apiURI);
			HttpGet getRequest = new HttpGet(bonitaURI + apiURI);
			response = httpClient.execute(getRequest, httpContext);
		} catch (Exception e) {
			logger.error("Error while perfoming executeGetRequest operation.", e);
		}
		return response;
	}

	private HttpResponse executePostRequest(String apiURI, String payloadAsString) {
		HttpResponse response = null;
		try {
			logger.info(bonitaURI + apiURI);
			HttpPost postRequest = new HttpPost(bonitaURI + apiURI);
			StringEntity input = new StringEntity(payloadAsString);
			input.setContentType("application/json");
			postRequest.setEntity(input);
			response = httpClient.execute(postRequest, httpContext);
		} catch (Exception e) {
			logger.error("Error while perfoming executePostRequest operation.", e);
		}
		return response;
	}

	private int executePostRequest(String apiURI, UrlEncodedFormEntity entity) {
		int responseStatus = -1;
		try {
			HttpPost postRequest = new HttpPost(bonitaURI + apiURI);
			postRequest.setEntity(entity);
			HttpResponse response = httpClient.execute(postRequest, httpContext);
			responseStatus = consumeResponse(response, true);
		} catch (Exception e) {
			logger.error("Bonita bundle may not have been started, or the URL is invalid. Please verify hostname and port number. URL used is: ",e);
		} 
		return responseStatus;
	}

	private int consumeResponse(HttpResponse response, boolean printResponse) {
		String responseAsString = consumeResponseIfNecessary(response);
		logger.info(responseAsString);
		return ensureStatusOk(response);
	}

	private String consumeResponseIfNecessary(HttpResponse response) {
		String responseStr = "";
		if (response.getEntity() != null) {
			try(BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
				StringBuilder result = new StringBuilder();
				String line;
				while ((line = rd.readLine()) != null) {
					result.append(line);
				}
				responseStr = result.toString();
			} catch (Exception e) {
				logger.error("Failed to consume response.", e); 
			}
		} 
		return responseStr;
	}

	private int ensureStatusOk(HttpResponse response) {
		if (response.getStatusLine().getStatusCode() != 201 && response.getStatusLine().getStatusCode() != 200) {
			logger.error("Failed : HTTP error code : " + response.getStatusLine().getStatusCode() + ":" + response.getStatusLine().getReasonPhrase());
		}
		return response.getStatusLine().getStatusCode();
	}

	private static PoolingClientConnectionManager getConnectionManager() {
		PoolingClientConnectionManager conMan = new PoolingClientConnectionManager(SchemeRegistryFactory.createDefault());
		conMan.setMaxTotal(200);
		conMan.setDefaultMaxPerRoute(200);
		return conMan;
	}

	private void enableProcess(long processId) {
		String payloadAsString = "{\"activationState\":\"ENABLED\"}";
		consumeResponse(executePutRequest("/API/bpm/process/" + processId, payloadAsString),true);
	}

	private long installProcessFromUploadedBar(String uploadedFilePath) {
		String payloadAsString = "{\"fileupload\":\"" + uploadedFilePath + "\"}";
		logger.info(payloadAsString);
		return extractProcessId(executePostRequest("/API/bpm/process", payloadAsString));
	}

	private long extractProcessId(HttpResponse response) {
		ensureStatusOk(response);
		try {
			String processInJSON = EntityUtils.toString(response.getEntity());

			String remain = processInJSON.substring(processInJSON.indexOf("id\":") + 5);
			String id = remain.substring(0, remain.indexOf("\""));

			return Long.parseLong(id);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private String uploadGeneratedBar(String barFilePath) throws IOException, ClientProtocolException {
		File barFile = new File(barFilePath);
		HttpPost post = new HttpPost(bonitaURI + "/portal/processUpload");
		MultipartEntity entity = new MultipartEntity();
		entity.addPart("file", new FileBody(barFile));
		post.setEntity(entity);
		HttpResponse response = httpClient.execute(post, httpContext);
		return extractUploadedFilePathFromResponse(response);
	}

	private String uploadFile(String filePath) throws IOException, ClientProtocolException {
		File file = new File(filePath);
		HttpPost post = new HttpPost(bonitaURI + "/portal/fileUpload");
		MultipartEntity entity = new MultipartEntity();
		entity.addPart("file", new FileBody(file));
		post.setEntity(entity);
		HttpResponse response = httpClient.execute(post, httpContext);
		return extractUploadedFilePathFromResponse(response);
	}

	private HttpResponse executePutRequest(String apiURI, String payloadAsString) {
		try {
			HttpPut putRequest = new HttpPut(bonitaURI + apiURI);
			putRequest.addHeader("Content-Type", "application/json");
			StringEntity input = new StringEntity(payloadAsString);
			input.setContentType("application/json");
			putRequest.setEntity(input);
			return httpClient.execute(putRequest, httpContext);
		} catch (Exception e) {
			logger.error("Error while executing PUT request.", e);
			return null;
		}
	}
	
	private int extractIdFrom(HttpResponse response) {
		try {
			String body = EntityUtils.toString(response.getEntity());
			logger.info(response.getEntity().toString());
			logger.info("extractIdFrom: response.getEntity() - "+response.getEntity()+"\nbody - "+body);
			JSONArray resp = new JSONArray(body);
			return resp.getJSONObject(0).getInt("id");
		} catch (Exception e) {
			logger.error("Error while extracting User ID from response.", e);
			return -1;
		}
	}

	public static int getUserIdUsingName(BPMConfiguration bpmConfiguration){
		int userId = -1;
		Resource resource = new ClassPathResource("dev.properties");
		try {
			Properties props = PropertiesLoaderUtils.loadProperties(resource);
			PoolingClientConnectionManager conMan = getConnectionManager();
			BPMUtil util = new BPMUtil(new DefaultHttpClient(conMan), bpmConfiguration.getUrl());
			util.loginAs(props.getProperty("bonita.user.technical.name"), props.getProperty("bonita.user.technical.pwd"));
			userId = util.extractIdFrom(util.executeGetRequest("/API/identity/user?p=0&c=10&o=lastname%20ASC&s=walter&f=enabled%3dtrue"));
			util.logout();
		} catch (IOException e) {
			logger.error("Error while getting User Id.", e);
		}
		
		return userId;
	}
	
	public static String assignHumanTask(BPMConfiguration bpmConfiguration, String processResponse) {
		String response = null;
		PoolingClientConnectionManager conMan = getConnectionManager();
		BPMUtil util = new BPMUtil(new DefaultHttpClient(conMan), bpmConfiguration.getUrl());
		try {
			String payLoad;
			payLoad = "{\"assigned_id\":\""+BPMUtil.getUserIdUsingName(bpmConfiguration)+"\"}";
			int caseId = new JSONObject(processResponse).getInt("caseId");

			//Get Task Id using Case ID
			HttpResponse httpResponse3 = BPMUtil.callBPMEngine(bpmConfiguration.getUrl(), "/API/bpm/humanTask?p=0&c=10&f=caseId="+caseId, bpmConfiguration.getUserName(),
					bpmConfiguration.getPwd(), "GET", null);
			processResponse = EntityUtils.toString(httpResponse3.getEntity());
			logger.info("processResponse: "+processResponse);
			JSONObject task = new JSONArray(processResponse).getJSONObject(0);
			String taskId = task.getString("id");
			
			//Assign Human task to User
			logger.info(bpmConfiguration.getUrl()+"/API/bpm/humanTask/"+taskId);
			HttpResponse httpResponse4 = BPMUtil.callBPMEngine(bpmConfiguration.getUrl(), "/API/bpm/humanTask/"+taskId, bpmConfiguration.getUserName(),
					bpmConfiguration.getPwd(), "PUT", payLoad);
			response = EntityUtils.toString(httpResponse4.getEntity());
		} catch (ParseException | JSONException | IOException e) {
			logger.error("Error while assigning Human Task.", e);
		}
		return response;
	}
	
	private int importOrganizationFromFile(File organizationFile) {
		int result = -1;
		try {
			logger.info("Deploying organization ... ");
			HttpPost post = new HttpPost(bonitaURI + "/portal/organizationUpload");

			MultipartEntity entity = new MultipartEntity();
			entity.addPart("file", new FileBody(organizationFile));
			post.setEntity(entity);

			HttpResponse response = httpClient.execute(post, httpContext);
			String uploadedFilePath = extractUploadedFilePathFromResponse(response);

			String payloadAsString = "{\"organizationDataUpload\":\"" + uploadedFilePath + "\"}";
			result = consumeResponse(executePostRequest("/services/organization/import", payloadAsString),true);
		} catch (Exception e) {
			logger.error("Error while importing Organization file.", e);
		}
		return result;
	}

	private String extractUploadedFilePathFromResponse(HttpResponse response) {
		try {
			return EntityUtils.toString(response.getEntity());
		} catch (Exception e) {
			logger.error("Error while uploading file.", e);
			return null;
		}
	}
	
	public BPMUtil(HttpClient client, String bonitaURI) {
		this.httpClient = client;
		this.bonitaURI = bonitaURI;
	}
}
