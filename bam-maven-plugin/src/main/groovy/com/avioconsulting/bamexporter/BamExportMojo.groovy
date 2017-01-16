package com.avioconsulting.bamexporter

import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugin.MojoFailureException
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter

@Mojo(name = 'bamExport')
class BamExportMojo extends AbstractBamMojo {
    @Parameter(property = 'bam.export.endpoint',
            defaultValue = '${soa.deploy.url}/soa-infra/services/default/BAMExporter/bamexporterprocess_client_ep')
    private String exportEndpoint

    @Parameter(property = 'bam.export', defaultValue = 'false')
    private boolean doExport

    void execute() throws MojoExecutionException, MojoFailureException {
        def classesDir = new File(this.mavenProject.build.outputDirectory)

        if (classesDir.exists()) {
            this.log.info "Cleaning output directory to avoid BAM cruft ${classesDir}..."
            classesDir.deleteDir()
            classesDir.mkdirs()
        }

        if (!this.doExport) {
            this.log.info 'Skipping BAM export, use -Dbam.export=true to enable it'
            return
        }

        def uri = new URI(this.exportEndpoint)
        this.log.info "Fetching BAM artifacts from ${uri}"
        def exportFetcher = new BamExportFetcher(uri)
        File.createTempFile('bamExport', '.zip').with {
            deleteOnExit()

            exportFetcher.fetch this.bamProject, new FileOutputStream(absolutePath)
            def antBuilder = new AntBuilder()
            def destinationDirectory = this.bamDestination
            if (destinationDirectory.exists()) {
                destinationDirectory.deleteDir()
            }

            antBuilder.unzip src: absolutePath,
                             dest: destinationDirectory.absolutePath,
                             overwrite: true
        }
    }
}
