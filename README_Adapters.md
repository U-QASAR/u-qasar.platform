# How to update an adapter implementation and take it into use in U-QASAR

## Adapter implementations
* The data adapters AKA wrappers enable fetching of data from external sources to the U-QASAR platform. The adapters fulfill the generic [U-QASAR interface](https://github.com/IntrasoftInternational/uQasarAdapter). At the moment there are reference implementations for the following external sources of data. The adapter implementations and their source code can be found in Github:

  * [Data adapter for Jira(R) Issue Tracking System](https://github.com/IntrasoftInternational/JiraAdapter)
  * [Data adapter for SonarQube(R) Static Code Analysis Software](https://github.com/wenns/SonarAdapter)
  * [Data adapter for TestLink Test Management Tool](https://github.com/MTPsqa/TestLinkAdapter)
  * [Data adapter for Cubes OLAP Server](https://github.com/ManuDevelopia/CubesAdapter)
  * [Data adapter for Jenkins Continuous Integration Tool](https://github.com/pialindqvist/JenkinsAdapter2/tree/jenkinskehitys)
  * [Data adapter for Gitlab Version Management System](https://github.com/minzen/gladapter)

## Update an adapter and compile a new package
* If you don't have an account at Github yet, create one.
* Create a fork of the chosen adapter implementation, make and commit the desired changes.
* Increase the version number in the pom.xml of the adapter to indicate the adapter has been updated.
* Compile the code and create a JAR archive file by using Maven:
  * On command line prompt give the command `$ maven package` in the directory where the adapter resides.
  * Find the compiled package under `/target` subdirectory.

## Test the adapter
* See the respective adapter implementation for details (which metrics are supported and in which format the query shall be provided)
* In general the following syntax should work:
  * `$ java -jar XxxAdapter-1.0.jar URL user:password QUERY`
* If everything works in a satisfactory manner, proceed to making the adapter available to the other users through Maven Artifactory.

## Upload the adapter to Maven repository
* The following applies to [ATB Artifactory](http://www.atb-bremen.de/artifactory); if you are using another Maven repository, follow the instructions provided for it.
* Log in to the artifactory with your credentials.
* Go to the section artifacts
* Expand `ext-releases-local` - the adapters are to be found under eu/uqasar
* Click `deploy`
* Drag and drop the jar file to the provided space on the page or browse the file.
* The Maven Artifact data should be acquired correctly from the pom.xml of the adapter. If not, verify the file and make the required changes (Group ID, Artifact ID, Version etc.)
* Click on "Deploy". The file is uploaded to the server and should be available to Maven.

## Make U-QASAR to use the updated adapter
* Find the respective adapter in the file `pom.xml` of the U-QASAR platform. Change the pom.xml to contain the updated version of the adapter implementation.
* If compiling in Eclipse, execute `Maven - Update Project` to update the Adapter to the newer version. Please note that if you have the adapter project in the same workspace and open, the JARs are not downloaded from the Maven repository as the adapter is available. :)
