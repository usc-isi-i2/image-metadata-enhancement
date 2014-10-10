def  boundingbox(lat1, lat2, lng1, lng2):  
        if  lat1 and lat2 and lng1 and lng2:  
                xdiff = abs(float(lat1) - float(lat2))
                ydiff = abs(float(lng1) - float(lng2))
                return lat1 + " " + lng1 + " " + lat1 + " " + str(float(lng1)-ydiff) + " " + lat2 + " " + lng2 + " " + lat2 + " " + str(float(lng2)+ydiff) + " " + lat1 + " " + lng1
        else:
                return ""

