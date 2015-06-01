
def buildingUri(buildingId):
    return "osm/building/%s" % buildingId

def streetUri(streetId):
    return "osm/street/%s" % streetId

def kml2globalLocationNumber(kml):
    """<Point><coordinates>41.7634935,-72.6830523</coordinates></Point>"""
    (lon,lat) = kml[20:-22].split(',')
    return "%s, %s" % (lat, lon)
