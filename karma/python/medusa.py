
def md_getLong(field):
	"""Get longitude"""
	return field.split(',')[0]

def md_getLat(field):
	"""Get latitude"""
	return field.split(',')[1]

def md_getAccuracy(field):
	"""Get accuracy"""
	return field.split(',')[2]

