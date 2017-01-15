package com.avioconsulting.bamexporter

import com.avioconsulting.bamexporterservice.BamExporterProcess
import com.avioconsulting.bamexporterservice.BamexporterprocessClientEp

import javax.xml.ws.BindingProvider

class BamExportFetcher {
    private final BamExporterProcess port

    BamExportFetcher(URI endpoint) {
        this.port = new BamexporterprocessClientEp().bamExporterProcessPt
        ((BindingProvider) this.port).requestContext[BindingProvider.ENDPOINT_ADDRESS_PROPERTY] = endpoint.toString()
    }

    def fetch(String bamProject, OutputStream stream) {
        def dataHandler = this.port.process bamProject
        dataHandler.writeTo stream
    }
}
