#!/usr/bin/env python

from fedorarest import FedoraClient
from lxml import etree
import fileinput

client = FedoraClient(url="http://localhost:8080/fedora",password="")

parser = etree.XMLParser(remove_blank_text=True)

for line in fileinput.input():
	pid = line[0:-1]
	response, body = client.getDatastream(pid,"EDM.xml")
	#print " %s" % (body)
	result_tree = etree.fromstring(body)
	ds_elems = result_tree.xpath("/f:datastreamProfile/f:dsLocation",namespaces={'f':'http://www.fedora.info/definitions/1/0/management/'})
	for ds_elem in ds_elems:
		dsLocation = ds_elem.text
		print "%s - dsLocation" % (dsLocation)

	response, body = client.getDatastreamDissemination(pid,"EDM.xml")
	tree = etree.fromstring(body,parser)
	for el in tree.xpath('/rdf:RDF/edm:ProvidedCHO/dc:title ',namespaces={'rdf':'http://www.w3.org/1999/02/22-rdf-syntax-ns#','edm' : 'http://www.europeana.eu/schemas/edm/', 'dc': 'http://purl.org/dc/elements/1.1/'}):
		#if el.text.startswith("urn:nbn:de"):
		#print "%s - found: %s" % (pid,el.text)
		el.text = el.text.upper()
		#print " %s" % (el.text)
	
		#	 el.getparent().remove(el)
	content = etree.tostring(tree, pretty_print=True)
	#print " %s" % (content)
	
	#response, body = client.modifyDatastream(pid,"EDM.xml",content)
	print "%s - status: %s" % (pid,response["status"])
	
