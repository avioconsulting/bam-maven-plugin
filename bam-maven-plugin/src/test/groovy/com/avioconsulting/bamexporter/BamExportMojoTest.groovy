package com.avioconsulting.bamexporter

import groovy.mock.interceptor.StubFor
import org.apache.maven.project.MavenProject
import org.junit.Before
import org.junit.Test

import static org.hamcrest.Matchers.*
import static org.junit.Assert.assertThat

class BamExportMojoTest {
    BamExportMojo mojo
    OutputStream stream
    List<String> bamProjectsExecuted
    final File baseDirectory = new File('build/tmp/the_base_dir')
    final File projectDirectory = new File(baseDirectory, 'baseProjectDir')
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
        mojo.mavenProject = [getBasedir: { this.projectDirectory }] as MavenProject
        if (projectDirectory.exists()) {
            projectDirectory.deleteDir()
        }
        projectDirectory.mkdirs()
    }

    @Test
    void normal() {
        // arrange
        def antBuilder = new AntBuilder()
        antBuilder.zip(destFile: validZip) {
            fileset(dir: new File('src/test/resources/bamExport'))
        }

        // act
        this.stub.use {
            mojo.execute()
        }

        // assert
        assertThat bamProjectsExecuted,
                   is(equalTo(['theProject']))
        def bamPath = new File(this.projectDirectory, 'src/main/resources/bam')
        def actualFiles = new FileNameFinder().getFileNames(bamPath.absolutePath, '**/*')
                .collect { f -> f.replace(new File(projectDirectory, 'src/main/resources/bam').absolutePath + '/', '') }
        assertThat actualFiles.contains('dataobject/Eligibility_File_PDO/DataObject.xml'),
                   is(equalTo(true))
        assertThat actualFiles.contains('bam.properties'),
                   is(equalTo(true))
    }
}
