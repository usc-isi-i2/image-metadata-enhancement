import json
from elasticsearch import Elasticsearch
import sys
from sys import stderr

def loadDatainES(filename,index,doctype,dataFileType,hostname="localhost",port=9200,mappingFilePath=None):
    try:
        print "Connecting to "+hostname + " at port: " + str(port)
        es=Elasticsearch(['http://localhost:9200'])

        if mappingFilePath:
            with open(mappingFilePath) as m:
                mapping = m.read()
                es.indices.create(index=index,body=mapping,ignore=400)

        if dataFileType==1:
            with open(filename) as f:
                data = json.load(f)
                for line in data:
                    es.index(index=index,doc_type=doctype,body=line)
                print "done indexing the json file"

        elif dataFileType==0:
            with open(filename) as f:
                lines = f.readlines()
                for line in lines:
                    if line.strip() != "":
                        json.loads(line.strip())
                        es.index(index=index,doc_type=doctype,body=line)

                print "done indexing the given json file"
    except Exception, e:
        print >> stderr.write('ERROR: %s\n' % str(e))


inputFileName = None
index = None
docType = None
dataType = 1

def parse_args():
    global inputFileName
    global index
    global docType
    global dataType

    for arg_idx,arg in enumerate(sys.argv):
        if arg == "--input":
            inputFileName=sys.argv[arg_idx+1]
            continue
        if arg == "--index":
            index=sys.argv[arg_idx+1]
            continue
        if arg == "--docType":
            docType=sys.argv[arg_idx+1]
            continue
        if arg == "--dataType":
            dataType=sys.argv[arg_idx+1]
            continue

def die():
    print "Please input required parameters"
    print "Usage: loadDatainES.py --input <input filename> --index <index>" \
          "--docType <document Type> [--dataType <line file or not>]"

parse_args()
if inputFileName is None or index is None or docType is None:
    die()

loadDatainES(inputFileName,index,docType,dataType)




