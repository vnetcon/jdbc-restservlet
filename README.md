# jdbc-restservlet
Web application that can be used as a REST API. The main idea is to reduce the middle tier development and keep the focus on client side development and database. This application is build on jdbc-rest driver.

Key features:
* REST API based on link [jdbc-rest](https://github.com/vnetcon/jdbc-rest) driver (select/insert/update/delete)
* email endpoint for sending emails
* upload endpoint for storing files into database (under development)
* download endpoint for retrieving files from database (under development)

## Building
mvn clean install
mvn package
mvn jetty:run 

The last command is for starting the jetty webserver for running the REST API.

## Quick start
Below are the steps to get up and running quickly. For some reason we like angluar and due to this web development based instructions are based on this.

1. Download the tomcat+jdbc-restservlet bundle from releases
2. create two file: /etc/vnetcon/database.properties and /etc/vnetcon/email.propeertis and add the required content into those
3. create schema VNETCON (in here the the size matters) into database you are using as your REST API database
4. create table REST_SERVLET_CONFIG (in here the size matters too) into VNETCON schema
5. Start tomcat bundle (after you have add the actual JDBC driver into it)
6. Create jdbc-rest sql statements in DBeaver and inseert those into REST_SERVLET_CONFIG table including endpoint etc. information
7. Configure a forward proxy to your web devlopment environment to point /rest calls to tomcat

After these steps you should be able to develop your web application against the jdbc-rest sql statements based REST API.

//TODO: Finalzie this documentation, add code and create wiki pages. In short: we will update these pages soon





