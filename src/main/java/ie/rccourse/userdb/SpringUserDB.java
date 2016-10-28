package ie.rccourse.userdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.DriverManagerDataSource;




public class SpringUserDB implements UserDB {
	// properties
	DriverManagerDataSource dmds;
	protected JdbcTemplate jdbcTemplate;


	// get and set methods
	public DriverManagerDataSource getDmds(){
		return dmds;
		
	}
	
	@Autowired
	public void setDmds(DriverManagerDataSource dmds) {
		this.dmds = dmds;
		
		jdbcTemplate = new JdbcTemplate(dmds);
	}
	
	// constructor (s)
	public SpringUserDB(){ // good idea to have a blank constructor in every object you create
	}
	
	public SpringUserDB(DriverManagerDataSource dmds) {
		this.dmds = dmds;
		jdbcTemplate = new JdbcTemplate(dmds);
	}
// other methods
// get the user with the specified id
	
	public User getUser(int id) throws UserDBException {
		
		User user = null;
		String sql = "SELECT * FROM users WHERE id=?";
		
	try {
		user = (User) jdbcTemplate.queryForObject(
				sql, new Object[] {id}, new BeanPropertyRowMapper<User>(User.class)); //new BeanPropertyRowMapper<User>(User.class));
		
	} catch (Exception ex) {
		throw new UserDBException(ex.getMessage());
	}
	return user;
	}
	

	public List<User> getUsers(){
		String sql = "SELECT * FROM users";
		
		List<User> users = jdbcTemplate.query(sql, new BeanPropertyRowMapper<User>(User.class));
		
		// List<User> users = jdbcTemplate.query(sql,
		// new BeanPropertyRowMapper<User>(User.class)); can use this instead of UserRowMapper, calls gets & sets for you
		return users;
		
	}
	public List<User> find(String search){
		//String sql = "SELECT * FROM users " + 
		//			"WHERE firstName LIKE '%" + search + "%'" +
		//			"OR lastName LIKE '%" + search + "%'";
		
		String sql = "SELECT * FROM users " +
				"WHERE firstName LIKE ?" +
				"OR lastName LIKE ?";
		
		List<User> users = jdbcTemplate.query(sql,
				new Object[]{"%" + search + "%", "%" + search + "%"},
				new BeanPropertyRowMapper<User>(User.class));
		
		return users;
	}
	public void create(User user) throws UserDBException {
		//String sql = "INSERT INTO users" +
			//	"(firstName, lastName, registered, dateOfBirth)" +
			//	"VALUES(?, ?, ?, ?)";
		//jdbcTemplate.update(sql, new Object[]{user.getfirstName})
		
		SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
		jdbcInsert.setTableName("users");
		jdbcInsert.setGeneratedKeyName("id");
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		parameters.put("firstName", user.getFirstName());
		parameters.put("lastName", user.getLastName());
		parameters.put("registered", user.isRegistered()?1:0);
		parameters.put("dateOfBirth", user.getDateOfBirth());
		
		
		Number id = jdbcInsert.executeAndReturnKey(
					new MapSqlParameterSource(parameters));
		user.setId(id.intValue());
	}
	
	public void delete(int id) {
		
		// delete the transactions for the user
		//String sql = "DELETE FROM transactions WHERE userId = ?";
		//jdbcTemplate.update(sql, new Object[]{id});
		
		deleteTransactionsForUser(id);
		// delete the user
		String sql = "DELETE FROM users WHERE id = ?";
		jdbcTemplate.update(sql, new Object[]{id});
	} 
	
	public void update(User user) {
		String sql = "UPDATE users " +
					"SET firstName = ?, " +
				"lastName = ?, " +
					"registered = ?, " +
					"dateOfBirth = ? " +
					"WHERE id = ?";
		jdbcTemplate.update(sql, new Object[]{user.getFirstName(),
				user.getLastName(),
				user.isRegistered(),
				user.getDateOfBirth(),
				user.getId()});
	}
	
	public List<UserTransaction> getTransactionsForUser(int userId){
		
		String sql = "SELECT * FROM transactions WHERE userId=?";
		
		List<UserTransaction> transactions = jdbcTemplate.query(sql,
				new Object[]{userId}, 
				new BeanPropertyRowMapper<UserTransaction>(UserTransaction.class));
		return transactions;
	}
	
	public UserTransaction createTransaction(UserTransaction userTransaction){
	
		SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
		jdbcInsert.setTableName("transactions");
		jdbcInsert.setGeneratedKeyName("id");
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		parameters.put("UserId", userTransaction.getUserId());
		parameters.put("description", userTransaction.getDescription());
		parameters.put("transactionDate", userTransaction.getTransactionDate());
		parameters.put("amount", userTransaction.getAmount());
		
		
		Number id = jdbcInsert.executeAndReturnKey(
					new MapSqlParameterSource(parameters));
		userTransaction.setId(id.intValue());

		return userTransaction;
	}
	//public UserTransaction create(User user, UserTransaction userTransaction){
	//return userTransaction; }
	
	public void updateTransaction(UserTransaction userTransaction){
		String sql = "UPDATE transactions " + 
						"SET description = ?, " + 
						"amount = ?, " +
						"transactionDate = ? " +
						"WHERE id = ?";
		jdbcTemplate.update(sql, new Object[]{
				userTransaction.getDescription(),
				userTransaction.getAmount(),
				userTransaction.getTransactionDate(),
				userTransaction.getId()
				});
		}
	
	
	public void deleteTransaction(int transactionId){
		String sql = "DELETE FROM transactions WHERE id = ? ";
		jdbcTemplate.update(sql, new Object[]{transactionId});
	}
	
	public void deleteTransactionsForUser(User user){
		String sql = "DELETE FROM transactions WHERE userId=?";
		jdbcTemplate.update(sql, new Object[]{user.getId()});
	}
	
	
	public void deleteTransactionsForUser(int userId){
		String sql = "DELETE FROM transactions WHERE userId=?";
		jdbcTemplate.update(sql, new Object[]{userId});
	}
	public void close(){
		
	}
	public static void main( String[] args )
    {
    	ApplicationContext context = new ClassPathXmlApplicationContext("SpringBeans.xml"); 
        
    	//DriverManagerDataSource dmds = context.getBean(DriverManagerDataSource.class);
   	
    	//SpringUserDB userDB = context.getBean(SpringUserDB.class);
    	
    	//SpringUserDB userDB = new SpringUserDB(dmds);
    	UserDB userDB = context.getBean(UserDB.class);
    	
    	userDB.deleteTransaction(1);
    	userDB.deleteTransactionsForUser(51);
    	
    	
    	
    	/*
    	User u = null;
    	try{
    		u = userDB.getUser(52);
    	} catch (SpringUserDBException e){
    		e.printStackTrace();
    	}
    	userDB.deleteTransactionsForUser(u);
    	*/
    	
    	List<UserTransaction>txs = userDB.getTransactionsForUser(53);
    	
    	for (UserTransaction tx : txs){
    		tx.setAmount(1999.99);
    		userDB.updateTransaction(tx);
    	}
    	
    	txs = userDB.getTransactionsForUser(53);
    	for (UserTransaction tx : txs){
    		System.out.println(tx);
    	}
    	
    	/*User user = new User(-1, "NEW", "USER", true, "2000-01-02");
    	
    	try{
    	
    	userDB.create(user);	
    	} catch (SpringUserDBException e1){
    		e1.printStackTrace();
    	}
    	System.out.println("NEW USER:" + user.getId());
    	
    	userDB.delete(57);
    	
    	user.setFirstName("CHANGED");
    	user.setLastName("CHANGED");
    	
    	userDB.update(user);
    	
    	user = null;
    	try{
    		user = userDB.getUser(57);
    		System.out.println(user);
    	} catch 
    	
    	JdbcTemplate jdbcTemplate = new JdbcTemplate(dmds);
    	
    	String sql = "INSERT INTO users " +
    				"(firstName, lastName, registered, dateOfBirth)"
    			+ "VALUES(?, ?, ?, ?)";
    	jdbcTemplate.update(sql, new Object[] {
    		"new", "user", 0, "2000-01-02"
    	});
    	
    	sql = "SELECT * FROM users WHERE ID =?";
    	
    	//jdbcTemplate = new JdbcTemplate(dmds);
    	
    	int id = 16;
    	User user = (User) jdbcTemplate.queryForObject(sql, new Object[] {id}, new UserRowMapper());
    	
    	sql = "SELECT * FROM users";
    	List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
    			
    	ArrayList<User> users = new ArrayList<User>();		
    			for (Map row: rows){
    				
    				User u = new User();
    				u.setId(Integer.parseInt(String.valueOf(row.get("id"))));
    				u.setFirstName(String.valueOf(row.get("firstName")));
    				u.setFirstName(String.valueOf(row.get("lastName")));
    				u.setRegistered((Integer)row.get("registered") == 1);
    				if (row.get("DateOfBirth") != null) {
    						u.setDateOfBirth(row.get("dateOfBirth").toString());
    				}
    				users.add(u);
    			}
    		
    			for(User u : users) {
    				System.out.println(u);
    			}
	*/
} 
}

