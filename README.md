# BAM Maven Plugin

## Summary

This plugin helps adopt a development lifecycle when working with Oracle BAM artifacts (including data objects and dashboards).

## Details

The general idea is to use the BAM console on some environment as an "IDE". The plugin then can use that service to export dashboards and data objects and store them in source control where they can be baselined and rolled out to other environments.

## BAM Exporter composite

It might be the case that the best environment for business users, etc. to create/customize dashboards is a TEST/UAT environment with limited developer access but access to better data. In order to facilitate that workflow, this plugin is designed to work with a "proxy" SOA composite (in this same repo) that runs `bamcommand` on the privileged environment and sends the ZIP file containing the export back to the developer's machine using a SOAP reply.

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

1. This plugin uses the `bamcommand` executable under the hood. As a result, it expects a JDeveloper/SOA Suite Quick Start install on the machine it's being run from.
2. Until the plugin is published, run `./gradlew clean install` from the `bam-maven-plugin` subdirectory in this repository, to install the plugin in your local `.m2` repository.
3. Ensure the machine running the plugin has network access to the port of the Weblogic managed server that the BAM server/cluster is running on.
4. Deploy the BAM exporter composite (from the `soa/BamExporterApp/BAMExporter` directory in this repository) on the environment(s) you wish to export BAM data from. 

## Running/usage

To export data from an environment:
```bash
# bam.export.endpoint is optional, by default it will be set to
# ${soa.deploy.url}/soa-infra/services/default/BAMExporter/bamexporterprocess_client_ep
mvn -Denv=LOCAL -Dbam.export=true -Dbam.export.endpoint=http://localhost:8001/soa-infra/services/default/BAMExporter/bamexporterprocess_client_ep generate-resources
```

## Wish List

Automatically deploy BAM exporter composite when Maven plugin runs if not already deployed
