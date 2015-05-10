



def md_photo_uri(image_name):
	"""Get the URI of a photo coming from Medusa."""
	return "photo/"+image_name.strip().lower().replace('.jpg','')

def md_photo_location_uri(image_uri):
	"""The URI of the location where a photo was taken."""
	return image_uri+"/location"


def md_getLong(field):
	"""Get longitude"""
	return field.split(',')[0]

def md_getLat(field):
	"""Get latitude"""
	return field.split(',')[1]

def md_getAccuracy(field):
	"""Get accuracy"""
	return field.split(',')[2]


def md_getAzimuth(field):
	"""Get azimuth"""
	return field.split(',')[0]

def md_getPitch(field):
	"""Get pitch"""
	return field.split(',')[1]

def md_getRoll(field):
	"""Get roll"""
	return field.split(',')[2]

