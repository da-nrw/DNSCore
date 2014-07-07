#!/usr/bin/env python

from fedorarest import FedoraClient
import fileinput
from lxml import etree
import re

client = FedoraClient(url="http://localhost:8080/fedora",password="")

for line in fileinput.input():
	pid = line[0:-1]
	response, body = client.listDatastreams(pid)
	result_tree = etree.fromstring(body)
	ds_elems = result_tree.xpath("//f:datastream",namespaces={'f':'http://www.fedora.info/definitions/1/0/access/'})
	for ds_elem in ds_elems:
		dsid = ds_elem.get("dsid")
		if re.match(r"institution-", dsid):
			response, body = client.purgeDatastream(pid,dsid)
			print "%s - %s - status: %s" % (pid, dsid,response["status"])
