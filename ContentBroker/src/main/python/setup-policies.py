#!/usr/bin/env python

from fedorarest import FedoraClient
import os

client = FedoraClient(url="http://localhost:8080/fedora",password="clDBmno7")

# delete standard policies
delete = ["access-staff","access-teacher","access-student","public-demo_demoObjectCollection"]
for policy in delete:
	pid = "fedora-policy:" + policy
	response, body = client.purgeObject(pid)
	print "purged %s - status: %s" % (pid,response["status"])

# ingest custom policies
foxmls = os.listdir("policies/");
for foxml in foxmls:
	content = open("policies/"+foxml).read()
	response, body = client.ingest(content=content)
	print "ingested %s - status: %s" % (foxml,response["status"])
	if response["status"][0] == "4" or response["status"][0] == "5":
		print body
