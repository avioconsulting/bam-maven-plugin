package com.avioconsulting.bamexporter

import groovy.xml.MarkupBuilder
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.SystemUtils
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugin.MojoFailureException
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter

@Mojo(name = 'bamImport')
class BamImportMojo extends AbstractBamMojo {
    @Parameter(property = 'bam.oracle.home', defaultValue = '${oracle.home}')
    private String oracleHome

    @Parameter(property = 'weblogic.user', required = true)
    private String weblogicUser

    @Parameter(property = 'weblogic.password', required = true)
    private String weblogicPassword

    @Parameter(property = 'bam.hostname', required = true)
    private String bamServerHostname

    @Parameter(property = 'bam.port', required = true)
    private int bamServerPort

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

    void writeBamConfig(File bamBinPath) {
        def config = [
                host    : this.bamServerHostname,
                port    : this.bamServerPort,
                username: this.weblogicUser,
                password: this.weblogicPassword
        ]
        def file = new File(bamBinPath, 'BAMCommandConfig.xml')
        file.write('<?xml version="1.0" encoding="UTF-8" standalone="yes"?>')
        file.write "\n"
        new FileWriter(file).with { sw ->
            new MarkupBuilder(sw).with {
                BAMCommandConfig {
                    config.collect { k, v ->
                        "$k" { v instanceof Map ? v.collect(owner) : mkp.yield(v) }
                    }
                }
            }
        }
    }

    void execute() throws MojoExecutionException, MojoFailureException {
        def bamBinPath = join new File(oracleHome), 'bam', 'bin'
        def scriptExecutable = join bamBinPath, 'bamcommand'
        if (this.isWindows()) {
            scriptExecutable = new File(scriptExecutable.absolutePath + '.cmd')
        }
        if (!scriptExecutable.exists()) {
            throw new Exception("Unable to find BAM script in path ${scriptExecutable}!")
        }
        writeBamConfig bamBinPath
        def tempFile = join this.mavenProject.basedir, 'tmp', 'bamExport.zip'
        // bamcommand doesn't like files ending in jar, even if they are zip files
        FileUtils.copyFile this.mavenProject.artifact.file,
                           tempFile
        try {
            def zipFilePath = tempFile.absolutePath
            // spaces in path names
            if (this.isWindows()) {
                zipFilePath = "\"${zipFilePath}\""
            }
            def command = "${scriptExecutable} -cmd import -type project -mode update -file ${zipFilePath}"
            this.log.info "Executing ${command}..."
            execute(command)
        }
        finally {
            deleteTempFile tempFile
        }
    }

    private deleteTempFile(File file) {
        // avoid potential Windows locking issues
        def success = false
        // on Windows, locking is causing this to fail unless retried like this
        for (int i = 0; i < 20; i++) {
            if (file.delete()) {
                success = true
                break
            }
            System.gc()
            Thread.yield()
        }
        if (!success) {
            throw new Exception("Unable to delete file from ${file}")
        }
    }
}
