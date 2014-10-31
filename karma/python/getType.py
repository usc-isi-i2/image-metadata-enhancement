def getType(values1, values2, values3, values4, values5, values6, values7):
	joinTypes = ' '.join(filter(None, (values1, values2, values3, values4, values5,values6, values7))).split()
	prepend = 'class/'
	finalType = (prepend + s for s in joinTypes)
	finalType = ', '.join(finalType)
	return finalType