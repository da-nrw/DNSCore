#!/usr/bin/env python

import httplib2
import urllib

class FedoraClient:

	def __init__(self,url="http://localhost:8080/fedora",username="fedoraAdmin",password="fedora"):
		self.h = httplib2.Http()
		self.h.add_credentials(username,password)
		self.url = url
		
	def ingest(self,pid="new",label="",ownerId="",content=None,format="info:fedora/fedora-system:FOXML-1.1"):
		
		params = {}
		params["format"] = format
		params["ignoreMime"] = "true"
		if label != "":	params["label"] = label
		if ownerId != "":	params["ownerId"] = ownerId
		
		request_url = "%s/objects/%s?%s" % (self.url, pid, urllib.urlencode(params))
		headers = {}		
		if content and len(content) > 0:
			headers['Content-Length'] = str(len(content))
		return self.h.request(request_url, body=content, headers=headers, method="POST")
		
		
	def addRelationship(self,pid,predicate,object,subject="",isLiteral=True,datatype=False):
		
		if not subject: subject = "info:fedora/" + pid
		
		params = {}
		params["subject"] = subject
		params["predicate"] = predicate
		params["object"] = object
		if isLiteral: params["isLiteral"] = "true"
		else: params["isLiteral"] = "false"
		if datatype: params["datatype"] = datatype
		
		request_url = "%s/objects/%s/relationships/new?%s" % (self.url, pid, urllib.urlencode(params))
		return self.h.request(request_url, method="POST")
		
	
	def purgeRelationship(self,pid,predicate,object="",subject="",isLiteral=True,datatype=False):
		
		if not subject: subject = "info:fedora/" + pid
		
		params = {}
		params["subject"] = subject
		params["predicate"] = predicate
		if object: params["object"] = object
		if isLiteral: params["isLiteral"] = "true"
		else: params["isLiteral"] = "false"
		if datatype: params["datatype"] = datatype
		
		request_url = "%s/objects/%s/relationships?%s" % (self.url, pid, urllib.urlencode(params))
		return self.h.request(request_url, method="DELETE")
		
	
	def addDatastream(self,pid,dsID,content,dsLabel="",versionable=True,
		mimeType="application/xml", controlGroup="X"):
	
		params = {}
		if dsLabel: params["dsLabel"] = dsLabel
		if controlGroup: params["controlGroup"] = controlGroup
		if not versionable: params["versionable"] = "false"
		
		request_url = "%s/objects/%s/datastreams/%s?%s" % (self.url, pid, dsID, urllib.urlencode(params))
		headers = {}
		headers['Content-Type'] = mimeType
		headers['Content-Length'] = str(len(content))
		return self.h.request(request_url, body=content, headers=headers, method="POST")
		
		
	def purgeObject(self,pid):

		request_url = "%s/objects/%s" % (self.url, pid)
		return self.h.request(request_url, method="DELETE")
		
		
	def modifyObject(self,pid,label="",ownerId="",state="",logMessage="",lastModified=""):
		
		params = {}
		if label != "":	params["label"] = label
		if ownerId != "":	params["ownerId"] = ownerId
		if state != "":	params["state"] = state
		if logMessage != "":	params["logMessage"] = logMessage
		if lastModified != "":	params["lastModified"] = lastModified
		
		request_url = "%s/objects/%s?%s" % (self.url, pid, urllib.urlencode(params))
		return self.h.request(request_url, method="PUT")

	def listDatastreams(self,pid):

		params = {}
		params['format'] = 'xml'

		request_url = "%s/objects/%s/datastreams?%s" % (self.url, pid, urllib.urlencode(params))
		return self.h.request(request_url, method="GET")
				
	def modifyDatastream(self,pid,dsID,content,dsLabel="",versionable=True,mimeType="application/xml"):
		
		params = {}
		if dsLabel: params["dsLabel"] = dsLabel
		if not versionable: params["versionable"] = "false"
		
		request_url = "%s/objects/%s/datastreams/%s?%s" % (self.url, pid, dsID, urllib.urlencode(params))
		headers = {}
		headers['Content-Type'] = mimeType
		headers['Content-Length'] = str(len(content))
		return self.h.request(request_url, body=content, headers=headers, method="PUT")

	def purgeDatastream(self,pid,dsID):

		request_url = "%s/objects/%s/datastreams/%s" % (self.url, pid, dsID)
		return self.h.request(request_url, method="DELETE")
	
	def findObjects(self,query,pid=True,label=False,maxResults="25",sessionToken=""):
		
		params = {}
		params['query'] = query
		params['resultFormat'] = 'xml'
		params['maxResults'] = maxResults
		if sessionToken: params['sessionToken'] = sessionToken
		if pid: params['pid'] = 'true'
		if label: params['label'] = 'true'
		
		request_url = "%s/objects?%s" % (self.url, urllib.urlencode(params))
		return self.h.request(request_url, method="GET")
		
	def getDatastreamDissemination(self,pid,dsID):
	
		request_url = "%s/objects/%s/datastreams/%s/content" % (self.url, pid, dsID)
		return self.h.request(request_url, method="GET")
		
	def export(self,pid,context="public"):
		
		params = {}
		if context: params["context"] = context
		
		request_url = "%s/objects/%s/export?%s" % (self.url, pid, urllib.urlencode(params))
		return self.h.request(request_url, method="GET")
		
		
