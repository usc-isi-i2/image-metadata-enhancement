
def buildingUri(buildingId):
    return "osm/building/%s" % buildingId

def streetUri(streetId):
    return "osm/street/%s" % streetId

def kml2globalLocationNumber(kml):
    """<Point><coordinates>41.7634935,-72.6830523</coordinates></Point>"""
    (lon,lat) = kml[20:-22].split(',')
    return "%s, %s" % (lat, lon)

def kml2latlong(kml):
    kmlList = kml[25:-27].split(',')

    jsonArray = []
    lat_list = []
    long_list = []
    for (index, value) in enumerate(kmlList):
        if index % 2 == 0 and value != '0':
            valueList = value.split(' ')
            if len(valueList) > 1:
                lat = valueList[1]
            else:
                lat = valueList[0]
            lat_list.append(lat)
        elif value != '0':
            long_list.append(value)

    for (lat, long) in zip(lat_list, long_list):
        jsonArray.append([ str(lat), str(long)])


    return json.dumps(jsonArray)