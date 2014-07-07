#!/usr/bin/env python

from fedorarest import FedoraClient
from lxml import etree
import fileinput

client = FedoraClient(url="http://localhost:8080/fedora",password="")

parser = etree.XMLParser(remove_blank_text=True)

for line in fileinput.input():
	pid = line[0:-1]
	response, body = client.getDatastreamDissemination(pid,"DC")
	tree = etree.fromstring(body,parser)
	for el in tree.xpath('//dc:title',namespaces={'dc': 'http://purl.org/dc/elements/1.1/'}):
		if el.text.startswith("urn:nbn:de"):
			 el.getparent().remove(el)
	content = etree.tostring(tree, pretty_print=True)
	response, body = client.addDatastream(pid,"DC",content)
	print "%s - status: %s" % (pid,response["status"])
	
