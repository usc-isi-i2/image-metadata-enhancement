
def md_getLong(field):
	"""Get longitude"""
	return field.split(',')[0]

def md_getLat(field):
	"""Get latitude"""
	return field.split(',')[1]

def md_getAccuracy(field):
	"""Get accuracy"""
	return field.split(',')[2]

def md_photo_uri(image_name):
	"""Get the URI of a photo coming from Medusa."""
	return "photo/"+image_name.strip().lower().replace('.jpg','')