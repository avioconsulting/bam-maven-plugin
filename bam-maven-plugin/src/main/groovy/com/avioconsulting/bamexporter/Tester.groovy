package com.avioconsulting.bamexporter

import com.avioconsulting.bamexporterservice.BamexporterprocessClientEp

import javax.xml.ws.BindingProvider

class Tester {
    public static void main(String[] args) {
        def service = new BamexporterprocessClientEp()
        def port = service.bamExporterProcessPt
        ((BindingProvider) port).requestContext[BindingProvider.ENDPOINT_ADDRESS_PROPERTY] = 'http://localhost:8001/soa-infra/services/default/BAMExporter/bamexporterprocess_client_ep'
        def result = port.process 'SPICE'
        result.writeTo(new FileOutputStream("stuff.zip"))
    }
}
