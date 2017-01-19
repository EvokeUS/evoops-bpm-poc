package com.evoke.researchlabs.row.hr;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.evoke.researchlabs.row.hr.domain.Transaction;
import com.evoke.researchlabs.row.hr.domain.User;
import com.evoke.researchlabs.row.hr.service.UserService;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

/**
 * 
 * @author Zama
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserTest extends BaseTest{
	private static Logger LOGGER = Logger.getLogger(UserTest.class);
	@Autowired
	private UserService userService;
	
	public static void main(String[] args) throws Exception{
		Transaction transaction = new Transaction(null, "test2", "delhi", "test1@gmail.com", "Update", "12-Dec-2016 01:01", "Zama");

		MongoClient mongo = new MongoClient("192.168.99.100", 27017);
		DB db = mongo.getDB("transactionData");
		DBCollection table = db.getCollection("transaction");
		BasicDBObject document = new BasicDBObject();
		document.put("username", transaction.getUsername());
		document.put("email", transaction.getEmail());
		document.put("requestedBy", transaction.getRequestedBy());
		document.put("requestType", transaction.getRequestType());
		document.put("requestTime", new Date());
		table.insert(document);

		List<Transaction> transactions = new ArrayList<>();
		DBCollection collection = db.getCollection("transaction");
		DBCursor dbCursor = collection.find();
		System.out.println("No of documents: "+dbCursor.count());

		while(dbCursor.hasNext()) {
			DBObject nextElement = dbCursor.next();
			Transaction t = new Transaction();
			t.setId(String.valueOf(nextElement.get("_id")));
			t.setUsername(String.valueOf(nextElement.get("username")));
			t.setEmail(String.valueOf(nextElement.get("email")));
			t.setRequestedBy(String.valueOf(nextElement.get("requestedBy")));
			t.setRequestTime(String.valueOf(nextElement.get("requestTime")));
			t.setAddress(String.valueOf(nextElement.get("address")));
			transactions.add(t);
		}

		for (Transaction tran : transactions) {
			System.out.println(tran);
		}
	}

	//@Test
	public void loadPropertiesFile() throws Exception{
		Resource resource = new ClassPathResource("dev.properties");
		Properties props = PropertiesLoaderUtils.loadProperties(resource);
		String mongoUrl = props.getProperty("database.mongo.url");
		String mongoPort = props.getProperty("database.mongo.port");
		System.out.println("URL: "+mongoUrl);
		System.out.println("PORT: "+mongoPort);

	}

	//@Test
	public void getTransactions(){
		List<Transaction> transactions = userService.getTransactions();

		for (Transaction transaction : transactions) {
			LOGGER.info(transaction);
		}
	}

	//@Test
	public void saveTransaction(){
		int counter = 10;
		for(int i =1; i<counter; i++){
			Transaction transaction = new Transaction(null, "test"+i, "Hyderabad", "test"+i+"@gmail.com", "Insert", "12-Dec-2016 1"+i+":5"+i, "Zama");
			userService.saveTransaction(transaction);
		}
	}

	@Test
	public void getAllUsers(){
		List<User> users = userService.getAllUsers();
		LOGGER.info("User Count: "+users.size());
		for (User user : users) {
			LOGGER.info(user);
		}
	}

	//@Test
	public void createUser(){
		userService.createUser(new User("user9","Hyderabad","zamamb@gmail.com"));
	}

	//@Test
	public void getUser_Found(){
		User user = userService.getUser(1);
		LOGGER.info(user);
	}

	//@Test
	public void getUser_NotFound(){
		User user = userService.getUser(0);
		LOGGER.info(user);
	}

	//@Test
	public void removeUser(){
		boolean status = userService.deleteUser(userService.getAllUsers().get(0));
		LOGGER.info("Status: "+status);
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}
