package com.curmudgeonlaureate.liquibase_mysql_scraper;
/** Description of ScraperData
* The purpose for this class is handle the Data Fetch from the 
* MySQL database
* 
* @author Michael Machado
* @version .1 July 3, 2015
*/
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ScraperData {
		
		private String dbName;
		private HashMap<String,String> views;
		private HashMap<String,String> storedProcedures;
		private HashMap<String,String> events;
		private HashMap<String,String> functions;
		private HashMap<String,String> triggers;
		private LinkedList<String> tables;
		
		private PreparedStatement preparedStatement = null;
		private Statement stmt = null;
		private ResultSet resultSet = null;
	

		
		public ScraperData() {
			super();
			this.views = null;
			this.storedProcedures = null;
			this.events = null;
			this.functions = null;
			this.triggers = null;
			this.tables = null;
			this.dbName = null;
		}

		/* Set all data collectors in one function */
		public void setAll(Connection connection, String dbName) throws SQLException{
			setDbName(dbName);
			setViews(connection, dbName);
			setStoredProcedures(connection, dbName);
			setEvents(connection, dbName);
			setFunctions(connection, dbName);
			setTables(connection, dbName);	
			setTriggers(connection, dbName);
		}
		
		public String getDbName() {
			return dbName;
		}

		public void setDbName(String dbName) {
			this.dbName = dbName;
		}

		public HashMap<String, String> getViews() {
			return views;
		}

		public void setViews(Connection connection, String dbName) throws SQLException {
			HashMap<String,String> fetchView = new HashMap<String,String>();
		    try {
				preparedStatement = connection.prepareStatement(ScraperUtils.fetchViews);
				preparedStatement.setString(1, dbName);
				resultSet = preparedStatement.executeQuery();
				while (resultSet.next()) {
		            String viewName = (resultSet.getString("TABLE_NAME"));
		            String viewContents = (resultSet.getString("VIEW_DEFINITION"));
		            fetchView.put(viewName, viewContents);
		        }
				
			} catch (SQLException ex) {
				 Logger.getLogger(ScraperData .class.getName()).log(Level.SEVERE, null, ex);
				System.out.println("method setViews");
				ex.printStackTrace();
			}finally {
		        if (preparedStatement != null) { preparedStatement.close(); }
		    }
		    System.out.println("Number of Views fetched: " + fetchView.size());
			this.views = fetchView;
		}

		public HashMap<String, String> getStoredProcedures() {
			return storedProcedures;
		}

		public void setStoredProcedures(Connection connection, String dbName) throws SQLException {
				HashMap<String,String> fetchStoredProcedures = new HashMap<String,String>();
			    try {
					preparedStatement = connection.prepareStatement(ScraperUtils.fetchStoredProcedures);
					preparedStatement.setString(1, dbName);
					resultSet = preparedStatement.executeQuery();
					while (resultSet.next()) {
			            String storedProcedureName = (resultSet.getString("SPECIFIC_NAME"));
			            String storedProcedureContents = (resultSet.getString("ROUTINE_DEFINITION"));
			            fetchStoredProcedures.put(storedProcedureName, storedProcedureContents);
			        }
					
				} catch (SQLException e) {
					System.out.println("method setStoredProcedures");
					e.printStackTrace();
				}finally {
			        if (preparedStatement != null) { preparedStatement.close(); }
			    }
			    System.out.println("Number of Stored Procedures fetched: " + fetchStoredProcedures.size());
			this.storedProcedures = fetchStoredProcedures;
		}

		public HashMap<String, String> getEvents() {
			return events;
		}

		public void setEvents(Connection connection, String dbName) throws SQLException {
			HashMap<String,String> fetchEvents = new HashMap<String,String>();
		    try {
				preparedStatement = connection.prepareStatement(ScraperUtils.fetchEvents);
				preparedStatement.setString(1, dbName);
				resultSet = preparedStatement.executeQuery();
				while (resultSet.next()) {
		            String eventName = (resultSet.getString("SPECIFIC_NAME"));
		            String eventContents = (resultSet.getString("ROUTINE_DEFINITION"));
		            fetchEvents.put(eventName, eventContents);
		          
		        }
				
			} catch (SQLException e) {
				System.out.println("method setEvents");
				e.printStackTrace();
			}finally {
		        if (preparedStatement != null) { preparedStatement.close(); }
		    }
		    System.out.println("Number of Events fetched: " + fetchEvents.size());
			this.events =fetchEvents;
		}

		public HashMap<String, String> getFunctions() {
			return functions;
		}

		public void setFunctions(Connection connection, String dbName) throws SQLException {
			List<String> functionList = new ArrayList<String>();
			HashMap<String,String> fetchFunctions = new HashMap<String,String>();
		    try {
				preparedStatement = connection.prepareStatement(ScraperUtils.fetchFunctionNames);
				preparedStatement.setString(1, dbName);
				resultSet = preparedStatement.executeQuery();
				/* First we fetch the names of the functions in the database */
				while (resultSet.next()) {
		            String functionName = (resultSet.getString("SPECIFIC_NAME"));
		            functionList.add(functionName);
		        }
				/* Second we iterate over the list and grab the create code for the function */
				for (String temp : functionList){
					String query =  "SHOW CREATE FUNCTION " + dbName + "." + temp;
					stmt = connection.createStatement();
			        ResultSet rs = stmt.executeQuery(query);    
			        while (rs.next()) {
						String functionContents = (rs.getString("Create Function"));
				        fetchFunctions.put(temp, functionContents);    
			        }
				}
				 
			} catch (SQLException e) {
				System.out.println("method setFunctions");
				e.printStackTrace();
			}finally {
		        if (preparedStatement != null) { preparedStatement.close(); }
		    }
		    System.out.println("Number of Functions fetched: " + fetchFunctions.size());
			this.functions = fetchFunctions;
		}

		public HashMap<String, String> getTriggers() {
			return triggers;
		}

		public void setTriggers(Connection connection, String dbName) throws SQLException  {
			HashMap<String,String> fetchTriggers = new HashMap<String,String>();
		    try {
				preparedStatement = connection.prepareStatement(ScraperUtils.fetchTriggers);
				preparedStatement.setString(1, dbName);
				resultSet = preparedStatement.executeQuery();
				while (resultSet.next()) {
		            String triggerName = (resultSet.getString("trigger_name"));
		            String triggerContents = (resultSet.getString("action_statement"));
		            fetchTriggers.put(triggerName, triggerContents);    
		        }	
			} catch (SQLException e) {
				System.out.println("method setTriggers");
				e.printStackTrace();
			}finally {
		        if (preparedStatement != null) { preparedStatement.close(); }
		    }
		    System.out.println("Number of Triggers fetched: " + fetchTriggers.size());
		    this.triggers = fetchTriggers;
		}

		
		public LinkedList<String> getTables() {
			return tables;
		}

		public void setTables(Connection connection, String dbName) throws SQLException{
			LinkedList<String>  fetchTables = new LinkedList<String>();
		    try {
		    	preparedStatement = connection.prepareStatement(ScraperUtils.fetchTables);
				preparedStatement.setString(1, dbName);
				resultSet = preparedStatement.executeQuery();
				while (resultSet.next()) {
		            String tableName = (resultSet.getString("table_name"));
		            fetchTables.add(tableName);
		        }	
			} catch (SQLException e) {
				System.out.println("method setTables");
				e.printStackTrace();
			}finally {
				 if (preparedStatement != null) { preparedStatement.close(); }
		    }
		    System.out.println("Number of Tables fetched: " + fetchTables.size());
			this.tables = fetchTables;
		}
		
		
}
