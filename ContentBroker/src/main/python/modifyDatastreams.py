#!/usr/bin/env python

import os
from sys import argv
from fedorarest import FedoraClient

client = FedoraClient(url="http://localhost:8080/fedora",password="")

for file in os.listdir(argv[1]):
	content = open(argv[1] + file).read()
	response, body = client.modifyDatastream(argv[2], file, content)
	print "%s - status: %s" % (file,response["status"])
	if response["status"][0] == "4" or response["status"][0] == "5":
		print body
