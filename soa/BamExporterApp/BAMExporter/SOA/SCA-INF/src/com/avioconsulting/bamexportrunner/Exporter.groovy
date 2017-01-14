package com.avioconsulting.bamexportrunner

import oracle.xml.parser.v2.XMLElement

class Exporter {
    static String test(XMLElement input) {
        // TODO: Validate that project name has no special characters
        def bamProjectNode = input.getElementsByTagName('bamProject').item(0)
        def bamProject = bamProjectNode.textContent
        return "we will export BAM project ${bamProject}"
    }
}
