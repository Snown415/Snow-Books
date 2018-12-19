package sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import lombok.Getter;
import lombok.Setter;

public class MySQL {

	public static LinkedList<Database> databases = new LinkedList<>();
	public static final String HOST = "localhost:3306";

	public enum Database {
		USERS("snowbooks", "user", "snoWbookSuseR");

		private @Getter @Setter String database, user, password;
		private @Getter @Setter Connection connection;
		private @Getter @Setter Statement statement;
		private @Getter @Setter ResultSet resultSet;

		private Database(String... settings) {

			if (settings.length < 3) {
				System.err.println("Invalid Database Settings! Supply -> DBName, DBUsername, DBPassword");
				return;
			}

			setDatabase(settings[0]);
			setUser(settings[1]);
			setPassword(settings[2]);

			setConnection(generateConnection(this));
			try {
				setStatement(connection.createStatement());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static String loginReturn;

	public static boolean validLogin(String username, String password) {

		Database db = Database.USERS;

		try {
			db.setResultSet(
					db.getStatement().executeQuery("SELECT password FROM users WHERE username = '" + username + "'"));

			if (!db.getResultSet().next()) {
				loginReturn = "There is no user '" + username + "' registered. Please select 'Register' instead.";
				System.err.println("No username registered under " + username);
				return false;
			}

			if (!db.getResultSet().getString("password").equals(password)) {
				loginReturn = "Invalid login credentials.";
				return false;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		db.setResultSet(null);
		return true;
	}

	public static boolean foundUser(String username) {

		Database db = Database.USERS;

		try {
			db.setResultSet(
					db.getStatement().executeQuery("SELECT username FROM users WHERE username = '" + username + "'"));

			if (db.getResultSet().next())
				return true;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		db.setResultSet(null);

		return false;
	}

	public static Integer getOwnershipId(String username) {

		Database db = Database.USERS;
		Integer value = null;

		try {
			db.setResultSet(db.getStatement()
					.executeQuery(("SELECT ownershipId FROM users WHERE username = '" + username + "'")));

			if (db.getResultSet().next()) {
				value = db.getResultSet().getInt("ownershipId");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return value;
	}
	
	public static String getBusinessList(Integer ownerId) {
		
		Database db = Database.USERS;
		String value = null;
		
		try {
			db.setResultSet(db.getStatement().executeQuery(("SELECT businessList FROM businesses WHERE ownershipId = '" + ownerId + "'")));
			
			
			if (db.getResultSet().next()) {
				return db.getResultSet().getString("businessList");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return value;
	}
	
	public static String getBusinessName(Integer id) {
		Database db = Database.USERS;
		String value = null;
		
		try {
			db.setResultSet(db.getStatement().executeQuery(("SELECT businessName FROM business WHERE businessId = '" + id + "'")));
			
			
			if (db.getResultSet().next()) {
				return db.getResultSet().getString("businessName");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return value;
	}

	public static void registerUser(String username, String password) {
		Database db = Database.USERS;
		try {
			db.getStatement().executeUpdate("INSERT INTO users VALUES ('" + username + "', '0', '" + password + "')");

		} catch (SQLException e) {
			e.printStackTrace();
		}

		db.setResultSet(null);
	}

	public static void init() {

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				for (Database db : Database.values())
					databases.add(db);

				System.out.println("All " + databases.size() + " SQL databases have been defined.");
			}

		});

		thread.start();
	}

	private static Connection generateConnection(Database db) {
		Connection connection;
		try {
			String url = "jdbc:mysql://" + HOST + "/" + db.getDatabase();
			connection = DriverManager.getConnection(url, db.getUser(), db.getPassword());
			return connection;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

}
