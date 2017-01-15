package com.avioconsulting.bamexportrunner

import groovy.xml.MarkupBuilder
import oracle.xml.parser.v2.XMLElement

class Exporter {
    private static final int COMMAND_TIMEOUT_MINUTES = 5

    static ArrayList<String> doExport(XMLElement input) {
        def logText = []
        def log = { String text -> logText << text }
        def bamProjectNode = input.getElementsByTagName('bamProject').item(0)
        def bamProject = bamProjectNode.textContent
        validateProjectName bamProject
        def bamPath = new File(System.getProperty("soa.oracle.home"), 'bam')
        def bamBinPath = new File(bamPath, 'bin')
        def script = new File(bamBinPath, 'bamcommand')
        assert script.exists()
        // matches file adapter's expected path
        def exportFile = '/tmp/bamExportForMavenClient.zip'
        def directory = new File(exportFile).parentFile
        if (!directory.exists()) {
            directory.mkdirs()
        }
        buildBamConnectConfig bamBinPath
        def command = "${script} -cmd export -name ${bamProject} -type project -file ${exportFile}"
        log "Executing command ${command}"
        def result = command.execute()
        result.waitForOrKill(COMMAND_TIMEOUT_MINUTES * 60 * 1000)
        log result.text
        if (result.exitValue() != 0) {
            log 'ERROR: BAM command exited with non-zero status'
        }
        logText
    }

    static validateProjectName(String projectName) {
        if (projectName.matches(/[A-Za-z0-9]+/)) {
            return true
        }
        def message = "Project name '${projectName}' includes "
        if (projectName.contains(' ')) {
            message += 'a space'
        } else if (projectName.contains(';')) {
            message += 'a semicolon'
        } else if (projectName.contains('"') || projectName.contains("'")) {
            message += 'quotes'
        }
        throw new RuntimeException(message + " and that's not supported!")
    }

    static buildBamConnectConfig(File bamBinPath) {
        def domainHome = System.getProperty("domain.home")
        def configHome = new File(domainHome, 'config')
        def configXmlPath = new File(configHome, 'config.xml')
        def initInfoPath = new File(domainHome, 'init-info')
        def nodeManagerXmlPath = new File(initInfoPath, 'config-nodemanager.xml')
        def fetcher = new BamServerInfoFetcher(configXmlPath,
                                               nodeManagerXmlPath,
                                               new PasswordDecryptor(domainHome))
        def info = fetcher.bamServerInfo
        def config = [
                host    : info.host,
                port    : info.port,
                username: info.username,
                password: info.password
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
