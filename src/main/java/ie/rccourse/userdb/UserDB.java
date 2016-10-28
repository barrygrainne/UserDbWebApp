package ie.rccourse.userdb;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public interface UserDB {
	// get and set methods
		public DriverManagerDataSource getDmds();
		@Autowired
		public void setDmds(DriverManagerDataSource dmds);
		public User getUser(int id) throws UserDBException;
		public List<User> getUsers();
		public List<User> find(String search);
		public void create(User user) throws UserDBException;
		public void delete(int id) ;
		public void update(User user);
		public List<UserTransaction> getTransactionsForUser(int userId);
		public UserTransaction createTransaction(UserTransaction userTransaction);
		public void updateTransaction(UserTransaction userTransaction);
		public void deleteTransaction(int transactionId);
		public void deleteTransactionsForUser(User user);
		public void deleteTransactionsForUser(int userId);
		public void close();		
}