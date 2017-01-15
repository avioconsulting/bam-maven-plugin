package com.avioconsulting.bamexporter

import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugin.MojoFailureException
import org.apache.maven.plugins.annotations.Component
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject

@Mojo(name = 'bamExport')
class BamExportMojo extends AbstractMojo {
    @Parameter(property = 'bam.export.endpoint',
            defaultValue = 'https://${soa.host}:${soa.port}/soa-infra/services/default/BAMExporter/bamexporterprocess_client_ep')
    private String exportEndpoint

    @Parameter(property = 'bam.mavenProject', required = true)
    private String bamProject

    @Component
    private MavenProject mavenProject

    protected static File join(File parent, String... parts) {
        def separator = System.getProperty 'file.separator'
        new File(parent, parts.join(separator))
    }

    void execute() throws MojoExecutionException, MojoFailureException {
        def exportFetcher = new BamExportFetcher(new URI(this.exportEndpoint))
        File.createTempFile('bamExport', '.zip').with {
            deleteOnExit()

            exportFetcher.fetch this.bamProject, new FileOutputStream(absolutePath)
            def antBuilder = new AntBuilder()
            def destinationDirectory = join this.mavenProject.basedir,
                                            'src',
                                            'main',
                                            'resources',
                                            'bam'
            if (destinationDirectory.exists()) {
                destinationDirectory.deleteDir()
            }
            antBuilder.unzip src: absolutePath,
                             dest: destinationDirectory.absolutePath,
                             overwrite: true
        }
    }
}
