image-metadata-enhancement
==========================

Requirements:

1. Karma should be running on your machine. You should run karma as a service. Refer https://github.com/usc-isi-i2/Web-Karma/wiki/Karma-As-A-Service

2. Elasticsearch should be running
3. Python dependencies:
Pip install geopy


API Requests:

## Four Square

GET request to get information on venues close to a point
```
https://api.foursquare.com/v2/venues/search?ll=34.02052371646544,-118.28535318374634&limit=50&radius=100&oauth_token=GGCGJ04UILGC43NGC2MRQODT1WQQQCN5EQCPORAWSIBPWKEU&v=20150508
```

GET request to get photos of a venue
```
https://api.foursquare.com/v2/venues/43695300f964a5208c291fe3/photos?oauth_token=GGCGJ04UILGC43NGC2MRQODT1WQQQCN5EQCPORAWSIBPWKEU&v=20150508
```

## Google nearBySearch
```
https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=500&types=food&key=
```

For OSM:
http://open.mapquestapi.com/xapi/ - Website

http://open.mapquestapi.com/xapi/api/0.6/node[amenity=pub][bbox=-77.041579,38.885851,-77.007247,38.900881]?key=YOUR_KEY_HERE


How to run the script:

python publishdata-ES.py --lat <latitude> --long <longitude>

In config.json file please configure the filepaths of karma-model uris and and files where


How it works:

1. We send a http request to each of foursquare, googlemaps and OSM api and process the response using karma as Service model. We will publish the json ld
we get from karma to ES.


Troubleshooting:
1. Encode the URL always when you send http request
2. Check the port numbers on which web service is running







