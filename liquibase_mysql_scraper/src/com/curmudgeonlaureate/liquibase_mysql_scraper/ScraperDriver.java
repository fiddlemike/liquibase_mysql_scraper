package com.curmudgeonlaureate.liquibase_mysql_scraper;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.ArgumentType;
import net.sourceforge.argparse4j.inf.Namespace;

/** Description of ScraperDriver
* The purpose for this class is to be the main driver class
* for the liquibase_mysql_scraper application. 
* 
* Also the checking of command line arguments will be handled in this class.
*  
* 
* @author Michael Machado
* @version .1 July 3, 2015
*/
public class ScraperDriver {

	
	private static class IpAddress implements ArgumentType<String> {

	    @Override
	    public String convert(ArgumentParser parser, Argument arg, String value)
	            throws ArgumentParserException {
	        try {
	            String address=value;
	            if (checkIPv4(address) != true) {
	                throw new ArgumentParserException(String.format(
	                        "%s is not a valid ip address", address), parser);
	            }
	            return address;
	        } catch (Exception e) {
	            throw new ArgumentParserException(e, parser);
	        }
	    }
	    
	    public static final boolean checkIPv4(final String ip) {
	        boolean isIPv4;
	        try {
		        final InetAddress inet = InetAddress.getByName(ip);
		        isIPv4 = inet.getHostAddress().equals(ip)
		                && inet instanceof Inet4Address;
	        } catch (final UnknownHostException e) {
	        	isIPv4 = false;
	        }
	        return isIPv4;
	    } // end method checkIPv4
	}// end static class IpAddress 
	    
	public static void main(String [ ] args)
	{
		ArgumentParser parser = ArgumentParsers.newArgumentParser("liquibase_mysql_scraper")
				.defaultHelp(true)
                .description("A program to scrape an exisiting mysql database instance\n "
                		+ "so that Liquibase source management can be used.");
        parser.addArgument("ipaddress")
                .metavar("IP Address")
                .type(new IpAddress())
                .nargs("+")
                .required(true)
                .help("The ip address of the server hosting the mysql database server");
        parser.addArgument("port")
		        .metavar("Port")
		        .type(Integer.class)
		        .choices(Arguments.range(1025, 65535))
		        .required(true)
		        .nargs("+")
		        .help("The port with which to connect to the mysql database server");
        parser.addArgument("dbName")
		        .metavar("dbName")
		        .type(String.class)
		        .nargs("+")
		        .required(true)
		        .help("The specific database instance on the mysql database server");
        parser.addArgument("dbUser")
		        .metavar("dbUser")
		        .type(String.class)
		        .nargs("+")
		        .required(true)
		        .help("The user account to connect to the mysql database instance on the mysql database server");
        parser.addArgument("dbPasswd")
		        .metavar("dbPasswd")
		        .type(String.class)
		        .nargs("+")
		        .required(true)
		        .help("The password for the user account used to connect to the mysql database instance");
        		Namespace res;
        try {
        		res = parser.parseArgs(args);
                System.out.println(res);
                
            } catch (ArgumentParserException e) {
                parser.handleError(e);
                System.exit(1);
            }
         
         
         // Create and test the connection
         DBConnection myConn = new DBConnection(args);
         if(myConn.getIsValidConnection() == true){
        	 System.out.println(myConn.getmysqlAccess());
         }else{
        	 System.out.println("Test Failed");
         }
         // Write the directory structure
         ScraperUtils.createDirectoryStructure();
         // Fetch the data
         ScraperData dbData = new ScraperData();
         try {
			dbData.setAll(myConn.getDbConn(), myConn.getDbName());
		} catch (SQLException ex) {
			Logger.getLogger(ScraperDriver .class.getName()).log(Level.SEVERE, null, ex);
		}
         // Write the XML files used by liquibase
         ScraperWriter.writeViewsXMLFiles(dbData); 
         ScraperWriter.writeProceduresXMLFiles(dbData); 
         ScraperWriter.writeFunctionsXMLFiles(dbData); 
         ScraperWriter.writeEventsXMLFiles(dbData); 
         ScraperWriter.writeTriggersXMLFiles(dbData); 
         ScraperWriter.writeTableSQLFiles(dbData,myConn);
         
         
	 } //end method main()
	
} // end class ScraperDriver
