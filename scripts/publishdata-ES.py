import json
import urllib2
import urllib
import sys
import osmscript
import json
import geopy
from geopy.distance import VincentyDistance
import xmltodict

def getBoundaryBox(lat,lng,dist):
    origin = geopy.Point(lat, lng)
    destination = VincentyDistance(kilometers=dist).destination(origin, 45)
    MAXLAT, MAXLON = destination.latitude, destination.longitude
    destination = VincentyDistance(kilometers=dist).destination(origin, 225)
    MINLAT,MINLON = destination.latitude,destination.longitude
    return "[bbox=" + str(MINLON) +"," + str(MINLAT) + "," + str(MAXLON) + ","  + str(MAXLAT) + "]"

def getType(tags):
    type = "way"
    if isinstance(tags,dict):
        tags = [tags]

    for kv in tags:
        if kv["@k"]=="building" or (kv["@k"]=="area" and kv["@v"]=="yes"):
            type = "building"
            break
        elif kv["@k"]=="highway" or kv["@k"]=="railway":
            type = kv["@k"]
            break
        elif kv["@k"]=="leisure":
            type = kv["@v"]
            break
        elif kv["@k"]=="amenity":
            type = kv["@v"]
            break
    return type


def getName(tags):
    name = ""
    if isinstance(tags,dict):
        tags=[tags]

    for tag in tags:
        if tag["@k"]=="name":
                name = tag["@v"]
                break
    return name

def getShapeCoords(nodes,type):
    points = [];

    for node in nodes:
        points.append([float(node["@lat"]),float(node["@lon"])])

    coords = ""
    for point in points:
        coords += " ";
        coords += str(point[0])
        coords += " "
        coords += str(point[1])
    return coords.strip()


#Add "node" information inline in "ways"
#Remove osm.node and osm.relations - keep osm.ways
#Extract name from "tags" and add in "way" object
def expand(o):
    nodesMap = {}
    paths = []
    buildings = []
    for n in o["osm"]["node"]:
        nodesMap[n["@id"]]=n;
    for way in o["osm"]["way"]:
        nds = []
        for node in way["nd"] :
            nds.append(nodesMap[node["@ref"]])
        name = ""
        type="way"
        if "tag" in way:
            name = getName(way["tag"])
            type = getType(way["tag"])

        way["name"] = name
        way["nd"] = nds
        way["type"] = type
        way["shapecoordinates"] = getShapeCoords(nds,type)

        if type=="building":
            buildings.append(way)
        else:
            paths.append(way)

        args = (len(way["nd"]),type,name)
        #print '{0:<5} {1:<15} {2:>8}'.format(*args)

    o["osm"].pop("node",None)
    o["osm"].pop("relation",None)
    o["osm"].pop("way",None)
    o["osm"]["paths"] = paths
    o["osm"]["buildings"] = buildings


    return o;



def convertToJSON(xmlResponse):
    o = xmltodict.parse(xmlResponse)
    o = expand(o)
    return o


def addGeoJSONtoJSONLD(o):

    for item in o:
        geoJSONstr = ""
        points = item["geo"]["line"].split(" ")
        geolinestring = []
        for i in range(0,len(points),2):
            geolinestring.append([points[i+1],points[i]])
            geoJSONstr += (str(points[i])+" " + str(points[i+1]))
        pin = {}
        pin["location"] = {"type":"linestring","coordinates":geolinestring}
        item["geoJSONpin"] = pin
        item["geoJSONpinStr"] = geoJSONstr
    return o;


def getjsonld(lat,lng,url,type,config):
    if type is 'foursquare':
        api_request = 'https://api.foursquare.com/v2/venues/search?ll=' + str(lat) + ',' + str(lng) \
        + '&client_id=4QJ5FZZWNOGPLT3A25UO4CN0EK3FQPJS0PNRO3KC31RL15YR&client_secret=FIYPITFGMNWYGJ02EX4XQINXJETDTNTSJ4ICTVCDVETQOHE3&v=20150528'
        print(api_request)
        encoded_url = urllib.quote_plus(api_request)
        req = 'R2rmlURI=file://' + config['FoursquareKarmaModel'] +'&ContentType=JSON&DataURL='
        req = req + encoded_url
        req=urllib2.Request(url,req)
        response=urllib2.urlopen(req)
        jsonOutput=json.loads(response.read())
        file = open(config['foursquare-jsonldPath'],'w')
        file.write(json.dumps(jsonOutput,indent=4))
        file.close()
    elif type is 'googlemaps':
        api_request = 'https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=' + str(lat) + ',' + str(lng) +  \
        '&radius=500&key=AIzaSyCvuvRV_XM1u49nRik46VsE0_1amOexOrw'
        encoded_url = urllib.quote_plus(api_request)
        req = 'R2rmlURI=file://' + config['GoogleMapsKarmaModel'] + '&ContentType=JSON&DataURL='
        req = req + encoded_url
        req=urllib2.Request(url,req)
        response=urllib2.urlopen(req)
        jsonOutput=json.loads(response.read())
        file = open(config['googlemaps-jsonldPath'],'w')
        file.write(json.dumps(jsonOutput,indent=4))
        file.close()
    elif type is 'osm':
        api_request_url = "http://open.mapquestapi.com/xapi/api/0.6/map/*" + getBoundaryBox(lat,lng,0.3) + "?key=b8uBAGZowqebVTadpoak56zmANQtQyUA"
        xmlResponse = urllib2.urlopen(api_request_url).read()
        o = convertToJSON(xmlResponse)
        file = open(config['osm-waysJsonPath'],'w')
        file.write(json.dumps(o,indent=4))
        file.close()

        req = 'R2rmlURI=file://' + config['OsmKarmaModel'] +'&ContentType=JSON&' \
            'DataURL=file://' + config['osm-waysJsonPath'];
        req=urllib2.Request(url,req)
        response=urllib2.urlopen(req)
        jsonOutput=json.loads(response.read())
        jsonOutput = addGeoJSONtoJSONLD(jsonOutput)
        file = open(config['osm-jsonldPath'],'w')
        file.write(json.dumps(jsonOutput,indent=4))
        file.close()


def pushjsonldToES(url,esHost,esPort,esIndex,esType,config):
    if esType is 'foursquare':
        api_request='DataURL=file://' + config['foursquare-jsonldPath']
    elif esType is 'googlemaps':
        api_request='DataURL=file://' + config['googlemaps-jsonldPath']
    elif esType is 'osm':
        api_request='DataURL=file://' + config['osm-jsonldPath']
    api_request = api_request + '&ESHostname='+esHost+'&ESPort='+esPort+'&ESIndex='+esIndex+'&ESType='+esType
    req=urllib2.Request(url,api_request)
    response=urllib2.urlopen(req)
    jsonOutput=json.loads(response.read())
    print(jsonOutput)

def main(argv):
    lat = 34.02235
    lng = -118.28512
    for arg_idx,arg in enumerate(argv):
        if arg == "--lat":
            lat=sys.argv[arg_idx+1]
            continue
        if arg == "--long":
            lng=sys.argv[arg_idx+1]
            continue

    config = json.load(open("../config.json"))
    karma_RDF_URL = config['Karma-RDF-URL']
    karma_publishES_URL = config['Karma-PublishES-URL']
    esHost = config['ElasticSearchHost']
    esPort = config['ElasticSearchPort']


    #for foursquare api
    getjsonld(lat,lng,karma_RDF_URL,'foursquare',config)
    pushjsonldToES(karma_publishES_URL,esHost,esPort,'foursquare_data','foursquare',config)


    #googlemaps-api:
    getjsonld(lat,lng,karma_RDF_URL,'googlemaps',config)
    pushjsonldToES(karma_publishES_URL,esHost,esPort,'googlemaps_data','googlemaps',config)

    #osm api
    getjsonld(lat,lng,karma_RDF_URL,'osm',config)
    pushjsonldToES(karma_publishES_URL,esHost,esPort,'osm_data','osm',config)



if __name__=="__main__":
    main(sys.argv[1:])