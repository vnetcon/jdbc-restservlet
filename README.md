# jdbc-restservlet
This servlet is build on [jdbc-rest](https://github.com/vnetcon/jdbc-rest) driver and it servers as REST API server.
The idea is to offer an out-of-the box REST AP solution for web developers (Angular, React, Vue etc.). 
In short web developer need to write only client side code and sql - no need for middle tier applications for accessing database. 

Key features:
* REST API based on link [jdbc-rest](https://github.com/vnetcon/jdbc-rest) driver (select/insert/update/delete)
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
If you want to build the war file your self and deploy it to some other servlet engine, take the following steps
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






