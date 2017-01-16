package com.avioconsulting.bamexporter

import org.apache.maven.artifact.Artifact
import org.apache.maven.model.Build
import org.apache.maven.project.MavenProject
import org.junit.Before

abstract class BaseBamTest {
    final File baseDirectory = new File('build/tmp/the_base_dir')
    final File projectDirectory = new File(baseDirectory, 'baseProjectDir')
    final File targetDirectory = new File(projectDirectory, 'target')
    final File classesDirectory = new File(targetDirectory, 'classes')
    final File bamPath = new File(this.projectDirectory, 'src/main/resources/bam')
    File mavenProjectArtifact

    protected mockMavenProject(mojo) {
        def build = [
                outputDirectory: classesDirectory.absolutePath,
                directory: this.targetDirectory,
                finalName: 'theBuildName'
        ] as Build
        def artifact = [
                getFile: { this.mavenProjectArtifact },
                setFile: { File file -> this.mavenProjectArtifact = file }
        ] as Artifact
        mojo.mavenProject = [
                getBasedir : { this.projectDirectory },
                getBuild   : { build },
                getArtifact: artifact
        ] as MavenProject
    }

    @Before
    void setup() {
        mavenProjectArtifact = null
        if (baseDirectory.exists()) {
            baseDirectory.deleteDir()
        }
        baseDirectory.mkdirs()
        if (projectDirectory.exists()) {
            projectDirectory.deleteDir()
        }
        projectDirectory.mkdirs()
    }
}
