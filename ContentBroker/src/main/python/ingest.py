#!/usr/bin/env python

from fedorarest import FedoraClient
import fileinput
from sys import argv

client = FedoraClient(url="http://localhost:8080/fedora",password="clBDmno7")

files = []

if argv[1] == "-f":
	files.append(argv[2])
else:
	files = fileinput.input()

for line in  files:
	filename = line.strip()
	content = open(filename).read()
	response, body = client.ingest(content=content)
	print "%s - status: %s" % (filename,response["status"])
	print "ingested %s - status: %s" % (filename,response["status"])
	if response["status"][0] == "4" or response["status"][0] == "5":
		print body
