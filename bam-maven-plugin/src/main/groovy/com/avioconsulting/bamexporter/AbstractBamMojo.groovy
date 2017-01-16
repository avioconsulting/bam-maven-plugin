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
}
