



def md_photo_uri(image_name):
	"""Get the URI of a photo coming from Medusa."""
	return "photo/"+image_name.strip().lower().replace('.jpg','')

def md_photo_location_uri(image_uri):
	"""The URI of the location where a photo was taken."""
	return image_uri+"/location"


def md_getLong(field):
	"""Get longitude"""
	return field.split(',')[0].strip()

def md_getLat(field):
	"""Get latitude"""
	return field.split(',')[1].strip()

def md_getAccuracy(field):
	"""Get accuracy"""
	return field.split(',')[2].strip()


def md_getAzimuth(field):
	"""Get azimuth"""
	return field.split(',')[0].strip()

def md_getPitch(field):
	"""Get pitch"""
	return field.split(',')[1].strip()

def md_getRoll(field):
	"""Get roll"""
	return field.split(',')[2].strip()


def md_face_uri(count):
	if int(count) > 0:
		return "medusa-content:human-face"
	else:
		return ''

def md_automobile_uri(count):
	if int(count) > 0:
		return "medusa-content:automobile"
	else:
		return ''

def md_out_indoor_uri(value):
	value = value.strip()
	if value == 'Outdoor':
		return "medusa-content:outdoor-scene"
	elif value == 'Indoor':
		return "medusa-content:indoor-scene"

	return ''

def md_blurry_uri(value):
	if int(value) > 0:
		return "medusa-technical:blurry"
	else:
		return ''
