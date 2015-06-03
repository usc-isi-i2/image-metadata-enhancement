

def glTypesNice(types):
	"""Make types into English words"""
	return types.replace('_',' ').title()

def getLatLong(latitude, longitude):
	"""returns the combination of latitude and longitude as required for ElasticSearch"""
	return latitude+", "+longitude