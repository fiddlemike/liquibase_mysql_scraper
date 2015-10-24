package com.curmudgeonlaureate.liquibase_mysql_scraper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Description of ScraperWriter
* The purpose for this class is write the data fetched from the 
* MySQL database to an appropriately formatted XML usable
* by Liquibase
* 
* @author Michael Machado
* @version .1 August 30, 2015
*/
public class ScraperWriter {
	
	
	public static void writeProceduresXMLFiles(ScraperData dbData){
		/* 
		 * We iterate through the hashMap and write the xml files
		 * We will also add the file names to a list so that we can write 
		 * the procedures's master changelog file
		 */
		List<String> fileList = new ArrayList<String>();
		String directory = "liquibase_files/stored_procedures";
		String procedureChangeLog = "storedProcedures.masterChangelog.xml";
		String dbName = dbData.getDbName();
		Iterator<Entry<String, String>> entries =   dbData.getStoredProcedures().entrySet().iterator();
		while (entries.hasNext()) {
		    Map.Entry<String, String> entry = entries.next();
		    String key = (String)entry.getKey();
		    String value = (String)entry.getValue();
		    String cleanedValue = ScraperUtils.cleanString(value);
		    String fileContents = ScraperUtils.changeLogHeader + "\r\n" 	    			
		    		/* ChangeSet creates the stored procedure*/
		    		+ "<changeSet "
		    		+ "author='liquibase-mysql_scraper' " + "\t"  
		    		+ "id='createProcedure-" + key + "'\t"  
		    		+ "runOnChange='true'"  + ">"      	+ "\r\n" 
		    		+ ScraperUtils.commentOpenTag + "\r\n" 
		    		+ "Adding stored procedure " + key +"  to the base system for database "  + dbName + "\r\n"
		    		+ ScraperUtils.commentCloseTag + "\r\n"
		    		+ "<sql dbms='mysql' endDelimiter='#' splitStatements='true'>" + "\r\n" 
		    		+ "DROP PROCEDURE IF EXISTS " + key + ";\r\n" + "#" + "\r\n"
		    		+ cleanedValue +  "\r\n" + "#" + "\r\n"
		    		+ "</sql>" +"\r\n" 
		    		+"</changeSet>" 
		    		+ ScraperUtils.changeLogFooter;
		    
		    
		    String fileName = key + ".xml";
		    writeFilesToDirectory(directory, fileName, fileContents);
		    String includeFileName = ScraperUtils.masterChangeLogIncludeBegin + 
		    		"liquibase_files/stored_procedures/"		+
		    		fileName + 
		    		ScraperUtils.masterChangeLogIncludeEnd;
		    fileList.add(includeFileName);
		}
		/* Here we write the procedures's master changelog file*/
		String changeLogIncludes = ScraperUtils.concatStringsWSep(fileList, "\r\n");
		String changeLogContents = ScraperUtils.changeLogHeader + "\r\n" + changeLogIncludes + ScraperUtils.changeLogFooter;
		writeFilesToDirectory(directory, procedureChangeLog, changeLogContents);
	} // END writeProceduresXMLFiles(ScraperData dbData)

	/* 
	 * Here we use the hashMap from the ScraperData class that 
	 * holds the key, value pairs for the views extracted from the database
	 * to write the liquibase compliant create view xml files.
	 * 
	 * example:
	 * 
	 *		<changeSet author="liquibase-mysql_scraper" id="createView-example">
	 *		<comment></comment>
	 *		    <createView 
	 *		            replaceIfExists="true"
	 *					runOnChange="true"
	 *			         schemaName="dbName"
	 *			          viewName="v_person">select id, name from person where id > 10</createView>
	 *			</changeSet>
	 *
	 */
	public static void writeViewsXMLFiles(ScraperData dbData){
		/* 
		 * We iterate through the hashMap and write the xml files
		 * We will also add the file names to a list so that we can write 
		 * the view's master changelog file
		 */
		List<String> fileList = new ArrayList<String>();
		String directory = "liquibase_files/views";
		String viewChangeLog = "views.masterChangelog.xml";
		String dbName = dbData.getDbName();
		Iterator<Entry<String, String>> entries =   dbData.getViews().entrySet().iterator();
		while (entries.hasNext()) {
		    Map.Entry<String, String> entry = entries.next();
		    String key = (String)entry.getKey();
		    String value = (String)entry.getValue();
		    String cleanedValue = ScraperUtils.cleanString(value);
		    String fileContents = ScraperUtils.changeLogHeader + "\r\n" 
		    		+ "<changeSet author='liquibase-mysql_scraper' id='createView-"
		    		+ key  + "'\t"  
		    		+ "runOnChange='true'>" + "\r\n" 
		    		+ ScraperUtils.commentOpenTag + "\r\n" 
		    		+ "adding view " + key + " to base system for database "  + dbName + "\r\n"
		    		+ ScraperUtils.commentCloseTag + "\r\n"
		    		+ "<createView" + "\t"
		    		+ "replaceIfExists='true'" + "\t" 
		    		+  "schemaName='" + dbName  + "'\t" 
		    		+ "viewName='" + key + "'>"
		    		+ "\r\n" + cleanedValue  +  "\r\n" 
		    		+ "</createView>" +"\r\n" 
		    		+"</changeSet>" 
		    		+ ScraperUtils.changeLogFooter;
		    String fileName = key + ".xml";
		    writeFilesToDirectory(directory, fileName, fileContents);
		    String includeFileName = ScraperUtils.masterChangeLogIncludeBegin + 
		    		"liquibase_files/views/" +
		    		fileName + 
		    		ScraperUtils.masterChangeLogIncludeEnd;
		    fileList.add(includeFileName);
		}
		/* Here we write the view's master changelog file*/
		String changeLogIncludes = ScraperUtils.concatStringsWSep(fileList, "\r\n");
		String changeLogContents = ScraperUtils.changeLogHeader + "\r\n" + changeLogIncludes + ScraperUtils.changeLogFooter;
		writeFilesToDirectory(directory, viewChangeLog, changeLogContents);
	} // END writeViewsXMLFiles(ScraperData dbData)
	
	public static void writeFunctionsXMLFiles(ScraperData dbData){
		/* 
		 * We iterate through the hashMap and write the xml files
		 * We will also add the file names to a list so that we can write 
		 * the functions' master changelog file
		 */
		List<String> fileList = new ArrayList<String>();
		String directory = "liquibase_files/functions";
		String functionChangeLog = "functions.masterChangelog.xml";
		String dbName = dbData.getDbName();
		Iterator<Entry<String, String>> entries =   dbData.getFunctions().entrySet().iterator();
		while (entries.hasNext()) {
		    Map.Entry<String, String> entry = entries.next();
		    String key = (String)entry.getKey();
		    String value = (String)entry.getValue();
		    String cleanedValue = ScraperUtils.cleanString(value);
		    String fileContents = ScraperUtils.changeLogHeader + "\r\n" 
		    		+ "<changeSet author='liquibase-mysql_scraper' id='createFunction-"
		    		+ key + "'" + "\t"  
		    		+ "runOnChange='true'>" + "\r\n" 
		    		+ ScraperUtils.commentOpenTag + "\r\n" 
		    		+ "Adding the stored function " + key + " to base system for database "  + dbName + "\r\n"
		    		+ ScraperUtils.commentCloseTag + "\r\n"
		    		+ "<sql dbms='mysql' endDelimiter='#' splitStatements='true'>" + "\r\n" 
		    		+ "DROP FUNCTION IF EXISTS " + key + ";\r\n" + "#" + "\r\n"
		    		+  cleanedValue +  "\r\n" + "#" + "\r\n"
		    		+ "</sql>" +"\r\n" 
		    		+"</changeSet>" 
		    		+ ScraperUtils.changeLogFooter;
		    String fileName = key + ".xml";
		    writeFilesToDirectory(directory, fileName, fileContents);
		    String includeFileName = ScraperUtils.masterChangeLogIncludeBegin + 
		    		"liquibase_files/functions/" +
		    		fileName + 
		    		ScraperUtils.masterChangeLogIncludeEnd;
		    fileList.add(includeFileName);
		}
		/* Here we write the view's master changelog file*/
		String changeLogIncludes = ScraperUtils.concatStringsWSep(fileList, "\r\n");
		String changeLogContents = ScraperUtils.changeLogHeader + "\r\n" + changeLogIncludes + ScraperUtils.changeLogFooter;
		writeFilesToDirectory(directory, functionChangeLog, changeLogContents);
	} // END writeFunctionsXMLFiles(ScraperData dbData)
	
	public static void writeEventsXMLFiles(ScraperData dbData){
		/* 
		 * We iterate through the hashMap and write the xml files
		 * We will also add the file names to a list so that we can write 
		 * the functions' master changelog file
		 */
		List<String> fileList = new ArrayList<String>();
		String directory = "liquibase_files/events";
		String eventChangeLog = "events.masterChangelog.xml";
		String dbName = dbData.getDbName();
		Iterator<Entry<String, String>> entries =   dbData.getEvents().entrySet().iterator();
		while (entries.hasNext()) {
		    Map.Entry<String, String> entry = entries.next();
		    String key = (String)entry.getKey();
		    String value = (String)entry.getValue();
		    String cleanedValue = ScraperUtils.cleanString(value);
		    String fileContents = ScraperUtils.changeLogHeader + "\r\n" 
		    		+ "<changeSet author='liquibase-mysql_scraper' id='createEvent-"
		    		+ key + "'" + "\t"  
		    		+ "runOnChange='true'>" + "\r\n" 
		    		+ ScraperUtils.commentOpenTag + "\r\n" 
		    		+ "Adding the event " + key + " to base system for database "  + dbName + "\r\n"
		    		+ ScraperUtils.commentCloseTag + "\r\n"
		    		+ "<sql dbms='mysql' endDelimiter='#' splitStatements='true'>" + "\r\n" 
		    		+ "DROP EVENT IF EXISTS " + key + ";\r\n" + "#" + "\r\n"
		    		+ cleanedValue +  "\r\n" + "#" + "\r\n"
		    		+ "</sql>" +"\r\n" 
		    		+"</changeSet>" 
		    		+ ScraperUtils.changeLogFooter;
		    String fileName = key + ".xml";
		    writeFilesToDirectory(directory, fileName, fileContents);
		    String includeFileName = ScraperUtils.masterChangeLogIncludeBegin + 
		    		"liquibase_files/events/" +
		    		fileName + 
		    		ScraperUtils.masterChangeLogIncludeEnd;
		    fileList.add(includeFileName);
		}
		/* Here we write the events's master changelog file*/
		String changeLogIncludes = ScraperUtils.concatStringsWSep(fileList, "\r\n");
		String changeLogContents = ScraperUtils.changeLogHeader + "\r\n" + changeLogIncludes + ScraperUtils.changeLogFooter;
		writeFilesToDirectory(directory,eventChangeLog, changeLogContents);
	} // END writeEventsXMLFiles(ScraperData dbData)
	
	
	public static void writeTriggersXMLFiles(ScraperData dbData){
		/* 
		 * We iterate through the hashMap and write the xml files
		 * We will also add the file names to a list so that we can write 
		 * the functions' master changelog file
		 */
		List<String> fileList = new ArrayList<String>();
		String directory = "liquibase_files/triggers";
		String triggerChangeLog = "triggers.masterChangelog.xml";
		String dbName = dbData.getDbName();
		Iterator<Entry<String, String>> entries =   dbData.getTriggers().entrySet().iterator();
		while (entries.hasNext()) {
		    Map.Entry<String, String> entry = entries.next();
		    String key = (String)entry.getKey();
		    String value = (String)entry.getValue();
		    String cleanedValue = ScraperUtils.cleanString(value);
		    String fileContents = ScraperUtils.changeLogHeader + "\r\n" 
		    		+ "<changeSet author='liquibase-mysql_scraper' id='createTrigger-"
		    		+ key + "'" + "\t"  
		    		+ "runOnChange='true'>" + "\r\n" 
		    		+ ScraperUtils.commentOpenTag + "\r\n" 
		    		+ "Adding the trigger " + key + " to base system for database "  + dbName + "\r\n"
		    		+ ScraperUtils.commentCloseTag + "\r\n"
		    		+ "<sql dbms='mysql' endDelimiter='#' splitStatements='true'>" + "\r\n" 
		    		+ "DROP TRIGGER IF EXISTS " + key + ";\r\n" + "#" + "\r\n"
		    		+  cleanedValue +  "\r\n" + "#" + "\r\n"
		    		+ "</sql>" +"\r\n" 
		    		+"</changeSet>" 
		    		+ ScraperUtils.changeLogFooter;
		    String fileName = key + ".xml";
		    writeFilesToDirectory(directory, fileName, fileContents);
		    String includeFileName = ScraperUtils.masterChangeLogIncludeBegin + 
		    		"liquibase_files/triggers/" +
		    		fileName + 
		    		ScraperUtils.masterChangeLogIncludeEnd;
		    fileList.add(includeFileName);
		}
		/* Here we write the trigger's master changelog file*/
		String changeLogIncludes = ScraperUtils.concatStringsWSep(fileList, "\r\n");
		String changeLogContents = ScraperUtils.changeLogHeader + "\r\n" +changeLogIncludes + ScraperUtils.changeLogFooter;
		writeFilesToDirectory(directory, triggerChangeLog, changeLogContents);
	} // END writeTriggersXMLFiles(ScraperData dbData)
	
	public static void writeTableSQLFiles(ScraperData dbData, DBConnection currentConnect){
		LinkedList<String>  fetchedTables = dbData.getTables();
		List<String> fileList = new ArrayList<String>();
		String directory = "liquibase_files/tables";
		String tableChangeLog = "tables.masterChangelog.xml";
		String dbName = dbData.getDbName();
		Iterator<String> tableIterator = fetchedTables.iterator();
		while (tableIterator.hasNext()) {
			String table = tableIterator.next();
			// writeTable(table, currentConnect);
			
			String fileContents = ScraperUtils.changeLogHeader + "\r\n" 
		    		+ "<changeSet author='liquibase-mysql_scraper' id='sqlFile-"
		    		+ table + "'" + "\t"  
		    		+ "runOnChange='true'>" + "\r\n" 
		    		+ ScraperUtils.commentOpenTag + "\r\n" 
		    		+ "Adding the table " + table + " data to base system for database "  + dbName + "\r\n"
		    		+ ScraperUtils.commentCloseTag + "\r\n"
		    		+ "<sqlFile dbms='mysql' " + "\r\n"
		    		+ "encoding='utf8' " + "\r\n"
		    		+ "path='data/" + dbName + "_"+ table + ".sql' " + "\r\n"
		    		+ "endDelimiter=';' " + "\r\n" 
		    		+ "relativeToChangelogFile='true' " + "\r\n"	    				
		    		+ "splitStatements='true' " + "\r\n"
		    		+ "stripComments='true' " + "\r\n"
		    		+ "/>" + "\r\n"
		    		+"</changeSet>" 
		    		+ ScraperUtils.changeLogFooter;
		    String fileName = table + ".xml";
		    writeFilesToDirectory(directory, fileName, fileContents);
		    String includeFileName = ScraperUtils.masterChangeLogIncludeBegin + 
		    		"liquibase_files/tables/" + 
		    		fileName + 
		    		ScraperUtils.masterChangeLogIncludeEnd;
		    fileList.add(includeFileName);
		}
		
		/* Here we write the tables's master changelog file*/
		String changeLogIncludes = ScraperUtils.concatStringsWSep(fileList, "\r\n");
		String changeLogContents = ScraperUtils.changeLogHeader + "\r\n" +changeLogIncludes + ScraperUtils.changeLogFooter;
		writeFilesToDirectory(directory, tableChangeLog, changeLogContents);

			
	}// END writeTableSQLFiles
	
	
	public static void writeFilesToDirectory(String directory, String fileName, String fileContents){

	    FileWriter fileWriter = null;
        try {
        	String filePathAndName = directory + '/' + fileName;
            File newXMLFile = new File(filePathAndName);
            fileWriter = new FileWriter(newXMLFile);
            fileWriter.write(fileContents);
            fileWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(ScraperWriter .class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fileWriter.close();
            } catch (IOException ex) {
                Logger.getLogger(ScraperWriter .class.getName()).log(Level.SEVERE, null, ex);
            }
        }
	} // END writeFilesToDirectory
	
	public static void writeTable(String table, DBConnection myConn){
        try{
           Statement tableQuery = myConn.getDbConn().createStatement 
        		   (ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
           
           /* Create the file to write to using the table name */
           String fileName = table + ".sql";
		   File file = new File("liquibase_files/tables/sql/" + fileName);

			// if file doesn't exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
           
			FileWriter fw = new FileWriter(file, true);
			BufferedWriter bw = new BufferedWriter(fw);
           
           bw.write("\n\n--\n-- Dumping data for table `" + table + "`\n--\n\n");
           
           tableQuery.executeQuery ("SELECT /*!40001 SQL_NO_CACHE */ * FROM " + table);
           ResultSet rs = tableQuery.getResultSet ();
           ResultSetMetaData rsMetaData = rs.getMetaData();
           int columnCount = rsMetaData.getColumnCount();
           String prefix = new String("REPLACE INTO " + table + " (");
           for (int i = 1; i <= columnCount; i++) {
               if (i == columnCount){
                   prefix += rsMetaData.getColumnName(i) + ") VALUES(";
               }else{
                   prefix += rsMetaData.getColumnName(i) + ",";
               }
           }
           String postfix = new String();
     
           while (rs.next ())
           {

               postfix = "";
               for (int i = 1; i <= columnCount; i++) {
                   if (i == columnCount){
                       System.err.println(rs.getMetaData().getColumnClassName(i));
                       postfix += "'" + rs.getString(i) + "');\n";
                   }else{

                       System.err.println(rs.getMetaData().getColumnTypeName(i));
                       if (rs.getMetaData().getColumnTypeName(i).equalsIgnoreCase("LONGBLOB")){
                           try{
                               postfix += "'" + ScraperUtils.escapeString(rs.getBytes(i)).toString() + "',";
                           }catch (Exception e){
                               postfix += "NULL,";
                           }
                       }else{
                           try{
                               postfix += "'" + rs.getString(i).replaceAll("\n","\\\\n").replaceAll("'","\\\\'") + "',";
                           }catch (Exception e){
                               postfix += "NULL,";
                           }
                   }   }
               }
               bw.write(prefix + postfix + "\n");
              
           }
           rs.close ();
           tableQuery.close();
           bw.flush();
           bw.close();
       }catch(IOException e){
    	   System.out.println("IOException");
           System.err.println (e.getMessage());
       }catch(SQLException e){
    	   System.out.println("SQLException");
           System.err.println (e.getMessage());
       }
   } //END writeTable
	
	
}
