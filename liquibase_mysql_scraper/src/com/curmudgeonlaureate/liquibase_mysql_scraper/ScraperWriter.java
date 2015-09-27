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
	

	/* 
	 * Here we use the hashMap from the ScraperData class that 
	 * holds the key, value pairs for the views extracted from the database
	 * to write the liquibase compliant create view xml files.
	 * 
	 * example:
	 * 
	 *		<changeSet author="liquibase-mysql_scraper" id="createView-example">
	 *		    <createView catalogName="cat"
	 *		            replaceIfExists="true"
	 *			            schemaName="public"
	 *			            viewName="v_person">select id, name from person where id > 10</createView>
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
		Iterator<Entry<String, String>> entries =   dbData.getViews().entrySet().iterator();
		while (entries.hasNext()) {
		    Map.Entry<String, String> entry = entries.next();
		    String key = (String)entry.getKey();
		    String value = (String)entry.getValue();
		    String fileContents = ScraperUtils.changeLogHeader + "\r\n" + "<changeSet author='liquibase-mysql_scraper' id='createView-"
		    		+ key + "'>" + "\r\n" + "<createView catalogName='test'" + "\r\n"
		    		+ " replaceIfExists='true'" + "\r\n" 
		    		+  "schemaName='public'" + "\r\n" 
		    		+ "viewName='" + key + "'>"
		    		+ "\r\n" + value +  "\r\n" 
		    		+ "</createView>" +"\r\n" 
		    		+"</changeSet>" 
		    		+ ScraperUtils.changeLogFooter;
		    String fileName = key + ".xml";
		    writeFilesToDirectory(directory, fileName, fileContents);
		    String includeFileName = ScraperUtils.masterChangeLogIncludeBegin + fileName + ScraperUtils.masterChangeLogIncludeEnd;
		    fileList.add(includeFileName);
		}
		/* Here we write the view's master changelog file*/
		String changeLogIncludes = ScraperUtils.concatStringsWSep(fileList, "");
		String changeLogContents = ScraperUtils.changeLogHeader + changeLogIncludes + ScraperUtils.changeLogFooter;
		writeFilesToDirectory(directory, viewChangeLog, changeLogContents);
	}
	
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
	}
	
	public static void writeTable(String table, DBConnection myConn){
        try{
           Statement tableQuery = myConn.getDbConn().createStatement 
        		   (ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
           
           /* Create the file to write to using the table name */
           String fileName = table + ".sql";
		   File file = new File("/liquibase_files/tables/" + fileName);

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
           String prefix = new String("INSERT INTO " + table + " (");
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
           System.err.println (e.getMessage());
       }catch(SQLException e){
           System.err.println (e.getMessage());
       }
   }
}
