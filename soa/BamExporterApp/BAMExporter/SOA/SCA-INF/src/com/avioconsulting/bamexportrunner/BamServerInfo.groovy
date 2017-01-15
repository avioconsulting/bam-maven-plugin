package com.avioconsulting.bamexportrunner

class BamServerInfo {
    final String host
    final int port
    final String username
    final String password

    BamServerInfo(String host, int port, String username, String password) {
        this.host = host
        this.port = port
        this.username = username
        this.password = password
    }
}
