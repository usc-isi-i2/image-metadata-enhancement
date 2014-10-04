def imageurl(photo_reference):
        if photo_reference:
                return "https://maps.googleapis.com/maps/api/place/photo?maxwidth=1600&photoreference="+ photo_reference
        else:
                return ""
