package com.avioconsulting.bamexporter

import groovy.mock.interceptor.StubFor
import org.apache.commons.io.FileUtils
import org.apache.maven.model.Build
import org.apache.maven.project.MavenProject
import org.junit.Before
import org.junit.Test

import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.is
import static org.junit.Assert.assertThat

class BamExportMojoTest {
    BamExportMojo mojo
    OutputStream stream
    List<String> bamProjectsExecuted
    final File baseDirectory = new File('build/tmp/the_base_dir')
    final File projectDirectory = new File(baseDirectory, 'baseProjectDir')
    final File classesDirectory = new File(new File(projectDirectory, 'target'), 'classes')
    final File bamPath = new File(this.projectDirectory, 'src/main/resources/bam')
    File validZip = new File(baseDirectory, 'bamExport.zip')
    StubFor stub

    @Before
    void setup() {
        if (baseDirectory.exists()) {
            baseDirectory.deleteDir()
        }
        baseDirectory.mkdirs()
        mojo = new BamExportMojo()
        mojo.bamProject = 'theProject'
        mojo.exportEndpoint = 'http://endpoint'
        stub = new StubFor(BamExportFetcher)
        this.bamProjectsExecuted = []
        this.stub.demand.fetch { String bamProject, OutputStream stream ->
            this.bamProjectsExecuted << bamProject
            stream.write validZip.readBytes()
        }
        def build = [outputDirectory: classesDirectory.absolutePath] as Build
        mojo.mavenProject = [
                getBasedir: { this.projectDirectory },
                getBuild  : { build }
        ] as MavenProject
        if (projectDirectory.exists()) {
            projectDirectory.deleteDir()
        }
        projectDirectory.mkdirs()
        def antBuilder = new AntBuilder()
        antBuilder.zip(destFile: validZip) {
            fileset(dir: new File('src/test/resources/bamExport'))
        }
    }

    @Test
    void disabled() {
        // arrange
        this.mojo.doExport = false

        // act
        mojo.execute()

        // assert
        assertThat bamProjectsExecuted,
                   is(equalTo([]))
    }

    @Test
    void normal() {
        // arrange
        this.mojo.doExport = true

        // act
        this.stub.use {
            mojo.execute()
        }

        // assert
        assertThat bamProjectsExecuted,
                   is(equalTo(['theProject']))
        def actualFiles = new FileNameFinder().getFileNames(bamPath.absolutePath, '**/*')
                .collect { f -> f.replace(new File(projectDirectory, 'src/main/resources/bam').absolutePath + '/', '') }
        assertThat actualFiles.contains('dataobject/Eligibility_File_PDO/DataObject.xml'),
                   is(equalTo(true))
        assertThat actualFiles.contains('bam.properties'),
                   is(equalTo(true))
    }

    @Test
    void cleanCruft() {
        // arrange
        this.mojo.doExport = false
        classesDirectory.mkdirs()
        def junkFile = new File(classesDirectory, 'junk.xml')
        FileUtils.touch junkFile

        // act
        mojo.execute()

        // assert
        assertThat junkFile.exists(),
                   is(equalTo(false))
    }

    @Test
    void cleansExistingDirectory() {
        // arrange
        this.mojo.doExport = true
        bamPath.mkdirs()
        def existingFile = new File(bamPath, 'something.xml')
        FileUtils.touch existingFile

        // act
        this.stub.use {
            mojo.execute()
        }

        // assert
        assertThat bamProjectsExecuted,
                   is(equalTo(['theProject']))
        def actualFiles = new FileNameFinder().getFileNames(bamPath.absolutePath, '**/*')
                .collect { f -> f.replace(new File(projectDirectory, 'src/main/resources/bam').absolutePath + '/', '') }
        assertThat actualFiles.contains('dataobject/Eligibility_File_PDO/DataObject.xml'),
                   is(equalTo(true))
        assertThat actualFiles.contains('bam.properties'),
                   is(equalTo(true))
        assertThat existingFile.exists(),
                   is(equalTo(false))
    }
}
