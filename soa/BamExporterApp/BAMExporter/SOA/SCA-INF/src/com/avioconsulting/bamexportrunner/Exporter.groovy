package com.avioconsulting.bamexportrunner

import groovy.xml.MarkupBuilder
import oracle.xml.parser.v2.XMLElement

class Exporter {
    private static final int COMMAND_TIMEOUT_MINUTES = 5

    static String doExport(XMLElement input) {
        def logText = []
        def log = { text -> logText << text }
        def bamProjectNode = input.getElementsByTagName('bamProject').item(0)
        def bamProject = bamProjectNode.textContent
        // TODO: Validate that project name has no special characters
        log "BRADY: Will export BAM project ${bamProject}"
        def bamPath = new File(System.getProperty("soa.oracle.home"), 'bam')
        def bamBinPath = new File(bamPath, 'bin')
        def script = new File(bamBinPath, 'bamcommand')
        assert script.exists()
        // matches file adapter's expected path
        def exportFile = '/tmp/bamExporter/bamExport.zip'
        def directory = new File(exportFile).parentFile
        if (!directory.exists()) {
            directory.mkdirs()
        }
        buildBamConfig bamBinPath
        def command = "${script} -cmd export -name \"${bamProject}\" -type project -file \"${exportFile}\""
        log "BRADY: Executing command ${command}"
        def result = command.execute()
        result.waitForOrKill(COMMAND_TIMEOUT_MINUTES*60*1000)
        log result.text
        if (result.exitValue() != 0) {
            log 'ERROR: BAM command exited with non-zero status'
        }
        // temporary for debugging (or maybe keep this??)
        println logText.join("\n")
        return logText.join("\n")
    }

    static buildBamConfig(File bamBinPath) {
        // TODO: Derive these from domain config, etc.
        def config = [
                host    : 'localhost',
                port    : '7001', // BAM port needed or is AdminServer OK??
                username: 'weblogic',
                password: 'oracle1234'
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
}
