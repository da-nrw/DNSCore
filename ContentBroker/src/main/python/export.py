#!/usr/bin/env python

from fedorarest import FedoraClient
import fileinput
from sys import argv

client = FedoraClient(url="http://localhost:8080/fedora",password="")

for line in fileinput.input():
	pid = line[0:-1]
	response, body = client.export(pid,context="archive")
	print "%s - status: %s" % (pid,response["status"])
	FILE = open(pid + ".xml","w")
	FILE.writelines(body)
	FILE.close()
