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
        def bamProps = new File(bamPath, 'bam.properties')
        FileUtils.touch bamProps

        // act
        mojo.execute()

        // assert
        assertThat this.mavenProjectArtifact,
                   is(equalTo(mojo.join(this.targetDirectory, 'theBuildName.jar')))
        def antBuilder = new AntBuilder()
        fail 'write this'
    }
}
