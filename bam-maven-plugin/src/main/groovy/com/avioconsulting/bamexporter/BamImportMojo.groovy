package com.avioconsulting.bamexporter

import org.apache.commons.lang3.SystemUtils
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugin.MojoFailureException
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter

@Mojo(name = 'bamImport')
class BamImportMojo extends AbstractBamMojo {
    @Parameter(property = 'bam.oracle.home', defaultValue = '${oracle.home}')
    private String oracleHome

    private static final int COMMAND_TIMEOUT_MINUTES = 5

    def execute(String command) {
        def result = command.execute()
        result.waitForOrKill(COMMAND_TIMEOUT_MINUTES * 60 * 1000)
        this.log.info result.text
        if (result.exitValue() != 0) {
            throw new Exception('Command output failed!')
        }
    }

    def isWindows() {
        SystemUtils.IS_OS_WINDOWS
    }

    void execute() throws MojoExecutionException, MojoFailureException {
        def scriptExecutable = join new File(oracleHome), 'bam', 'bin', 'bamcommand'
        if (this.isWindows()) {
            scriptExecutable = new File(scriptExecutable.absolutePath + '.cmd')
        }
        if (!scriptExecutable.exists()) {
            throw new Exception("Unable to find BAM script in path ${scriptExecutable}!")
        }
        def artifact = this.mavenProject.artifact.file.absolutePath
        // spaces in path names
        if (this.isWindows()) {
            artifact = "\"${artifact}\""
        }
        def command = "${scriptExecutable} -cmd import -type project -file ${artifact}"
        this.log.info "Executing ${command}..."
        execute(command)
    }
}
