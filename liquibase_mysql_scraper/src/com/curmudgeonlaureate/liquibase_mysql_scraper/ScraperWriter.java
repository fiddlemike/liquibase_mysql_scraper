package com.curmudgeonlaureate.liquibase_mysql_scraper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/** Description of ScraperWriter
* The purpose for this class is write the data fetched from the 
* MySQL database to an appropriately formatted XML usable
* by Liquibase
* 
* @author Michael Machado
* @version .1 August 30, 2015
*/
public class ScraperWriter {

	
	
	
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
