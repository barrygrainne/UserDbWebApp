package ie.rccourse.webapp;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import ie.rccourse.userdb.User;
import ie.rccourse.userdb.UserDB;

@Controller
public class SpringTestObj {
	
	ApplicationContext context;
	UserDB userDB;
	
	public SpringTestObj() {
		context = new ClassPathXmlApplicationContext(
				"SpringBeans.xml");
		userDB = context.getBean(UserDB.class);
	}
	
	public String getName(){
		
		List<User> users = userDB.getUsers();
		
		String name = users.get(0).getFirstName();
		return name;
		//return "THE NAME";
		//String s = (String) context.getBean("str");
		//return s;
	}
	
	@RequestMapping("/welcome")
	public  ModelAndView testRequest(){
		
		List<User> users = userDB.getUsers();
		return new ModelAndView("welcome", "users", users);
		
		
		//return new ModelAndView("welcome", "user", users.get(0));
	//String message = "hello";
		
		//return new ModelAndView("welcome", "message", message);
	}
	
	@RequestMapping("/goodbye")
	public ModelAndView sayGoodbye(){
		
		return new ModelAndView("goodbye", "message", "goodbye");
	}
}
