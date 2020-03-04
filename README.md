# jdbc-restservlet
This servlet is build on [jdbc-rest](https://github.com/vnetcon/jdbc-rest) driver and it serves as REST API server.
The idea is to offer an out-of-the box REST API solution for web developers (Angular, React, Vue etc.). 
In short web developer need to write only client side code and sql - no need for middle tier applications for accessing database. 

Key features:
* REST API based on [jdbc-rest](https://github.com/vnetcon/jdbc-rest) driver (select/insert/update/delete)
* email endpoint for sending emails
* upload endpoint for storing files into database (under development)
* download endpoint for retrieving files from database (under development)

## Quick Start (for windows 64bit)
For setting up the development environment you don't need install anything. 
You just download the zip file and start the downloaded software with following steps:  

* create folder c:\vnetcon
* Download Development environment [here]() to c:\vnetcon
* unzip the file. After this you should have c:\vnetcon\dev-env folder
* Create folder c:\etc\vnetcon and copy the database.properties file there
* Start databaes by double clicking 1_StartPostgreSQL.bat
* Start [DBeaver](https://dbeaver.io/) database tool by double clicking 2_StartDBeaver.bat
* Start Tomcat by double clicking 3_StartTomcat8.bat

After this you can point your browser to http://localhost:8080/jdbc-rest/rest/default/getUser/v1?userid=3 
to see the demo json



## Building
If you want to build the war file your self and deploy it to some other servlet engine, take the following steps. Before these steps you need to build the [jdbc-rest](https://github.com/vnetcon/jdbc-rest) jdbc driver to get it to your local maven repository.
1. Clone the repo and move to the folder where pom.xml exists
2. execute: mvn clean isntall
3. execute: mvn package  
4. Deploy the jdbc-rest.war form target folder to servlet engine 

If you develop the servlet you can run

mvn jetty:run

To start the development servlet engine.

## Commercial use
If you want to use this in closed code project or product you can buy a 99 USD license [here](https://vnetcon.com)  
If you think the price is too low or high you can also change the price there :)

## sql syntex and parameters
In short the idea is to contert normal sql to json by with --[json] comment. This comment will tell the driver 
to convert execute the statement as jdbc-rest statement.  
Below are some notes related to this. More detailed examples can be fuond from dev-nev.zip and DBeaver in there.
* --[json]: convert the result set to json and in insert/update/delete replace the rest-json parameters '{param_name}' with correct valus
* --[json=Person]: Give the name for root elemente in select statements
* r_ at the begining of parameter indicates that the actual value is retrived from htttp request (e.g. client send client id the parameter is sql should be '{r_clientid}'
* --[json:Person; r_clientid=default_value]: Set the default value for parameter 
* hidden_ indicates that the column should not be displayed in result json (e.g. select a as hidden_a from table)
* subquery_ indicates that the column is a select that should be executed (e.g. select 'select a, b form table' as subquery_colname). It is possible to have subqueries in subqueries.
* t_ indicates that the param value should be replaced in subquery with "parent sql column value" (e.g.  '{t_userid}' would be replaced with userid columnvalue from main query
  

## Security
In general the main idea for taking care of the security is to put this rest api server behind apacahe or other http server that takes care of authentication (ldap, openidc, shibboleth etc.). There are also some build in features in server  
* all values are "escaped" to prevent sql injections
* vnetcon-token can be set in http headers and configured to REST_SERVLET_CONFIG table (syntax "token-1";"token-2"). Only valid tokens are then served by server

All http headers are also passed to processing as request parameters so you can use those also in your sql statements as regular request paraeters (e.g. username from http headers send by apache)
  
## Scaling





