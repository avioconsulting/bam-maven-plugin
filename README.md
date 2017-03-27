# BAM Maven Plugin

## Summary

This plugin helps adopt a development lifecycle when working with Oracle BAM artifacts (including data objects and dashboards).

## Details

The general idea is to use the BAM console on some environment as an "IDE". The plugin then can use that service to export dashboards and data objects and store them in source control where they can be baselined and rolled out to other environments.

## BAM Exporter composite

It might be the case that the best environment for business users, etc. to create/customize dashboards is a test/UAT environment which tends to have better data than a dev environment. The catch is that test or prod environments often have limited developer access. In order to allow read only/export access to those environments, this plugin is designed to work with a "proxy" SOA composite (in this same repo) that runs `bamcommand` on the privileged environment and sends the ZIP file containing the export back to the developer's machine using a SOAP reply.

## Maven goals

This plugin defines a new packaging type (bam) and hooks into the Maven lifecycle at the following phases:

### generate-resources
* Runs the `bamExport` goal if the `bam.export` property is set to true.
* Calls the BAM exporter composite, which in turn executes `bamcommand -cmd export -name bamProject -type project` on the server and automatically sets up `BAMCommandConfig.xml` on the server the export is sourced from with the proper credentials/server information.
* After retrieving the ZIP file of dashboards/data objects, the plugin expands those files into source control on the developer's machine.
### package
Runs the `bamPackage` goal. This goal simply puts the XML files in the proper ZIP file structure in order to import it to the server.
### pre-integration-test
Runs the `bamImport` goal. It will:
* Configure `BAMCommandConfig.xml` on the machine running the Maven plugin with the proper credentials/server information.
* Executes `bamcommand -cmd import -name bamProject -type project -mode update` to import the data objects and dashboards to the server.

## Setup

### Building/installing

1. This plugin uses the `bamcommand` executable under the hood. As a result, it expects a JDeveloper/SOA Suite Quick Start install on the machine it's being run from.
2. Until the plugin is published, run `./gradlew clean install` from the `bam-maven-plugin` subdirectory in this repository, to install the plugin in your local `.m2` repository.
3. Ensure the machine running the plugin has network access to the port of the Weblogic managed server that the BAM server/cluster is running on.
4. Deploy the BAM exporter composite (from the `soa/BamExporterApp/BAMExporter` directory in this repository) on the environment(s) you wish to export BAM data from.

### POM setup

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.avioconsulting.project</groupId>
  <artifactId>Bam</artifactId>
  <version>1.0-SNAPSHOT</version>
  <description>Project for BAM artifacts</description>
  <packaging>bam</packaging>

  <properties>
    <bam.project>BAMProjectName</bam.project>
    <!-- Business user dashboard development happens in TEST -->
    <bam.export.endpoint>https://test.environment/soa-infra/services/default/BAMExporter/bamexporterprocess_client_ep</bam.export.endpoint>
    <weblogic.user>username</weblogic.user>
    <weblogic.password>thepassword</weblogic.password>
    <bam.hostname>thebamserverhostname</bam.hostname>
    <bam.port>thebamserverport</bam.port>
  </properties>

  <build>
    <plugins>     
      <plugin>
         <groupId>com.avioconsulting</groupId>
         <artifactId>bam-maven-plugin</artifactId>
         <version>1.0.0</version>
         <extensions>true</extensions>
         <configuration>
             <!-- If we don't repeat the username here, overriden values from settings.xml do not make it into the plugin for some reason -->
            <weblogicUser>${weblogic.user}</weblogicUser>
            <!-- not sure if problem is happening w/ password but included it for above reason -->
            <weblogicPassword>${weblogic.password}</weblogicPassword>
         </configuration>
     </plugin>
    </plugins>
    <!-- see copy-project-properties above for why we're doing this -->
    <filters>
      <filter>${project.basedir}/../../../env/${env}.properties</filter>
    </filters>
  </build>
</project>
```

## Running

To export data from an environment:
```bash
# bam.export.endpoint is optional, by default it will be set to
# ${soa.deploy.url}/soa-infra/services/default/BAMExporter/bamexporterprocess_client_ep
mvn -Dbam.export=true -Dbam.export.endpoint=http://localhost:8001/soa-infra/services/default/BAMExporter/bamexporterprocess_client_ep generate-resources
```

Deploy the project like you would a SOA composite:

```
mvn pre-integration-test
```

## Wish List

Automatically deploy BAM exporter composite when Maven plugin runs if not already deployed
