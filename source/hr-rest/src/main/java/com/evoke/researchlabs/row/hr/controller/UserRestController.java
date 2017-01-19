package com.evoke.researchlabs.row.hr.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.evoke.researchlabs.row.hr.domain.BPMConfiguration;
import com.evoke.researchlabs.row.hr.domain.BPMTask;
import com.evoke.researchlabs.row.hr.domain.Transaction;
import com.evoke.researchlabs.row.hr.domain.User;
import com.evoke.researchlabs.row.hr.service.UserService;
import com.evoke.researchlabs.row.hr.util.BPMUtil;

/**
 * 
 * @author Zama
 *
 */
@RestController
@RequestMapping("/hrrest")
public class UserRestController {
	@Autowired
	private UserService userService; 
	private Logger logger = Logger.getLogger(UserRestController.class);

	@RequestMapping(value = "/bpm/task" , method = RequestMethod.GET,headers="Accept=application/json")
	public List<BPMTask> getBonitaTask() {
		List<BPMTask> tasks = new ArrayList<>();
		try {
			BPMConfiguration bpmConf = getBPMConfiguration();
			HttpResponse httpResponse = BPMUtil.callBPMEngine(bpmConf.getUrl(), "/API/bpm/humanTask?p=0&c=10", bpmConf.getUserName(), bpmConf.getPwd(), "GET", null);
			String responseBody = EntityUtils.toString(httpResponse.getEntity());
			logger.info(responseBody);
			JSONArray json = new JSONArray(responseBody);
			if(json!=null){
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
			}
		} catch (ParseException | IOException | JSONException e) {
			logger.error(e);
		}
		logger.info(tasks);
		return tasks;
	}

	@RequestMapping(value = "/transactions" , method = RequestMethod.GET,headers="Accept=application/json")
	public List<Transaction> getAllTransactions() {
		List<Transaction> transactions = userService.getTransactions();
		logger.info(transactions);
		return transactions;
	}

	@RequestMapping(value = "/users" , method = RequestMethod.GET,headers="Accept=application/json")
	public List<User> getAllUsers() {
		List<User> users = userService.getAllUsers();
		logger.info(users);
		return users;
	}

	@RequestMapping(value = "/user/{id}" , method = RequestMethod.GET,headers="Accept=application/json")
	public User getUser(@PathVariable("id") int id) {
		logger.info("Fetching User with id " + id);
		User user = userService.getUser(id);
		logger.info("User with id " + id + " not found");
		return user;
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public String createUser(@RequestBody User user, UriComponentsBuilder ucBuilder) {
		String status = "fail";
		logTransaction(user, "INSERT");
		logger.info("Calling BPM REST API ...");

		try {
			BPMConfiguration bpmConfiguration = getBPMConfiguration();
			String processId = getProcessId(user, bpmConfiguration);

			if(processId != null){
				String responseBody1 = createTask(user, bpmConfiguration, processId);
				if(responseBody1 != null){
					responseBody1 = BPMUtil.assignHumanTask(bpmConfiguration, responseBody1);
				}
				logger.info(responseBody1);
				status = "success";
			}
		} catch (Exception e) {
			logger.error("Error while creating case.", e);
		}

		return getStatusMessage(status);
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String updateUser(@RequestBody User user, UriComponentsBuilder ucBuilder) {
		String status = "fail";
		logTransaction(user, "UPDATE");

		logger.info("Updating User: " + user);
		if (userService.updateUser(user)) {
			status = "success";
		}
		return getStatusMessage(status);
	}

	@RequestMapping(value = "/remove", method = RequestMethod.POST)
	public String deleteUser(@RequestBody String id){
		String status = "fail";
		logTransaction(userService.getUser(Integer.valueOf(id)), "REMOVE");

		logger.info("Deleting User with Id: " + id);
		if (userService.deleteUser(new User(Integer.valueOf(id),null,null,null, null))) {
			status = "success";
		}
		return getStatusMessage(status);
	}

	private void logTransaction(User user, String requestType) {
		logger.info("Saving Transaction: " + user);
		Transaction transaction = new Transaction();
		transaction.setUsername(user.getUsername());
		transaction.setEmail(user.getEmail());
		List<String> names = Arrays.asList("zama","rakesh","ajitesh");
		transaction.setRequestedBy(names.get(new Random().nextInt(names.size())));
		transaction.setAddress(user.getAddress());
		transaction.setRequestType(requestType);
		userService.saveTransaction(transaction);
	}

	private String getStatusMessage(String status) {
		return "[{\"status\":\""+ status+"\"}]";
	}

	private BPMConfiguration getBPMConfiguration(){
		BPMConfiguration configuration = null;
		try {
			Resource resource = new ClassPathResource("dev.properties");
			Properties props = PropertiesLoaderUtils.loadProperties(resource);
			configuration = new BPMConfiguration(props.getProperty("bonita.url"), props.getProperty("bonita.user.walter.name"), props.getProperty("bonita.user.walter.pwd"));
			logger.info(configuration);
		} catch (IOException e) {
			logger.error(e);
		}
		return configuration;
	}
	
	private String getProcessId(User user, BPMConfiguration bpmConfiguration) throws IOException, JSONException {
		HttpResponse httpResponse = BPMUtil.callBPMEngine(bpmConfiguration.getUrl(), "/API/bpm/process?p=0&c=10&f=displayName%3d"+user.getProcessName(), 
				bpmConfiguration.getUserName(), bpmConfiguration.getPwd(), "GET", null);
		String responseBody = EntityUtils.toString(httpResponse.getEntity());
		logger.info(responseBody);
		JSONArray json = new JSONArray(responseBody);
		JSONObject details = json.getJSONObject(0);
		logger.info(details);
		String processId = details.getString("id");
		logger.info("Process Id: "+processId);
		return processId;
	}

	private String createTask(User user, BPMConfiguration bpmConfiguration, String processId) throws IOException {
		String responseBody;
		logger.info("Creating case with process id: "+processId);
		String payLoad = "{\"registation_refInput\":{\"userName\":\""+user.getUsername()+"\",\"city\":\""+user.getAddress()+"\",\"email\":\""+user.getEmail()+"\"}}";

		HttpResponse httpResponse2 = BPMUtil.callBPMEngine(bpmConfiguration.getUrl(), "/API/bpm/process/"+processId+"/instantiation", bpmConfiguration.getUserName(),
				bpmConfiguration.getPwd(), "POST", payLoad);
		responseBody = EntityUtils.toString(httpResponse2.getEntity());
		logger.info(responseBody);
		return responseBody;
	}
}
