package com.avioconsulting.bamexporter

import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.Component
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject

abstract class AbstractBamMojo extends AbstractMojo {
    @Parameter(property = 'bam.project', required = true)
    protected String bamProject

    @Component
    protected MavenProject mavenProject

    protected File getBamDestination() {
        join this.mavenProject.basedir,
             'src',
             'main',
             'resources',
             'bam'
    }

    protected static File join(File parent, String... parts) {
        def separator = System.getProperty 'file.separator'
        new File(parent, parts.join(separator))
    }
}
