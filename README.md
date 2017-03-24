# BAM Maven Plugin

This plugin helps adopt a development lifecycle when working with BAM artifacts.

The general idea is to use the BAM console on some environment as an "IDE". It might be the case that the best environment for business users, etc. to customize dashboards is a TEST/UAT environment with limited developer access. In order to facilitate that workflow, this plugin is designed to work with a SOA composite (in this same repo) that exports BAM artifacts via a web service.
environment settings.

To export data from an environment:
```bash
# bam.export.endpoint is optional, by default it will be set to
# ${soa.deploy.url}/soa-infra/services/default/BAMExporter/bamexporterprocess_client_ep
mvn -Denv=LOCAL -Dbam.export=true -Dbam.export.endpoint=http://localhost:8001/soa-infra/services/default/BAMExporter/bamexporterprocess_client_ep generate-resources
```