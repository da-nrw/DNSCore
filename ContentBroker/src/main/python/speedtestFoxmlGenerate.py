#!/usr/bin/env python

from fedorarest import FedoraClient
from sys import argv
import os

client = FedoraClient(url="http://localhost:8080/fedora",password="")

foxml = open(argv[2], "w")

head = """<?xml version="1.0" encoding="UTF-8"?>
<foxml:digitalObject xmlns:foxml="info:fedora/fedora-system:def/foxml#" VERSION="1.1" PID="test:speedFoxml"
                     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                     xsi:schemaLocation="info:fedora/fedora-system:def/foxml#
                                         http://www.fedora.info/definitions/1/0/foxml1-1.xsd">
  <foxml:objectProperties>
    <foxml:property NAME="info:fedora/fedora-system:def/model#state" VALUE="A"/>
    <foxml:property NAME="info:fedora/fedora-system:def/model#label" VALUE="FOXML Speedtest"/>
  </foxml:objectProperties>"""
foxml.write(head)

dsTemplate = """<foxml:datastream CONTROL_GROUP="E" ID="{0}" STATE="A">
    <foxml:datastreamVersion ID="{0}.0" MIMETYPE="image/png"
                             LABEL="Test image">
      <foxml:contentLocation REF="{1}" TYPE="URL"/>
    </foxml:datastreamVersion>
  </foxml:datastream>"""

for i in range(50):
	dsId = "ds_%s" % i
	foxml.write(dsTemplate.format(dsId, argv[1]))

foxml.write('</foxml:digitalObject>')

print "OK"
