package com.avioconsulting.bamexportrunner

import oracle.xml.parser.v2.XMLElement

class Exporter {
    static def test(XMLElement input) {
        def bamProjectNode = input.getElementsByTagName('bamProject').item(0)
        def bamProject = bamProjectNode.textContent
        println "we will export BAM project ${bamProject}"
    }
}
