package com.curmudgeonlaureate.liquibase_mysql_scraper;
/** Description of DBConnection 
* The purpose for this class is to:
* 2. Assemble the arguments into a mysql database connection string.
* 3. Verify that the connection string can actually connect to the desired database.
* 4. Return a connection to the target database
* @author Michael Machado
* @version .1 July 3, 2015
*/
import java.sql.*;


public class DBConnection {
	private String ipAddress;
	private String port;
	private String dbName;
    private String dbUser;
    private String dbPassword;
	private Connection dbConn;
    private String mysqlAccess;
    private Boolean isValidConnection;
    
   
    /**  Constructor DBConnection(String [] connectionArgs) 
	 * 
	 * @param ipAddress				The ip address of the connection string
	 * @param dbName					The database to connect to
	 * @param dbUser					The user name with permission to access the database
	 * @param dbPassword				The password for the specified use name
	 * @param setmysqlAccess()		The connection string member field is set using setmysqlAccess()
	 */
    public DBConnection(String [] connectionArgs) {
		super();
		this.ipAddress = connectionArgs[0];
		this.port = connectionArgs[1];
		this.dbName = connectionArgs[2];
		this.dbUser = connectionArgs[3];
		this.dbPassword = connectionArgs[4];
		this.isValidConnection = false;
		setmysqlAccess();
		setDbConn();
	} // end constructor DBConnection
    
    public void closeDbConn() {
		try {
			this.dbConn.close();
		} catch (SQLException e) {
			System.out.println("closing connection to database.");
			e.printStackTrace();		
		}
    }
    
    public Connection getDbConn() {
		return dbConn;
    }
	
	public void setDbConn() {
			 try {
				   // This will load the MySQL driver, each DB has its own driver
		            Class.forName("com.mysql.jdbc.Driver").newInstance();
		            this.dbConn = DriverManager.getConnection(this.mysqlAccess);
		            setIsValidConnection(true);
		            System.out.println("connection is valid!");
		        } catch (SQLException ex) {
		            ex.printStackTrace();
		            this.dbConn = null;
		            setIsValidConnection(false);
		        } catch (InstantiationException ex) {
		            ex.printStackTrace();
		            this.dbConn = null;
		            setIsValidConnection(false);
		        } catch (IllegalAccessException ex) {
		            ex.printStackTrace();
		            this.dbConn = null;
		            setIsValidConnection(false);
		        } catch (ClassNotFoundException ex) {
		            ex.printStackTrace();
		            this.dbConn = null;
		            setIsValidConnection(false);
		        }
	} // end setDbConn()

    /*
     *  The general format for a JDBC URL for connecting to a MySQL server is as follows, 
     *  "jdbc:mysql://ipaddress:port/dbname?" + "user=userName&password=password"
     *  
     */
	public void setmysqlAccess() {
		this.mysqlAccess = "jdbc:mysql://" + ipAddress + ":" + port + "/" + dbName +
												"?user=" + dbUser + "&password=" + dbPassword ;
	} // end method setmysqlAccess
    
	public String getmysqlAccess() {
		return mysqlAccess;
	} // end method getmysqlAccess

	public String getDbName() {
		return dbName;
	}

	public Boolean getIsValidConnection() {
		return isValidConnection;
	}

	public void setIsValidConnection(Boolean isValidConnection) {
		this.isValidConnection = isValidConnection;
	}
	

} // end class DBConnection
