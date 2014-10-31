def trimData(value):
	if(value):
		return ''.join(value.replace("_","").split()).lower();
