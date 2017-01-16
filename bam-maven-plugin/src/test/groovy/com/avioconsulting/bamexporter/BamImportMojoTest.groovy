package com.avioconsulting.bamexporter

import org.apache.commons.io.FileUtils
import org.junit.Before
import org.junit.Test

import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.is
import static org.junit.Assert.assertThat

class BamImportMojoTest extends BaseBamTest {
    BamImportMojo mojo
    List<String> commandsRun
    File bamBinPath

    @Before
    void setup() {
        mojo = new BamImportMojo()
        mojo.bamProject = 'theProject'
        mockMavenProject mojo
        def packageMojo = new BamPackageMojo()
        packageMojo.bamProject = 'theProject'
        mockMavenProject packageMojo
        packageMojo.execute()
        this.commandsRun = []
        mojo.metaClass.execute = { String command ->
            this.commandsRun << command
        }
        def oracleHome = new File(baseDirectory, 'oracleHome')
        mojo.oracleHome = oracleHome.absolutePath
        bamBinPath = mojo.join oracleHome, 'bam', 'bin'
        bamBinPath.mkdirs()
    }

    @Test
    void nonWindows() {
        // arrange
        mojo.metaClass.isWindows = {
            false
        }
        def scriptPath = new File(bamBinPath, 'bamcommand')
        FileUtils.touch scriptPath

        // act
        mojo.execute()

        // assert
        assertThat this.commandsRun.size(),
                   is(equalTo(1))
        assertThat this.commandsRun[0].toString(),
                   is(equalTo("${scriptPath.absolutePath} -cmd import -type project -file ${mojo.mavenProject.artifact.file.absolutePath}".toString()))
    }

    @Test
    void windows() {
        // arrange
        mojo.metaClass.isWindows = {
            true
        }
        def scriptPath = new File(bamBinPath, 'bamcommand.cmd')
        FileUtils.touch scriptPath

        // act
        mojo.execute()

        // assert
        assertThat this.commandsRun.size(),
                   is(equalTo(1))
        assertThat this.commandsRun[0].toString(),
                   is(equalTo("${scriptPath.absolutePath} -cmd import -type project -file \"${mojo.mavenProject.artifact.file.absolutePath}\"".toString()))
    }
}
