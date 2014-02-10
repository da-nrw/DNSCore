#!/usr/bin/env python

from fedorarest import FedoraClient
from sys import argv
import time

client = FedoraClient(url="http://da-nrw-vm2.hki.uni-koeln.de:8080/fedora",password="Herrlich456FFEE")
times = []

t = time.time()

content = open(argv[1], "r").read()
response, body = client.ingest(argv[2], "FOXML Speedtest", "fedoraAdmin", content)
print "%s - status: %s" % (argv[2], response["status"])
if response["status"][0] == "4" or response["status"][0] == "5":
	print body

print "Total: %s s" % (time.time() - t)
