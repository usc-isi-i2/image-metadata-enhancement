

def photoGetRequest(venueId):
	"""https://api.foursquare.com/v2/venues/43695300f964a5208c291fe3/photos?oauth_token=GGCGJ04UILGC43NGC2MRQODT1WQQQCN5EQCPORAWSIBPWKEU&v=20150508"""
	return "https://api.foursquare.com/v2/venues/"+venueId+"/photos?oauth_token="


def photoUrl(prefix, suffix):
	"""Construct the GET request for a foursquare photo."""
	return prefix+"original"+suffix

def getLatLong(latitude, longitude):
	"""returns the combination of latitude and longitude as required for ElasticSearch"""
	return latitude+", "+longitude