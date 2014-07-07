#!/usr/bin/env python

from fedorarest import FedoraClient
from sys import argv
import time

client = FedoraClient(url="http://localhost:8080/fedora",password="")
times = []

for i in range(50):
	content = open(argv[1], "rb").read()
	t = time.time()
	dsId = "ds_%s" % i
	response, body = client.addDatastream(argv[2], dsId, content, mimeType="image/tiff", controlGroup="M")
	times.append(time.time() - t)
	print "%s - status: %s" % (dsId, response["status"])
	if response["status"][0] == "4" or response["status"][0] == "5":
		print body

sum = sum(times)
print "Average: %s s" % (sum / len(times))
print "Total: %s s" % sum
