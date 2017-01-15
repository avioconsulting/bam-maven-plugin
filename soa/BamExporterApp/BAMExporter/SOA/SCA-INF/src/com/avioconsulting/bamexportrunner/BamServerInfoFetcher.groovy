package com.avioconsulting.bamexportrunner

import groovy.util.slurpersupport.NodeChild

class BamServerInfoFetcher {
    private final BamServerInfo info

    BamServerInfoFetcher(File domainConfigXml,
                         File nodeManagerInitInfoXml,
                         PasswordDecryptor passwordDecryptor) {
        def domainConfig = new XmlSlurper().parse(domainConfigXml)
        NodeChild bamNode = domainConfig.server.find { node ->
            node.name == 'bam_server1'
        }
        def nodeManagerConfig = new XmlSlurper().parse(nodeManagerInitInfoXml)
        String host = bamNode.'listen-address'
        int port = new Integer(bamNode.'listen-port'.text())
        String username = nodeManagerConfig.userName.text()
        String encryptedPassword = nodeManagerConfig.password.text()
        this.info = new BamServerInfo(host,
                                      port,
                                      username,
                                      passwordDecryptor.decrypt(encryptedPassword))
    }

    BamServerInfo getBamServerInfo() {
        this.info
    }
}
