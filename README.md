# U-QASAR Platform

[U-QASAR ](http://www.uqasar.eu) aims at creating a flexible Quality Assurance, Control and Measurement Methodology to measure the quality of Internet-related software development projects and their resulting products. The methodology is supported by an Internet solution composed of several knowledge services based on open standards that will be able to detect changes in the scope and requirements of an Internet application (or changes in its development process) and provide the adequate set of assessments to deliver an accurate measurement of the quality of the process and product at any time.

The U-QASAR methodology [web book](http://webbook.uqasar.eu/) provides an overview of the methodology and the platform. This source code is the actual software platform.


## Architecture

U-QASAR platform is a SOA based Web application following an MVC design pattern. The *model* layer contains persistence framework and ontology/knowledge repositories. The persistence layer provides CRUD operations to the data store. The controller takes care of the business logic and contains essential services for the platform, i.e. U-QASAR services (monitoring, analytical, enhancement, visualization and reporting) as well as supporting utility services. The *view* layer provides the user interfaces of the platform in form of HTML web pages. 

* Web Framework: Apache Wicket
* JavaScript Framework: JQuery
* CSS Framework: Twitter Bootstrap

The U-QASAR platform is based on [Maven](http://maven.apache.org) executed on top of [JBoss AS 7.1.1](http://www.jboss.org/jbossas). The current variant of the platform requires Java 7 SDK.


## Importing project to Eclipse

To use the project in Eclipse, the following plugin has to be installed:

* m2e - Maven Integration for Eclipse

To import the project to an Eclipse workspace: 

* "File --> Import... --> Existing Maven Projects"

The required files by Eclipse (.project, .classpath) are created during the import. 

After the import the following command has to be executed in Eclipse: 

* "Run As --> Maven generate-sources" 

* Modify in the pom.xml the entity `internal-repository` to point to your Maven repository. You will need the adapter libraries available (see Data adapters).

The project can then be deployed on a JBoss-Server 

* As requirement the corresponding JBoss installation has to be configured as server in Eclipse
* The project has been tested with the version [JBoss AS 7.1.1.Final Certified Java EE 6 Full Profile](http://www.jboss.org/jbossas/downloads/)


## Database

* The platform requires a database. As of default an in-memory H2 database is used.	One can alternatively use a file based H2 database or a MySQL database. 
* The DB configuration shall be provided in the included file `default.properties`. 
* The instructions for configuring the platform to use MySQL are provided in the file README_MySQL.

	
## Known issues

* In case there are errors of type "JavaScript Problems" shown in Eclipse that are related to the file `src/main/webapp/assets/js/jquery-ui-1.9.1.custom.min.js`:
* Rightclick on the project --> Properties --> JavaScript --> Include Path --> Tab "Source"
* Open the item `src/main/webapp` --> Choose Excluded / Edit
* Add the following Exclusion-Pattern: `„assets/js/jquery*.js“`
* Finish / OK


## Data adapters

The U-QASAR platform acquires the external measurement data by using data wrapper, i.e. adapter implementations. The adapters fulfill the generic [U-QASAR interface](https://github.com/IntrasoftInternational/uQasarAdapter). At the moment there are reference implementations for the following external sources of data. The adapter implementations and their source code can be found in Github: 

* [Data adapter for Jira(R) Issue Tracking System](https://github.com/IntrasoftInternational/JiraAdapter)
* [Data adapter for SonarQube(R) Static Code Analysis Software](https://github.com/wenns/SonarAdapter) 
* [Data adapter for TestLink Test Management Tool](https://github.com/MTPsqa/TestLinkAdapter)
* [Data adapter for Cubes OLAP Server](https://github.com/ManuDevelopia/CubesAdapter)
* [Data adapter for Jenkins Continuous Integration Tool](https://github.com/pialindqvist/JenkinsAdapter2/tree/jenkinskehitys)
* [Data adapter for Gitlab Version Management System](https://github.com/minzen/gladapter)


# License

* The U-QASAR Platform is licensed under Apache License, version 2.0 (see LICENSE and NOTICE for details).
* Please note that while Wicked Charts (https://github.com/thombergs/wicked-charts) is licensed under Apache 2.0 License, Highcharts itself is only free for non-commercial use. See here: [http://shop.highsoft.com/highcharts.html]

