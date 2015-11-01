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

/** Description of Scraper_utils
* The purpose for this class is hold utility methods 
* and String constants
* 
* @author Michael Machado
* @version .1 July 3, 2015
*/
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScraperUtils {
	  /** Description of ScraperUtils
	   *   A utility class for String Constants and helper methods
	   * @author Michael Machado
	   * @version .1 July 3, 2015
	  */
	
	/*
	 * The caller references the constants using Scraper_utils.<STRING>
	 * and so on. Thus, the caller should be prevented from constructing objects of 
	 * this class, by declaring this private constructor. 
	 */
	  private ScraperUtils (){
	    throw new AssertionError();
	  }
/*
 * String Constants
 * We will use these to assemble SQL statements and XML files.
 * */
	public static final String changeLogHeader =  "<databaseChangeLog" + "\r\n"
			+ "xmlns='http://www.liquibase.org/xml/ns/dbchangelog'" + "\r\n"
			+ "xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'" + "\r\n" 
			+ "xmlns:ext='http://www.liquibase.org/xml/ns/dbchangelog-ext'" +  "\r\n"
			+ "xsi:schemaLocation='http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.1.xsd http://www.liquibase.org/xml/ns/dbchangelog-ext http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-ext.xsd'>";
	
	public static final String changeLogFooter = "\r\n</databaseChangeLog>";
	
	public static final String masterChangeLogIncludeBegin = "<include file='";
	
	public static final String masterChangeLogIncludeEnd = "'/>";
	
	public static final String fetchViews = "SELECT TABLE_NAME, VIEW_DEFINITION FROM INFORMATION_SCHEMA.VIEWS WHERE TABLE_SCHEMA = ?";
	
	public static final String fetchStoredProcedureNames = "SELECT SPECIFIC_NAME FROM INFORMATION_SCHEMA.ROUTINES WHERE ROUTINE_TYPE = 'PROCEDURE' AND ROUTINE_SCHEMA = ?";
	
	public static final String fetchEventNames = "SELECT EVENT_NAME FROM INFORMATION_SCHEMA.EVENTS WHERE EVENT_SCHEMA = ?";
	
	public static final String fetchFunctionNames = "SELECT SPECIFIC_NAME FROM INFORMATION_SCHEMA.ROUTINES WHERE ROUTINE_TYPE = 'Function' AND ROUTINE_SCHEMA = ?"; 
	
	public static final String fetchTriggerNames = "SELECT trigger_name FROM information_schema.triggers WHERE trigger_schema = ?";
	
	public static final String scraperMasterChangeLogIncludes = "\r\n<include file='liquibase_files/stored_procedures/storedProcedures.masterChangelog.xml'/>"
			+ "\r\n<include file='liquibase_files/views/views.masterChangelog.xml'/> \r\n<include file='liquibase_files/functions/functions.masterChangelog.xml'/>"
			+ " \r\n<include file='liquibase_files/triggers/triggers.masterChangelog.xml'/>  \r\n<include file='liquibase_files/events/events.masterChangelog.xml'/> "
			+ " \r\n<include file='liquibase_files/tables/tables.masterChangelog.xml'/>";
	
	public static final String commentOpenTag = "<comment>";
	
	public static final String commentCloseTag = "</comment>";
	
	public static final String[] directoriesToCreate = { "stored_procedures","views","events","triggers","functions","tables","tables/data"};

	/*
	 * We write to write the directory structure for our result files.
	 * */
	 public static void createDirectoryStructure () {
		 /* First write the defined directory structure */
		 for (String element : directoriesToCreate) {
			    String scraperDirectories = "liquibase_files/" + element;
				File files = new File(scraperDirectories);			
				if (!files.exists()) {
			        if (files.mkdirs()) {
			            System.out.println("sub directories created successfully for " + element);
			        } else {
			            System.out.println("failed to create sub directories for " + element);
			        }
			    } // end !files.exists()
		} // end for loop
		 /* Second write the defined Master Changelog for the database */
		String scraperMasterChangelog = ScraperUtils.changeLogHeader 
				+ ScraperUtils.scraperMasterChangeLogIncludes 
				+ ScraperUtils.changeLogFooter ;
		FileWriter fileWriter = null;
        try {
            File newXMLFile = new File("liquibase_files/scraper.masterChangelog.xml");
            fileWriter = new FileWriter(newXMLFile);
            fileWriter.write(scraperMasterChangelog);
            fileWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(ScraperUtils.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fileWriter.close();
            } catch (IOException ex) {
                Logger.getLogger(ScraperUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
			
	 } //end method createDirectoryStructure
	 
/*
 * We want to parse special characters for the contents of the xml files.
 * The big issue is < and > being used in sql code. We will translate them 
 * to UTF-8 encoding and Liquibase will not throw an exception.
 *  Sequencing here matters
 * */
	 public static String cleanString(String inputString){
		 String cleanedString = inputString.replaceAll("#", "-- ");	
		 cleanedString = cleanedString.replaceAll(">", "&#62;");
		 cleanedString = cleanedString .replaceAll("<", "&#60;");	
		 return cleanedString;
	 } // END cleanString

	 
	 /*
	     * Concatenate the strings in a list into a string
	     * 
	     */
	    public static String concatStringsWSep(Iterable<String> strings, String separator) {
	        StringBuilder sb = new StringBuilder();
	        String sep = "";
	        for(String s: strings) {
	            sb.append(sep).append(s);
	            sep = separator;
	        }
	        return sb.toString();                           
	    } // END concatStringsWSep
}// END  class Scraper_utils 
