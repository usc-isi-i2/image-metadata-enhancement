import math
import geopy
from geopy.distance import vincenty
from geopy.distance import VincentyDistance
import subprocess
import sys
import os
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

#helper function for computing the distance between two lat long pairs
def compute_dist(lat1,long1,lat2,long2):
    source=(lat1,long1)
    dest = (lat2,long2)
    print vincenty(source,dest).km

lat = 34.036254
long = -118.283990
distance = 0.1
TYPE="building"


def getOsmData(lat,long,distance):

    origin = geopy.Point(lat, long)
#gives max lat long
    destination = VincentyDistance(kilometers=distance).destination(origin, 45)

    MAXLAT, MAXLON = destination.latitude, destination.longitude

    destination = VincentyDistance(kilometers=distance).destination(origin, 225)

#gives min lat long
    MINLAT,MINLON = destination.latitude,destination.longitude

    cmd = 'mvn exec:java -Dexec.mainClass="edu.isi.karma.ugde.osm.osmFetch" -Dexec.args="%s %s %s %s %s" 2>&1 > /dev/null' % (MINLON, MINLAT, MAXLON, MAXLAT, TYPE)
    print cmd
    output=subprocess.call(cmd,shell=True)
    print "output was %s" % output


if __name__ == "__main__":
    getOsmData(lat,long,distance)

