import json
import urllib2
import urllib
import sys
from elasticsearch import Elasticsearch
from sys import stderr
import osmscript

def createMappings(filepath,hostname,port,index):
    try:
        print("Connecting to "+hostname + " at port: " + str(port))
        es=Elasticsearch(['http://localhost:9200'])
        with open(filepath) as m:
            mapping = m.read()
            print "Mapping "+mapping
            es.indices.create(index=index,body=mapping,ignore=400)
    except Exception, e:
        print >> stderr.write('ERROR: %s\n' % str(e))

def useKarmaService(url,fileUri,filename):
    req=urllib2.Request(url,fileUri)
    response=urllib2.urlopen(req)
    jsonOutput=json.loads(response.read())
    file = open(filename,'w')
    file.write(json.dumps(jsonOutput,indent=4))
    print(jsonOutput)


def getjsonld(lat,long,url,type):

    if type is 'foursquare':
        api_request = 'https://api.foursquare.com/v2/venues/search?ll='+str(lat)+','+str(long)+\
              '&client_id=4QJ5FZZWNOGPLT3A25UO4CN0EK3FQPJS0PNRO3KC31RL15YR&client_secret=FIYPITFGMNWYGJ02EX4XQINXJETDTNTSJ4ICTVCDVETQOHE3&v=20150528'

        encoded_url = urllib.quote_plus(api_request)

        req = 'R2rmlURI=file:///Users/d3admin/Desktop/github_repos/image-metadata-enhancement/datasets/' \
              'foursquare/foursquare-model-new.ttl&ContentType=JSON&'
        req = req + encoded_url

        req=urllib2.Request(url,req)
        response=urllib2.urlopen(req)
        jsonOutput=json.loads(response.read())
        file = open('foursquare.json','w')
        file.write(json.dumps(jsonOutput,indent=4))
        print(jsonOutput)

    elif type is 'googlemaps':
        api_request = 'https://maps.googleapis.com/maps/api/place/nearbysearch/json?' \
              'location='+str(lat)+','+str(long)+'&radius=500&key=AIzaSyCvuvRV_XM1u49nRik46VsE0_1amOexOrw'
        encoded_url = urllib.quote_plus(api_request)

        req = 'R2rmlURI=file:///Users/d3admin/Desktop/github_repos/image-metadata-enhancement/datasets/' \
          'google/nearbysearch/google-nearbysearch-model-new.ttl&ContentType=JSON&'

        req = req + encoded_url

        req=urllib2.Request(url,req)
        response=urllib2.urlopen(req)
        jsonOutput=json.loads(response.read())
        file = open('googlemaps.json','w')
        file.write(json.dumps(jsonOutput,indent=4))
        print(jsonOutput)


    elif type is 'osm':
        req = 'R2rmlURI=file:///Users/d3admin/Desktop/image-metadata-enhancement/datasets/osm/building-model.ttl&ContentType=XML&' \
              'DataURL=file:///Users/d3admin/Desktop/image-metadata-enhancement/datasets/osm/building-sample.xml'

        req=urllib2.Request(url,req)
        response=urllib2.urlopen(req)
        jsonOutput=json.loads(response.read())
        file = open('osm.json','w')
        file.write(json.dumps(jsonOutput,indent=4))
        print(jsonOutput)



def pushjsonldToES(url,esHost,esPort,esIndex,esType):
    if esType is 'foursquare':
        api_request='DataURL=file:///Users/d3admin/Desktop/image-metadata-enhancement/scripts/foursquare.json'
    elif esType is 'googlemaps':
        api_request='DataURL=file:///Users/d3admin/Desktop/image-metadata-enhancement/scripts/googlemaps.json'
    elif esType is 'osm':
        api_request='DataURL=file:///Users/d3admin/Desktop/image-metadata-enhancement/scripts/osm.json'

    api_request = api_request + '&ESHostname='+esHost+'&ESPort='+esPort+'&ESIndex='+esIndex+'&ESType='+esType

    req=urllib2.Request(url,api_request)
    response=urllib2.urlopen(req)
    jsonOutput=json.loads(response.read())
    file = open('osm.json','w')
    file.write(json.dumps(jsonOutput,indent=4))
    print(jsonOutput)

lat = 40.7
long = -74

def parse_args():
    global lat
    global long
    for arg_idx,arg in enumerate(sys.argv):
        if arg == "--lat":
            lat=sys.argv[arg_idx+1]
            continue
        if arg == "--long":
            long=sys.argv[arg_idx+1]
            continue


esHost='localhost'
esPort='9200'
esIndex = 'foursquare_data'
esType = 'foursquare'


url = 'http://localhost:8080/rdf/r2rml/json'
osmscript.getOsmData(26.2,-15.3,0.1)

#for foursquare api
createMappings('../mappingFiles/foursquaremapping.json',esHost,esPort,'foursquare_data')
getjsonld(lat,long,url,'foursquare')
url = 'http://localhost:8080/publish/es/jsonld'
pushjsonldToES(url,'localhost','9200','foursquare_data','foursquare')


#googlemaps-api:
createMappings('../mappingFiles/googlemapping.json',esHost,esPort,'googlemaps_data')
url = 'http://localhost:8080/rdf/r2rml/json'
getjsonld(lat,long,url,'googlemaps')
url = 'http://localhost:8080/publish/es/jsonld'
pushjsonldToES(url,'localhost','9200','googlemaps_data','googlemaps')

##osm api
createMappings('../mappingFiles/osmmapping.json',esHost,esPort,'osm_data')
url = 'http://localhost:8080/rdf/r2rml/json'
getjsonld(lat,long,url,'osm')
url = 'http://localhost:8080/publish/es/jsonld'
pushjsonldToES(url,'localhost','9200','osm_data','osm')
