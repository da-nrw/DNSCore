#!/usr/bin/env python

from fedorarest import FedoraClient
from lxml import etree
import argparse

parser = argparse.ArgumentParser(description='Transform a Fedora XML Datastream with XSLT. Expects a list of pids as input.')
parser.add_argument('pid_file', help='a text file containing the pids of the objects to be processed, seperated by newline')
parser.add_argument('source_dsId', help='the dsId of the datastream to be processed')
parser.add_argument('target_dsId', help='the dsId of the datastream the result will be saved in')
parser.add_argument('xslt_path', help='path to the xslt file used for the transformation')
parser.add_argument('--merge', action='store_true',
                   help='merge the result with the target datastream (only works for flat xmls)')
parser.add_argument('--test', action='store_true',
                   help='do not change target datastream, instead print the result')

args = parser.parse_args()

client = FedoraClient(url="http://localhost:8080/fedora",password="")

xslt_tree = etree.parse(args.xslt_path)
transform = etree.XSLT(xslt_tree)
parser = etree.XMLParser(remove_blank_text=True)

for line in open(args.pid_file,'r'):
	try:
		pid = line[0:-1]
		response, body = client.getDatastreamDissemination(pid,args.source_dsId)
		orig_tree = etree.fromstring(body,parser)
		result_tree = transform(orig_tree, object_id=("'%s'" % pid.split(':')[1]))
		if args.merge:
			response, body = client.getDatastreamDissemination(pid,args.target_dsId)
			merge_tree = etree.fromstring(body,parser)
			for child in list(result_tree.getroot()):
				if child.text:
					merge_tree.append(child)
			etree.cleanup_namespaces(merge_tree)
			content = etree.tostring(merge_tree, pretty_print=True)
		else:
			content = etree.tostring(result_tree, pretty_print=True)
		if args.test:
			print "%s - content: %s" % (pid,content)
		else:
			response, body = client.addDatastream(pid,args.target_dsId,content)
			print "%s - status: %s" % (pid,response["status"])
	except Exception as e:
		print "ERROR: while processing pid %s" % pid
		print e
