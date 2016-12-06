#!/usr/bin/env python
# coding: utf8

from fedorarest import FedoraClient
from lxml import etree
import fileinput
import datetime

import sys
reload(sys)
sys.setdefaultencoding('utf8')

'''
Repair Metadata in PIP's

requirements:
sudo yum install python-lxml
'''

client = FedoraClient(url="http://localhost:8080/fedora",password="")

parser = etree.XMLParser(remove_blank_text=True,encoding='UTF-8')

LOG_FILE="./jp-fedora"+datetime.datetime.now().strftime("%Y%m%d-%H%M%S")+".log"
XML_NAMESPACES={'rdf':'http://www.w3.org/1999/02/22-rdf-syntax-ns#','edm' : 'http://www.europeana.eu/schemas/edm/', 'dc': 'http://purl.org/dc/elements/1.1/','xsi':'http://www.w3.org/2001/XMLSchema-instance','mets':'http://www.loc.gov/METS/','xlink' : 'http://www.w3.org/1999/xlink', 'vls': 'http://semantics.de/vls', 'mods':'http://www.loc.gov/mods/v3'}


def getPathToFile(targetFile):
	response, body = client.getDatastream(pid,targetFile)
	print "status: %s" % (response["status"])
	#print " %s" % (body)
	result_tree = etree.fromstring(body)
	ds_elems = result_tree.xpath("/f:datastreamProfile/f:dsLocation",namespaces={'f':'http://www.fedora.info/definitions/1/0/management/'})
	for ds_elem in ds_elems:
		dsLocation = ds_elem.text
		print "%s - dsLocation" % (dsLocation)

	response, body = client.getDatastreamDissemination(pid,targetFile)
	#tree = etree.fromstring(body,parser)
	return body



'''
Die Funktion findet die mets:dmdSec-Knoten in dem Mets-Tree und die ZUGEHÖRIGEN edm:ProvidedCHO im EDM-Tree. 
Das zurückgegebene Resultat ist ein Assiziatives-Array. 
'''
def createMetsEdmHashArr(metsTree,edmTree):
	metsElemList=metsTree.xpath('/mets:mets/mets:dmdSec ',namespaces=XML_NAMESPACES)
	edmElemList=edmTree.xpath('/rdf:RDF/edm:ProvidedCHO ',namespaces=XML_NAMESPACES)
	returnHash={}
	#print "metsElemList children: ",metsElemList;
	#print "edmElemList children: ",edmElemList;
	if( len(metsElemList)<>len(edmElemList)):
		raise RuntimeError(u'Die Anzahl der Elemente stimmt in der EDM- und METS-File nicht überein: ',len(metsElemList)," vs ",len(edmElemList))
	#Fuer jeden METS-dmdSec muss das Entsprehcnende EDM-ProvidedCHO herausgefunden werden
	for i in range(len(metsElemList)): 
		idPart=metsElemList[i].get('ID')
		for j in range(len(edmElemList)):
			if idPart in edmElemList[j].xpath('./@rdf:about',namespaces=XML_NAMESPACES)[0]:
				#removeRoleTermFromOneNodePair(metsElemList[i],edmElemList[j])
				returnHash[metsElemList[i]]=edmElemList[j]
	
	
	print "ende removeRoleTermCodePart"
	return returnHash;

'''

'''
def correctRoleTermForNodePair(metsNode,edmNode):
	metsElemList=metsNode.xpath('./mets:mdWrap/mets:xmlData/mods:mods/mods:name ',namespaces=XML_NAMESPACES)
	edmElemList=edmNode.xpath('./dc:contributor',namespaces=XML_NAMESPACES)
	if( len(metsElemList)<>len(edmElemList)):
		raise RuntimeError(u'Die Anzahl der Verfasser/Autoren stimmt in der EDM- und METS-File nicht überein: ',len(metsElemList)," vs ",len(edmElemList))

	for i in range(len(edmElemList)):
		roleText=''
		roleElements=metsElemList[i].xpath('./mods:role/mods:roleTerm[contains(@type, \'text\')][1] ',namespaces=XML_NAMESPACES)
		if(len(roleElements)==1):
			roleText=roleElements[0].text+": "
		#print "removeRoleTermFromOneNodePair before: ",edmElemList[i].text
		edmElemList[i].text=roleText+metsElemList[i].xpath('./mods:namePart[1] ',namespaces=XML_NAMESPACES)[0].text#.getchildren()[0].text
		#print "removeRoleTermFromOneNodePair after: ",edmElemList[i].text
	
	
	print "ende removeRoleTermFromOneNodePair"
	
	
'''
Generiert aus dem Mets-TitleInfo ein DC-Title und überschreibt das vorhandene DC-Title

Die logische Funktionalität ist aus Java-Code übernommen worden
https://github.com/da-nrw/DNSCore/blob/1d13ed65cdee42fc5a13b602158bf0c212f64e14/DNSCommon/src/main/java/de/uzk/hki/da/metadata/MetsParser.java [473-531]
	
'''
def correctTitleConsiderNonSort(metsTree,edmTree):
	metsElemList=metsTree.xpath('/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/mods:mods/mods:titleInfo ',namespaces=XML_NAMESPACES)
	edmElemList=edmTree.xpath('/rdf:RDF/edm:ProvidedCHO/dc:title ',namespaces=XML_NAMESPACES)

	if( len(metsElemList)<>1 or len(edmElemList)<>1):
		raise RuntimeError(u'Die Anzahl der Titel ist nicht gleich 1: ',len(metsElemList)," vs ",len(edmElemList))
		
	nonSortElem,titleElem,displayLabelElem,subTitleElem=None,None,None,None
	mainTitle=""
	tmpElemArr=metsElemList[0].xpath('./mods:nonSort[1] ',namespaces=XML_NAMESPACES)
	if len(tmpElemArr)==1:
		nonSortElem=tmpElemArr[0]
	tmpElemArr=metsElemList[0].xpath('./mods:title[1] ',namespaces=XML_NAMESPACES)
	if len(tmpElemArr)==1:
		titleElem=tmpElemArr[0]
	tmpElemArr=metsElemList[0].xpath('./mods:displayLabel[1] ',namespaces=XML_NAMESPACES)
	if len(tmpElemArr)==1:
		displayLabelElem=tmpElemArr[0]
	tmpElemArr=metsElemList[0].xpath('./mods:subTitle[1] ',namespaces=XML_NAMESPACES)
	if len(tmpElemArr)==1:
		subTitleElem=tmpElemArr[0]
	
	if titleElem is not None:
		if nonSortElem is not None:
			mainTitle=nonSortElem.text+" "+titleElem.text	
	elif nonSortElem is not None:
		mainTitle=nonSortElem.text
	else:
		mainTitle=displayLabelElem.text
	
	if subTitleElem is not None:
		mainTitle=mainTitle+" : "+subTitleElem.text
	
	#print "correctTitleConsiderNonSort before: ",edmElemList[0].text
	edmElemList[0].text=mainTitle
	#print "correctTitleConsiderNonSort after: ",edmElemList[0].text
	
	
	print "ende correctTitleConsiderNonSort"






for line in fileinput.input('inputId.txt'):
	with open(LOG_FILE,'wb') as LOGFILE:
		print " %s" % (line)
		pid = line[0:-1]
		print " %s" % (pid)
		edmTree = etree.fromstring(getPathToFile("EDM.xml"),parser)
		metsTree = etree.fromstring(getPathToFile("METS.xml"),parser)
		
		#print etree.tostring(edmTree, pretty_print=True)
		#print etree.tostring(metsTree, pretty_print=True)
		#help(metsTree)
		try:
			metsEdmHashArr=createMetsEdmHashArr(metsTree,edmTree);
			for metsNode in metsEdmHashArr:
				correctRoleTermForNodePair(metsNode,metsEdmHashArr[metsNode])
				correctTitleConsiderNonSort(metsNode,metsEdmHashArr[metsNode])
				#correctTitleConsiderNoTitleForSubPackage(metsNode,metsEdmHashArr[metsNode])
				#correctOriginInfo(metsNode,metsEdmHashArr[metsNode])
			#removeXEpicURLIfUrnIsSpecific();
			#removeMETS_XSLT();
			LOGFILE.write(pid+" -> Success")
		except RuntimeError as e:
			LOGFILE.write(pid+" -> Error: "+str(e))
			print pid+" -> Error: "+str(e)
		
		
		
		
		print "etree: ",edmTree
		for el in edmTree.xpath('/rdf:RDF/edm:ProvidedCHO/dc:title ',namespaces={'rdf':'http://www.w3.org/1999/02/22-rdf-syntax-ns#','edm' : 'http://www.europeana.eu/schemas/edm/', 'dc': 'http://purl.org/dc/elements/1.1/'}):
			#if el.text.startswith("urn:nbn:de"):
			#print "%s - found: %s" % (pid,el.text)
			el.text = el.text.upper()
			print " %s" % (el.text)
		
			#	 el.getparent().remove(el)
		content = etree.tostring(edmTree, pretty_print=True)
		#print " %s" % (content)
		#outFile = open(getPathToFile("EDM.xml")+'TEST', 'w')
		etree.ElementTree(edmTree).write('file:///ci/storage/WorkArea/pips/public/TEST/1-20161205838/EDM_OUT.xml', xml_declaration=True, encoding='utf-16', pretty_print=True) 
		#response, body = client.modifyDatastream(pid,"EDM.xml",content)
		#print "%s - status: %s" % (pid,response["status"])
		
