#!/usr/bin/env python

from fedorarest import FedoraClient
from lxml import etree
from sys import argv
import re

client = FedoraClient(url="http://localhost:8080/fedora",password="")

# get objects from fedora
pids = []
sessionToken = ""

while True:
	response,body = client.findObjects(argv[1],sessionToken=sessionToken)
	result_tree = etree.fromstring(body)
	token_elem = result_tree.xpath("/f:result/f:listSession/f:token",namespaces={'f':'http://www.fedora.info/definitions/1/0/types/'})
	pid_elems = result_tree.xpath("//f:pid",namespaces={'f':'http://www.fedora.info/definitions/1/0/types/'})
	for pid_elem in pid_elems:
		pids.append(pid_elem.text)
	if len(token_elem) == 0:
		break
	sessionToken = token_elem[0].text
	
for pid in pids:
	print pid
