# jdbc-restservlet
This servlet is build on [jdbc-rest](https://github.com/vnetcon/jdbc-rest) driver and it serves as REST API server.
The idea is to offer an out-of-the box REST API solution for web developers (Angular, React, Vue etc.). 
In short web developer need to write only client side code and sql - no need for middle tier applications for accessing database. In our mind the development environment would be something like

* Angular/React/Vue etc project using the IDE/Editor you like. Or you can use [Page.js](https://github.com/vnetcon/page.js) - very simple pure html + javascrpt way to do things without "heavy frameworks".
* JDBC-RESTSERVLET web server running and configured to use your development database
* DBeaver for accessing database. This will be your backend & rest api development ide. For this you need to configure a database connection using [jdbc-rest](https://github.com/vnetcon/jdbc-rest) driver.
* Browser enabled with CORS for accessing the JDBC-RESTSERVLET during the development (e.g. chrome with [CORS plugin](https://chrome.google.com/webstore/detail/moesif-orign-cors-changer/digfbfaphojjndkpccljibejjbppifbc)

And thats it.

## Design your rest endpoint: Create a sql query that return the values you need
![jdbc-servlet_queryeditor](http://vnetcon.s3-website-eu-west-1.amazonaws.com/img/jdbc-servlet_queryeditor.PNG)
  
## Create an rest endpoint: Add your sql query to REST_SERVLET_CONFIG table
![jdbc-servlet_configview](http://vnetcon.s3-website-eu-west-1.amazonaws.com/img/jdbc-servlet_configview.PNG)
  
## View and debug rest api calls: View all your requests from REST_SERVLET_LOG table
![jdbc-servlet_logview](http://vnetcon.s3-website-eu-west-1.amazonaws.com/img/jdbc-servlet_logview.PNG)
  
## Confgure database connection: Simply edit text file
All configurations jdbc rest configuration are in one file: /opt/vnetcon/conf/database.properties (c:\opt\vnetcon\conf\database.properties in windows).
Below is a simple configuration against portgresql database

```
# connection properties
default.jdbc.driver=org.postgresql.Driver
default.jdbc.url=jdbc:postgresql://localhost:5432/postgres
default.jdbc.user=<your database username>
default.jdbc.pass=<your database password>
default.jdbc.logcon=default
```
  
Below are required sql statements for creating necessary tables to Postgresql database
```sql
CREATE SCHEMA "VNETCON";

CREATE TABLE "VNETCON"."REST_SERVLET_CONFIG" (
	"REST_ENDPOINT_ID" varchar(100) NOT NULL,
	"REST_ENDPOINT" varchar(100) NOT NULL,
	"VERSION" varchar(100) NOT NULL,
	"ALLOWED_TOKENS" varchar(2000) NULL DEFAULT 'ALL'::character varying,
	"ALLOWED_IPADDRESSES" varchar(2000) NULL DEFAULT 'ALL'::character varying,
	"JSON_SQL" varchar(2000) NOT NULL,
	"ENABLED" int4 NULL DEFAULT 1,
	CONSTRAINT "REST_SERVLET_CONFIG_pkey" PRIMARY KEY ("REST_ENDPOINT_ID")
);

CREATE TABLE "VNETCON"."REST_SERVLET_LOG" (
	"LOGTIME" timestamp NULL,
	"SQL" text NULL,
	"REQUEST_PARAMS" text NULL,
	"RESPONSE_JSON" text NULL,
	"REAL_VALUES" text NULL,
	"MESSAGE" text NULL
);

```
  
You can configure multiple database connections to database.properteis file. In short this mean that jdbc-restservlet can connect to 1-n databases same time and servee as rest api endpoint to several data sources.
One option to bind several databases into one and combine those data with sql is to use drill.
Below is an example configuration where drill is used for quering the data and REST_SERVLET_CONFIG and REST_SERVLET_LOG are configured to postgresql database.
  
```
# connection properties
default.jdbc.driver=org.postgresql.Driver
default.jdbc.url=jdbc:postgresql://localhost:5432/postgres
default.jdbc.user=<username>
default.jdbc.pass=<password>
default.jdbc.logcon=default

# rest servlet properties
# this is used when writing log events
default.jdbc.timestampfunc=current_timestamp

# connection properties
drill.jdbc.driver=org.apache.drill.jdbc.Driver
drill.jdbc.url=jdbc:drill:drillbit=localhost:31010
drill.jdbc.user=<username if authentication is configured>
drill.jdbc.pass=<password if authentication is configured>

# restservlet properties
drill.jdbc.restcon=default
drill.jdbc.logcon=default

```



## Enable email sending: Configure your email settings
Email configuration file is /opt/vnetcon/conf/email.properties (c:\opt\vnetcon\conf\email.properties in windows).
Below is an example.
```
email.from=<sender email address>
email.replyto=<replyto address>
email.host=smtp.gmail.com
email.port=587
email.user=<email username>
email.pass=<password>
```
If you are using gmail for sending emails you need to allow "unsecure applications" to send email before this start to work.
  
## Making api calls from javascript
Below are couple of examples  of making api requests. In general all the requests shoul be done the same way html form send requests to server. For easy web app development we have also created a [page.js framework](https://github.com/vnetcon/page.js). 
```javascript
// the base url. The "default" in url point to database connection 
// you have configured in database.properties file (the preofix in properties)
const appdataurl = 'http://localhost:8040/jdbc-rest/rest/default/';

// method for sending requests
makeRequest(endpoint, fdata, callback) {
    fetch(endpoint, {
        headers: { "Content-Type": "application/x-www-form-urlencoded; charset=utf-8" },
        method: 'post',
        body: fdata
    })
        .then(function (response) {
            return response.json();
        })
        .then(data => callback(true, data))
        .catch(e => callback(false, e));
}

// ajax call for retrieving data from dataabse
click_AjaxCall(){
    var d = new FormData();
    d.append("paramname", 'paramvalue'); // the paramname in sql should be following '{r_paramname}'. This will be replaced with the value put here.
    makeRequest(appdataurl + "/endpoin", d, click_AjaxCallReply);
}

// handle retrieved data here
clic_AjaxCallReply(success, data){
    console.log("reply: " + success);
}

// ajax call for sending email
sendEmail(){
    var msg = "Message in html format";
    var d = new FormData();
    d.append('email', 'to-aimail-address');
    d.append('subject', 'Subject for email');
    d.append('message', msg);
    makeRequest(appdataurl + "/email", d, sendEmaillReply);
}

// handle email send reply here
sendEmailReply(success, data){
}

```


## Key features
* REST API based on [jdbc-rest](https://github.com/vnetcon/jdbc-rest) driver (select/insert/update/delete)
* email endpoint for sending emails
* upload endpoint for storing files into database (under development)
* download endpoint for retrieving files from database (under development)
* From GDPR point of view rest api requests (sselect/insert/update/delete) are logged automtically to REST_SERVLET_LOG table
  

<!--
## Quick Start (for windows 64bit) - updated 06/03/2020 (dd/mm/yyyy)
For setting up the development environment you don't need install anything. 
You just download the zip file and start the downloaded software with following steps:  

* create folder c:\vnetcon
* Download Development environment [here](http://vnetcon.s3-website-eu-west-1.amazonaws.com/dev-env.zip) to c:\vnetcon
* unzip the file. After this you should have c:\vnetcon\dev-env folder
* Create folder c:\etc\vnetcon and copy the database.properties file there
* Start apache drill by double clicking 1_StartDrill.bat
* Start database by double clicking 1_StartPostgreSQL.bat
* Start [DBeaver](https://dbeaver.io/) database tool by double clicking 2_StartDBeaver.bat
* Start Tomcat by double clicking 3_StartTomcat8.bat

After this you can point your browser to http://localhost:8080/jdbc-rest/rest/default/getUser/v1?userid=3 
to see the demo json
-->


## Building
If you want to build the war file your self and deploy it to some other servlet engine, take the following steps. Before these steps you need to build the [jdbc-rest](https://github.com/vnetcon/jdbc-rest) jdbc driver to get it to your local maven repository.
1. Clone the repo and move to the folder where pom.xml exists
2. execute: mvn clean isntall
3. execute: mvn package  
4. Deploy the jdbc-rest.war form target folder to servlet engine 

If you develop the servlet you can run

mvn jetty:run

To start the development servlet engine.

## Suported databases
In theory all databases that have JDBC driver. Postgresql, Oracle, SQL Server etc.
  
You can add your database jdbc driver by creating a libs folder and put your jdbc driver jar files there. Thos folder should locate at the root level where the pom.xml locates. All jars added to this folder will be added to war so you don't need to add thos to servlet engine (e.g. tomcat, apache karaf etc.)

## REST_SERVLET_CONFIG -table
This is the key table in REST API server. Into this table all sql statements should be stored 
and in here you configure the url endpoint. This table must locate in VNETCON shema and both the scehma and table name must be upper case.  

* REST_ENDPOINT_ID: unique id for row
* REST_ENDPOINT: endpoint to be used in url for executing the sql with query params
* VERSION: endpoint version. This must be a part of the url
* ALLOWED_TOKENS: list of "toke-1" string that are allowed to use the service. Client pass this in vnetcon-token http header.
* ALLOWED_IPADDRESSES: Allowed ip addresses (not implemented yet)
* JSON_SQL: The sql statement containing the --[json] configuration
* ENABLED: 1 endabled all other values disabled



## Commercial use
If you want to use this in closed code project or product you can buy a 99 USD license [here](https://vnetcon.com)  
If you think the price is too low or high you can also change the price there :)
  
We offer also consulting, online traing and support so you don't need to figure these all out on your own.

## sql syntax and parameters
In short the idea is to contert normal sql to json by with --[json] comment. This comment will tell the driver 
to convert execute the statement as jdbc-rest statement. Below is a simple example

```
select fname as "FirstName", lname as "LastName" 
from miki.mikitest 
where fname = '{r_fname}' --[json=Person; r_fname=Adam]
```
which poduce following json

```json
{
  "Person": {
    "FirstName": "Adam",
    "LastName": "Smith"
  }
}
```

Below are some notes related to this. More detailed examples can be fuond from dev-nev.zip and DBeaver in there.
* --[json]: convert the result set to json and in insert/update/delete replace the rest-json parameters '{param_name}' with correct valus
* --[json=Person]: Give the name for root elemente in select statements
* r_ at the begining of parameter indicates that the actual value is retrived from htttp request (e.g. client send client id the parameter is sql should be '{r_clientid}'
* --[json:Person; r_clientid=default_value]: Set the default value for parameter 
* hidden_ indicates that the column should not be displayed in result json (e.g. select a as hidden_a from table)
* subquery_ indicates that the column is a select that should be executed (e.g. select 'select a, b form table' as subquery_colname). It is possible to have subqueries in subqueries.
* t_ indicates that the param value should be replaced in subquery with "parent sql column value" (e.g.  '{t_userid}' would be replaced with userid columnvalue from main query

## Data types
All data is treated as stings. If you need to insert/update data in different data type you need to put the parameter into database function that will do the conversion.  
insert into table a (a, b) values ('{r_a}', to_number('{r_b}') --[json]

## Security
In general the main idea for taking care of the security is to put this rest api server behind apacahe or other http server that takes care of authentication (ldap, openidc, shibboleth etc.). There are also some build in features in server  
* all values are "escaped" to prevent sql injections
* vnetcon-token can be set in http headers and configured to REST_SERVLET_CONFIG table (syntax "token-1";"token-2"). Only valid tokens are then served by server

All http headers are also passed to processing as request parameters so you can use those also in your sql statements as regular request paraeters (e.g. username from http headers send by apache)
  
## Scaling
The rest server doesn't use any sessions so it should be possible to create an cluster of these servers. At the moment there is no support for connection pooling so all the requests open new connection and close it after execution. In really busy sites this can be an issue from database point of view.  
  
If you are running a really busy site even the connection pooling can cause issues to database. In this case you might want to take a closer look to HBase + Phoenix JDBC. This setup doesn't hava connection limitatiosn - if I have understand this right. 

## OSGI
The server have been successfylly deployed to Apache Karaf but this is littel bit tricy operation to do due to dependencies. The main Karaf environment we are interesed in is Talend ESB's Runtime_ESB (build on Apache Karaf). On this you can build with Talend ESB Open Studio quite easily different kind of services with minimal coding.

