# liquibase_mysql_scraper
A java console application to scrape a MySQL database and create the xml files needed for Liquibase version control system. 
http://www.liquibase.org/

Liquibase is a Database version control system. It has a utility, generateChangeLog, to scrape data from existing databases.
However this utility only generates the change log to recreate the current database schema. Stored procedures,functions,
triggers, and events are not included.

This is the issue that this application seeks to address. Connect to a database extract the information and create the xml files
need to add stored procedures,functions,triggers, and events to version control.

When the application is run on the command line as:

java -jar liquibase\_mysql\_scraper.jar -h

The following usage message is returned.
<pre>
usage: liquibase\_mysql\_scraper [-h] IP Address [IP Address ...]
                               Port [Port ...] dbName [dbName ...]
                               dbUser [dbUser ...] dbPasswd [dbPasswd ...]

A program to scrape an existing mysql database instance
 so that Liquibase source management can be used.

positional arguments:
  IP Address             The ip address  of  the  server  hosting the mysql
                         database server
  Port                   The port  with  which  to  connect  to  the  mysql
                         database server
  dbName                 The  specific  database  instance   on  the  mysql
                         database server
  dbUser                 The user account to connect  to the mysql database
                         instance on the mysql database server
  dbPasswd               The password for the user  account used to connect
                         to the mysql database instance
 
optional arguments:
  -h, --help             show this help message and exit
 
 
 </pre>                        
  
When the correct parameters are passed and a database connection is made, the application will extract 
information from the data and creates the following directory and file structure. You can the run the 
liquibase update command pointed at the scraper.masterChangelog.xml file and import these changes to 
your liquibase version control.
<pre>
|-- liquibase_files/
    |-- scraper.masterChangelog.xml
    |-- stored_procedures/
          |-- storedProcedures.masterChangelog.xml
          |-- all other xml files
    |-- views/
          |-- views.masterChangelog.xml
          |-- all other xml files
    |-- events/
          |-- events.masterChangelog.xml
          |-- all other xml files
    |-- triggers/
          |-- triggers.masterChangelog.xml
          |-- all other xml files
    |-- functions/
          |-- functions.masterChangelog.xml
          |-- all other xml files
    |-- tables/
          |-- tables.masterChangelog.xml
          |-- all other xml files
          |-- data/
</pre>

#Credit where due:  
The application is using the argparse4j library available at  https://github.com/tatsuhiro-t/argparse4j/releases 
to handle the command line argument processing. (argparse4j-0.6.0.jar)

You will also need the MySQL connector (mysql-connector-java-5.1.36-bin.jar)

# Where it's currently at in the development process
I just finished phase one testing against a sample database running on MySQL 5.6. It was able to complete the extraction without
error and I then was able to use liquibase update against the generated files and liquibase ran without throwing any exceptions.

Phase two testing will require a comparison of the sample database under version control with a control version of the sample database for a test of data integrity. Eventually I will find time for this but be aware that PHASE TWO TESTING IS NOT COMPLETE.

# Future of the project
1. Add a GUI.
2. Add additional database systems to the list. SQL Server, PostgreSQL, etc.
3. Have a beer.

# Disclaimer
Use of this application at your own risk. Author is not responsible for data loss, data corruption or 
hair loss due to the aforementioned. This software is copyright protected.




