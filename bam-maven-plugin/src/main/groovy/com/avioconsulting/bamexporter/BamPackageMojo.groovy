package com.avioconsulting.bamexporter

import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugin.MojoFailureException
import org.apache.maven.plugins.annotations.Mojo

@Mojo(name = 'bamPackage')
class BamPackageMojo extends AbstractBamMojo {
    void execute() throws MojoExecutionException, MojoFailureException {
        def mavenBuild = this.mavenProject.build
        def zipFile = join new File(mavenBuild.directory), "${mavenBuild.finalName}.jar"
        this.log.info "Zipping up BAM artifacts into ${zipFile}..."
        def antBuilder = new AntBuilder()
        antBuilder.zip(destFile: zipFile.absolutePath) {
            fileset(dir: this.bamDestination)
        }
        this.mavenProject.artifact.file = zipFile
    }
}
