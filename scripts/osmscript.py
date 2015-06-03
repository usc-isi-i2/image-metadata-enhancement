import math
import geopy
from geopy.distance import vincenty
from geopy.distance import VincentyDistance
import subprocess
import sys

def compute_dist(lat1,long1,lat2,long2):
    source=(lat1,long1)
    dest = (lat2,long2)
    print vincenty(source,dest).km

lat = 40.69961164689744
long = 73.99835740533105
distance = 1000
TYPE="building"
def parse_args():
    global lat
    global long
    global distance

    for arg_idx,arg in enumerate(sys.argv):
        if arg == '--lat':
            lat = sys.argv[arg_idx+1]
            continue
        if arg == '--long':
            long = sys.argv[arg_idx + 1]
            continue
        if arg == '--dist':
            distance = sys.argv[arg_idx + 1]
            continue

origin = geopy.Point(lat, long)
#gives max lat long
destination = VincentyDistance(kilometers=1000).destination(origin, 45)

MAXLAT, MAXLON = destination.latitude, destination.longitude

destination = VincentyDistance(kilometers=1000).destination(origin, 225)

#gives min lat long
MINLAT,MINLON = destination.latitude,destination.longitude

cmd = 'mvn exec:java -Dexec.mainClass="edu.isi.karma.ugde.osm.osmFetch" -Dexec.args="%s %s %s %s %s" 2>&1 > /dev/null' % (MINLON, MINLAT, MAXLON, MAXLAT, TYPE)

#calls the osm fetch java code to get all data in boundingbox of min lat long and max lat long
output = subprocess.call(cmd,shell=True)

print output