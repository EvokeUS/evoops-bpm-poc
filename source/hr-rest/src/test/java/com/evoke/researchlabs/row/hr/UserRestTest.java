package com.evoke.researchlabs.row.hr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.evoke.researchlabs.row.hr.domain.BPMConfiguration;
import com.evoke.researchlabs.row.hr.domain.BPMTask;
import com.evoke.researchlabs.row.hr.domain.User;
import com.evoke.researchlabs.row.hr.util.BPMUtil;

public class UserRestTest {
	private static final Logger logger = Logger.getLogger(UserRestTest.class);
	private static final String SERVICE_URL = "http://localhost:8181/hr-rest/hrrest/";
	private static final String PROCESS_NAME = "SimpleProcess";
	private RestTemplate restTemplate = new RestTemplate();
	private BPMConfiguration bpmConfiguration = getBPMConfiguration(); 


	//@Test
	public void setupBPMEnvironment(){
		BPMConfiguration bpmConfiguration = getBPMConfiguration();
		
		ResponseEntity<Boolean> response = restTemplate.postForEntity("http://localhost:8181/hr-rest/bpm/setup", bpmConfiguration, Boolean.class);
		logger.info("http://localhost:8181/hr-rest/bpm/setup");
		logger.info(response.getBody().toString());
	}
	
	public void getUserId(){
		
	}
	
	@Test
	public void createUser(){
		User user = new User("user7","Delhi", "user7"+"@gmail.com");
		user.setProcessName(PROCESS_NAME);
		ResponseEntity<String> response = restTemplate.postForEntity(SERVICE_URL+"create", user, String.class);
		logger.info(SERVICE_URL+"create"+"\n");
		logger.info(response.getBody().toString());
		/*BPMConfiguration bpmConfiguration = getBPMConfiguration();
		logger.info(BPMUtil.getUserIdUsingName(bpmConfiguration));*/
	}
	
	//@Test
	public void setupOrganization() throws ParseException, IOException{
		HttpResponse httpResponse  = updateUserProfile("1", "USER", "20");
		String responseBody = EntityUtils.toString(httpResponse.getEntity());
		logger.info(responseBody);
	}


	private HttpResponse updateUserProfile(String profileId, String memberType, String userId) {
		String payLoad = "{\"profile_id\":\""+profileId+"\",\"member_type\":\""+memberType+"\",\"user_id\":\"+"+userId+"\"}";
		return BPMUtil.callBPMEngine(bpmConfiguration.getUrl(), "/API/portal/profileMember", bpmConfiguration.getUserName(), bpmConfiguration.getPwd(), "POST", payLoad);
	}

	//@Test
	public void submitBPMCase(){
		User user = new User("test1", "Hyd", "test1@gmail.com");
		user.setProcessName("SimpleProcess");
		BPMConfiguration bpmConf;
		try {
			HttpResponse httpResponse = BPMUtil.callBPMEngine(bpmConfiguration.getUrl(), "/API/bpm/process?p=0&c=10&f=displayName%3d"+user.getProcessName(), 
					bpmConfiguration.getUserName(), bpmConfiguration.getPwd(), "GET", null);
			String responseBody = EntityUtils.toString(httpResponse.getEntity());
			logger.info(responseBody);
			JSONArray json = new JSONArray(responseBody);
			JSONObject details = json.getJSONObject(0);
			logger.info(details);
			String processId = details.getString("id");
			logger.info("Process Id: "+processId);

			if(processId != null){
				logger.info("Creating case with process id: "+processId);
				String payLoad = "{\"registation_refInput\":{\"userName\":\""+user.getUsername()+"\",\"city\":\""+user.getAddress()+"\",\"email\":\""+user.getEmail()+"\"}}";

				HttpResponse httpResponse2 = BPMUtil.callBPMEngine(bpmConfiguration.getUrl(), "/API/bpm/process/"+processId+"/instantiation", bpmConfiguration.getUserName(), bpmConfiguration.getPwd(), "POST", payLoad);
				responseBody = EntityUtils.toString(httpResponse2.getEntity());
				logger.info(responseBody);
			}
		} catch (Exception e) {
			logger.error("Error while creating case.", e);
		}
	}

	//@Test
	public void bpmGetAllTasks(){
		HttpResponse httpResponse = BPMUtil.callBPMEngine(bpmConfiguration.getUrl(), "/API/bpm/humanTask?p=0&c=10", bpmConfiguration.getUserName(), bpmConfiguration.getPwd(), "GET", null);
		String responseBody;
		try {
			responseBody = EntityUtils.toString(httpResponse.getEntity());
			logger.info(responseBody);
			JSONArray json = new JSONArray(responseBody);
			if(json!=null){
				List<BPMTask> tasks = new ArrayList<>();

				for(int i=0; i<json.length(); i++){
					JSONObject detail = json.getJSONObject(i);
					BPMTask task = new BPMTask();
					task.setCaseId(detail.getString("caseId"));
					task.setDisplayDescription(detail.getString("displayDescription"));
					task.setDisplayName(detail.getString("displayName"));
					task.setDueDate(detail.getString("dueDate"));
					task.setId(detail.getString("id"));
					task.setParentCaseId(detail.getString("parentCaseId"));
					task.setPriority(detail.getString("priority"));
					task.setProcessId(detail.getString("processId"));
					task.setState(detail.getString("state"));
					task.setType(detail.getString("type"));
					task.setLastUpdateDate(detail.getString("last_update_date"));
					tasks.add(task);
				}
				logger.info(tasks);
			}
		} catch (ParseException | IOException | JSONException e) {
			logger.error(e);
		}
	}

	//@Test
	public void bpmGetProcessByName(){
		HttpResponse httpResponse = BPMUtil.callBPMEngine(bpmConfiguration.getUrl(), "/API/bpm/process?p=0&c=10&f=displayName%3dSimpleProcess", bpmConfiguration.getUserName(), bpmConfiguration.getPwd(), "GET", null);

		String responseBody;
		try {
			responseBody = EntityUtils.toString(httpResponse.getEntity());
			logger.info(responseBody);
			JSONArray json = new JSONArray(responseBody);
			JSONObject details = json.getJSONObject(0);
			logger.info(details);
			JSONObject process = json.getJSONObject(0);
			logger.info("Process Id: "+details.getString("id"));
		} catch (ParseException | IOException | JSONException e) {
			logger.error(e);
		}
	}

	//@Test
	public void bpmGetAllProcess(){
		HttpResponse httpResponse = BPMUtil.callBPMEngine(bpmConfiguration.getUrl(), "/API/bpm/process?p=0&c=10", bpmConfiguration.getUserName(), bpmConfiguration.getPwd(), "GET", null);

		String responseBody;
		try {
			responseBody = EntityUtils.toString(httpResponse.getEntity());
			logger.info(responseBody);
			JSONArray json = new JSONArray(responseBody);
			JSONObject details = json.getJSONObject(0);
			logger.info(details);
			//JSONObject process = json.getJSONObject(0);
			//LOGGER.info("Process Id: "+details.getString("id"));
		} catch (ParseException | IOException | JSONException e) {
			logger.error(e);
		}
	}

	//@Test
	@SuppressWarnings("rawtypes")
	public void getAllUsers(){
		ResponseEntity<List> response = restTemplate.getForEntity(SERVICE_URL+"users", List.class);
		logger.info("\n"+response.getBody().toString());
	}

	//@Test
	public void getUserById(){
		String userId = "3"; 
		ResponseEntity<User> response = restTemplate.getForEntity(SERVICE_URL+"user/"+userId, User.class);
		logger.info("\n"+response.getBody().toString());
	}

	//@Test
	public void updateUser(){
		User user = new User("user_updated","Hyd_updated", "user_updated@gmail.com");
		ResponseEntity<String> response = restTemplate.postForEntity(SERVICE_URL+"update", user, String.class);
		logger.info("\n"+response.getBody().toString());
	}

	//@Test
	public void deleteUser(){
		String url = SERVICE_URL+"remove";
		ResponseEntity<String> response = restTemplate.postForEntity(url, "119", String.class);
		logger.info(url+"\n"+response.getBody().toString());
	}

	private BPMConfiguration getBPMConfiguration(){
		BPMConfiguration configuration = null;
		try {
			Resource resource = new ClassPathResource("dev.properties");
			Properties props = PropertiesLoaderUtils.loadProperties(resource);
			configuration = new BPMConfiguration();
			configuration.setUrl(props.getProperty("bonita.url"));
			configuration.setTechnicalUser(props.getProperty("bonita.user.technical.name"));
			configuration.setTechnicalPwd(props.getProperty("bonita.user.technical.pwd"));
			configuration.setUserName(props.getProperty("bonita.user.walter.name"));
			configuration.setPwd(props.getProperty("bonita.user.walter.pwd"));
			configuration.setBarFile("C:\\Users\\bmohammad\\bpm\\artifacts\\SimpleProcess-1.0.bar");
			configuration.setBdmFile("C:\\Users\\bmohammad\\bpm\\artifacts\\bdm.zip");
			configuration.setOrganizationFile("C:\\Users\\bmohammad\\bpm\\artifacts\\Organization_Data.xml");
			configuration.setProcessName(PROCESS_NAME);
			logger.info(configuration);
		} catch (IOException e) {
			logger.error(e);
		}
		return configuration;
	}
}
