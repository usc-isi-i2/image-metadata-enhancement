## OPENSTREETMAP_STREET.xml

### PyTransforms
#### _street_uri_
From column: _osm / kml / Document / Placemark / id_
>``` python
return generateStreetUri(getValue("id"))
```


### Semantic Types
| Column | Property | Class |
|  ----- | -------- | ----- |
| _CityName_ | `bldg:cityName` | `ugde:Location1`|
| _CountyName_ | `bldg:countyName` | `ugde:Location1`|
| _KmlGeometry_ | `BuildingOntology:streetKml` | `bldg:Street1`|
| _Name_ | `bldg:streetName` | `bldg:Street1`|
| _StateName_ | `bldg:stateName` | `ugde:Location1`|
| _Type_ | `bldg:streetType` | `bldg:Street1`|
| _id_ | `bldg:openstreetmapNumber` | `bldg:Street1`|
| _street_uri_ | `uri` | `bldg:Street1`|


### Links
| From | Property | To |
|  --- | -------- | ---|
| `bldg:Street1` | `ugde:street_location` | `ugde:Location1`|
