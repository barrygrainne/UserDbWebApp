package ie.rccourse.webapp;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import ie.rccourse.userdb.User;
import ie.rccourse.userdb.UserDB;
import ie.rccourse.userdb.UserDBException;
import ie.rccourse.userdb.UserTransaction;

@Controller
public class UserDBApi {
	
	protected UserDB userDB;

	
	public UserDBApi(){
		ApplicationContext context = new ClassPathXmlApplicationContext(																				
				"SpringBeans.xml");
		userDB = context.getBean(UserDB.class);
	}
	
	@RequestMapping("/showUsers")
	public ModelAndView showUsers(){
		
		List<User>users = userDB.getUsers();
		
		return new ModelAndView("showUsers", "users", users);
	}
	@RequestMapping("/users")
	@ResponseBody
	public List<User> getUsers(){
		List<User>users = userDB.getUsers();
		return users;
	}
	@RequestMapping("/user")
	@ResponseBody
	public User getUser(@RequestParam("id") int id){
		User user = null;
		try {
			user = userDB.getUser(id);
		} catch (UserDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return user;
	}
	@RequestMapping(value = "/users/user", method = RequestMethod.DELETE)
	@ResponseBody
	public String deleteUser(@RequestParam("id") int id) {
	
		userDB.delete(id);
		return "Deleted";
	}
	@RequestMapping(value = "/users/user/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	public String deleteUserWithPathParam(@PathVariable("id") int id) {
	
		userDB.delete(id);
		return "Deleted";
	}
	@RequestMapping(value = "/users/user", method = RequestMethod.PUT)
	@ResponseBody
	public String createUser(@RequestBody User user){
		
		String result = "";
	 
		try {
			userDB.create(user);
			result = "Added:" + user.getId();
		} catch (UserDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = "Failed to add";
		}
		
		return result;
	}
	

	
	@RequestMapping(value ="/users/user", method = RequestMethod.POST)
	@ResponseBody
	public User updateUserOriginal(@RequestBody User user) {
		
		
		userDB.update(user);
		
		return user;
	}
	@RequestMapping(value = "/users/user/{id}", method = RequestMethod.GET)
	@ResponseBody
	public User getUserWithPathParam(@PathVariable("id") Integer id){
		User user = null;
		try {
			user = userDB.getUser(id);
		} catch (UserDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return user;
		
	}
	
	@RequestMapping(value = "/users/user/{id}/txs", method = RequestMethod.GET)
	@ResponseBody
	public List<UserTransaction> getTransactionsForUser(@PathVariable("id") int id){
		
		List<UserTransaction> txs = userDB.getTransactionsForUser(id);
		
		return txs;
	}
	
	@RequestMapping(value ="/users/user/{id}/txs/tx/{tid}", method = RequestMethod.GET)
	@ResponseBody
	public UserTransaction getTransactionById(@PathVariable("id") int id, @PathVariable("tid") int tid){
		List<UserTransaction> txs = userDB.getTransactionsForUser(id);
		
		for(UserTransaction utx : txs) {
			if(utx.getId() == tid) {
				return utx;
			}
		}
		return null;
	}
	
	@RequestMapping(value="/users/user/{id}/txs/tx/{tid}", method = RequestMethod.DELETE)
	@ResponseBody
	
	public void deletetransaction(@PathVariable("id") int id, @PathVariable("tid") int tid){
	
		userDB.deleteTransaction(tid);
	}
	
	// "/users/user/{id}/txs/tx/", method=RequestMethod.PUT
	// "/users/user/{id}/txs/tx/", method=RequestMethod.POST
	
	@RequestMapping(value="/users/user/{id}/txs/tx/", method=RequestMethod.PUT)
	@ResponseBody
	public UserTransaction createTransaction(@PathVariable("id") int id, @RequestBody UserTransaction userTransaction){
		
		userDB.createTransaction(userTransaction);
		
		return userTransaction;
		}
	@RequestMapping(value="/users/user/{id}/txs/tx/", method=RequestMethod.POST)
	@ResponseBody
	public UserTransaction updateTransaction(@PathVariable("id") int id, @RequestBody UserTransaction userTransaction){
		
		userDB.updateTransaction(userTransaction);
		
		return userTransaction;
		}
	
	@RequestMapping(value="/arraytest", method=RequestMethod.GET)
	@ResponseBody
	public int getStrings(@RequestParam("months") String[] months) {
		return months.length;
	}
	
}

