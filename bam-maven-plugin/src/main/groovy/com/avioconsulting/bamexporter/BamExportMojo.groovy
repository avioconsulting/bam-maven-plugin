package com.avioconsulting.bamexporter

import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugin.MojoFailureException
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter

@Mojo(name = 'bamExport')
class BamExportMojo extends AbstractMojo {
    @Parameter(property = 'bam.export.endpoint',
               defaultValue = 'https://${soa.host}:${soa.port}/soa-infra/services/default/BAMExporter/bamexporterprocess_client_ep')
    private String exportEndpoint

    void execute() throws MojoExecutionException, MojoFailureException {

    }
}
