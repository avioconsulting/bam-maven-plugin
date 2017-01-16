package com.avioconsulting.bamexporter

import org.apache.commons.io.FileUtils
import org.junit.Before
import org.junit.Test

import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.is
import static org.junit.Assert.assertThat

class BamPackageMojoTest extends BaseBamTest {
    BamPackageMojo mojo

    @Before
    void setup() {
        mojo = new BamPackageMojo()
        mojo.bamProject = 'theProject'
        mockMavenProject mojo
    }

    @Test
    void normal() {
        // arrange

        // act
        mojo.execute()

        // assert
        def zipFile = mojo.join(this.targetDirectory, 'theBuildName.jar')
        assertThat zipFile.exists(),
                   is(equalTo(true))
        assertThat this.mavenProjectArtifact,
                   is(equalTo(zipFile))
        File.createTempDir().with {
            def antBuilder = new AntBuilder()
            antBuilder.unzip src: zipFile,
                             dest: absolutePath,
                             overwrite: true
            def contents = new File(absolutePath, 'bam.properties')
            assertThat contents.exists(),
                       is(equalTo(true))
            absoluteFile.deleteDir()
        }
    }
}
