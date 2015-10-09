# How to register MySQL as JBoss Datasource and use it from U-QASAR

## Installation as module to JBoss
The easiest way to take MySQL with U-QASAR into use is to install it as JBoss module. 
This approach requires adequate user rights to the server. 

### JBoss Configuration
* create the directory `$JBOSS_ROOT\modules\com\mysql\main`
* copy the mysql driver jar to there (e.g. mysql-connector-java-5.1.21.jar) (or a newer version of the library)
* add a module.xml file to the same directory with the following content:

`<?xml version="1.0" encoding="UTF-8"?>`  
`<module xmlns="urn:jboss:module:1.0" name="com.mysql">`  
`  <resources>`  
`    <resource-root path="mysql-connector-java-5.1.21.jar"/>`  
`  </resources>`  
`  <dependencies>`  
`    <module name="javax.api"/>`  
`  </dependencies>`  
`</module>`  
`
 

* Modify the standalone.xml and add a definition for the MySQL driver (within `<subsystem xmlns="urn:jboss:domain:datasources:1.0">`

            <datasources>
			...
				<drivers>
				...
				
                    <driver name="com.mysql" module="com.mysql">
                        <xa-datasource-class>com.mysql.jdbc.jdbc2.optional.MysqlXADataSource</xa-datasource-class>
                    </driver>

```

## U-QASAR Project Configuration
 
* Uncomment and modify the entries in the section `## MySQL Database ##` in default.properties to contain valid access data for the MySQL server and comment out the default DB configuration if not already using MySQL. 
* On first deployment there should be `ddl=create`
* After successful creation of tables, change this to `ddl=update` in order to prevent the re-creation of the tables. 
