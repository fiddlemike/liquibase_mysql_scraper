package com.curmudgeonlaureate.liquibase_mysql_scraper;
/*
* Copyright 2015 Michael Machado
* 
*		   Licensed under the Apache License, Version 2.0 (the "License");
*		   you may not use this file except in compliance with the License.
*		   You may obtain a copy of the License at
*
*		     http://www.apache.org/licenses/LICENSE-2.0
*
*		   Unless required by applicable law or agreed to in writing, software
*		   distributed under the License is distributed on an "AS IS" BASIS,
*		   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*		   See the License for the specific language governing permissions and
*		   limitations under the License.
*
*/

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
			this.dbName = null;
		}

		/* Set all data collectors in one function */
		public void setAll(Connection connection, String dbName) throws SQLException{
			setDbName(dbName);
			setViews(connection, dbName);
			setStoredProcedures(connection, dbName);
			setEvents(connection, dbName);
			setFunctions(connection, dbName);
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
				List<String> procedureList = new ArrayList<String>();
				HashMap<String,String> fetchStoredProcedures = new HashMap<String,String>();
			    try {
					preparedStatement = connection.prepareStatement(ScraperUtils.fetchStoredProcedureNames);
					preparedStatement.setString(1, dbName);
					resultSet = preparedStatement.executeQuery();
					/* First we fetch the names of the stored procedures in the database */
					while (resultSet.next()) {
			            String storedProcedureName = (resultSet.getString("SPECIFIC_NAME"));
			            procedureList.add(storedProcedureName);
			        }
					/* Second we iterate over the list and grab the create code for the Stored Procedure */
					for (String temp : procedureList){
						String query =  "SHOW CREATE PROCEDURE " + dbName + "." + temp;
						stmt = connection.createStatement();
				        ResultSet rs = stmt.executeQuery(query); 	
				        // Here we add the procedure name and the create code to the hashmap
				        while (rs.next()) {
							String procedureContents = (rs.getString("Create Procedure"));
							fetchStoredProcedures.put(temp, procedureContents);    
				        }
					}
				} catch (SQLException ex) {
					Logger.getLogger(ScraperData .class.getName()).log(Level.SEVERE, null, ex);
					System.out.println("method setStoredProcedures");
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
			List<String> eventList = new ArrayList<String>();
			HashMap<String,String> fetchEvents = new HashMap<String,String>();
		    try {
				preparedStatement = connection.prepareStatement(ScraperUtils.fetchEventNames);
				preparedStatement.setString(1, dbName);
				resultSet = preparedStatement.executeQuery();
				/* First we fetch the names of the events in the database */
				while (resultSet.next()) {
		            String eventName = (resultSet.getString("EVENT_NAME"));
		            eventList.add(eventName);
		        }
				/* Second we iterate over the list and grab the create code for the EVENT */
				for (String temp : eventList){
					String query =  "SHOW CREATE EVENT " + dbName + "." + temp;
					stmt = connection.createStatement();
			        ResultSet rs = stmt.executeQuery(query); 	
			        // Here we add the event name and the create code to the hashmap
			        while (rs.next()) {
						String functionContents = (rs.getString("Create Event"));
				        fetchEvents.put(temp, functionContents);    
			        }
				}
			} catch (SQLException ex) {
				Logger.getLogger(ScraperData .class.getName()).log(Level.SEVERE, null, ex);
				System.out.println("method setEvents");
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
			        // Here we add the function name and the create code to the hashmap
			        while (rs.next()) {
						String functionContents = (rs.getString("Create Function"));
				        fetchFunctions.put(temp, functionContents);    
			        }
				}
				 
			} catch (SQLException ex) {
				Logger.getLogger(ScraperData .class.getName()).log(Level.SEVERE, null, ex);
				System.out.println("method setFunctions");
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
			List<String> triggerList = new ArrayList<String>();
			HashMap<String,String> fetchTriggers = new HashMap<String,String>();
		    try {
				preparedStatement = connection.prepareStatement(ScraperUtils.fetchTriggerNames);
				preparedStatement.setString(1, dbName);
				resultSet = preparedStatement.executeQuery();
				/* First we fetch the names of the triggers in the database */
				while (resultSet.next()) {
		            String triggerName = (resultSet.getString("trigger_name"));
		            triggerList.add(triggerName);
		        }		
				/* Second we iterate over the list and grab the create code for the TRIGGERS */
				for (String temp : triggerList){
					String query =  "SHOW CREATE TRIGGER " + dbName + "." + temp;
					stmt = connection.createStatement();
			        ResultSet rs = stmt.executeQuery(query);    
			        // Here we add the trigger name and the create code to the hashmap
			        while (rs.next()) {
						String triggerContents = (rs.getString("SQL Original Statement"));
				        fetchTriggers.put(temp, triggerContents);    
			        }
				}
			} catch (SQLException ex) {
				Logger.getLogger(ScraperData .class.getName()).log(Level.SEVERE, null, ex);
				System.out.println("method setTriggers");
			}finally {
		        if (preparedStatement != null) { preparedStatement.close(); }
		    }
		    System.out.println("Number of Triggers fetched: " + fetchTriggers.size());
		    this.triggers = fetchTriggers;
		} // END setTriggers	
		
}
