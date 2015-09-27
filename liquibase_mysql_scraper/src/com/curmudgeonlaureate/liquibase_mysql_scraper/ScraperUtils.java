package com.curmudgeonlaureate.liquibase_mysql_scraper;
import java.io.ByteArrayOutputStream;
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
	  /**
	   The caller references the constants using Scraper_utils.<STRING>
	   and so on. Thus, the caller should be prevented from constructing objects of 
	   this class, by declaring this private constructor. 
	  */
	  private ScraperUtils (){
	    throw new AssertionError();
	  }

	public static final String changeLogHeader =  "<databaseChangeLog" + "\r\n"
			+ "xmlns='http://www.liquibase.org/xml/ns/dbchangelog'" + "\r\n"
			+ "xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'" + "\r\n" 
			+ "xmlns:ext='http://www.liquibase.org/xml/ns/dbchangelog-ext'" +  "\r\n"
			+ "xsi:schemaLocation='http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.1.xsd http://www.liquibase.org/xml/ns/dbchangelog-ext http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-ext.xsd'>";
	public static final String changeLogFooter = "\r\n</databaseChangeLog>";
	public static final String masterChangeLogIncludeBegin = "<include file='";
	public static final String masterChangeLogIncludeEnd = "'/>";
	public static final String fetchViews = "SELECT TABLE_NAME, VIEW_DEFINITION FROM INFORMATION_SCHEMA.VIEWS WHERE TABLE_SCHEMA = ?";
	public static final String fetchStoredProcedures = "SELECT SPECIFIC_NAME,ROUTINE_DEFINITION FROM INFORMATION_SCHEMA.ROUTINES WHERE ROUTINE_TYPE = 'PROCEDURE' AND ROUTINE_SCHEMA = ?";
	public static final String fetchEvents = "SELECT EVENT_NAME,EVENT_DEFINITION FROM INFORMATION_SCHEMA.EVENTS WHERE EVENT_SCHEMA = ?";
	public static final String fetchFuntions = "SELECT SPECIFIC_NAME,ROUTINE_DEFINITION FROM INFORMATION_SCHEMA.ROUTINES WHERE ROUTINE_TYPE = 'Function' AND ROUTINE_SCHEMA = ?"; 
	public static final String fetchTables = "SELECT  TABLE_NAME  FROM information_schema.tables WHERE TABLE_TYPE = 'BASE TABLE' AND TABLE_SCHEMA = ? AND table_name NOT LIKE '%cxm_entity_instance%' AND table_name NOT LIKE '%databasechangelog%';";
	public static final String fetchTriggers = "SELECT trigger_name, action_statement FROM information_schema.triggers WHERE trigger_schema = ?";
	public static final String scraperMasterChangeLogIncludes = "\r\n<include file='stored_procedures/storedProcedures.masterChangelog.xml'/>"
			+ "\r\n<include file='views/views.masterChangelog.xml'/> \r\n<include file='functions/functions.masterChangelog.xml'/>"
			+ " \r\n<include file='triggers/triggers.masterChangelog.xml'/>  \r\n<include file='events/events.masterChangelog.xml'/> ";
	
	static final String[] directoriesToCreate = { "stored_procedures","views","events","triggers","functions","tables" };

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

	 
	 
	 /**
	    * Escape string ready for insert via mysql client
	    *
	    * @param  bytesIn       String to be escaped passed in as byte array
	    * @return bytesOut      MySQL compatible insert ready ByteArrayOutputStream
	    */
	    public static ByteArrayOutputStream escapeString(byte[] bytesIn){
	        int countBytes = bytesIn.length;
	        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream(countBytes+ 2);
	        for (int i = 0; i < countBytes; ++i) {
	            byte b = bytesIn[i];

	            switch (b) {
	            case 0: /* Must be escaped for 'mysql' */
	                    bytesOut.write('\\');
	                    bytesOut.write('0');
	                    break;

	            case '\n': /* Must be escaped for logs */
	                    bytesOut.write('\\');
	                    bytesOut.write('n');
	                    break;

	            case '\r':
	                    bytesOut.write('\\');
	                    bytesOut.write('r');
	                    break;

	            case '\\':
	                    bytesOut.write('\\');
	                    bytesOut.write('\\');

	                    break;

	            case '\'':
	                    bytesOut.write('\\');
	                    bytesOut.write('\'');

	                    break;

	            case '"': /* Better safe than sorry */
	                    bytesOut.write('\\');
	                    bytesOut.write('"');
	                    break;

	            case '\032': /* This gives problems on Win32 */
	                    bytesOut.write('\\');
	                    bytesOut.write('Z');
	                    break;

	            default:
	                    bytesOut.write(b);
	            }
	        }
	        return bytesOut;
	    }
	
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
	    }
}// end class Scraper_utils 
