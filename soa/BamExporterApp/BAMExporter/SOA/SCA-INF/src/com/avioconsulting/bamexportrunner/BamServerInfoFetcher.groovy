package com.avioconsulting.bamexportrunner

class BamServerInfoFetcher {
    private final BamServerInfo info
    private final String BAM_SERVER = 'bam_server1'

    BamServerInfoFetcher(File domainConfigXml,
                         File nodeManagerInitInfoXml,
                         PasswordDecryptor passwordDecryptor) {
        def domainConfig = new XmlSlurper().parse(domainConfigXml)
        def bamNode = domainConfig.server.find { node ->
            node.name == BAM_SERVER
        }
        if (!bamNode) {
            throw new Exception("Was not able to find BAM server ${BAM_SERVER}! (in ${domainConfigXml.absolutePath})")
        }
        String host = bamNode.'listen-address'.text()
        if (host.empty) {
            host = 'localhost'
        }
        int port = new Integer(bamNode.'listen-port'.text())
        def nodeManagerConfig = new XmlSlurper().parse(nodeManagerInitInfoXml)
        String username = nodeManagerConfig.userName.text()
        String encryptedPassword = nodeManagerConfig.password.text()
        if (username.empty || encryptedPassword.empty) {
            throw new Exception("Was not able to find username/password! (in ${nodeManagerInitInfoXml.absolutePath})")
        }
        this.info = new BamServerInfo(host,
                                      port,
                                      username,
                                      passwordDecryptor.decrypt(encryptedPassword))
    }

    BamServerInfo getBamServerInfo() {
        this.info
    }
}
