package com.avioconsulting.bamexporter

import org.apache.maven.model.Build
import org.apache.maven.project.MavenProject
import org.junit.Before

abstract class BaseBamTest {
    final File baseDirectory = new File('build/tmp/the_base_dir')
    final File projectDirectory = new File(baseDirectory, 'baseProjectDir')
    final File classesDirectory = new File(new File(projectDirectory, 'target'), 'classes')
    final File bamPath = new File(this.projectDirectory, 'src/main/resources/bam')

    protected def mockMavenProject(mojo) {
        def build = [outputDirectory: classesDirectory.absolutePath] as Build
        mojo.mavenProject = [
                getBasedir: { this.projectDirectory },
                getBuild  : { build }
        ] as MavenProject
    }

    @Before
    void setup() {
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
