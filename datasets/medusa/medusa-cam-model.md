## medusa-cam-sample.json

### PyTransforms
#### _long_
From column: _Location (long, lat, accuracy)_
>``` python
return md_getLong(getValue("Location (long, lat, accuracy)"))
```

#### _lat_
From column: _Location (long, lat, accuracy)_
>``` python
return md_getLat(getValue("Location (long, lat, accuracy)"))
```

#### _accuracy_
From column: _Location (long, lat, accuracy)_
>``` python
return md_getAccuracy(getValue("Location (long, lat, accuracy)"))
```

#### _photo_uri_
From column: _Image_
>``` python
return md_photo_uri(getValue("Image"))
```

#### _azimuth_
From column: _Azimuth, Pitch, Roll_
>``` python
return md_getAzimuth(getValue("Azimuth, Pitch, Roll"))
```

#### _pitch_
From column: _Azimuth, Pitch, Roll_
>``` python
return md_getPitch(getValue("Azimuth, Pitch, Roll"))
```

#### _roll_
From column: _Azimuth, Pitch, Roll_
>``` python
return md_getRoll(getValue("Azimuth, Pitch, Roll"))
```

#### _photo_location_uri_
From column: _photo_uri_
>``` python
return md_photo_location_uri(getValue("photo_uri"))
```

#### _faces_feature_
From column: _Num Faces_
>``` python
return md_face_uri(getValue("Num Faces"))
```

#### _automobile_feature_
From column: _Num Cars_
>``` python
return md_automobile_uri(getValue("Num Cars"))
```

#### _in_out_door_uri_
From column: _Indoor/Outdoor_
>``` python
return md_out_indoor_uri(getValue("Indoor/Outdoor"))
```


### Semantic Types
| Column | Property | Class |
|  ----- | -------- | ----- |
| _accuracy_ | `medusa:accuracy` | `schema:GeoCoordinates1`|
| _automobile_feature_ | `uri` | `medusa:ContentFeature2`|
| _azimuth_ | `medusa:azimuth` | `medusa:SensorOrientation1`|
| _faces_feature_ | `uri` | `medusa:ContentFeature1`|
| _in_out_door_uri_ | `uri` | `medusa:ContentFeature3`|
| _lat_ | `schema:latitude` | `schema:GeoCoordinates1`|
| _long_ | `schema:longitude` | `schema:GeoCoordinates1`|
| _photo_location_uri_ | `uri` | `schema:Place1`|
| _photo_uri_ | `uri` | `schema:Photograph1`|
| _pitch_ | `medusa:pitch` | `medusa:SensorOrientation1`|
| _roll_ | `medusa:roll` | `medusa:SensorOrientation1`|


### Links
| From | Property | To |
|  --- | -------- | ---|
| `schema:Photograph1` | `medusa:contentFeatures` | `medusa:ContentFeature1`|
| `schema:Photograph1` | `medusa:contentFeatures` | `medusa:ContentFeature2`|
| `schema:Photograph1` | `medusa:contentFeatures` | `medusa:ContentFeature3`|
| `schema:Photograph1` | `medusa:sensorOrientation` | `medusa:SensorOrientation1`|
| `schema:Photograph1` | `schema:contentLocation` | `schema:Place1`|
| `schema:Place1` | `schema:geo` | `schema:GeoCoordinates1`|
